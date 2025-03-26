package projet.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import projet.database.DatabaseConnection;
import projet.model.Utilisateur;
import projet.view.navbar;

import java.sql.*;
import java.time.format.DateTimeFormatter;

public class ReservationManagementController {
    private Stage primaryStage;
    private int utilisateurId;
    private BorderPane root;
    private TableView<Reservation> reservationTable;
    private ObservableList<Reservation> reservationList;

    public ReservationManagementController(Stage primaryStage, int utilisateurId) {
        this.primaryStage = primaryStage;
        this.utilisateurId = utilisateurId;
        this.root = new BorderPane();

        initializeUI();
        loadReservations();
    }

    private void initializeUI() {
        // Configuration de la navbar
        root.setTop(navbar.createNavBar(primaryStage, "Gestion Réservations", utilisateurId));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f9f4ed 0%, #e8e0d1 100%);");

        // Configuration de la table
        reservationTable = new TableView<>();
        setupTable();

        // Boutons d'action
        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-padding: 8px 16px;");
        deleteButton.setOnAction(e -> deleteSelectedReservation());

        Button refreshButton = new Button("Actualiser");
        refreshButton.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-padding: 8px 16px;");
        refreshButton.setOnAction(e -> loadReservations());

        Button backButton = new Button("Retour");
        backButton.setStyle("-fx-background-color: #5d4037; -fx-text-fill: white; -fx-padding: 8px 16px;");
        backButton.setOnAction(e -> {
            AdminInterfaceController adminInterface = new AdminInterfaceController(root, primaryStage, utilisateurId);
            root.setCenter(adminInterface.getView());
        });

        HBox buttonBox = new HBox(10, backButton, deleteButton, refreshButton);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setAlignment(Pos.CENTER);

        VBox container = new VBox(20, reservationTable, buttonBox);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 15;");

        root.setCenter(container);

        // Configuration de la scène
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
    }

    private void setupTable() {
        TableColumn<Reservation, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        idCol.setPrefWidth(80);

        TableColumn<Reservation, String> coursCol = new TableColumn<>("Cours");
        coursCol.setCellValueFactory(new PropertyValueFactory<>("nomCours"));
        coursCol.setPrefWidth(200);

        TableColumn<Reservation, String> userCol = new TableColumn<>("Utilisateur");
        userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        userCol.setPrefWidth(150);

        TableColumn<Reservation, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
        dateCol.setPrefWidth(150);

        reservationTable.getColumns().addAll(idCol, coursCol, userCol, dateCol);
        reservationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadReservations() {
        reservationList = FXCollections.observableArrayList();

        String sql = "SELECT r.reservation_id, r.date, u.nom as user_name, c.nom as cours_name " +
                "FROM reservations r " +
                "JOIN utilisateurs u ON r.utilisateur_id = u.id " +
                "JOIN cours c ON r.cours_id = c.cours_id " +
                "ORDER BY r.date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reservationList.add(new Reservation(
                        rs.getInt("reservation_id"),
                        rs.getString("cours_name"),
                        rs.getString("user_name"),
                        rs.getTimestamp("date").toLocalDateTime()
                ));
            }

            reservationTable.setItems(reservationList);

            if (reservationList.isEmpty()) {
                showAlert("Information", "Aucune réservation trouvée");
            }

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur de chargement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteSelectedReservation() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une réservation");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la réservation #" + selected.getReservationId());
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer cette réservation ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            String sql = "DELETE FROM reservations WHERE reservation_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, selected.getReservationId());
                int affected = stmt.executeUpdate();

                if (affected > 0) {
                    showAlert("Succès", "Réservation supprimée");
                    loadReservations();
                }
            } catch (SQLException e) {
                showAlert("Erreur", "Échec de la suppression: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BorderPane getView() {
        return root;
    }

    public static class Reservation {
        private final int reservationId;
        private final String nomCours;
        private final String userName;
        private final java.time.LocalDateTime date;
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        public Reservation(int reservationId, String nomCours, String userName, java.time.LocalDateTime date) {
            this.reservationId = reservationId;
            this.nomCours = nomCours;
            this.userName = userName;
            this.date = date;
        }

        public int getReservationId() { return reservationId; }
        public String getNomCours() { return nomCours; }
        public String getUserName() { return userName; }
        public String getFormattedDate() { return date.format(formatter); }
    }
}