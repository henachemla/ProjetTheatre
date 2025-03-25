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

    public ReservationController(Stage primaryStage,int utilisateurId) {
        this.utilisateurId = utilisateurId;
        this.primaryStage = primaryStage;

        // Interface réservation
        

        messageLabel = new Label();

        // Interface affichage des réservations
        Label titreReservations = new Label("📅 Mes Réservations");
        titreReservations.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        reservationsTable = new TableView<>();
        reservationsTable.setItems(reservationsList);

        // Colonne nom du cours
        TableColumn<Reservation, String> nomCol = new TableColumn<>("Cours");
        nomCol.setCellValueFactory(cell -> cell.getValue().nomCoursProperty());
        nomCol.setMaxWidth(1f * Integer.MAX_VALUE * 50);

        // Colonne date
        TableColumn<Reservation, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cell -> cell.getValue().dateProperty());
        dateCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        
        // Colonne bouton Supprimer
        TableColumn<Reservation, Void> actionCol = new TableColumn<>("Action");
        actionCol.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("🗑");

            {
                btn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                btn.setOnAction(e -> {
                    Reservation res = getTableView().getItems().get(getIndex());
                    supprimerReservation(res);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        reservationsTable.getColumns().addAll(nomCol, dateCol, actionCol);
        reservationsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        chargerReservations();

        // Organisation de l'affichage
        view = new VBox(10, messageLabel, titreReservations,reservationsTable);
        view.setStyle("-fx-padding: 20px; -fx-alignment: center;");
        
        root = new BorderPane();
        root.setTop(navbar.createNavBar(primaryStage, "Mes Réservations", utilisateurId));
        root.setCenter(view);

    }

    
    private void supprimerReservation(Reservation res) {
        String sql = "DELETE FROM reservations WHERE utilisateur_id = ? AND cours_id = ?";
        String updatePlaces = "UPDATE cours SET places_dispo = places_dispo + 1 WHERE cours_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Supprimer réservation
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, utilisateurId);
                stmt.setInt(2, res.getCoursId());
                stmt.executeUpdate();
            }

            // Réaugmenter places dispo
            try (PreparedStatement updateStmt = conn.prepareStatement(updatePlaces)) {
                updateStmt.setInt(1, res.getCoursId());
                updateStmt.executeUpdate();
            }

            // Mise à jour de la table
            reservationsList.remove(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ajouterReservations(List<Cours> coursSelectionnes) {
        for (Cours cours : coursSelectionnes) {
            ((ComboBox<String>) reservationsList).getItems().add("📌 " + cours.nomProperty().get() + " - " + cours.prixProperty().get());
        }
    }

    // 📌 Charger les réservations de l'utilisateur
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

        // MAJ visuelle de la TableView
        reservationsTable.setItems(reservationsList);
    }


    public BorderPane getView() {
        return root;
    }
}
