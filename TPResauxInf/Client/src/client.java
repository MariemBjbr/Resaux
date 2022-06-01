
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class client {
	private static String ip;
	private static String port;
	
	

	public static final Scanner scanner = new Scanner(System.in);
	public static Commande commande;
	public static String path = File.separator;
	public static Path clientPath = Paths.get(System.getProperty("user.dir"));
	
	public static void main(String[] args) {
		setIpAndPort();
		
		Socket socket = null;
		DataInputStream in = null;
		DataOutputStream out = null;
		BufferedReader br = null;		
		try {
			socket = new Socket(ip, Integer.parseInt(port));
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			br = new BufferedReader(new InputStreamReader(System.in));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		System.out.printf("Connecte au serveur a %s:%s\n", ip, port);
		String input = "";
		commande = new Commande(in, out);
		while(!input.equalsIgnoreCase("exit")) {
			try {
				System.out.print(path + "> ");
				input = br.readLine();
				if (Commande.isCommand(input)) {
					commande.execute(input);
				} else {
					System.err.println("Commande non valide");
				}
			} catch (IOException e) {
//				e.printStackTrace();
			}
		}
		
		System.out.println("Fermeture de la connection");
		try {
			socket.close();
			in.close();
			out.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		scanner.close();
	}
	
	private static void setIpAndPort() {
		while (true) {
			System.out.print("Adresse IP: ");
			ip = scanner.next();
			
		while (true) {
			System.out.print("Port: ");
			port = scanner.next();
			int portInt = Integer.parseInt(port) ;
			if (portInt <5000 || portInt>5050) 
			   System.err.println("Port non valide (doit etre entre 5002 et 5049)");
		}
	}
}
