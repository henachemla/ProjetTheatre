package projet.controller;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import projet.view.AccueilView;
import projet.view.GestionUtilisateursView;

public class AdminInterfaceController {
    private BorderPane root;
    private VBox view;
    private Stage primaryStage;  // Ajout de l'attribut primaryStage

    public AdminInterfaceController(BorderPane root, Stage primaryStage) {
        this.root = root;
        this.primaryStage = primaryStage;  // Initialisation de primaryStage

        Label welcomeLabel = new Label("👨‍💼 Bienvenue, Matthieu !");
        Button manageUsersButton = new Button("👥 Gérer les utilisateurs");
        Button logoutButton = new Button("🚪 Déconnexion");

        manageUsersButton.setOnAction(e -> afficherGestionUtilisateurs());
        logoutButton.setOnAction(e -> {
            primaryStage.getScene().setRoot(new AccueilView(primaryStage).getView());
        });

        view = new VBox(10, welcomeLabel, manageUsersButton, logoutButton);
        view.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        root.setCenter(view);
    }

    private void afficherGestionUtilisateurs() {
        // Crée une nouvelle vue pour la gestion des utilisateurs
        GestionUtilisateursView gestionUtilisateursView = new GestionUtilisateursView(primaryStage);
        primaryStage.getScene().setRoot(gestionUtilisateursView.getView());
    }

    public VBox getView() {
        return view;
    }
}
