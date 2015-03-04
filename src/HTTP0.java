import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.select.Elements;


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
		if (siteURL == "www"){
			siteURL = url.split(".")[1];
		}
		
		File f = null;
		if (this.path == ""){
			f = new File(siteURL+".html");
		}
		else{
			f = new File(siteURL+"/"+this.path);
		}
		//als de file nog niet bestaat, slaan we de response gewoon op in de file, moet de If-Modified-Since niet gecontroleerd worden
		if(!f.exists()) { 
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
			getImages(f);
		}
	}
	
	protected void getImages(File f) throws IOException{
		ArrayList imageSources = new ArrayList();
		
		Document doc = Jsoup.parse(f, "UTF-8");
		Elements images = doc.select("img");
		
		for(Element image: images){
			System.out.println(image.attr("src")); //TODO is gewoon een check, dus mag nog weg
			imageSources.add(image.attr("src"));
		}
		
	}
	
	@Override
	protected int getHttpVersion() {
		return 0;
	}
}
