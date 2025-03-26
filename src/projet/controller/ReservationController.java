package projet.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import projet.database.DatabaseConnection;
import projet.model.Cours;
import projet.model.Reservation;
import projet.view.navbar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class ReservationController {
    private BorderPane root;
    private VBox view;
    private TableView<Reservation> reservationsTable;
    private ObservableList<Reservation> reservationsList = FXCollections.observableArrayList();
    private Label messageLabel;
    private int utilisateurId;
    private Stage primaryStage;

    public ReservationController(Stage primaryStage, int utilisateurId) {
        this.utilisateurId = utilisateurId;
        this.primaryStage = primaryStage;

        // 📌 Titre
        Label titreReservations = new Label("📅 Mes Réservations");
        titreReservations.setStyle("-fx-font-family: 'Palatino Linotype'; -fx-font-size: 20px; -fx-text-fill: #6a0f1c; -fx-font-weight: bold;");

        // ❗ Message d'information
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // 📋 TableView
        reservationsTable = new TableView<>();
        reservationsTable.setItems(reservationsList);
        reservationsTable.setStyle("-fx-background-color: #fefcf7; -fx-font-family: 'Georgia';");
        reservationsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 🧱 Colonnes
        TableColumn<Reservation, String> nomCol = new TableColumn<>("Cours");
        nomCol.setCellValueFactory(cell -> cell.getValue().nomCoursProperty());
        nomCol.setMaxWidth(1f * Integer.MAX_VALUE * 50);

        TableColumn<Reservation, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cell -> cell.getValue().dateProperty());
        dateCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);

        TableColumn<Reservation, Void> actionCol = new TableColumn<>("Action");
        actionCol.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("🗑");

            {
                btn.setStyle(
                    "-fx-background-color: #6a0f1c; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 6px; " +
                    "-fx-padding: 4px 8px;"
                );
                btn.setOnAction(e -> {
                    Reservation res = getTableView().getItems().get(getIndex());
                    supprimerReservation(res);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        reservationsTable.getColumns().addAll(nomCol, dateCol, actionCol);

        // 📦 Vue centrale
        view = new VBox(10, messageLabel, titreReservations, reservationsTable);
        view.setStyle("-fx-padding: 20px; -fx-alignment: center; -fx-background-color: #f9f4ed;");

        // 📌 Structure générale
        root = new BorderPane();
        root.setTop(navbar.createNavBar(primaryStage, "Mes Réservations", utilisateurId));
        root.setCenter(view);
        root.setStyle("-fx-background-color: #f9f4ed;");

        // 📥 Chargement des données
        chargerReservations();
    }

    // ❌ Supprimer une réservation
    private void supprimerReservation(Reservation res) {
        String sql = "DELETE FROM reservations WHERE utilisateur_id = ? AND cours_id = ?";
        String updatePlaces = "UPDATE cours SET places_dispo = places_dispo + 1 WHERE cours_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, utilisateurId);
                stmt.setInt(2, res.getCoursId());
                stmt.executeUpdate();
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(updatePlaces)) {
                updateStmt.setInt(1, res.getCoursId());
                updateStmt.executeUpdate();
            }

            reservationsList.remove(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 📥 Charger les réservations
    private void chargerReservations() {
        reservationsList.clear();
        String sql = "SELECT c.nom, r.date, c.cours_id FROM reservations r JOIN cours c ON r.cours_id = c.cours_id WHERE r.utilisateur_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, utilisateurId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String nomCours = rs.getString("nom");
                String date = rs.getString("date");
                int coursId = rs.getInt("cours_id");

                reservationsList.add(new Reservation(nomCours, date, coursId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        reservationsTable.setItems(reservationsList);
    }

    public BorderPane getView() {
        return root;
    }
}
