import java.io.BufferedReader;
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
		if(givenURL.split("/").length < 2){
			this.path = "";
		}
		else{
			this.path = givenURL.replace(url, "");
			this.path = (String) this.path.subSequence(1, this.path.length()-1);
		}
	}
	
	/**
	 * Send the command to the chosen HTTP version with the given path.
	 */
	public void sendRequest(){
		try {
			setSocket();	
	        
	        String sentence = this.command + " /" + this.path + " HTTP/1." + this.getHttpVersion(); //sentence is wat naar de server gestuurd moet worden
        	sendSentence(sentence);
		        	        
		} catch (UnknownHostException e) {
			// TODO juist?
			System.out.println("No HTTP server found on this host.");
		} catch (IOException e) {
			// TODO juist?
			System.out.println("No server found on this portnumber.");
		}
	}
	
	public abstract void setSocket() throws UnknownHostException, IOException;
	
	public abstract void sendSentence(String sentence) throws IOException;
	
	public void handleResponse() throws IOException{
		switch (this.command){
			case "HEAD":
				outToServer.writeBytes("\n");
				handleHeadResponse();
				break;
			case "GET":
				//nog geen 2e 'newline' --> als we if-modified-since moeten opvragen moet dat er nog tussen
				handleGetResponse();
				break;
			case "PUT":
				outToServer.writeBytes("\n");
				handlePutPostResponse();
				break;
			case "POST":
				outToServer.writeBytes("\n");
				handlePutPostResponse();
				break;
		}
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
	 * 		If the writer receives invalid input, throw IOException.
	 */
	protected void handleGetResponse() throws IOException{
		//url splitten op punten, zodat we 'google' of 'example' kunnen gebruiken in de filename van de file waarin we de html-file opslaan
		String siteURL = url.split("\\.")[0];
		if (siteURL.equals("www")){
			siteURL = url.split("\\.")[1];
		}
		
		//file aanmaken waarin we de html-pagina zullen opslaan
		File f = null;
		if (this.path == ""){
			f = new File(siteURL + ".html");
		}
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
	
	protected void readHtmlPage(File f) throws IOException{
		PrintWriter writer = new PrintWriter(f);
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
	 * Retrieve the images from the given file.
	 * 
	 * @param f
	 * 		The file from which the images need to be retrieved.
	 * 
	 * @throws IOException
	 * 		When the writer receives invalid input, throw IOException.
	 */
	protected void getImages(File f) throws IOException{
		Document doc = Jsoup.parse(f, "UTF-8");
		Elements images = doc.select("img");
		
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
	 * 		When the host cannot be resolved, throw UnknownHostException.
	 * @throws IOException
	 * 		When the writer receives invalid input, throw IOException.
	 */
	protected void getImage(String imageSource) throws UnknownHostException, IOException{
		System.out.println(imageSource);//TODO check
		
		setSocket();
		
		String imageName = imageSource.split("/")[imageSource.split("/").length-1];
		ByteArrayOutputStream outPutStream = new ByteArrayOutputStream();
		FileOutputStream toFile = new FileOutputStream(imageName);
				
		String getImageSentence = "GET " + imageSource +" HTTP/1." + getHttpVersion() + "\n";
		try {
			sendSentence(getImageSentence);
		} catch (Exception e) {
			System.out.println("2"); //TODO check
		}
		
		//TODO checken wanneer header gedaan is, zodat enkel image-bytes in de image-file opgeslagen worden
		
		byte[] buffer = new byte[2048];
		int n;
		while((n = dataInFromServer.read(buffer)) != -1){
			outPutStream.write(buffer,0,n);
		}
		byte [] imageResponse = outPutStream.toByteArray();
		toFile.write(imageResponse);
		
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
		this.outToServer.writeBytes(input);
		String line;
		if((line = this.inFromServer.readLine()) != null){
			System.out.println(line);
		}	
	}
	
	protected abstract int getHttpVersion();
}