package projet.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import projet.controller.AdminInterfaceController;
import projet.controller.LoginController;
import projet.controller.ReservationController;

public class AccueilView {

    private BorderPane root; // ✅ Déclaration de l'attribut
    private Stage primaryStage;
    private int utilisateurId;

    public AccueilView(Stage primaryStage) {
    	this.primaryStage = primaryStage;
		
        // 📌 Texte de bienvenue
        Label welcomeText = new Label("Bienvenue sur notre plateforme de réservation de cours !\n" +
                                      "Découvrez notre univers et réservez vos cours en toute simplicité.");
        welcomeText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");

        // 📌 Contenu principal
        root = new BorderPane();
        root.setTop(navbar.createNavBar(primaryStage, "Accueil", utilisateurId));

        root.setCenter(welcomeText);
    }

    public BorderPane getView() {
        return root;
    }
}
