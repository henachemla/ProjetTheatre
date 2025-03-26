package projet.model;

public class Utilisateur {
    private static int utilisateurId = -1;
    private static String nom = "Invité";

    public static int getUtilisateurId() { return utilisateurId; }
    public static void setUtilisateurId(int id) { utilisateurId = id; }
    public static void deconnecter() {
        utilisateurId = -1;
        nom = "Invité";
    }
    public static boolean estConnecte() { return utilisateurId != -1; }

    public static void setNom(String nom) { Utilisateur.nom = nom; }
    public static String getNom() { return nom; }
    public static String getNomCourt() {
        return estConnecte() ? nom.split(" ")[0] : "Invité";
    }
}