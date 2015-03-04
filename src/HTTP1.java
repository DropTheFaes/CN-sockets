import java.io.File;
import java.io.IOException;


public class HTTP1 extends HTTP{
	
	public HTTP1(String command, String url, int port){
		super(command, url, port);		
	}

	@Override
	protected void handleResponse() throws IOException{
		outToServer.writeBytes("Host: " + this.url);
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
	protected void handleGetResponse() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void getImages(File f) throws IOException{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected int getHttpVersion() {
		return 1;
	}

}
