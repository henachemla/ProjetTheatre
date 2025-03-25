package projet.view;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import projet.view.navbar;
import projet.controller.ReservationController;
import projet.model.Cours;
import projet.model.Utilisateur;

import java.util.ArrayList;
import java.util.List;

public class CoursView {
    private BorderPane root;
    private Stage primaryStage;
    private TableView<Cours> tableCours;
    private ObservableList<Cours> coursList;
    private Button validerReservationsButton;
    private int utilisateurId;
    private Label erreurLabel;


    public CoursView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // 📌 Barre de navigation
        root = new BorderPane();
        root.setTop(navbar.createNavBar(primaryStage, "Cours", utilisateurId));

        // 📌 Contenu principal
        Label titre = new Label("📚 Liste des Cours Disponibles");
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // 📌 Table des cours
        tableCours = new TableView<>();
        setupTable();

        // 📌 Bouton de validation
        validerReservationsButton = new Button("Valider mes réservations");
        validerReservationsButton.setDisable(true);
        validerReservationsButton.setOnAction(e -> {
        	if (!Utilisateur.estConnecte()) {
        	    erreurLabel.setText("❌ Vous devez être connecté pour réserver un cours !");
        	    return;
        	}
            List<Cours> selectionnes = getCoursSelectionnes(); // ✅ Récupère les cours sélectionnés
            System.out.println("🔥 Cours sélectionnés : " + selectionnes.size()); // ✅ Debug
            
            if (!selectionnes.isEmpty()) {
                validerReservations(selectionnes);
            } else {
                System.out.println("❌ Aucun cours sélectionné !");
            }
            
        });

        // 📌 Sélectionner/désélectionner des cours
        tableCours.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableCours.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            validerReservationsButton.setDisable(tableCours.getSelectionModel().getSelectedItems().isEmpty());
        });
        
        erreurLabel = new Label();
        erreurLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");


        VBox content = new VBox(10, titre, tableCours, validerReservationsButton,erreurLabel);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));

        root.setCenter(content);

        // 📌 Section Forfaits
        Label forfait = new Label("Forfaits");
        forfait.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        VBox forfaitsBox = new VBox(10);
        forfaitsBox.setAlignment(Pos.CENTER_LEFT);
        forfaitsBox.setPadding(new Insets(10));
        forfaitsBox.getChildren().addAll(
            createForfaitBox("5 cours", "Idéal pour une initiation"),
            createForfaitBox("10 cours", "Parfait pour un suivi régulier"),
            createForfaitBox("20 cours", "Pour les passionnés de théâtre")
        );

        // 📌 Section paiement
        Label paiementLabel = new Label("💳 Tous les paiements se font sur place.");
        paiementLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
        paiementLabel.setAlignment(Pos.CENTER);

        VBox rightContent = new VBox(15, forfait, forfaitsBox);
        rightContent.setAlignment(Pos.TOP_LEFT);
        rightContent.setPadding(new Insets(20));

        root.setRight(rightContent);
        root.setBottom(paiementLabel);
    }

    private void setupTable() {
        TableColumn<Cours, String> colNom = new TableColumn<>("Cours");
        colNom.setCellValueFactory(data -> data.getValue().nomProperty());

        TableColumn<Cours, String> colDescription = new TableColumn<>("Description");
        colDescription.setCellValueFactory(data -> data.getValue().descriptionProperty());

        TableColumn<Cours, String> colTrancheAge = new TableColumn<>("Tranche d'âge");
        colTrancheAge.setCellValueFactory(data -> data.getValue().trancheAgeProperty());

        TableColumn<Cours, String> colPrix = new TableColumn<>("Prix");
        colPrix.setCellValueFactory(data -> data.getValue().prixProperty());

        // ✅ Ajout de la colonne des CheckBox
        TableColumn<Cours, CheckBox> colSelection = new TableColumn<>("");
        colSelection.setCellValueFactory(data -> {
            CheckBox check = new CheckBox();
            check.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                validerReservationsButton.setDisable(getCoursSelectionnes().isEmpty());
            });
            data.getValue().setCheckBox(check); 
            return new SimpleObjectProperty<>(check);
        });

        tableCours.getColumns().addAll(colNom, colDescription, colTrancheAge, colPrix, colSelection);
        tableCours.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 📌 Ajout des cours (exemple)
        coursList = FXCollections.observableArrayList(
                new Cours("Théâtre Classique", "Découverte des classiques", "15-25 ans", "30€"),
                new Cours("Improvisation", "Ateliers de scène libre", "16-30 ans", "25€"),
                new Cours("Expression Scénique", "Travail sur l'émotion", "18+ ans", "35€"),
                new Cours("Mise en Scène", "Création d'une pièce", "20+ ans", "40€")
        );

        tableCours.setItems(coursList);
    }

    
    private List<Cours> getCoursSelectionnes() {
        List<Cours> selectionnes = new ArrayList<>();
        for (Cours cours : tableCours.getItems()) {
            if (cours.getCheckBox().isSelected()) { // ✅ Vérifie si la CheckBox est cochée
                selectionnes.add(cours);
            }
        }
        return selectionnes;
    }



    private VBox createForfaitBox(String titre, String description) {
        Label labelTitre = new Label(titre);
        labelTitre.setStyle("-fx-font-weight: bold;");

        Label labelDesc = new Label(description);
        labelDesc.setStyle("-fx-font-size: 12px;");

        VBox box = new VBox(5, labelTitre, labelDesc);
        box.setStyle("-fx-border-color: black; -fx-padding: 10px; -fx-border-radius: 5px; -fx-background-color: #f0f0f0;");
        return box;
    }

    private void validerReservations(List<Cours> coursSelectionnes) {
        if (!coursSelectionnes.isEmpty()) {
            System.out.println("✅ Cours réservés : " + coursSelectionnes);

            // 🔥 Redirection vers la page "Mes Réservations" avec les cours sélectionnés
            ReservationController reservationController = new ReservationController(primaryStage,  Utilisateur.getUtilisateurId());
            reservationController.ajouterReservations(coursSelectionnes);
            primaryStage.getScene().setRoot(reservationController.getView());
        } else {
            System.out.println("❌ Aucun cours sélectionné !");
        }
    }



    public BorderPane getView() {
        return root;
    }
}
