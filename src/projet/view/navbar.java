package projet.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import projet.controller.LoginController;
import projet.controller.ReservationController;
import projet.model.Utilisateur;

public class navbar {

    public static BorderPane createNavBar(Stage primaryStage, String activeTab, int utilisateurId) {
        BorderPane navbar = new BorderPane();
        navbar.getStyleClass().add("navbar");

        // 📌 Boutons de la navigation
        Button btnAccueil = createNavButton("Accueil", activeTab, primaryStage);
        Button btnCours = createNavButton("Cours", activeTab, primaryStage);
        Button btnReservations = createNavButton("Mes Réservations", activeTab, primaryStage);
        Button btnAPropos = createNavButton("À propos", activeTab, primaryStage);

        
     // 📌 Navigation entre les pages
        btnAccueil.setOnAction(e -> {
            AccueilView accueilView = new AccueilView(primaryStage);
            primaryStage.getScene().setRoot(accueilView.getView()); // 🔥 Change uniquement le contenu
        });
        
        btnCours.setOnAction(e -> {
            CoursView coursView = new CoursView(primaryStage);
            primaryStage.getScene().setRoot(coursView.getView());
        });


        btnReservations.setOnAction(e -> {
			ReservationController reservationController = new ReservationController(primaryStage, utilisateurId); // Remplace 1 par l'ID utilisateur actuel si possible
            primaryStage.getScene().setRoot(reservationController.getView());
        });
        

        // 📌 Section gauche (Onglets)
        HBox leftSection = new HBox(10, btnAccueil, btnCours, btnReservations, btnAPropos);
        leftSection.setAlignment(Pos.CENTER);
        leftSection.setPadding(new Insets(0, 20, 0, 0));
        navbar.setLeft(leftSection);

        // 📌 Section droite (Contact + Connexion)
        HBox rightSection = new HBox(10);
        rightSection.setAlignment(Pos.CENTER);
        rightSection.setPadding(new Insets(0, 20, 0, 0));
        
        Button btnContact = createNavButton("Contact", activeTab, primaryStage);
        btnContact.setStyle("-fx-font-size: 14px; -fx-background-color: #4c70ba; -fx-text-fill: white; -fx-padding: 5px 10px; -fx-border-radius: 5px;");

        
        if (Utilisateur.estConnecte()) {
            Button btnDeconnexion = new Button("Déconnexion");
            btnDeconnexion.setStyle("-fx-font-size: 14px; -fx-background-color: red; -fx-text-fill: white; -fx-padding: 5px 10px; -fx-border-radius: 5px;");
            btnDeconnexion.setOnAction(e -> {
                Utilisateur.deconnecter();
                primaryStage.setScene(new Scene(new LoginController(primaryStage).getView(), 900, 600));
            });
            rightSection.getChildren().addAll(btnContact, btnDeconnexion);
        } else {
            Button btnConnexion = new Button("👤");
            btnConnexion.setStyle("-fx-font-size: 16px; -fx-background-color: transparent; -fx-text-fill: black;");
            btnConnexion.setOnAction(e -> {
                primaryStage.setScene(new Scene(new LoginController(primaryStage).getView(), 900, 600));
            });
            rightSection.getChildren().addAll(btnContact, btnConnexion);
        }

        
        navbar.setRight(rightSection);

        return navbar;
    }

    // ✅ Méthode pour créer un bouton avec style + mise en gras de l'onglet actif
    private static Button createNavButton(String text, String activeTab, Stage primaryStage) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 14px; -fx-background-color: transparent; -fx-text-fill: black;");        
     // 🔥 Mettre en gras si c'est l'onglet actif
        if (text.equals(activeTab)) {
            button.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: black;");
        } else {
            button.setStyle("-fx-font-size: 14px; -fx-background-color: transparent; -fx-text-fill: black;");
        }
        
     // 📌 Ajout de la navigation dynamique
        button.setOnAction(e -> {
            switch (text) {
                case "Accueil":
                    primaryStage.getScene().setRoot(new AccueilView(primaryStage).getView());
                    break;
                case "Mes Réservations":
                    primaryStage.getScene().setRoot(new ReservationController(primaryStage, 1).getView()); // Remplace 1 par l'ID utilisateur actuel
                    break;
                case "Cours":
                    // Ajouter ici la redirection pour "Cours" si nécessaire
                    break;
                case "À propos":
                	primaryStage.getScene().setRoot(new aPropos(primaryStage).getView());                	
                	break;
            }
        });

        return button;
    }
}
