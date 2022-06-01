

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public class Commande {
	private DataInputStream in;
	private DataOutputStream out;
	
	
	public Commande(DataInputStream in, DataOutputStream out) {
		this.in = in;
		this.out = out;
	}
	
	public boolean execute(String command) {
		String[] cmd = command.split(" ");
		
		try {
			switch (cmd[0]) {
			case "ls": return ls(cmd);
			case "cd": return cd(cmd);
			case "mkdir": return mkdir(cmd);
			case "delete": return delete(cmd);
			case "upload": return upload(cmd);
			case "download": return download(cmd);
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean ls(String[] args) {
		try {
			out.writeUTF(args[0]);
			String fromServer = in.readUTF();
			System.out.println(fromServer);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean cd(String[] args) {
		try {
			out.writeUTF(args[0] + " " + args[1]);
			String fromServer = in.readUTF();
			client.path = fromServer;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean mkdir(String[] args) {
		try {
			out.writeUTF(args[0] + " " + args[1]);
			String fromServer = in.readUTF();
			System.out.println(fromServer);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean delete(String[] args) {
		try {
			out.writeUTF(args[0] + " " + args[1]);
			String fromServer = in.readUTF();
			System.out.println(fromServer);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean upload(String[] args) {
		File file = client.clientPath.resolve(args[1]).toFile();
		if (!file.exists()) {
			System.out.println("Fichier n'existe pas");
			return false;
		} else if (file.isDirectory()) {
			System.out.println("On ne peut pas televerser un dossier");
			return false;
		}
		try {
			// Send file name
			out.writeUTF(args[0] + " " + args[1]);
			out.writeUTF(args[1]);
			out.writeLong(file.length());
			// Reading File Stream
			FileInputStream inputStream = new FileInputStream(file);
			// Writing File to Socket OutputStream
			byte[] buffer = new byte[4096];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			inputStream.close();

			String fromServer = in.readUTF();
			System.out.println(fromServer);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean download(String[] args) {
		try {
			// Send file name
			out.writeUTF(args[0] + " " + args[1] + (args.length >= 2 ? " " + args[2] : ""));
			// Reading File Stream
			Path downloadPath = client.clientPath.resolve(args.length > 2 ? args[1].split("\\.")[0] + ".zip" : args[1]);
			FileOutputStream fileStream = new FileOutputStream(downloadPath.toString());
			// Writing File to Socket OutputStream
			long size = in.readLong();
			byte[] buffer = new byte[4096];
			int bytesRead = -1;
			while (size > 0 && (bytesRead = in.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
				fileStream.write(buffer, 0, bytesRead);
				size -= bytesRead;
			}
			fileStream.close();

			String fromServer = in.readUTF();
			System.out.println(fromServer);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Checks if a command is valid
	 * @param command
	 * @return true if command is valid, false otherwise
	 */
	public static boolean isCommand(String command) {
		String _cmd = command.split(" ")[0];
		
		for (String cmd : constante.ListeCommands)
			if (_cmd.equalsIgnoreCase(cmd)) return true;
		
		return false;
	}
}
