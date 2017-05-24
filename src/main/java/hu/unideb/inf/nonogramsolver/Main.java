package hu.unideb.inf.nonogramsolver;

import hu.unideb.inf.nonogramsolver.Controller.FXML.MainFXMLController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import static javafx.application.Application.launch;


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
     * 
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
