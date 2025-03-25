package projet.model;

public class Utilisateur {
	private static int utilisateurId = -1;

    public static int getUtilisateurId() {
        return utilisateurId;
    }

    public static void setUtilisateurId(int id) {
        utilisateurId = id;
    }

    public static void deconnecter() {
        utilisateurId = -1;
    }

    public static boolean estConnecte() {
        return utilisateurId != -1;
    }
}
