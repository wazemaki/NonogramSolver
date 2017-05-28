package hu.unideb.inf.nonogramsolver;

import hu.unideb.inf.nonogramsolver.GUI.WindowHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import static javafx.application.Application.launch;

/**
 * Main osztály.
 */
public class Main extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainScene.fxml"));
        WindowHandler.showWindow(loader.load(), "WM Nonogram Fejtő", null,null,null);
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
