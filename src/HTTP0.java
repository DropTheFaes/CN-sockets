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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class HTTP0 extends HTTP{
	
	public HTTP0(String command, String url, int port){
		super(command, url, port);
	}

	@Override
	protected void handleResponse() throws IOException{
		switch (this.command){
			case "HEAD":
				outToServer.writeBytes("\n");
				handleHeadResponse();
				break;
			case "GET":
				handleGetResponse();
				break;
			case "PUT":
				outToServer.writeBytes("\n");
				handlePutPostResponse();
				break;
			case "POST":
				outToServer.writeBytes("\n");
				break;
		}
	}
	
	@Override
	protected void handleGetResponse() throws IOException {
		//rekening houden met If-Modified-Since:
		//	de eerste keer waarop we een pagina requesten met GET moeten we hem zoiezo binnenhalen,
		// 		maar als we de pagina al eens hebben gerequest en opgeslagen, moeten we eerst controleren of die pagina is gemodified sinds dan,
		//		als dat niet zo is, hebben we ze al, als ze wel is gemodified moeten we ze opnieuw getten
		// 		GET doet dat automatisch met de 'If-Modified-Since'-header die we sturen na de eerste 'enter' na de GET-request
		
		//url splitten op punten, zodat we 'google' of 'example' kunnen gebruiken in de filename van de file waarin we de html-file opslaan
		String siteURL = url.split("\\.")[0];
		if (siteURL.equals("www")){
			siteURL = url.split("\\.")[1];
		}
		
		File f = null;
		if (this.path == ""){
			f = new File(siteURL+".html");
		}
		else{
			f = new File(siteURL+"/"+this.path);
		}
		//als de file nog niet bestaat, slaan we de response gewoon op in de file, moet de If-Modified-Since niet gecontroleerd worden
		//--> door de if op de lijn hieronder, maar laten we voorlopig effe buiten beschouwing, doen we wel als alles werkt pakt
		//if(!f.exists()) { 
		outToServer.writeBytes("\n");
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
		this.socket.close(); //HTTP 1.0, dus socket is aan de server-side al geclosed en is dus niet meer bruikbaar, voor de images zullen we telkens een nieuwe openen
		outToServer.close();
		inFromServer.close();
		
		getImages(f);
		//}
	}
	
	protected void getImages(File f) throws IOException{
		Document doc = Jsoup.parse(f, "UTF-8");
		Elements images = doc.select("img");
		
		for(Element image: images){
			getImage(image.attr("src"));
		}
	}
	
	protected void getImage(String imageSource) throws UnknownHostException, IOException{
		String imageName = imageSource.split("/")[imageSource.split("/").length-1];
		
		this.socket = new Socket(this.url, this.port);
		this.dataInFromServer = new DataInputStream(socket.getInputStream());
		this.outToServer = new DataOutputStream(socket.getOutputStream());
		this.outPutStream = new ByteArrayOutputStream();
		FileOutputStream toFile = new FileOutputStream(imageName);
				
		String getImageSentence = "GET " + imageSource +" HTTP/1.0" + "\n";
		try {
			outToServer.writeBytes(getImageSentence + "\n");
		} catch (Exception e) {
			System.out.println("2"); //TODO check
		}
		
		byte[] buffer = new byte[2048];
		int n;
		while((n = dataInFromServer.read(buffer)) != -1){
			outPutStream.write(buffer,0,n);
		}
		byte [] imageResponse = outPutStream.toByteArray();
		toFile.write(imageResponse);
		
		socket.close();
		dataInFromServer.close();
		outToServer.close();
		outPutStream.close();
		toFile.close();
	}
	
	@Override
	protected int getHttpVersion() {
		return 0;
	}
}
