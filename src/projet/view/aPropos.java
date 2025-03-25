package projet.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class aPropos {

    private Stage primaryStage;

    public aPropos(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public BorderPane getView() {
        BorderPane layout = new BorderPane();
        layout.setTop(navbar.createNavBar(primaryStage, "À propos", 1)); // 1 = utilisateurId à adapter

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label titre = new Label("À propos de Matthieu Poulet");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label intro = new Label("Depuis la création de la compagnie l’Heure Avant l’Aube en 2019, Matthieu Poulet a créé 4 spectacles tout en menant un intense travail de territoire dans le bassin de Creil (60).");

        Label partenaires = new Label("En partenariat avec la Faïencerie de Creil, il a mené de nombreux ateliers avec demandeurs d’asile, personnes en situation de handicap, enfants...");

        Label troupeTitre = new Label("La compagnie : l’Heure Avant l’Aube");
        troupeTitre.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        Label troupeDesc = new Label("Ancrée dans le territoire, cette compagnie mêle création contemporaine et actions sociales.");

        Label creationsTitre = new Label("Créations marquantes :");
        creationsTitre.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label creation1 = new Label("• 2021 : \"Prenez garde à son petit couteau\" (Lorenzaccio) - tournée nationale.");
        Label creation2 = new Label("• 2022 : \"Pisteur de Faune\" - balade contée en réalité augmentée.");
        Label creation3 = new Label("• 2022 : \"Les Grandes Découvertes\" - duo de clowns.");
        Label creation4 = new Label("• 2025 : \"Jamais\" - solo clownesque, avec vidéo interactive et mapping.");

        VBox inner = new VBox(10, titre, intro, partenaires, troupeTitre, troupeDesc, creationsTitre, creation1, creation2, creation3, creation4);
        inner.setPadding(new Insets(10));
        ScrollPane scroll = new ScrollPane(inner);
        scroll.setFitToWidth(true);

        layout.setCenter(scroll);
        return layout;
    }
}
