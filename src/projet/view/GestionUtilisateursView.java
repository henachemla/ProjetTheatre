package projet.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import projet.model.Reservation;
import projet.model.Utilisateur;
import projet.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GestionUtilisateursView {

    private BorderPane root;
    private VBox view;
    private TableView<UtilisateurReservationData> utilisateursTable;
    private ObservableList<UtilisateurReservationData> utilisateursList = FXCollections.observableArrayList();
    private Stage primaryStage;

    public GestionUtilisateursView(Stage primaryStage) {
        this.primaryStage = primaryStage;

        root = new BorderPane();
        root.setTop(navbar.createNavBar(primaryStage, "Gestion des utilisateurs", Utilisateur.getUtilisateurId()));

        Label titre = new Label("📚 Gestion des utilisateurs");
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #5d4037;");

        utilisateursTable = new TableView<>();
        utilisateursTable.setItems(utilisateursList);
        utilisateursTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Création des colonnes
        TableColumn<UtilisateurReservationData, String> utilisateurCol = new TableColumn<>("Utilisateur");
        utilisateurCol.setCellValueFactory(cell -> cell.getValue().utilisateurProperty());

        TableColumn<UtilisateurReservationData, String> coursCol = new TableColumn<>("Cours");
        coursCol.setCellValueFactory(cell -> cell.getValue().coursProperty());

        TableColumn<UtilisateurReservationData, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cell -> cell.getValue().dateProperty());

        utilisateursTable.getColumns().addAll(utilisateurCol, coursCol, dateCol);

        view = new VBox(20, titre, utilisateursTable);
        root.setCenter(view);

        chargerUtilisateurs();
    }

    private void chargerUtilisateurs() {
        utilisateursList.clear();
        String sql = "SELECT u.nom, c.nom AS cours_nom, r.date FROM reservations r " +
                "JOIN utilisateurs u ON r.utilisateur_id = u.id " +
                "JOIN cours c ON r.cours_id = c.cours_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String utilisateurNom = rs.getString("nom");
                String coursNom = rs.getString("cours_nom");
                String date = rs.getString("date");

                utilisateursList.add(new UtilisateurReservationData(utilisateurNom, coursNom, date));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        utilisateursTable.setItems(utilisateursList);
    }

    public BorderPane getView() {
        return root;
    }
}
