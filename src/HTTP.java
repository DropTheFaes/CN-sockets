import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Super class for HTTP.
 */
public abstract class HTTP {

	protected String command; //HEAD-GET-PUT-POST
	protected String url; //[www.]example.com
	protected String path; //everything that comes after the '.com'
	protected int port;
	protected Socket socket;
	protected DataOutputStream outToServer;
	protected BufferedReader inFromServer;
	protected DataInputStream dataInFromServer;
	
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
		
		//Check if a path is given, if it is not, the path is set to the empty string.
		if(givenURL.split("/").length < 2){
			this.path = "";
		}
		//If a path is given, set the given path to the given path.
		else{
			this.path = givenURL.replace(url, "");
			this.path = (String) this.path.subSequence(1, this.path.length()-1);
		}
	}
	
	/**
	 * Send the command to the chosen HTTP version with the given path.
	 */
	public void sendRequest(){
		//Try to initialize the socket and send the command to the server.
		try {
			setSocket();	
	        
	        String sentence = this.command + " /" + this.path + " HTTP/1." + this.getHttpVersion(); //sentence is wat naar de server gestuurd moet worden
        	sendSentence(sentence);
		        	        
		}
		//If the given host does not exist, throw an UnknownHostException.
		catch (UnknownHostException e) {
			System.out.println("No HTTP server found on this host.");
		}
		//If the port number is incorrect, throw an IOException.
		catch (IOException e) {
			System.out.println("No server found on this port number.");
		}
	}
	
	public abstract void setSocket() throws UnknownHostException, IOException;
	
	public abstract void sendSentence(String sentence) throws IOException;
	
	/**
	 * Handle the sent responses accordingly.
	 */
	public void handleResponse() throws IOException{
		switch (this.command){
			//If the command is a HEAD-request, pass the request to the HEAD-handler.
			case "HEAD":
				outToServer.writeBytes("\n");
				handleHeadResponse();
				break;
			//If the command is a GET-request, pass the request to the GET-handler.
			case "GET":
				//nog geen 2e 'newline' --> als we if-modified-since moeten opvragen moet dat er nog tussen
				handleGetResponse();
				break;
			//If the command is a PUT-request, pass the request to the PUT- & POST-handler.
			case "PUT":
				outToServer.writeBytes("\n");
				handlePutPostResponse();
				break;
			//If the command is a POST-request, pass the request to the PUT- & POST-handler.
			case "POST":
				outToServer.writeBytes("\n");
				handlePutPostResponse();
				break;
		}
		//Close the socket and the in- and outputstream to and from the server. Close the data connection with the server.
		this.socket.close(); 
		outToServer.close();
		inFromServer.close();
		dataInFromServer.close();
	}
	
	/**
	 * Handle the HEAD-response.
	 * 
	 * @throws IOException
	 * 		When the writer receives invalid input, throw IOException.
	 */
	protected void handleHeadResponse() throws IOException{
		PrintWriter writer = new PrintWriter("headResponse.txt");
		ArrayList<String> response = readHeader();
		//Write the response lines to the headResponse.txt-file
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
	 * 		If the writer receives invalid input, throw IOException.
	 */
	protected void handleGetResponse() throws IOException{
		readHeader();
		
		//url splitten op punten, zodat we 'google' of 'example' kunnen gebruiken in de filename van de file waarin we de html-file opslaan
		//Split the URL at dots so that the name of the site can be used as the file name where the HTML-file will be saved.
		String siteURL = url.split("\\.")[0];
		if (siteURL.equals("www")){
			siteURL = url.split("\\.")[1];
		}
		
		//file aanmaken waarin we de html-pagina zullen opslaan
		//Create the file where the HTML-page will be saved.
		File f = null;
		if (this.path == ""){
			f = new File(siteURL + ".html");
		}
		//Open a file with the url as the name.
		else{
			f = new File(siteURL + "/" + this.path);
		}
		
		//TODO als de file nog niet bestaat, slaan we de response gewoon op in de file, moet de If-Modified-Since niet gecontroleerd worden
		//--> door de if op de lijn hieronder, maar laten we voorlopig effe buiten beschouwing, doen we wel als alles werkt pakt
		//if(!f.exists())
		
		outToServer.writeBytes("\n");
		
		readHtmlPage(f);
		
		getImages(f);
	}
	

	protected ArrayList<String> readHeader() throws IOException{
		ArrayList<String> header = new ArrayList<String>();
		
		byte[] b = new byte[1];
		String currentString = "";
		String newChar;
		int line;
		
		while((line = this.dataInFromServer.read(b, 0, 1)) != -1){
			newChar = new String(b, 0, line);
			if((newChar.contains("\n")  || newChar.contains("\r") )){
				if((header.size() == 0 && currentString != "") || header.size() != 0)
					header.add(currentString);
				currentString = "";
			}else
				currentString += newChar;
			
			if(header.size() > 2 && (header.get(header.size()-1).equals("") && header.get(header.size()-2).equals("")))
				break;
		}
		
		//in geval dat er nog rommel staa voor het begin van de header
		while(!header.get(0).contains("HTTP")){
			header.remove(0);
		}
		
		return header;
	}
	
	/**
	 * Read the HTML page from the given file.
	 * 
	 * @param f
	 * 		The file that needs to be read.
	 * @throws IOException
	 * 		If the file contains invalid input, throw an IOException.
	 */
	protected void readHtmlPage(File f) throws IOException{
		PrintWriter writer = new PrintWriter(f);
		ArrayList<String> response = new ArrayList<String>();
		String line = null;
		//While the file contains text, add it to the response.
		while((line = this.inFromServer.readLine()) != null){
			response.add(line);
		}
		//Print and write the response that was read from the file.
		for (String responseLine : response) {
			System.out.println(responseLine);
			writer.println(responseLine);
		}
		writer.close();
	}
	
	/**
	 * Retrieve the images from the given file.
	 * 
	 * @param f
	 * 		The file from which the images need to be retrieved.
	 * 
	 * @throws IOException
	 * 		When the writer receives invalid input, throw an IOException.
	 */
	protected void getImages(File f) throws IOException{
		Document doc = Jsoup.parse(f, "UTF-8");
		Elements images = doc.select("img");
		
		//Retrieve the sources of the images and get them.
		for(Element image: images){
			getImage(image.attr("src"));
		}
	}
	
	/**
	 * Retrieve the images from the given source.
	 * 
	 * @param imageSource
	 * 		The source from which the images need to be retrieved.
	 * 
	 * @throws UnknownHostException
	 * 		When the host cannot be resolved, throw an UnknownHostException.
	 * @throws IOException
	 * 		When the writer receives invalid input, throw an IOException.
	 */
	protected void getImage(String imageSource) throws UnknownHostException, IOException{
		System.out.println(imageSource);//TODO check
		
		setSocket();
		
		String imageName = imageSource.split("/")[imageSource.split("/").length-1];
		String imageKind = imageName.split("\\.")[1];
		ByteArrayOutputStream outPutStream = new ByteArrayOutputStream();
		FileOutputStream toFile = new FileOutputStream(imageName);
				
		String getImageSentence = "GET " + imageSource +" HTTP/1." + getHttpVersion() + "\n";
		
		sendSentence(getImageSentence);
		
		ArrayList<String> header = readHeader();
		
		int nbBytes = -1;
		String[] headerLine;
		for(String property: header){
			headerLine = property.split(" ");
			if(headerLine[0].contains("Content-Length")){
				nbBytes = Integer.parseInt(headerLine[1]);
				break;
			}
		}
		
		
		byte[] buffer = new byte[nbBytes];
		dataInFromServer.read();
		dataInFromServer.readFully(buffer);
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(buffer));
//		int n;
//		//Write the image to a buffer.
//		while((n = dataInFromServer.read(buffer)) != -1){
//			outPutStream.write(buffer,0,n);
//		}
//		byte [] imageResponse = outPutStream.toByteArray();
//		//Write the image to a file.
//		toFile.write(image);
		
		ImageIO.write(image, imageKind, toFile);
		
		outPutStream.close();
		toFile.close();
	}
	
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
		//Write the input that was retrieved via the console to the server.
		this.outToServer.writeBytes(input);
		String line;
		//Al long as the file contains text, print it to the console.
		if((line = this.inFromServer.readLine()) != null){
			System.out.println(line);
		}	
	}
	
	protected abstract int getHttpVersion();
}