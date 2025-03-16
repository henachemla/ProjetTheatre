package projet.controller;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminInterfaceController {
    public void start(Stage adminStage) {
        adminStage.setTitle("Tableau de Bord Administrateur");

        Label welcomeLabel = new Label("👨‍💼 Bienvenue, Administrateur !");
        Button manageUsersButton = new Button("👥 Gérer les utilisateurs");
        Button logoutButton = new Button("🚪 Déconnexion");

        manageUsersButton.setOnAction(e -> afficherGestionUtilisateurs());
        logoutButton.setOnAction(e -> {
            adminStage.close();
            System.out.println("Déconnexion effectuée !");
        });

        VBox layout = new VBox(10, welcomeLabel, manageUsersButton, logoutButton);
        layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        Scene scene = new Scene(layout, 400, 300);
        adminStage.setScene(scene);
        adminStage.show();
    }

    private void afficherGestionUtilisateurs() {
        System.out.println("🔍 Affichage de la gestion des utilisateurs (à implémenter)");
    }
}
