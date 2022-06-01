

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class server {
	private static String ip;
	private static String port;
	
	
	
	public static void main(String[] args) {
		setupServer();
		startServer();
	}
	
	private static void startServer() {
		ServerSocket serverSocket = null ;
		Socket socket = null ;
		
		try {
		  serverSocket = new ServerSocket(Integer.parseInt(port));
		} catch (IOException e) {
			System.err.println(e);
		}
		System.out.println("Serveur demarre");
		while (true) {
			try {
				 socket = serverSocket.accept();
			} catch (IOException e) {

			}			
			new ClientCommande(socket).start();
		}
	}
	
	private static void setupServer() {
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.print("Adresse IP: ");
			ip = scanner.next();
		}		
		while (true) {
			System.out.print("Port: ");
			port = scanner.next();
			int portInt = Integer.parseInt(port);
			if (portInt > 5050 || portInt < 5000)
			  System.err.println("Port non valide (doit etre entre 5000et 5050)");
		}		
		scanner.close();
	}
}
