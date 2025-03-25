package projet.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class aPropos {

    private Stage primaryStage;

    public aPropos(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public BorderPane getView() {
        BorderPane layout = new BorderPane();
        layout.setTop(navbar.createNavBar(primaryStage, "À propos", 1)); // ID utilisateur à adapter

        VBox container = new VBox(30);
        container.setPadding(new Insets(30));
        container.setStyle("-fx-background-color: #f9f4ed;"); // Beige doux
        container.setAlignment(Pos.TOP_CENTER);

        // 🎭 Titre principal centré
        Label titre = new Label("À propos de Matthieu Poulet");
        titre.setFont(Font.font("Palatino Linotype", FontWeight.BOLD, 30));
        titre.setTextFill(Color.web("#6a0f1c")); // Bordeaux
        titre.setPadding(new Insets(0, 0, 10, 0));

        // ✏️ Section Présentation
        VBox presentationBox = createTextBlock(
                "Présentation",
                "Depuis la création de la compagnie l’Heure Avant l’Aube en 2019, Matthieu Poulet a créé 4 spectacles tout en menant un travail de territoire dans le bassin de Creil (60).\n\n" +
                        "En partenariat avec la Faïencerie de Creil, il a mené des ateliers avec demandeurs d’asile, personnes en situation de handicap, enfants et amateurs, en travaillant sur la confiance en soi et la sincérité par le jeu."
        );

        // 🎭 Troupe
        VBox troupeBox = createTextBlock(
                "La compagnie : l’Heure Avant l’Aube",
                "Fondée en 2019, cette compagnie théâtrale mêle création contemporaine et actions sociales. Ancrée dans le territoire, elle valorise la sincérité du jeu, l’inclusion et l’innovation artistique."
        );

        // 📜 Créations
        VBox creationsBox = createTextBlock(
                "Créations marquantes",
                "• 2021 : \"Prenez garde à son petit couteau\" (d’après Lorenzaccio) – avec Jean-Luc Vincent, Céline Fuhrer et Gaëtan Peau. Jouée dans plusieurs scènes nationales.\n\n" +
                        "• 2022 : \"Pisteur de Faune\" – balade contée avec réalité augmentée, en collaboration avec Caroline Panzera.\n\n" +
                        "• 2022 : \"Les Grandes Découvertes\" – duo clownesque avec Aurélia Pie.\n\n" +
                        "• 2025 : \"Jamais\" – solo de clown autour de Peter Pan, intégrant mapping vidéo et interactions numériques."
        );

        container.getChildren().addAll(titre, presentationBox, troupeBox, creationsBox);

        ScrollPane scroll = new ScrollPane(container);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent;");

        layout.setCenter(scroll);
        return layout;
    }

    // 🔧 Méthode utilitaire : crée une section avec titre et paragraphe
    private VBox createTextBlock(String titre, String contenu) {
        Label labelTitre = new Label(titre);
        labelTitre.setFont(Font.font("Palatino Linotype", FontWeight.BOLD, 20));
        labelTitre.setTextFill(Color.web("#6a0f1c"));

        Label texte = new Label(contenu);
        texte.setFont(Font.font("Georgia", FontPosture.REGULAR, 14));
        texte.setWrapText(true);
        texte.setTextFill(Color.web("#2e2e2e"));
        texte.setPadding(new Insets(5, 0, 0, 0));

        VBox box = new VBox(5, labelTitre, texte);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: #fefcf7; -fx-border-color: #e0d4c3; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        box.setMaxWidth(700);
        return box;
    }
}
