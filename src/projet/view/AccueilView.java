package projet.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class AccueilView {

    private BorderPane root;
    private Stage primaryStage;
    private int utilisateurId;

    public AccueilView(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // ✅ Layout principal avec fond beige
        root = new BorderPane();
        root.setTop(navbar.createNavBar(primaryStage, "Accueil", utilisateurId));
        root.setStyle("-fx-background-color: #f9f4ed;");
        

        // 🎭 Contenu centré
        VBox content = new VBox(30);
        content.setPadding(new Insets(60, 20, 60, 20));
        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle("-fx-background-color: rgba(255,255,255,0.6);");

        // ✨ Titre
        Label titre = new Label("Bienvenue sur notre plateforme de cours de théâtre !");
        titre.setFont(Font.font("Palatino Linotype", FontWeight.BOLD, 24));
        titre.setTextFill(Color.web("#6a0f1c"));
        titre.setWrapText(true);
        titre.setMaxWidth(600);
        titre.setAlignment(Pos.CENTER);

        // 📝 Sous-titre
        Label sousTitre = new Label("Réservez vos cours en toute simplicité.");
        sousTitre.setFont(Font.font("Georgia", FontPosture.REGULAR, 16));
        sousTitre.setTextFill(Color.web("#2e2e2e"));
        sousTitre.setWrapText(true);
        sousTitre.setMaxWidth(600);
        sousTitre.setAlignment(Pos.CENTER);

        // 🧱 Boîte stylisée
        VBox box = new VBox(10, titre, sousTitre);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));
        box.setStyle(
            "-fx-background-color: #fefcf7;" +
            "-fx-border-color: #e0d4c3;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;"
        );
        box.setMaxWidth(700);

        content.getChildren().add(box);
        root.setCenter(content);
    }

    public BorderPane getView() {
        return root;
    }
}