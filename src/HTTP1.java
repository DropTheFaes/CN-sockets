
public class HTTP1 extends HTTP{
	
	public HTTP1(String command, String url, int port){
		super(command, url, port);		
	}

	@Override
	protected void handleGetResponse() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int getHttpVersion() {
		return 1;
	}
}
