package projet.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/projettheatre";
    private static final String USER = "root"; // Remplace par ton utilisateur MySQL (par défaut, c'est root)
    private static final String PASSWORD = ""; // Laisse vide si tu n’as pas mis de mot de passe

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("✅ Connexion réussie à la base de données !");
        } else {
            System.out.println("❌ Erreur de connexion à MySQL.");
        }
    }
}
