import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;


public abstract class HTTP {

	protected String command;
	protected String url;
	protected String path;
	protected int port;
	protected Socket socket;
	protected DataOutputStream outToServer;
	protected BufferedReader inFromServer;
	
	public HTTP(String command, String url, int port){
		this.command = command;
		this.port = port;
		
		this.url = url.split("/")[0];	
		if(url.split("/").length < 2){
			this.path = "";
		}
		else{
			this.path = url.split("/")[1];
		}
		
		try {
			this.socket = new Socket(this.url, this.port);
			
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
        	String sentence = this.command + " /" + this.path + " HTTP/1." + this.getHttpVersion(); //sentence is wat naar de server gestuurd moet worden
        	outToServer.writeBytes(sentence + '\n' + '\n'); //TODO als tweede '\n' niet nodig is bij POST: 
        													//zend die apart in specifieke 'handlerequest'-methode, 
        													//of extra methode 'sendrequestspecific' die erbijgezet wordt in 
        													//de case bij 'handleresponse'-methode 
			
        	handleResponse();
        	
        	this.outToServer.close();
        	this.inFromServer.close();
        	this.socket.close();
		
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
	
	protected void handlePostResponse() throws IOException{
		Scanner scan = new Scanner(System.in);
		System.out.println("Give a String for input, please:");
		String input = scan.nextLine();
		scan.close();
		this.outToServer.writeBytes(input);
		String line;
		if((line = this.inFromServer.readLine()) != null){
			System.out.println(line);
		}	
	}
	
	protected abstract int getHttpVersion();
}
