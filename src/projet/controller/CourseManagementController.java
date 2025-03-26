package projet.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import projet.database.DatabaseConnection;
import projet.model.Cours;
import projet.view.navbar;

import java.sql.*;
import java.util.Optional;

public class CourseManagementController {
    private Stage primaryStage;
    private int utilisateurId;
    private BorderPane root;
    private TableView<Cours> courseTable;
    private ObservableList<Cours> courseList;

    public CourseManagementController(Stage primaryStage, int utilisateurId) {
        this.primaryStage = primaryStage;
        this.utilisateurId = utilisateurId;
        this.root = new BorderPane();

        initializeUI();
        loadCourses();
    }

    private void initializeUI() {
        // Navbar
        root.setTop(navbar.createNavBar(primaryStage, "Gestion Cours", utilisateurId));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f9f4ed 0%, #e8e0d1 100%);");

        // Table configuration
        courseTable = new TableView<>();
        setupTable();

        // Action buttons
        Button addButton = new Button("Ajouter");
        addButton.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white;");
        addButton.setOnAction(e -> showAddCourseDialog());

        Button refreshButton = new Button("Actualiser");
        refreshButton.setStyle("-fx-background-color: #1565c0; -fx-text-fill: white;");
        refreshButton.setOnAction(e -> loadCourses());

        Button backButton = new Button("Retour");
        backButton.setStyle("-fx-background-color: #5d4037; -fx-text-fill: white;");
        backButton.setOnAction(e -> returnToAdminInterface());

        HBox buttonBox = new HBox(10, addButton, refreshButton, backButton);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setAlignment(Pos.CENTER);

        VBox container = new VBox(20, courseTable, buttonBox);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 15;");

        root.setCenter(container);

        // Scene setup
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
    }

    private void setupTable() {
        // Columns
        TableColumn<Cours, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("coursId"));

        TableColumn<Cours, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Cours, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Cours, String> ageCol = new TableColumn<>("Tranche d'âge");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("trancheAge"));

        TableColumn<Cours, String> prixCol = new TableColumn<>("Prix");
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prix"));

        TableColumn<Cours, Integer> placesCol = new TableColumn<>("Places");
        placesCol.setCellValueFactory(new PropertyValueFactory<>("placesdispo"));

        // Action column
        TableColumn<Cours, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(createActionCellFactory());

        courseTable.getColumns().addAll(idCol, nomCol, descCol, ageCol, prixCol, placesCol, actionsCol);
        courseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private Callback<TableColumn<Cours, Void>, TableCell<Cours, Void>> createActionCellFactory() {
        return param -> new TableCell<>() {
            private final Button editButton = new Button("✏️");
            private final Button deleteButton = new Button("🗑️");
            private final HBox pane = new HBox(5, editButton, deleteButton);

            {
                editButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #d32f2f;");

                editButton.setOnAction(event -> {
                    Cours cours = getTableView().getItems().get(getIndex());
                    showEditCourseDialog(cours);
                });

                deleteButton.setOnAction(event -> {
                    Cours cours = getTableView().getItems().get(getIndex());
                    deleteCourse(cours);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };
    }

    private void loadCourses() {
        courseList = FXCollections.observableArrayList();

        String sql = "SELECT * FROM cours ORDER BY cours_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                courseList.add(new Cours(
                        rs.getInt("cours_id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getInt("places_dispo"),
                        rs.getString("tranche_age"),
                        rs.getString("prix")
                ));
            }

            courseTable.setItems(courseList);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les cours: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAddCourseDialog() {
        Dialog<Cours> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un nouveau cours");
        dialog.setHeaderText("Remplissez les détails du nouveau cours");

        // Configuration des champs du formulaire
        TextField nomField = new TextField();
        nomField.setPromptText("Nom du cours");

        TextArea descField = new TextArea();
        descField.setPromptText("Description");
        descField.setPrefRowCount(3);

        TextField ageField = new TextField();
        ageField.setPromptText("Tranche d'âge (ex: 15-25 ans)");

        TextField prixField = new TextField();
        prixField.setPromptText("Prix (ex: 30€)");

        TextField placesField = new TextField();
        placesField.setPromptText("Nombre de places disponibles");

        // Organisation des champs dans un GridPane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Tranche d'âge:"), 0, 2);
        grid.add(ageField, 1, 2);
        grid.add(new Label("Prix:"), 0, 3);
        grid.add(prixField, 1, 3);
        grid.add(new Label("Places:"), 0, 4);
        grid.add(placesField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Configuration des boutons
        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Conversion du résultat
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    return new Cours(
                            0, // L'ID sera généré par la base de données
                            nomField.getText(),
                            descField.getText(),
                            Integer.parseInt(placesField.getText()),
                            ageField.getText(),
                            prixField.getText()
                    );
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Veuillez entrer un nombre valide pour les places");
                    return null;
                }
            }
            return null;
        });

        Optional<Cours> result = dialog.showAndWait();
        result.ifPresent(newCourse -> {
            addCourseToDatabase(newCourse);
            loadCourses();
        });
    }

    private void addCourseToDatabase(Cours cours) {
        String sql = "INSERT INTO cours (nom, description, places_dispo, tranche_age, prix) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cours.getNom());
            stmt.setString(2, cours.getDescription());
            stmt.setInt(3, cours.getPlacesDispo());
            stmt.setString(4, cours.getTrancheAge());
            stmt.setString(5, cours.getPrix());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        cours.setCoursId(generatedKeys.getInt(1));
                    }
                }
                showAlert("Succès", "Cours ajouté avec succès");
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Échec de l'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showEditCourseDialog(Cours cours) {
        Dialog<Cours> dialog = new Dialog<>();
        dialog.setTitle("Modifier le cours");
        dialog.setHeaderText("Éditer les détails du cours");

        // Set up form fields with current values
        TextField nomField = new TextField(cours.getNom());
        TextArea descField = new TextArea(cours.getDescription());
        TextField ageField = new TextField(cours.getTrancheAge());
        TextField prixField = new TextField(cours.getPrix());
        TextField placesField = new TextField(String.valueOf(cours.getPlacesDispo()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Tranche d'âge:"), 0, 2);
        grid.add(ageField, 1, 2);
        grid.add(new Label("Prix:"), 0, 3);
        grid.add(prixField, 1, 3);
        grid.add(new Label("Places:"), 0, 4);
        grid.add(placesField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Cours(
                        cours.getCoursId(),
                        nomField.getText(),
                        descField.getText(),
                        Integer.parseInt(placesField.getText()),
                        ageField.getText(),
                        prixField.getText()
                );
            }
            return null;
        });

        Optional<Cours> result = dialog.showAndWait();
        result.ifPresent(updatedCourse -> {
            updateCourseInDatabase(updatedCourse);
            loadCourses();
        });
    }

    private void updateCourseInDatabase(Cours cours) {
        String sql = "UPDATE cours SET nom = ?, description = ?, places_dispo = ?, tranche_age = ?, prix = ? WHERE cours_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cours.getNom());
            stmt.setString(2, cours.getDescription());
            stmt.setInt(3, cours.getPlacesDispo());
            stmt.setString(4, cours.getTrancheAge());
            stmt.setString(5, cours.getPrix());
            stmt.setInt(6, cours.getCoursId());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                showAlert("Succès", "Cours mis à jour avec succès");
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Échec de la mise à jour: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteCourse(Cours cours) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le cours " + cours.getNom());
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer définitivement ce cours ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            String sql = "DELETE FROM cours WHERE cours_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, cours.getCoursId());
                int affected = stmt.executeUpdate();

                if (affected > 0) {
                    showAlert("Succès", "Cours supprimé avec succès");
                    loadCourses();
                }
            } catch (SQLException e) {
                showAlert("Erreur", "Échec de la suppression: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void returnToAdminInterface() {
        AdminInterfaceController adminInterface = new AdminInterfaceController(root, primaryStage, utilisateurId);
        root.setCenter(adminInterface.getView());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BorderPane getView() {
        return root;
    }
}