package projet.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import projet.database.DatabaseConnection;
import projet.model.Reservation;
import projet.view.navbar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

        root = new BorderPane();
        root.setTop(navbar.createNavBar(primaryStage, "Mes Réservations", utilisateurId));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f9f4ed 0%, #e8e0d1 100%);");

        Label titreReservations = new Label("📅 Mes Réservations");
        titreReservations.setStyle("-fx-font-family: 'Playfair Display'; -fx-font-size: 24px; -fx-text-fill: #5d4037; " +
                "-fx-font-weight: bold; -fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.1), 2, 0, 1, 1);");

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 12px;");

        reservationsTable = new TableView<>();
        reservationsTable.setItems(reservationsList);
        reservationsTable.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 15; " +
                "-fx-border-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 0);");
        reservationsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        reservationsTable.setRowFactory(tv -> {
            TableRow<Reservation> row = new TableRow<>();
            row.hoverProperty().addListener((obs, oldVal, isHovering) -> {
                if (isHovering && !row.isEmpty()) {
                    row.setStyle("-fx-background-color: #f3e9e1;");
                } else {
                    row.setStyle("");
                }
            });
            return row;
        });

        TableColumn<Reservation, String> nomCol = new TableColumn<>("Cours");
        nomCol.setCellValueFactory(cell -> cell.getValue().nomCoursProperty());
        nomCol.setStyle("-fx-font-family: 'Georgia'; -fx-text-fill: #5d4037;");

        TableColumn<Reservation, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cell -> cell.getValue().dateProperty());
        dateCol.setStyle("-fx-font-family: 'Georgia';");

        TableColumn<Reservation, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("🗑");

            {
                btn.setStyle(
                        "-fx-background-color: #6a0f1c; " +
                                "-fx-text-fill: white; " +
                                "-fx-background-radius: 15px; " +
                                "-fx-padding: 5px 10px; " +
                                "-fx-cursor: hand;"
                );

                btn.setOnMouseEntered(e -> btn.setStyle(
                        "-fx-background-color: #8d6e63; " +
                                "-fx-text-fill: white; " +
                                "-fx-background-radius: 15px; " +
                                "-fx-padding: 5px 10px;"
                ));

                btn.setOnMouseExited(e -> btn.setStyle(
                        "-fx-background-color: #6a0f1c; " +
                                "-fx-text-fill: white; " +
                                "-fx-background-radius: 15px; " +
                                "-fx-padding: 5px 10px;"
                ));

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

        view = new VBox(20, messageLabel, titreReservations, reservationsTable);
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(30));
        view.setStyle("-fx-background-color: rgba(255,255,255,0.5); -fx-background-radius: 15; -fx-padding: 20;");

        StackPane centerContainer = new StackPane(view);
        centerContainer.setPadding(new Insets(20));

        root.setCenter(centerContainer);

        chargerReservations();
    }

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

    // 📥 Charger les réservations (identique à l'original)
    private void chargerReservations() {
        reservationsList.clear();
        String sql = "SELECT u.id, u.nom, u.email, c.nom AS cours_nom, r.date\n" +
                "FROM reservations r\n" +
                "JOIN utilisateurs u ON r.utilisateur_id = u.id\n" +
                "JOIN cours c ON r.cours_id = c.cours_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            //stmt.setInt(1, utilisateurId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int utilisateurId = rs.getInt("u.id");
                String utilisateurNom = rs.getString("u.nom");
                String utilisateurEmail = rs.getString("u.email");
                String nomCours = rs.getString("cours_nom");
                String date = rs.getString("r.date");

                reservationsList.add(new Reservation(nomCours, date, utilisateurId));
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