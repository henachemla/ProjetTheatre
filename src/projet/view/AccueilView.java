package projet.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class AccueilView {
    private BorderPane root;
    private Stage primaryStage;

    public AccueilView(Stage primaryStage) {
        this.primaryStage = primaryStage;

        root = new BorderPane();
        root.setTop(navbar.createNavBar(primaryStage, "Accueil", -1));


        VBox mainContent = new VBox(40);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(50, 20, 80, 20));


        VBox textBox = createTextBox();

        Button ctaButton = createCTAButton();

        HBox features = createFeaturesSection();

        mainContent.getChildren().addAll( textBox, ctaButton, features);
        
     // 📸 Appliquer l'image de fond au BorderPane
        BackgroundImage backgroundImage = new BackgroundImage(
            new Image(getClass().getResource("/images/jamais.jpg").toExternalForm(), 900, 600, false, true),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(100, 100, true, true, false, true)
        );
        root.setBackground(new Background(backgroundImage));

        StackPane contentContainer = new StackPane(mainContent);
        contentContainer.setPadding(new Insets(20));
        contentContainer.setStyle("-fx-background-color: rgba(255,255,255,0.7); " +
                "-fx-background-radius: 15; " +
                "-fx-border-radius: 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 0);");

        root.setCenter(contentContainer);
    }

    private ImageView createHeaderImage() {
        try {
            Image image = new Image(getClass().getResourceAsStream("/images/theatre-header.jpg"));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(600);
            imageView.setPreserveRatio(true);
            imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 5, 0);");
            return imageView;
        } catch (Exception e) {
            ImageView placeholder = new ImageView();
            placeholder.setStyle("-fx-background-color: #d7ccc8; -fx-min-width: 600px; -fx-min-height: 200px;");
            return placeholder;
        }
    }

    private VBox createTextBox() {
        Label mainTitle = new Label("L'Art du Théâtre à Votre Portée");
        mainTitle.setFont(Font.font("Playfair Display", FontWeight.BOLD, 32));
        mainTitle.setTextFill(Color.web("#5d4037"));
        mainTitle.setStyle("-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.1), 2, 0, 1, 1);");

        Label subtitle = new Label("Découvrez nos cours adaptés à tous les niveaux");
        subtitle.setFont(Font.font("Raleway", 18));
        subtitle.setTextFill(Color.web("#6d4c41"));

        VBox textBox = new VBox(10, mainTitle, subtitle);
        textBox.setAlignment(Pos.CENTER);
        return textBox;
    }

    private Button createCTAButton() {
        Button button = new Button("Découvrir nos cours");
        button.setStyle("-fx-background-color: #8d6e63; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 12px 30px; " +
                "-fx-background-radius: 25px;");

        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() + "-fx-background-color: #6d4c41;"));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle() + "-fx-background-color: #8d6e63;"));

        button.setOnAction(e -> {
            CoursView coursView = new CoursView(primaryStage);
            primaryStage.getScene().setRoot(coursView.getView());
        });

        return button;
    }

    private HBox createFeaturesSection() {
        HBox features = new HBox(30);
        features.setAlignment(Pos.CENTER);

        VBox feature1 = createFeatureItem("🎭", "Cours Variés", "Classique, improvisation, mise en scène...");

        VBox feature2 = createFeatureItem("👥", "Petits Groupes", "Maximum 10 participants par cours");

        VBox feature3 = createFeatureItem("🏛️", "Lieu Unique", "Studio en plein cœur de Paris");

        features.getChildren().addAll(feature1, feature2, feature3);
        return features;
    }

    private VBox createFeatureItem(String emoji, String title, String description) {
        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle("-fx-font-size: 28px;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #5d4037;");

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6d4c41; -fx-wrap-text: true;");
        descLabel.setMaxWidth(200);

        VBox item = new VBox(8, emojiLabel, titleLabel, descLabel);
        item.setAlignment(Pos.CENTER);
        item.setStyle("-fx-padding: 20px; -fx-background-color: rgba(255,255,255,0.8); " +
                "-fx-background-radius: 10; -fx-border-radius: 10;");

        return item;
    }

    public BorderPane getView() {
        return root;
    }
}