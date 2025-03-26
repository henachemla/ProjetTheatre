package projet.view;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import projet.controller.ReservationController;
import projet.database.DatabaseConnection;
import projet.model.Cours;
import projet.model.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

        // 🧱 Layout principal
        root = new BorderPane();
        root.setTop(navbar.createNavBar(primaryStage, "Cours", utilisateurId));
        root.setStyle("-fx-background-color: #f9f4ed;");

        // 📚 Titre principal
        Label titre = new Label("📚 Liste des Cours Disponibles");
        titre.setStyle("-fx-font-family: 'Palatino Linotype'; -fx-font-size: 20px; -fx-text-fill: #6a0f1c; -fx-font-weight: bold;");

        // 🗂 Table des cours
        tableCours = new TableView<>();
        setupTable();

        // ✅ Bouton validation
        validerReservationsButton = new Button("Valider mes réservations");
        validerReservationsButton.setDisable(true);
        validerReservationsButton.setStyle(
                "-fx-background-color: #6a0f1c;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 8px 16px;" +
                "-fx-background-radius: 8px;"
        );
        validerReservationsButton.setOnAction(e -> validerReservations(getCoursSelectionnes()));

        // 🎯 Activation / désactivation du bouton
        tableCours.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableCours.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            validerReservationsButton.setDisable(tableCours.getSelectionModel().getSelectedItems().isEmpty());
        });

        // ❌ Label erreur
        erreurLabel = new Label();
        erreurLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        VBox content = new VBox(10, titre, tableCours, validerReservationsButton, erreurLabel);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));

        root.setCenter(content);

        // 📦 Forfaits à droite
        Label forfait = new Label("Forfaits");
        forfait.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #6a0f1c;");

        VBox forfaitsBox = new VBox(10);
        forfaitsBox.setAlignment(Pos.CENTER_LEFT);
        forfaitsBox.setPadding(new Insets(10));
        forfaitsBox.getChildren().addAll(
                createForfaitBox("5 cours", "Idéal pour une initiation"),
                createForfaitBox("10 cours", "Parfait pour un suivi régulier"),
                createForfaitBox("20 cours", "Pour les passionnés de théâtre")
        );

        VBox rightContent = new VBox(15, forfait, forfaitsBox);
        rightContent.setAlignment(Pos.TOP_LEFT);
        rightContent.setPadding(new Insets(20));

        root.setRight(rightContent);

        // 💳 Paiement
        Label paiementLabel = new Label("💳 Tous les paiements se font sur place.");
        paiementLabel.setStyle("-fx-font-size: 13px; -fx-font-style: italic; -fx-text-fill: #6a0f1c;");
        paiementLabel.setAlignment(Pos.CENTER);
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
        tableCours.setStyle("-fx-background-color: #fefcf7; -fx-font-family: 'Georgia';");

        coursList = FXCollections.observableArrayList(
                new Cours(1, "Théâtre Classique", "Découverte des classiques - 1h - Lundi", 10, "15-25 ans", "30€"),
                new Cours(2, "Improvisation", "Ateliers de scène libre - 1h30 - Mardi", 10, "16-30 ans", "30€"),
                new Cours(3, "Expression Scénique", "Travail sur l'émotion - 2h - Mercredi", 10, "18+ ans", "40€"),
                new Cours(4, "Mise en Scène", "Création d'une pièce - 2h30 - Jeudi", 10, "20+ ans", "50€"),
                new Cours(5, "Jeu corporel", "Expression par le corps - 1h - Vendredi", 10, "15-20 ans", "20€"),
                new Cours(6, "Lecture à voix haute", "Interprétation de textes - 1h30 - Samedi", 10, "16-25 ans", "30€"),
                new Cours(7, "Impro musicale", "Improvisation avec instruments - 2h - Dimanche", 10, "16-30 ans", "40€")
        );

        tableCours.setItems(coursList);
    }

    private List<Cours> getCoursSelectionnes() {
        List<Cours> selectionnes = new ArrayList<>();
        for (Cours cours : tableCours.getItems()) {
            if (cours.getCheckBox().isSelected()) {
                selectionnes.add(cours);
            }
        }
        return selectionnes;
    }

    private VBox createForfaitBox(String titre, String description) {
        Label labelTitre = new Label(titre);
        labelTitre.setStyle("-fx-font-weight: bold; -fx-text-fill: #6a0f1c;");

        Label labelDesc = new Label(description);
        labelDesc.setStyle("-fx-font-size: 12px; -fx-font-family: 'Georgia';");

        VBox box = new VBox(5, labelTitre, labelDesc);
        box.setStyle(
                "-fx-border-color: #6a0f1c;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 12px;" +
                "-fx-background-color: #fefcf7;"
        );
        return box;
    }

    private void validerReservations(List<Cours> coursSelectionnes) {
        if (!Utilisateur.estConnecte()) {
            erreurLabel.setText("❌ Veuillez vous connecter pour réserver un cours.");
            return;
        }

        if (!coursSelectionnes.isEmpty()) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                for (Cours cours : coursSelectionnes) {
                    String sql = "INSERT INTO reservations (utilisateur_id, cours_id, date) VALUES (?, ?, NOW())";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, Utilisateur.getUtilisateurId());
                        stmt.setInt(2, cours.getCoursId());
                        stmt.executeUpdate();
                    }

                    String updateSql = "UPDATE cours SET places_dispo = places_dispo - 1 WHERE cours_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, cours.getCoursId());
                        updateStmt.executeUpdate();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ReservationController reservationController = new ReservationController(primaryStage, Utilisateur.getUtilisateurId());
            primaryStage.getScene().setRoot(reservationController.getView());
        } else {
            System.out.println("❌ Aucun cours sélectionné !");
        }
    }

    public BorderPane getView() {
        return root;
    }
}
