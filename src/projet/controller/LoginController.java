package projet.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import projet.database.DatabaseConnection;
import projet.model.Utilisateur;
import projet.view.AccueilView;
import projet.view.navbar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    private VBox view;
    private BorderPane root;
    private TextField nomField, emailField, loginEmailField;
    private PasswordField passwordField, loginPasswordField;
    private Button inscrireButton, connecterButton;
    private Label messageLabel;
    private Stage primaryStage;
    private int utilisateurId = -1;

    public LoginController(Stage primaryStage) {
        this.primaryStage = primaryStage;

        root = new BorderPane();
        root.setTop(navbar.createNavBar(primaryStage, "Connexion", utilisateurId));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f9f4ed 0%, #e8e0d1 100%);");

        VBox mainContainer = new VBox();
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setStyle("-fx-background-color: rgba(255,255,255,0.7); " +
                "-fx-background-radius: 15; -fx-border-radius: 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 0);");

        VBox inscriptionBox = createFormSection("📝 Inscription", true);
        nomField = createStyledTextField("Nom");
        emailField = createStyledTextField("Email");
        passwordField = createStyledPasswordField("Mot de passe");
        inscrireButton = createStyledButton("S'inscrire", "#6a0f1c");
        inscrireButton.setOnAction(e -> handleRegister());

        VBox connexionBox = createFormSection("🔐 Connexion", false);
        loginEmailField = createStyledTextField("Email");
        loginPasswordField = createStyledPasswordField("Mot de passe");
        connecterButton = createStyledButton("Se connecter", "#2e7d32");
        connecterButton.setOnAction(e -> handleLogin());

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 14px;");

        inscriptionBox.getChildren().addAll(nomField, emailField, passwordField, inscrireButton);
        connexionBox.getChildren().addAll(loginEmailField, loginPasswordField, connecterButton);

        mainContainer.getChildren().addAll(
                inscriptionBox,
                new Separator(),
                connexionBox,
                messageLabel
        );

        if (utilisateurId != -1) {
            Button deconnexionButton = createStyledButton("Déconnexion", "#a52a2a");
            deconnexionButton.setOnAction(e -> handleLogout());
            mainContainer.getChildren().add(deconnexionButton);
        }

        root.setCenter(mainContainer);
    }

    private VBox createFormSection(String title, boolean withNameField) {
        Label titre = new Label(title);
        titre.setStyle("-fx-font-family: 'Playfair Display'; -fx-font-size: 20px; " +
                "-fx-text-fill: #5d4037; -fx-font-weight: bold;");

        VBox section = new VBox(15);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: rgba(255,255,255,0.8); " +
                "-fx-background-radius: 10; -fx-padding: 20px;");
        section.getChildren().add(titre);

        return section;
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle("-fx-font-family: 'Raleway'; -fx-font-size: 14px; " +
                "-fx-background-radius: 5; -fx-padding: 8px; " +
                "-fx-border-color: #e0d4c3; -fx-border-radius: 5;");
        field.setMaxWidth(250);
        return field;
    }

    private PasswordField createStyledPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setStyle("-fx-font-family: 'Raleway'; -fx-font-size: 14px; " +
                "-fx-background-radius: 5; -fx-padding: 8px; " +
                "-fx-border-color: #e0d4c3; -fx-border-radius: 5;");
        field.setMaxWidth(250);
        return field;
    }

    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-font-family: 'Raleway'; -fx-font-size: 14px; " +
                "-fx-padding: 10px 25px; -fx-background-radius: 20px;");


        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + darkenColor(color) + "; " +
                "-fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-padding: 10px 25px; -fx-background-radius: 20px;"));

        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-padding: 10px 25px; -fx-background-radius: 20px;"));
        return btn;
    }

    private String darkenColor(String hexColor) {
        switch(hexColor) {
            case "#6a0f1c": return "#4a0a13";
            case "#2e7d32": return "#1e5a22";
            case "#a52a2a": return "#851a1a";
            default: return "#333333";
        }
    }

    private void handleLogout() {
        Utilisateur.deconnecter();
        primaryStage.getScene().setRoot(new LoginController(primaryStage).getView());
        System.out.println("❌ Déconnexion réussie !");
    }

    private void handleRegister() {
        String nom = nomField.getText();
        String email = emailField.getText();
        String motDePasse = passwordField.getText();

        if (nom.isEmpty() || email.isEmpty() || motDePasse.isEmpty()) {
            messageLabel.setText("❌ Tous les champs doivent être remplis !");
            return;
        }

        String sql = "INSERT INTO utilisateurs (nom, email, mot_de_passe, role, forfait_restants) VALUES (?, ?, ?, 'user', 10)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nom);
            stmt.setString(2, email);
            stmt.setString(3, motDePasse);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                messageLabel.setText("✅ Inscription réussie ! Vous pouvez maintenant vous connecter.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("❌ Erreur lors de l'inscription !");
        }
    }

    private void handleLogin() {
        String email = loginEmailField.getText();
        String motDePasse = loginPasswordField.getText();

        if (email.isEmpty() || motDePasse.isEmpty()) {
            messageLabel.setText("❌ Veuillez remplir tous les champs !");
            return;
        }

        String sql = "SELECT id, nom, role FROM utilisateurs WHERE email = ? AND mot_de_passe = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, motDePasse);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                String nom = rs.getString("nom");
                String role = rs.getString("role");

                Utilisateur.setUtilisateurId(userId);
                Utilisateur.setNom(nom);

                messageLabel.setText("✅ Connexion réussie !");

                if (role.equals("admin")) {
                    afficherInterfaceAdmin();
                } else {
                    afficherInterfaceUtilisateur(userId);
                }
            } else {
                messageLabel.setText("❌ Email ou mot de passe incorrect !");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            messageLabel.setText("❌ Erreur lors de la connexion !");
        }
    }

    private void afficherInterfaceAdmin() {
        AdminInterfaceController adminInterface = new AdminInterfaceController(root, primaryStage);
        root.setCenter(adminInterface.getView());
    }

    private void afficherInterfaceUtilisateur(int utilisateurId) {
        AccueilView accueilView = new AccueilView(primaryStage);
        Scene accueilScene = new Scene(accueilView.getView(), 900, 600);
        primaryStage.setScene(accueilScene);
    }

    public BorderPane getView() {
        return root;
    }
}