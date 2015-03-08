import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class HTTP1 extends HTTP{
	
	public HTTP1(String command, String url, int port){
		super(command, url, port);	
		this.socket = null;
	}

	@Override
	public void setSocket() throws UnknownHostException, IOException{
		if(this.socket == null){
			this.socket = new Socket(this.url, this.port);
			this.outToServer = new DataOutputStream(this.socket.getOutputStream());
	        this.inFromServer = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
	        this.dataInFromServer = new DataInputStream(socket.getInputStream());
		}
	}
	
	public void sendSentence(String sentence) throws IOException{
		this.outToServer.writeBytes(sentence + "\n");
		outToServer.writeBytes("Host: " + this.url + "\n");
	}
	
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
//	 * Handle the GET-response for HTTP 1.1.
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
//	@Override
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
//	 * 		If the given source has an unknown host, throw UnknownHostException
//	 * @throws IOException
//	 * 		If the writer receives invalid input, throw IOException.
//	 */
//	@Override
//	protected void getImage(String imageSource) throws UnknownHostException, IOException{
//		
//	}
	
	@Override
	protected int getHttpVersion() {
		return 1;
	}

}
