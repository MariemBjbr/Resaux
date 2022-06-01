package serveur;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
// affiche les instructions exécutées dans le coté client.
public class Affichable {
	
		public static void Affichage(String IPclient, String Portclient, String commande) {
			System.out.printf(
				"[%s:%s // %s]: %s\n",
				IPclient,
				Portclient,
				new SimpleDateFormat("yyyy-MM-dd@HH:mm:ss").format(Date.from(Instant.now())),
				commande
			);
		}
	}

	//pour l'affichage dans le console les information

