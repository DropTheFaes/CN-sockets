import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Super class for HTTP.
 */
public abstract class HTTP {

	protected String command;
	protected String url;
	protected String path;
	protected int port;
	protected Socket socket;
	protected DataOutputStream outToServer;
	protected BufferedReader inFromServer;
	protected DataInputStream dataInFromServer;
	protected ByteArrayOutputStream outPutStream;
	
	/**
	 * Initialize the HTTP-version with the given command, given URL and given port.
	 * 
	 * @param command
	 * 		The command that needs to be handled.
	 * @param givenURL
	 * 		The URL to which the command that needs to be handled applies.
	 * @param port
	 * 		The port that needs to be accessed.
	 */
	public HTTP(String command, String givenURL, int port){
		this.command = command;
		this.port = port;
		this.url = givenURL.split("/")[0];	
		if(givenUrl.split("/").length < 2){
			this.path = "";
		}
		else{
			this.path = givenUrl.replace(url, "");
			this.path = (String) this.path.subSequence(1, this.path.length()-1);
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
	
	/**
	 * Send the command to the chosen HTTP version with the given path.
	 */
	public void sendRequest(){
        try{
        	String sentence = this.command + " /" + this.path + " HTTP/1." + this.getHttpVersion(); //sentence is wat naar de server gestuurd moet worden
        	outToServer.writeBytes(sentence + '\n');
			
        	handleResponse();
        	
        	this.outToServer.close();
        	this.inFromServer.close();
        	this.socket.close();
		
		} catch (IOException e) {
		// TODO juist?
		System.out.println("No server found on this portnumber.");
		}
	}
	
	protected abstract void handleResponse() throws IOException;
	
	/**
	 * Handle the HEAD-response.
	 * 
	 * @throws IOException
	 * 		When the writer receives invalid input, throw IOException.
	 */
	protected void handleHeadResponse() throws IOException{
		PrintWriter writer = new PrintWriter("headResponse.txt");
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
	
	/**
	 * Handle the GET-response.
	 * 
	 * @throws IOException
	 * 		When the writer receives invalid input, throw IOException.
	 */
	protected abstract void handleGetResponse() throws IOException;
	
	/**
	 * Return the images from the given file.
	 * 
	 * @param f
	 * 		The file from which the images need to be retrieved.
	 * 
	 * @throws IOException
	 * 		When the writer receives invalid input, throw IOException.
	 */
	protected abstract void getImages(File f) throws IOException;
	
	/**
	 * Return the images from the given source.
	 * 
	 * @param imageSource
	 * 		The source from which the images need to be retrieved.
	 * 
	 * @throws UnknownHostException
	 * 		When the host cannot be resolved, throw UnknownHostException.
	 * @throws IOException
	 * 		When the writer receives invalid input, throw IOException.
	 */
	protected abstract void getImage(String imageSource) throws UnknownHostException, IOException;
	
	/**
	 * Handle the PUT- or POST-response.
	 * 
	 * @throws IOException
	 * 		When the writer receives invalid input, throw IOException.
	 */
	protected void handlePutPostResponse() throws IOException{
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