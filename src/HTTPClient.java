import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;



public class HTTPClient {

	/**
	 * @param args[0] = command
	 * @param args[1] = URI
	 * @param args[2] = Port
	 * @param args[3] = HTTPVersion
	 */
	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		try{
			while(args.length != 4){
				System.out.println("Give 4 arguments please!");
				args = br.readLine().split(" ");
			}
			br.close();
		}catch (IOException e) {
			//TODO hoe catchen?
    		e.printStackTrace();
    		System.exit(0);
    	}
		
		String command = args[0];
		String url = args[1];
		int port = Integer.parseInt(args[2]);
		int httpVersion = Integer.parseInt(args[3].split(".")[1]);
		
		HTTP http = null;
		if(httpVersion == 0){
			http = new HTTP0(command, url, port);
		}
		else if(httpVersion == 1){
			http = new HTTP1(command, url, port);
		}
		http.sendRequest();
		
				
	}

}
