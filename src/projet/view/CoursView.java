package projet.view;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
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
    private Label erreurLabel;
    private Label infoConnexionLabel;

    public CoursView(Stage primaryStage) {
        this.primaryStage = primaryStage;

        root = new BorderPane();
        root.setTop(navbar.createNavBar(primaryStage, "Cours", Utilisateur.getUtilisateurId()));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f9f4ed 0%, #e8e0d1 100%);");

        Label titre = new Label("📚 Liste des Cours Disponibles");
        titre.setStyle("-fx-font-family: 'Playfair Display'; -fx-font-size: 24px; -fx-text-fill: #5d4037; " +
                "-fx-font-weight: bold; -fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.1), 2, 0, 1, 1);");

        tableCours = new TableView<>();
        tableCours.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 15; " +
                "-fx-border-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 0);");

        tableCours.setRowFactory(tv -> {
            TableRow<Cours> row = new TableRow<>();
            row.hoverProperty().addListener((obs, oldVal, isHovering) -> {
                if (isHovering && !row.isEmpty()) {
                    row.setStyle("-fx-background-color: #f3e9e1;");
                } else {
                    row.setStyle("");
                }
            });
            return row;
        });

        setupTable();

        validerReservationsButton = new Button("Valider mes réservations");
        validerReservationsButton.setDisable(true);
        validerReservationsButton.setStyle("-fx-background-color: #8d6e63; -fx-text-fill: white; " +
                "-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12px 30px; " +
                "-fx-background-radius: 25px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 0);");

        validerReservationsButton.setOnMouseEntered(e ->
                validerReservationsButton.setStyle("-fx-background-color: #6d4c41; -fx-text-fill: white; " +
                        "-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12px 30px; " +
                        "-fx-background-radius: 25px;")
        );
        validerReservationsButton.setOnMouseExited(e ->
                validerReservationsButton.setStyle("-fx-background-color: #8d6e63; -fx-text-fill: white; " +
                        "-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12px 30px; " +
                        "-fx-background-radius: 25px;")
        );

        validerReservationsButton.setOnAction(e -> {
            if (!Utilisateur.estConnecte()) {
                showConnexionError();
            } else {
                validerReservations(getCoursSelectionnes());
            }
        });

        erreurLabel = new Label();
        erreurLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 12px;");

        infoConnexionLabel = new Label();
        infoConnexionLabel.setStyle("-fx-text-fill: #6a0f1c; -fx-font-size: 12px;");
        updateConnexionStatus();

        VBox content = new VBox(20, titre, tableCours, validerReservationsButton, infoConnexionLabel, erreurLabel);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: rgba(255,255,255,0.5); -fx-background-radius: 15; -fx-padding: 20;");

        Label forfait = new Label("Forfaits");
        forfait.setStyle("-fx-font-family: 'Playfair Display'; -fx-font-size: 20px; " +
                "-fx-text-fill: #5d4037; -fx-font-weight: bold;");

        VBox forfaitsBox = new VBox(15,
                createForfaitBox("🎭 5 cours", "Idéal pour une initiation"),
                createForfaitBox("🎭🎭 10 cours", "Parfait pour un suivi régulier"),
                createForfaitBox("🎭🎭🎭 20 cours", "Pour les passionnés")
        );
        forfaitsBox.setAlignment(Pos.CENTER);

        VBox rightContent = new VBox(20, forfait, forfaitsBox);
        rightContent.setAlignment(Pos.TOP_CENTER);
        rightContent.setPadding(new Insets(20));
        rightContent.setStyle("-fx-background-color: rgba(255,255,255,0.5); -fx-background-radius: 15; -fx-padding: 20;");

        Label paiementLabel = new Label("💳 Tous les paiements se font sur place.");
        paiementLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #6a0f1c; -fx-font-size: 13px;");

        StackPane centerContainer = new StackPane(content);
        centerContainer.setPadding(new Insets(20));

        root.setCenter(centerContainer);
        root.setRight(rightContent);
        root.setBottom(paiementLabel);
        BorderPane.setAlignment(paiementLabel, Pos.CENTER);

        FadeTransition fadeTable = new FadeTransition(Duration.millis(500), tableCours);
        fadeTable.setFromValue(0);
        fadeTable.setToValue(1);
        fadeTable.play();
    }

    private void updateConnexionStatus() {
        if (Utilisateur.estConnecte()) {
            infoConnexionLabel.setText("✅ Vous êtes connecté(e)");
            infoConnexionLabel.setStyle("-fx-text-fill: #2e7d32;");
        } else {
            infoConnexionLabel.setText("ℹ️ Connectez-vous pour réserver des cours");
            infoConnexionLabel.setStyle("-fx-text-fill: #6a0f1c;");
        }
    }

    private void showConnexionError() {
        erreurLabel.setText("❌ Veuillez vous connecter pour réserver un cours.");

        FadeTransition ft = new FadeTransition(Duration.millis(300), erreurLabel);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void setupTable() {
        TableColumn<Cours, String> colNom = new TableColumn<>("Cours");
        colNom.setCellValueFactory(data -> data.getValue().nomProperty());
        colNom.setStyle("-fx-font-family: 'Georgia'; -fx-text-fill: #5d4037;");

        TableColumn<Cours, String> colDescription = new TableColumn<>("Description");
        colDescription.setCellValueFactory(data -> data.getValue().descriptionProperty());
        colDescription.setStyle("-fx-font-family: 'Georgia';");

        TableColumn<Cours, String> colTrancheAge = new TableColumn<>("Tranche d'âge");
        colTrancheAge.setCellValueFactory(data -> data.getValue().trancheAgeProperty());
        colTrancheAge.setStyle("-fx-font-family: 'Georgia';");

        TableColumn<Cours, String> colPrix = new TableColumn<>("Prix");
        colPrix.setCellValueFactory(data -> data.getValue().prixProperty());
        colPrix.setStyle("-fx-font-family: 'Georgia';");

        TableColumn<Cours, CheckBox> colSelection = new TableColumn<>("");
        colSelection.setCellValueFactory(data -> {
            CheckBox check = new CheckBox();
            check.setStyle("-fx-text-fill: #5d4037;");
            check.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                validerReservationsButton.setDisable(getCoursSelectionnes().isEmpty());
            });
            data.getValue().setCheckBox(check);
            return new SimpleObjectProperty<>(check);
        });

        tableCours.getColumns().addAll(colNom, colDescription, colTrancheAge, colPrix, colSelection);
        tableCours.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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

    private VBox createForfaitBox(String titre, String description) {
        Label labelTitre = new Label(titre);
        labelTitre.setStyle("-fx-font-weight: bold; -fx-text-fill: #5d4037; -fx-font-size: 16px;");

        Label labelDesc = new Label(description);
        labelDesc.setStyle("-fx-font-family: 'Georgia'; -fx-text-fill: #6d4c41; -fx-font-size: 14px; -fx-wrap-text: true;");

        VBox box = new VBox(8, labelTitre, labelDesc);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(255,255,255,0.8); -fx-background-radius: 10; " +
                "-fx-border-radius: 10; -fx-padding: 15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        box.setMaxWidth(200);

        return box;
    }

    private List<Cours> getCoursSelectionnes() {
        List<Cours> selectionnes = new ArrayList<>();
        for (Cours cours : tableCours.getItems()) {
            if (cours.getCheckBox() != null && cours.getCheckBox().isSelected()) {
                selectionnes.add(cours);
            }
        }
        return selectionnes;
    }

    private void validerReservations(List<Cours> coursSelectionnes) {
        if (coursSelectionnes.isEmpty()) {
            erreurLabel.setText("❌ Veuillez sélectionner au moins un cours.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            for (Cours cours : coursSelectionnes) {
                String checkSql = "SELECT places_dispo FROM cours WHERE cours_id = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, cours.getCoursId());
                    var rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt("places_dispo") <= 0) {
                        erreurLabel.setText("❌ Plus de places disponibles pour: " + cours.nomProperty());
                        return;
                    }
                }

                String insertSql = "INSERT INTO reservations (utilisateur_id, cours_id, date) VALUES (?, ?, NOW())";
                try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
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

            ReservationController reservationController = new ReservationController(primaryStage, Utilisateur.getUtilisateurId());
            primaryStage.getScene().setRoot(reservationController.getView());

        } catch (Exception e) {
            e.printStackTrace();
            erreurLabel.setText("❌ Erreur lors de la réservation. Veuillez réessayer.");
        }
    }

    public BorderPane getView() {
        return root;
    }
}