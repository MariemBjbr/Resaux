package serveur;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ClientCommande extends Thread {
	// verifie les commandeDeclient vérifie les 
	//informations requises par le serveur pour autoriser la connexion des clients au serveur. Lorsque le client 
	//est connecté (socket accepté), elle envoie des fichiers ou données selon la commande exécutée par le 
	//client.
	
		private Socket socket;
		private DataInputStream in = null;
		private DataOutputStream out = null;
		
		private String ip;
		private String port;
		private Path path;
		private Path root;
		//buffersize=4096
	
		  // call getRoot() to get root path
        //Path root = path.getRoot();
  
		public ClientCommande(Socket clientSocket) {
			this.socket = clientSocket;
			try {
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();;
			}
			this.ip = socket.getInetAddress().toString().split("/")[1];
			this.port = Integer.toString(socket.getPort());
			Affichable.Affichage(this.ip, this.port, "Connecte");
			createStorageDirectory();
			this.path = Paths.get(System.getProperty("user.dir"));
			// to returns the system property denoted by the specified key passed
			this.root = Paths.get(this.path.toString()).normalize();
			
		}
		
		public void Execute() {
			String message;
			while (true) {
				try {
					message = in.readUTF();
					String[] commande = message.split(" ");
					Affichable.Affichage(this.ip, this.port, message);

					switch(commande[0].toLowerCase()) {
					case "cd":
						out.writeUTF(Deplacer(commande));
						break;
					case "ls":
						String ls = Arrays.toString(listDir());
						out.writeUTF(". .. " + ls.substring(1, ls.length() - 1).replaceAll(",", ""));
						break;
					case "mkdir":
						out.writeUTF(mkdir(commande));
						break;
					
					case "upload":
						out.writeUTF(upload(commande));
						break;
					case "download":
						out.writeUTF(download(commande));
						break;
					case "exit":
						out.writeUTF(upload(commande));
						break;
					}
				} catch (IOException e) {
//					e.printStackTrace();
					Affichable.Affichage(this.ip, this.port, "Deconnecte");
					return;
				}
			}
		}
		
		private String Deplacer(String[] args) {
			Path newPath = Paths.get(this.path.toString(), args[1]).normalize();
			if (!newPath.startsWith(this.root)) {
				
				this.path = Paths.get(this.root.toString()).normalize();
				return File.separator;
			}
			if (newPath.toFile().isDirectory()) {
				this.path = newPath.normalize();
			}
			String output = this.path.toString().substring(this.root.toString().length());
			return output.length() > 0 ? output : File.separator;
		}
		
		private String[] listDir() {
			return this.path.toFile().list();
		}
		
		private String mkdir(String[] args) {
			Path newDirPath = this.path.resolve(args[1]).normalize();
			if (!newDirPath.toFile().mkdirs())
				return " la Dossier existe deja";
			else
				return " la Dossier est  cree";
		}
		
		private String delete(String[] args) {
			try {
				File file = new File(path.resolve(args[1]).normalize().toString());
				System.out.println(file.toPath().toString());
				if (!file.exists()) {
					return "Fichier n'existe pas";
				}
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "Fichier supprimer";
		}
		
		private String upload(String[] args) {
			try {
				// Writing Data Stream
				File file = new File(path.resolve(in.readUTF()).normalize().toString());
				FileOutputStream outputStream = new FileOutputStream(file);
				
				// Receiving Data
				long size = in.readLong();
				byte[] buffer = new byte[4096];
				int bytesRead = -1;
				while (size > 0 && (bytesRead = in.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
					outputStream.write(buffer, 0, bytesRead);
					size -= bytesRead;
				}
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				return "Erreur, fichier n'a pas ete televerse";
			}
			
			return "Fichier televerse avec succes";
		}
		
		private String download(String[] args) {
			File file = new File(path.resolve(args[1]).normalize().toString());
			if (!file.exists()) {
				return "Fichier n'existe pas";
			}
			File zipFile = null;
			if (args.length >= 2 && args[2].equalsIgnoreCase("-z")) zipFile = zipFile(file.getPath());
			try {
				FileInputStream inputStream = new FileInputStream(zipFile != null ? zipFile : file);
				out.writeLong(zipFile != null ? zipFile.length() : file.length());
				
				byte[] buffer = new byte[4096];
				int bytesRead = -1;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				return "Erreur, fichier n'a pas ete telecharge";
			}
			if (zipFile != null) zipFile.delete();
			return "Fichier telecharge avec succes";
		}

		// Zipping: https://www.codejava.net/java-se/file-io/how-to-compress-files-in-zip-format-in-java
		static private File zipFile(String filePath) {
			try {
				File file = new File(filePath);
				String zipFileName = "temp.zip";
				File zipFile = new File(zipFileName);
				FileOutputStream fos = new FileOutputStream(zipFile);
				ZipOutputStream zos = new ZipOutputStream(fos);
				zos.putNextEntry(new ZipEntry(file.getName()));
				
				byte[] bytes = Files.readAllBytes(Paths.get(file.getPath()));
				zos.write(bytes, 0, bytes.length);
				zos.closeEntry();
				zos.close();
				
				return zipFile;
			} catch (FileNotFoundException ex) {
	            System.err.format("The file %s does not exist", filePath);
	        } catch (IOException ex) {
	            System.err.println("I/O error: " + ex);
	        }
			return null;
		}
		
		private void createStorageDirectory() {
			new File(Paths.get(System.getProperty("user.dir")).toString()).mkdirs();
		}
	}

}
