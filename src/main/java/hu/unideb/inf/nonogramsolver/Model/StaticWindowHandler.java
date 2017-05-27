package hu.unideb.inf.nonogramsolver.Model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author wazemaki
 */
public class StaticWindowHandler {
    
    /**
     *
     * @param parent
     * @param title
     * @param width
     * @param height
     * @param onCloseEvent
     */
    public static void showWindow(Parent parent, String title, Integer width, Integer height, EventHandler<WindowEvent> onCloseEvent){
        Stage stage = new Stage();
        stage.setTitle(title);
        
        Scene scene;
        if(width != null && height != null){
            scene = new Scene(parent,width,height);
        } else {
            scene = new Scene(parent);
        }
        
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setScene(scene);
        stage.show();
        
        if(onCloseEvent != null){
            stage.setOnCloseRequest(onCloseEvent);
        }
    }
    
    /**
     *
     * @param node
     * @param enabledFiletypes
     * @param isSaveDialog
     * @return
     */
    public static File getFileFromChooser(Node node, String[] enabledFiletypes, boolean isSaveDialog){
        
        FileChooser fileChooser = new FileChooser();
        
        FileChooser.ExtensionFilter filter;
        List<String> allExtensions = new ArrayList<>();
        
       
        for(String fileType : enabledFiletypes){
            filter = new FileChooser.ExtensionFilter(fileType.toUpperCase() + " fájlok", "*." + fileType);
            fileChooser.getExtensionFilters().add(filter);
            allExtensions.add("*." + fileType);
        }
        filter = new FileChooser.ExtensionFilter("Minden támogatott fájl", allExtensions);
        fileChooser.getExtensionFilters().add(0, filter);

        File file;
        if(isSaveDialog){
            file = fileChooser.showSaveDialog(node.getScene().getWindow());
        } else {
            file = fileChooser.showOpenDialog(node.getScene().getWindow());
        }
        
        return file;
    }
}
