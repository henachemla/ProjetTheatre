package projet.controller;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import projet.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    private VBox view;
    private TextField nomField, emailField, loginEmailField;
    private PasswordField passwordField, loginPasswordField;
    private Button inscrireButton, connecterButton;
    private Label messageLabel;
    private OnLoginSuccessListener loginSuccessListener;

    public LoginController() {
        // 📝 Section Inscription
        Label titreInscription = new Label("📝 Inscription");
        nomField = new TextField();
        nomField.setPromptText("Nom");

        emailField = new TextField();
        emailField.setPromptText("Email");

        passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");

        inscrireButton = new Button("S'inscrire");
        inscrireButton.setOnAction(e -> inscrireUtilisateur());

        // 🔐 Section Connexion
        Label titreConnexion = new Label("🔐 Connexion");
        loginEmailField = new TextField();
        loginEmailField.setPromptText("Email");

        loginPasswordField = new PasswordField();
        loginPasswordField.setPromptText("Mot de passe");

        connecterButton = new Button("Se connecter");
        connecterButton.setOnAction(e -> connecterUtilisateur());

        messageLabel = new Label();

        // 📌 Organisation de l'affichage
        view = new VBox(10, titreInscription, nomField, emailField, passwordField, inscrireButton, 
                        titreConnexion, loginEmailField, loginPasswordField, connecterButton, messageLabel);
        view.setStyle("-fx-padding: 20px; -fx-alignment: center;");
    }

    // ✅ Inscription
    private void inscrireUtilisateur() {
        String nom = nomField.getText();
        String email = emailField.getText();
        String motDePasse = passwordField.getText();

        if (nom.isEmpty() || email.isEmpty() || motDePasse.isEmpty()) {
            messageLabel.setText("❌ Tous les champs doivent être remplis !");
            return;
        }

        String sql = "INSERT INTO utilisateurs (nom, email, mot_de_passe, forfait_restants) VALUES (?, ?, ?, 10)";

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

    // ✅ Connexion
    private void connecterUtilisateur() {
        String email = loginEmailField.getText();
        String motDePasse = loginPasswordField.getText();

        if (email.isEmpty() || motDePasse.isEmpty()) {
            messageLabel.setText("❌ Veuillez remplir tous les champs !");
            return;
        }

        String sql = "SELECT id FROM utilisateurs WHERE email = ? AND mot_de_passe = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, motDePasse);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int utilisateurId = rs.getInt("id");
                messageLabel.setText("✅ Connexion réussie !");
                if (loginSuccessListener != null) {
                    loginSuccessListener.onLoginSuccess(utilisateurId);
                }
            } else {
                messageLabel.setText("❌ Email ou mot de passe incorrect !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("❌ Erreur lors de la connexion !");
        }
    }

    public VBox getView() {
        return view;
    }

    // Interface pour déclencher un événement après la connexion
    public interface OnLoginSuccessListener {
        void onLoginSuccess(int utilisateurId);
    }

    public void setOnLoginSuccess(OnLoginSuccessListener listener) {
        this.loginSuccessListener = listener;
    }
}

