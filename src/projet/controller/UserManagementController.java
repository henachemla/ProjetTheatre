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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManagementController {
    private Stage primaryStage;
    private int utilisateurId;
    private BorderPane root;
    private TableView<User> userTable;
    private ObservableList<User> userList;

    public UserManagementController(Stage primaryStage, int utilisateurId) {
        this.primaryStage = primaryStage;
        this.utilisateurId = utilisateurId;

        root = new BorderPane();
        root.setTop(navbar.createNavBar(primaryStage, "Gestion Utilisateurs", utilisateurId));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f9f4ed 0%, #e8e0d1 100%);");

        // Création de la table
        userTable = new TableView<>();
        setupTable();

        // Boutons d'action
        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> deleteSelectedUser());

        Button refreshButton = new Button("Actualiser");
        refreshButton.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white;");
        refreshButton.setOnAction(e -> refreshUsers());

        Button backButton = new Button("Retour");
        backButton.setStyle("-fx-background-color: #5d4037; -fx-text-fill: white;");
        backButton.setOnAction(e -> {
            AdminInterfaceController adminInterface = new AdminInterfaceController(root, primaryStage, utilisateurId);
            root.setCenter(adminInterface.getView());
        });

        HBox buttonBox = new HBox(10, backButton, deleteButton, refreshButton);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setAlignment(Pos.CENTER);

        VBox container = new VBox(20, userTable, buttonBox);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: rgba(255,255,255,0.7); " +
                "-fx-background-radius: 15;");

        root.setCenter(container);

        // Chargement initial des utilisateurs
        loadUsers();

        // Configuration de la scène
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
    }

    private void setupTable() {
        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<User, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, Integer> forfaitCol = new TableColumn<>("Forfait restant");
        forfaitCol.setCellValueFactory(new PropertyValueFactory<>("forfaitRestant"));

        userTable.getColumns().addAll(idCol, nomCol, emailCol, forfaitCol);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadUsers() {
        userList = FXCollections.observableArrayList();

        String sql = "SELECT id, nom, email, forfait_restants FROM utilisateurs WHERE role != 'admin'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                userList.add(new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getInt("forfait_restants")
                ));
            }

            userTable.setItems(userList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les utilisateurs");
        }
    }

    private void deleteSelectedUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner un utilisateur à supprimer");
            return;
        }

        String sql = "DELETE FROM utilisateurs WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, selectedUser.getId());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                userList.remove(selectedUser);
                showAlert("Succès", "Utilisateur supprimé avec succès");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de supprimer l'utilisateur");
        }
    }

    private void refreshUsers() {
        loadUsers();
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

    // Classe interne pour représenter un utilisateur
    public static class User {
        private final int id;
        private final String nom;
        private final String email;
        private final int forfaitRestant;

        public User(int id, String nom, String email, int forfaitRestant) {
            this.id = id;
            this.nom = nom;
            this.email = email;
            this.forfaitRestant = forfaitRestant;
        }

        // Getters
        public int getId() { return id; }
        public String getNom() { return nom; }
        public String getEmail() { return email; }
        public int getForfaitRestant() { return forfaitRestant; }
    }
}