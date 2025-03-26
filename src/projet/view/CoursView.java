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
import java.sql.ResultSet;
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
        initializeUI();
        loadCoursesFromDatabase();
    }

    private void initializeUI() {
        root = new BorderPane();
        root.setTop(navbar.createNavBar(primaryStage, "Cours", Utilisateur.getUtilisateurId()));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f9f4ed 0%, #e8e0d1 100%);");

        Label titre = new Label("📚 Liste des Cours Disponibles");
        titre.setStyle("-fx-font-family: 'Playfair Display'; -fx-font-size: 24px; -fx-text-fill: #5d4037; " +
                "-fx-font-weight: bold; -fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.1), 2, 0, 1, 1);");

        tableCours = new TableView<>();
        tableCours.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 15; " +
                "-fx-border-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 0);");

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

        TableColumn<Cours, Integer> colPlaces = new TableColumn<>("Places");
        colPlaces.setCellValueFactory(data -> data.getValue().placesDispoProperty().asObject());
        colPlaces.setStyle("-fx-font-family: 'Georgia';");

        TableColumn<Cours, CheckBox> colSelection = new TableColumn<>("Réserver");
        colSelection.setCellValueFactory(data -> {
            CheckBox check = data.getValue().getCheckBox();
            check.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                validerReservationsButton.setDisable(getCoursSelectionnes().isEmpty());
            });
            return new SimpleObjectProperty<>(check);
        });

        tableCours.getColumns().addAll(colNom, colDescription, colTrancheAge, colPrix, colPlaces, colSelection);
        tableCours.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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

        validerReservationsButton = new Button("Valider mes réservations");
        validerReservationsButton.setDisable(true);
        styleButton(validerReservationsButton, "#8d6e63", "#6d4c41");
        validerReservationsButton.setOnAction(e -> handleReservation());

        erreurLabel = new Label();
        erreurLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 12px;");

        infoConnexionLabel = new Label();
        updateConnexionStatus();

        VBox content = new VBox(20, titre, tableCours, validerReservationsButton, infoConnexionLabel, erreurLabel);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: rgba(255,255,255,0.5); -fx-background-radius: 15; -fx-padding: 20;");

        VBox forfaitsBox = createForfaitsSection();
        VBox rightContent = new VBox(20, new Label("Forfaits"), forfaitsBox);
        styleRightContent(rightContent);

        Label paiementLabel = new Label("💳 Tous les paiements se font sur place.");
        stylePaiementLabel(paiementLabel);

        StackPane centerContainer = new StackPane(content);
        centerContainer.setPadding(new Insets(20));

        root.setCenter(centerContainer);
        root.setRight(rightContent);
        root.setBottom(paiementLabel);
        BorderPane.setAlignment(paiementLabel, Pos.CENTER);

        animateTable();
    }

    private void loadCoursesFromDatabase() {
        coursList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM cours WHERE places_dispo > 0 ORDER BY cours_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cours cours = new Cours(
                        rs.getInt("cours_id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getInt("places_dispo"),
                        rs.getString("tranche_age"),
                        rs.getString("prix")
                );
                coursList.add(cours);
            }

            tableCours.setItems(coursList);
        } catch (Exception e) {
            e.printStackTrace();
            showError("❌ Erreur lors du chargement des cours");
        }
    }

    private void handleReservation() {
        if (!Utilisateur.estConnecte()) {
            showConnexionError();
        } else {
            validerReservations(getCoursSelectionnes());
        }
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
            showError("❌ Veuillez sélectionner au moins un cours.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Démarrer une transaction

            try {
                for (Cours cours : coursSelectionnes) {
                    // Vérifier les places disponibles
                    String checkSql = "SELECT places_dispo FROM cours WHERE cours_id = ? FOR UPDATE";
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                        checkStmt.setInt(1, cours.getCoursId());
                        ResultSet rs = checkStmt.executeQuery();
                        if (rs.next() && rs.getInt("places_dispo") <= 0) {
                            throw new Exception("Plus de places disponibles pour: " + cours.getNom());
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
                conn.commit();

                ReservationController reservationController = new ReservationController(primaryStage, Utilisateur.getUtilisateurId());
                primaryStage.getScene().setRoot(reservationController.getView());

            } catch (Exception e) {
                conn.rollback();
                showError("❌ " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("❌ Erreur lors de la réservation. Veuillez réessayer.");
        }
    }

    private void styleButton(Button button, String baseColor, String hoverColor) {
        button.setStyle("-fx-background-color: " + baseColor + "; " +
                "-fx-text-fill: white; -fx-font-size: 16px; " +
                "-fx-font-weight: bold; -fx-padding: 12px 30px; " +
                "-fx-background-radius: 25px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 0);");

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + hoverColor + "; " +
                "-fx-text-fill: white; -fx-font-size: 16px; " +
                "-fx-font-weight: bold; -fx-padding: 12px 30px; " +
                "-fx-background-radius: 25px;"));

        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + baseColor + "; " +
                "-fx-text-fill: white; -fx-font-size: 16px; " +
                "-fx-font-weight: bold; -fx-padding: 12px 30px; " +
                "-fx-background-radius: 25px;"));
    }

    private VBox createForfaitsSection() {
        VBox forfait1 = createForfaitBox("🎭 5 cours", "Idéal pour une initiation");
        VBox forfait2 = createForfaitBox("🎭🎭 10 cours", "Parfait pour un suivi régulier");
        VBox forfait3 = createForfaitBox("🎭🎭🎭 20 cours", "Pour les passionnés");

        return new VBox(15, forfait1, forfait2, forfait3);
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

    private void styleRightContent(VBox rightContent) {
        rightContent.setAlignment(Pos.TOP_CENTER);
        rightContent.setPadding(new Insets(20));
        rightContent.setStyle("-fx-background-color: rgba(255,255,255,0.5); -fx-background-radius: 15; -fx-padding: 20;");

        Label forfaitLabel = (Label) rightContent.getChildren().get(0);
        forfaitLabel.setStyle("-fx-font-family: 'Playfair Display'; -fx-font-size: 20px; " +
                "-fx-text-fill: #5d4037; -fx-font-weight: bold;");
    }

    private void stylePaiementLabel(Label label) {
        label.setStyle("-fx-font-style: italic; -fx-text-fill: #6a0f1c; -fx-font-size: 13px;");
    }

    private void animateTable() {
        FadeTransition fadeTable = new FadeTransition(Duration.millis(500), tableCours);
        fadeTable.setFromValue(0);
        fadeTable.setToValue(1);
        fadeTable.play();
    }

    private void updateConnexionStatus() {
        infoConnexionLabel.setStyle("-fx-font-size: 12px;");
        if (Utilisateur.estConnecte()) {
            infoConnexionLabel.setText("✅ Vous êtes connecté(e)");
            infoConnexionLabel.setStyle("-fx-text-fill: #2e7d32;");
        } else {
            infoConnexionLabel.setText("ℹ️ Connectez-vous pour réserver des cours");
            infoConnexionLabel.setStyle("-fx-text-fill: #6a0f1c;");
        }
    }

    private void showConnexionError() {
        showError("❌ Veuillez vous connecter pour réserver un cours.");
        animateErrorLabel();
    }

    private void showError(String message) {
        erreurLabel.setText(message);
        animateErrorLabel();
    }

    private void animateErrorLabel() {
        FadeTransition ft = new FadeTransition(Duration.millis(300), erreurLabel);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    public BorderPane getView() {
        return root;
    }
}