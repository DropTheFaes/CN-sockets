import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;


public class HTTP1 extends HTTP{
	
	public HTTP1(String command, String url, int port){
		super(command, url, port);		
	}

	@Override
	protected void handleResponse() throws IOException{
		outToServer.writeBytes("Host: " + this.url);
		outToServer.writeBytes("\n");
		switch (this.command){
			case "HEAD":
				handleHeadResponse();
				break;
			case "GET":
				handleGetResponse();
				break;
			case "PUT":
				handlePutPostResponse();
				break;
			case "POST":
				handlePutPostResponse();
				break;
		}
	}
	
	@Override
	protected void handleGetResponse() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void getImages(File f) throws IOException{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void getImage(String imageSource) throws UnknownHostException, IOException{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected int getHttpVersion() {
		return 1;
	}

}
