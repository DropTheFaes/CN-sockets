import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;


public class HTTP0 extends HTTP{
	
	/**
	 * Initialize the HTTP-version to HTTP version 1.0 with the given command, given url and given port.
	 * 
	 * @param command
	 * 		The command that needs to be handled.
	 * @param url
	 * 		The url on which the given command needs to be executed.
	 * @param port
	 * 		The port that needs to be accessed.
	 */
	public HTTP0(String command, String url, int port){
		super(command, url, port);
	}

	/**
	 * Is called everytime a connection to a server is needed.
	 * In the case of HTTP1.0 there should be a new socket everytime this happens.
	 */
	@Override
	public void setSocket() throws UnknownHostException, IOException{
		//If a socket is already running, close it before creating a new one.
		//TODO reden bijzetten!
		if(this.socket != null){
			this.socket.close(); 
			outToServer.close();
			inFromServer.close();
			dataInFromServer.close();
		}
		//Create a new socket and all the needed attributes.
		this.socket = new Socket(this.url, this.port);
		this.outToServer = new DataOutputStream(this.socket.getOutputStream());
        this.inFromServer = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.dataInFromServer = new DataInputStream(socket.getInputStream());
	}
	
	/**
	 * Send the given sentence (command, URI and HTTP-version to the server. 
	 */
	public void sendSentence(String sentence) throws IOException{
		this.outToServer.writeBytes(sentence + "\n");
	}
	
//	/**
//	 * Handle the response from the command.
//	 * 
//	 * @throws IOException
//	 * 		If the command is not known, throw IOException.
//	 */
//	@Override
//	public void handleResponse() throws IOException{
//		switch (this.command){
//			case "HEAD":
//				outToServer.writeBytes("\n");
//				handleHeadResponse();
//				break;
//			case "GET":
//				//nog geen 2e 'newline' --> als we if-modified-since moeten opvragen moet dat er nog tussen
//				handleGetResponse();
//				break;
//			case "PUT":
//				outToServer.writeBytes("\n");
//				handlePutPostResponse();
//				break;
//			case "POST":
//				outToServer.writeBytes("\n");
//				handlePutPostResponse();
//				break;
//		}
//		this.socket.close(); 
//		outToServer.close();
//		inFromServer.close();
//		dataInFromServer.close();
//	}
//	
//	/**
//	 * Handle the GET-response for HTTP 1.0.
//	 * 
//	 * @throws IOException
//	 * 		If the writer receives invalid input, throw IOException.
//	 */
//	@Override
//	protected void handleGetResponse() throws IOException {
//		
//	}
//	
//	/**
//	 * Retrieve the images from the given file and append them to a file.
//	 * 
//	 * @param f
//	 * 		The file from which the images need to be retrieved.
//	 * 
//	 * @throws IOException
//	 * 		If the document receives invalid input, throw IOException.
//	 */
//	protected void getImages(File f) throws IOException{
//		
//	}
//	
//	/**
//	 * Retrieve the images from the given source and append them to a file.
//	 * 
//	 * @param imageSource
//	 * 		The source from which the images need to be retrieved.
//	 * 
//	 * @throws UnknownHostException
//	 * 		If the given source has an unknown host, trhow UnknownHostException
//	 * @throws IOException
//	 * 		If the writer receives invalid input, throw IOException.
//	 */
//	protected void getImage(String imageSource) throws UnknownHostException, IOException{
//		
//	}
//	
	/**
	 * Return the HTTP-version that is used.
	 * 
	 * @return The HTTP-version that is used.
	 */
	@Override
	protected int getHttpVersion() {
		return 0;
	}
}
