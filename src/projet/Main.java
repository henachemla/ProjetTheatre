package projet;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import projet.controller.LoginController;
import projet.controller.ReservationController;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        LoginController loginController = new LoginController();
        BorderPane root = new BorderPane(loginController.getView());

        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("Application Théâtre");
        stage.setScene(scene);
        stage.show();

        // Après connexion, afficher l'écran de réservation
        loginController.setOnLoginSuccess(utilisateurId -> {
            ReservationController reservationController = new ReservationController(utilisateurId);
            root.setCenter(reservationController.getView());
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
