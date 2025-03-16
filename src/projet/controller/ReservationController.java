package projet.controller;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import projet.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReservationController {
    private VBox view;
    private ComboBox<String> coursComboBox;
    private Button reserverButton;
    private ListView<String> reservationsList;
    private Label messageLabel;
    private int utilisateurId;

    public ReservationController(int utilisateurId) {
        this.utilisateurId = utilisateurId;

        // Interface réservation
        Label titre = new Label("📌 Réserver un cours");
        coursComboBox = new ComboBox<>();
        chargerCoursDisponibles();

        reserverButton = new Button("Réserver");
        reserverButton.setOnAction(e -> reserverCours());

        messageLabel = new Label();

        // Interface affichage des réservations
        Label titreReservations = new Label("📅 Mes Réservations");
        reservationsList = new ListView<>();
        chargerReservations();

        // Organisation de l'affichage
        view = new VBox(10, titre, coursComboBox, reserverButton, messageLabel, titreReservations, reservationsList);
        view.setStyle("-fx-padding: 20px; -fx-alignment: center;");
    }

    // 📌 Charger les cours disponibles
    private void chargerCoursDisponibles() {
        String sql = "SELECT nom FROM cours WHERE places_disponibles > 0";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                coursComboBox.getItems().add(rs.getString("nom"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 📌 Réserver un cours
    private void reserverCours() {
        String coursChoisi = coursComboBox.getValue();

        if (coursChoisi == null) {
            messageLabel.setText("❌ Sélectionnez un cours !");
            return;
        }

        String sqlGetCours = "SELECT id FROM cours WHERE nom = ?";
        String sqlReservation = "INSERT INTO reservations (utilisateur_id, cours_id, date) VALUES (?, ?, NOW())";
        String sqlUpdatePlaces = "UPDATE cours SET places_disponibles = places_disponibles - 1 WHERE id = ?";
        String sqlUpdateForfait = "UPDATE utilisateurs SET forfait_restants = forfait_restants - 1 WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtGetCours = conn.prepareStatement(sqlGetCours);
             PreparedStatement stmtReservation = conn.prepareStatement(sqlReservation);
             PreparedStatement stmtUpdatePlaces = conn.prepareStatement(sqlUpdatePlaces);
             PreparedStatement stmtUpdateForfait = conn.prepareStatement(sqlUpdateForfait)) {

            stmtGetCours.setString(1, coursChoisi);
            ResultSet rs = stmtGetCours.executeQuery();

            if (rs.next()) {
                int coursId = rs.getInt("id");

                stmtReservation.setInt(1, utilisateurId);
                stmtReservation.setInt(2, coursId);
                stmtReservation.executeUpdate();

                stmtUpdatePlaces.setInt(1, coursId);
                stmtUpdatePlaces.executeUpdate();

                stmtUpdateForfait.setInt(1, utilisateurId);
                stmtUpdateForfait.executeUpdate();

                messageLabel.setText("✅ Réservation confirmée !");
                chargerReservations(); // Mise à jour de la liste des réservations
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("❌ Erreur lors de la réservation !");
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

    public VBox getView() {
        return view;
    }
}
