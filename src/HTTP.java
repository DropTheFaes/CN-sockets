import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


public abstract class HTTP {

	protected String command;
	protected String url;
	protected int port;
	protected Socket socket;
	protected DataOutputStream outToServer;
	protected BufferedReader inFromServer;
	
	public HTTP(String command, String url, int port){
		this.command = command;
		this.url = url;
		this.port = port;
		
		try {
			this.socket = new Socket(url, port);
			
			this.outToServer = new DataOutputStream(socket.getOutputStream());
	        this.inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));	        
		        	        
		} catch (UnknownHostException e) {
			// TODO juist?
			System.out.println("No HTTP server found on this host.");
		} catch (IOException e) {
			// TODO juist?
			System.out.println("No server found on this portnumber.");
		}
	}
	
	public void sendRequest(){
        try{
        	String sentence = this.command + " / " + "HTTP/1." + this.getHttpVersion(); //sentence is wat naar de server gestuurd moet worden
        	outToServer.writeBytes(sentence + '\n' + '\n'); //TODO als tweede '\n' niet nodig is bij POST: 
        													//zendt die apart in specifieke 'handlerequest'-methode, 
        													//of extra methode 'sendrequestspecific' die erbijgezet wordt in 
        													//de case bij 'handleresponse'-methode 
			
        	handleResponse();
        	
        	//close streams & socket?? (--> hangt af van antwoord assistent op vraag over of connection moet openblijven)
		
		} catch (IOException e) {
		// TODO juist?
		System.out.println("No server found on this portnumber.");
		}
	}
	
	private void handleResponse() throws IOException{
		switch (this.command){
			case "HEAD":
				handleHeadResponse();
				break;
			case "GET":
				handleGetResponse();
				break;
			case "PUT":
				handlePutResponse();
				break;
			case "POST":
				handlePostResponse();
				break;
		}
	}
	
	protected void handleHeadResponse() throws IOException{
		PrintWriter writer = new PrintWriter("headResponse.txt"); //, "UTF-8"
		ArrayList<String> response = new ArrayList<String>();
		String line = null;
		while((line = this.inFromServer.readLine()) != null){
			response.add(line);
		}
		for (String responseLine : response) {
			System.out.println(responseLine);
			writer.println(responseLine);
		}
		writer.close();
	}
	
	protected abstract void handleGetResponse();
	
	protected abstract void handlePutResponse();
	
	protected abstract void handlePostResponse();
	
	protected abstract int getHttpVersion();
}
