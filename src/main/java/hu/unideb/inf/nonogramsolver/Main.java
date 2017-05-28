package hu.unideb.inf.nonogramsolver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import static javafx.application.Application.launch;

/**
 * Main osztály.
 */
public class Main extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainScene.fxml"));
        
        Scene scene = new Scene(loader.load());
        
        scene.getStylesheets().add("/styles/Styles.css");
        
        stage.setTitle("WM Nonogram Fejtő");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Main metódus, indítja az alkalmazást.
     *
     * @param args Argumentumok
     */
    public static void main(String[] args) {
        launch(args);
    }

}
