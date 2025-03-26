package projet.controller;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import projet.view.AccueilView;
import projet.view.navbar;

public class AdminInterfaceController {
    private BorderPane root;
    private VBox view;
    private Stage primaryStage;
    private int utilisateurId;

    public AdminInterfaceController(BorderPane root, Stage primaryStage, int utilisateurId) {
        this.root = root;
        this.primaryStage = primaryStage;
        this.utilisateurId = utilisateurId;

        // Configuration de la navbar avec les 3 paramètres
        root.setTop(navbar.createNavBar(primaryStage, "Admin", utilisateurId));

        Label welcomeLabel = new Label("👨‍💼 Bienvenue, " + projet.model.Utilisateur.getNomCourt() + " !");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #5d4037;");

        Button manageUsersButton = new Button("👥 Gérer les utilisateurs");
        Button manageCoursesButton = new Button("📚 Gérer les cours");
        Button manageReservationsButton = new Button("📋 Gérer les réservations");

        // Style des boutons
        String buttonStyle = "-fx-background-color: #6a0f1c; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 10px 20px; " +
                "-fx-background-radius: 5px;";

        manageUsersButton.setStyle(buttonStyle);
        manageCoursesButton.setStyle(buttonStyle);
        manageReservationsButton.setStyle(buttonStyle);

        // Actions des boutons
        manageUsersButton.setOnAction(e -> new UserManagementController(primaryStage, utilisateurId));
        manageCoursesButton.setOnAction(e -> new CourseManagementController(primaryStage, utilisateurId));
        manageReservationsButton.setOnAction(e -> new ReservationManagementController(primaryStage, utilisateurId));

        // Effets de survol
        manageUsersButton.setOnMouseEntered(e -> manageUsersButton.setStyle(buttonStyle + "-fx-background-color: #8d6e63;"));
        manageUsersButton.setOnMouseExited(e -> manageUsersButton.setStyle(buttonStyle));
        manageCoursesButton.setOnMouseEntered(e -> manageCoursesButton.setStyle(buttonStyle + "-fx-background-color: #8d6e63;"));
        manageCoursesButton.setOnMouseExited(e -> manageCoursesButton.setStyle(buttonStyle));
        manageReservationsButton.setOnMouseEntered(e -> manageReservationsButton.setStyle(buttonStyle + "-fx-background-color: #8d6e63;"));
        manageReservationsButton.setOnMouseExited(e -> manageReservationsButton.setStyle(buttonStyle));

        view = new VBox(20, welcomeLabel, manageUsersButton, manageCoursesButton, manageReservationsButton);
        view.setStyle("-fx-padding: 40px; -fx-alignment: center; -fx-background-color: rgba(255,255,255,0.7); " +
                "-fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        root.setCenter(view);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f9f4ed 0%, #e8e0d1 100%);");
    }

    public VBox getView() {
        return view;
    }
}