package hu.unideb.inf.nonogramsolver;

import hu.unideb.inf.nonogramsolver.Controller.MainFXMLController;
import hu.unideb.inf.nonogramsolver.Controller.SolverController;
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
        Parent root = (Parent)loader.load();
        
        MainFXMLController controller = loader.<MainFXMLController>getController();
        
        SolverController solver = new SolverController();
        controller.setSolver(solver);
        
        Scene scene = new Scene(root);
        
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
