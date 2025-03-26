package projet.view;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import projet.controller.*;
import projet.model.Utilisateur;

public class navbar {

    public static BorderPane createNavBar(Stage primaryStage, String activeTab, int utilisateurId) {
        BorderPane navbar = new BorderPane();
        applyNavbarStyle(navbar);

        // Section navigation
        HBox leftSection = createLeftSection(primaryStage, activeTab);
        navbar.setLeft(leftSection);

        // Section utilisateur
        HBox rightSection = createRightSection(primaryStage);
        navbar.setRight(rightSection);

        return navbar;
    }

    private static void applyNavbarStyle(BorderPane navbar) {
        navbar.setStyle("-fx-background-color: #f9f4ed; " +
                "-fx-border-color: #e0d4c3; " +
                "-fx-border-width: 0 0 1px 0; " +
                "-fx-padding: 15;");
    }

    private static HBox createLeftSection(Stage stage, String activeTab) {
        HBox leftBox = new HBox(20);
        leftBox.setAlignment(Pos.CENTER_LEFT);
        leftBox.setPadding(new Insets(0, 0, 0, 30));

        String[] tabs = {"Accueil", "Cours", "Mes Réservations", "À propos"};
        for (String tab : tabs) {
            Button btn = createNavButton(tab, tab.equals(activeTab));
            btn.setOnAction(e -> handleNavigation(stage, tab));
            leftBox.getChildren().add(btn);
        }

        return leftBox;
    }

    private static HBox createRightSection(Stage stage) {
        HBox rightBox = new HBox(15);
        rightBox.setAlignment(Pos.CENTER);
        rightBox.setPadding(new Insets(0, 30, 0, 0));

        Button contactBtn = createActionButton("Contact", "#6a0f1c");
        contactBtn.setOnAction(e -> handleNavigation(stage, "Contact"));

        if (Utilisateur.estConnecte()) {
            // Affichez le nom au lieu de l'ID
            Label userLabel = new Label(Utilisateur.getNomCourt());
            userLabel.setStyle("-fx-text-fill: #5d4037; -fx-font-weight: bold;");

            Button logoutBtn = createActionButton("Déconnexion", "#a52a2a");
            logoutBtn.setOnAction(e -> {
                Utilisateur.deconnecter();
                stage.setScene(new Scene(new LoginController(stage).getView()));
            });

            rightBox.getChildren().addAll(userLabel, contactBtn, logoutBtn);
        } else {
            Button loginBtn = createActionButton("Connexion", "#2e7d32");
            loginBtn.setOnAction(e -> stage.setScene(new Scene(new LoginController(stage).getView())));
            rightBox.getChildren().addAll(contactBtn, loginBtn);
        }

        return rightBox;
    }

    private static Button createNavButton(String text, boolean isActive) {
        Button btn = new Button(text);
        String baseStyle = "-fx-background-color: transparent; " +
                "-fx-font-family: 'Raleway'; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 5 10; " +
                "-fx-cursor: hand; ";

        if (isActive) {
            btn.setStyle(baseStyle + "-fx-text-fill: #6a0f1c; -fx-font-weight: bold;");
        } else {
            btn.setStyle(baseStyle + "-fx-text-fill: #5d4037;");
            btn.setOnMouseEntered(e -> btn.setStyle(baseStyle + "-fx-text-fill: #8d6e63;"));
            btn.setOnMouseExited(e -> btn.setStyle(baseStyle + "-fx-text-fill: #5d4037;"));
        }
        return btn;
    }

    private static Button createActionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-family: 'Raleway'; " +
                "-fx-font-size: 13px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 7 15; " +
                "-fx-background-radius: 20; " +
                "-fx-cursor: hand;");

        btn.setOnMouseEntered(e -> btn.setOpacity(0.85));
        btn.setOnMouseExited(e -> btn.setOpacity(1));
        return btn;
    }

    private static void handleNavigation(Stage stage, String destination) {
        switch (destination) {
            case "Accueil":
                stage.getScene().setRoot(new AccueilView(stage).getView());
                break;
            case "Mes Réservations":
                stage.getScene().setRoot(new ReservationController(stage, Utilisateur.getUtilisateurId()).getView());
                break;
            case "Cours":
                stage.getScene().setRoot(new CoursView(stage).getView());
                break;
            case "Contact":
                stage.getScene().setRoot(new ContactView(stage, Utilisateur.getUtilisateurId()).getView());
                break;
            case "À propos":
                stage.getScene().setRoot(new aPropos(stage).getView());
                break;
        }
    }
}