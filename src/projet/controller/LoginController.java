package projet.controller;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    private Stage primaryStage;// 🔥 Ajout du Stage pour gérer les scènes
    private int utilisateurId; // ✅ -1 signifie "non connecté"


    public LoginController(Stage primaryStage) {
        this.primaryStage = primaryStage; // 🔥 Stocke le stage actuel
       
        
     // 📝 Section Inscription
        Label titreInscription = new Label("📝 Inscription");
        nomField = new TextField();
        nomField.setPromptText("Nom");

        emailField = new TextField();
        emailField.setPromptText("Email");

        passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");

        inscrireButton = new Button("S'inscrire");
        inscrireButton.setOnAction(e -> handleRegister());



        // 🔐 Section Connexion
        Label titreConnexion = new Label("🔐 Connexion");
        loginEmailField = new TextField();
        loginEmailField.setPromptText("Email");

        loginPasswordField = new PasswordField();
        loginPasswordField.setPromptText("Mot de passe");

        connecterButton = new Button("Se connecter");
        connecterButton.setOnAction(e -> handleLogin()); // 🔥 Lié directement à handleLogin()

        messageLabel = new Label();
        
     // ✅ Bouton Déconnexion
        if (utilisateurId != -1) {
        Button deconnexionButton = new Button("Déconnexion");
        deconnexionButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        deconnexionButton.setOnAction(e -> handleLogout());
        }
        
        // 📌 Organisation de l'affichage
        view = new VBox(10, titreInscription, nomField, emailField, passwordField, inscrireButton, 
titreConnexion, loginEmailField, loginPasswordField, connecterButton, messageLabel);
        view.setStyle("-fx-padding: 20px; -fx-alignment: center;");
        
     // ✅ Ajout de la NavBar
        root = new BorderPane();

        // ✅ Ajout de la NavBar avec "Connexion" comme onglet actif
        root.setTop(navbar.createNavBar(primaryStage, "Connexion", utilisateurId));

        root.setCenter(view);
    }
    
    private void handleLogout() {
    	Utilisateur.deconnecter();
    	primaryStage.getScene().setRoot(new LoginController(primaryStage).getView());
        System.out.println("❌ Déconnexion réussie !");
    }

    
 // ✅ Méthode pour l'inscription d'un utilisateur
    private void handleRegister() {
        String nom = nomField.getText();
        String email = emailField.getText();
        String motDePasse = passwordField.getText();

        if (nom.isEmpty() || email.isEmpty() || motDePasse.isEmpty()) {
            messageLabel.setText("❌ Tous les champs doivent être remplis !");
            return;
        }

        // 🔥 Ajout du champ "role" par défaut en "user"
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

    //methode pour la connexion d'un utilisateur
    private void handleLogin() {
        String email = loginEmailField.getText();
        String motDePasse = loginPasswordField.getText();

        if (email.isEmpty() || motDePasse.isEmpty()) {
            messageLabel.setText("❌ Veuillez remplir tous les champs !");
            return;
        }

        String sql = "SELECT id, role FROM utilisateurs WHERE email = ? AND mot_de_passe = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, motDePasse);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
            	Utilisateur.setUtilisateurId(rs.getInt("id"));
                String role = rs.getString("role");  // Récupération du rôle

                messageLabel.setText("✅ Connexion réussie !");
                
                // 🔥 Redirection immédiate selon le rôle
                if (role.equals("admin")) {
                    afficherInterfaceAdmin();
                } else {
                    afficherInterfaceUtilisateur(utilisateurId);
                }
            } else {
                messageLabel.setText("❌ Email ou mot de passe incorrect !");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            messageLabel.setText("❌ Erreur lors de la connexion !");
        }
    }

 // ✅ Afficher l'interface administrateur après connexion
    private void afficherInterfaceAdmin() {
        AdminInterfaceController adminInterface = new AdminInterfaceController(root, primaryStage);
        root.setCenter(adminInterface.getView());    
    }


    // ✅ Afficher l'interface utilisateur
    private void afficherInterfaceUtilisateur(int utilisateurId) {
    	AccueilView accueilView = new AccueilView(primaryStage);
        Scene accueilScene = new Scene(accueilView.getView(), 900, 600);
        primaryStage.setScene(accueilScene);
    }
    

    public BorderPane getView() {
        return root;
    }
}
