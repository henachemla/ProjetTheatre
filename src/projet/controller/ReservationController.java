package projet.controller;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import projet.database.DatabaseConnection;
import projet.model.Cours;
import projet.view.navbar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class ReservationController {
	private BorderPane root;
    private VBox view;
    private ComboBox<String> coursComboBox;
    private Button reserverButton;
    private ListView<String> reservationsList;
    private Label messageLabel;
    private int utilisateurId;
    private Stage primaryStage;

    public ReservationController(Stage primaryStage,int utilisateurId) {
        this.utilisateurId = utilisateurId;
        this.primaryStage = primaryStage;

        // Interface réservation
        

        messageLabel = new Label();

        // Interface affichage des réservations
        Label titreReservations = new Label("📅 Mes Réservations");
        titreReservations.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        reservationsList = new ListView<>();
        chargerReservations();

        // Organisation de l'affichage
        view = new VBox(10, messageLabel, titreReservations, reservationsList);
        view.setStyle("-fx-padding: 20px; -fx-alignment: center;");
        
        root = new BorderPane();
        root.setTop(navbar.createNavBar(primaryStage, "Mes Réservations", utilisateurId));
        root.setCenter(view);

    }

    
    
    public void ajouterReservations(List<Cours> coursSelectionnes) {
        for (Cours cours : coursSelectionnes) {
            reservationsList.getItems().add("📌 " + cours.nomProperty().get() + " - " + cours.prixProperty().get());
        }
    }

    // 📌 Charger les réservations de l'utilisateur
    private void chargerReservations() {
        reservationsList.getItems().clear();
        String sql = "SELECT c.nom, r.date FROM reservations r JOIN cours c ON r.cours_id = c.id WHERE r.utilisateur_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, utilisateurId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reservationsList.getItems().add("📌 " + rs.getString("nom") + " - 🗓 " + rs.getDate("date"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BorderPane getView() {
        return root;
    }
}
