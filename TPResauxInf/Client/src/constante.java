/**
 * 
 */

/**
 * @author marie
 *
 */
public class constante {

		public static class Descriptions {
			static final String CD = "cd <Nom d'un repertoir sur le serveur>\nSe deplacer vers un repertoire enfant ou parent";
			static final String LS = "Afficher tous les dossiers et fichiers dans le repertroire courant";
			static final String MKDIR = "Cree un dossier";
			static final String UPLOAD = "Televerser un fichier dans le client vers le serveur";
			static final String DOWNLOAD = "Telecharger un fichier dans le serveur vers le client, -z compresser le fichier avant de l'envoyer au client";
		}
		public static String[] ListeCommands = { "cd", "ls", "mkdir", "delete", "upload", "download" };
		
	}

}
