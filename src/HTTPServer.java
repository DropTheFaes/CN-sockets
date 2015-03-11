package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;




public class HTTPServer {
	
	public static void main(String[] args) throws IOException {
		
		Scanner scanner = new Scanner(System.in);
		
		while(args.length != 1) {
			System.out.println("Please enter exactly one argument, namely the port number: ");			
			args = scanner.next().split(" ");
		}
		
		scanner.close();
		
		int portNumber = Integer.parseInt(args[0]);
		ServerSocket initialSocket = new ServerSocket(portNumber); 
				
		while(true) {
			Socket socket = initialSocket.accept();
			if(socket != null) {
				Handler request = new Handler(socket); //TODO Handler klasse maken!
				Thread thread = new Thread(request);
				thread.start();
			}
		}
	}

}
