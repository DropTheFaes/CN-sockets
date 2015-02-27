import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class Head{
	
	String url;
	int port;
	int httpVersion;
	
	public Head(String url, int port, int httpVersion){
		this.url = url;
		this.port = port;
		this.httpVersion = httpVersion;
	}

	public void run() {
		DataOutputStream outToServer = null;
		BufferedReader inFromServer = null;
		try {
			Socket socket = new Socket(url, port);
			
			outToServer = new DataOutputStream(socket.getOutputStream());
	        inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        
	        String sentence = ""; //TODO sentence is wat naar de server gestuurd moet worden
			outToServer.writeBytes(sentence + '\n');
			
	        String response = inFromServer.readLine();
	        
		} catch (UnknownHostException e) {
			// TODO moet wss iets anders zijn
			System.out.println("No host found on this ip.");
		} catch (IOException e) {
			// TODO moet wss iets anders zijn
			System.out.println("No server found on this portnumber.");
		}

	}
	
	private void handleResponse(){
		//TODO implement
	}

}
