package projet;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import projet.view.AccueilView;

public class Main extends Application {
    private BorderPane root;
    private AccueilView accueil;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        accueil = new AccueilView(primaryStage); // ✅ Passer le stage pour changer les scènes
        root.setCenter(accueil.getView());
        
        
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Application Théâtre");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
