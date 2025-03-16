package projet.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import projet.controller.AdminInterfaceController;
import projet.controller.LoginController;
import projet.controller.ReservationController;

public class AccueilView {

    private VBox view; // ✅ Déclaration de l'attribut
    private Stage primaryStage;

    public AccueilView(Stage primaryStage) {
    	this.primaryStage = primaryStage;
        // 📌 Barre de navigation
        HBox navbar = new HBox(20);
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setStyle("-fx-background-color: #E6D5C2; -fx-padding: 15px;");

        Button btnAccueil = new Button("Accueil");
        Button btnCours = new Button("Cours");
        Button btnReservations = new Button("Mes Réservations");
        Button btnAPropos = new Button("À propos");
        Button btnContact = new Button("Contact");

        Button btnConnexion = new Button("👤");
        btnConnexion.setStyle("-fx-font-size: 16px; -fx-background-color: transparent;");
        
        btnConnexion.setOnAction(e -> {
            LoginController loginController = new LoginController(primaryStage);
            Scene loginScene = new Scene(loginController.getView(), 900, 600);
            primaryStage.setScene(loginScene);
        });




        HBox rightSection = new HBox(btnConnexion);
        rightSection.setAlignment(Pos.CENTER_RIGHT);
        rightSection.setPrefWidth(100);
        
        navbar.getChildren().addAll(btnAccueil, btnCours, btnReservations, btnAPropos, btnContact, rightSection);

        // 📌 Texte de bienvenue
        Label welcomeText = new Label("Bienvenue sur notre plateforme de réservation de cours !\n" +
                                      "Découvrez notre univers et réservez vos cours en toute simplicité.");
        welcomeText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #3E2723;");

        // 📌 Contenu principal
        view = new VBox(20, navbar , welcomeText); // ✅ Initialisation de view ici !
        view.setAlignment(Pos.CENTER);
    }

    public VBox getView() {
        return view;
    }
}
