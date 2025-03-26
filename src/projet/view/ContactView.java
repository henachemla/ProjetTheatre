package projet.view;

import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ContactView {
    private BorderPane view;

    public ContactView(Stage primaryStage, int utilisateurId) {
        view = new BorderPane();
        view.setBackground(new Background(new BackgroundFill(Color.web("#f5f5dc"), null, null))); // fond beige
        // 💡 Navbar en haut
        BorderPane navBar = navbar.createNavBar(primaryStage, "Contact", utilisateurId);
        view.setTop(navBar);

        // 💌 Contenu Contact au centre
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new javafx.geometry.Insets(40));
        content.setStyle(
            "-fx-background-color: rgba(255,255,255,0.5);" +
            "-fx-border-color: #800020;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 15;" +
            "-fx-background-radius: 15;"
        );

        Image profilePic;
        try {
            // Chemin correct : PAS de "/resources", car "resources" est déjà dans le classpath
            profilePic = new Image(getClass().getResource("images/matthieu poulet.jpg").toExternalForm());
            System.out.println("✅ Image chargée depuis : " + getClass().getResource("images/matthieu poulet.jpg"));
        } catch (Exception e) {
            System.out.println("❌ Erreur de chargement image : " + e.getMessage());
            profilePic = new Image("https://via.placeholder.com/100"); // fallback au cas où
        }

        ImageView profileImage = new ImageView(profilePic);
        profileImage.setFitWidth(200);
        profileImage.setFitHeight(200);
        profileImage.setClip(new javafx.scene.shape.Circle(100, 100, 100)); // Affiche l'image en cercle

        Label title = new Label("Contact");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #800020;");

        Label email = new Label("Email : Mathieu.Poulet@gmail.com");
        email.setStyle("-fx-font-size: 16px; -fx-text-fill: #800020;");

        Label phone = new Label("Téléphone : 06.06.43.99.71");
        phone.setStyle("-fx-font-size: 16px; -fx-text-fill: #800020;");

        Hyperlink facebookLink = new Hyperlink("Page Facebook : Compagnie L’Heure Avant l’Aube");
        facebookLink.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-underline: true; -fx-text-fill: #800020;");
        facebookLink.setOnAction(e -> {
            try {
                java.awt.Desktop.getDesktop().browse(
                    new java.net.URI("https://www.facebook.com/compagnielheureavantlaube")
                );
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        content.getChildren().addAll(profileImage, title, email, phone, facebookLink);
        view.setCenter(content);
    }

    public BorderPane getView() {
    	return view;
    }
}