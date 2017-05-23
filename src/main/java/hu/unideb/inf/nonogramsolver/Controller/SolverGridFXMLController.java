package hu.unideb.inf.nonogramsolver.Controller;

import hu.unideb.inf.nonogramsolver.Model.Drawing.PuzzleDrawer;
import hu.unideb.inf.nonogramsolver.Model.Writers.ImageFileWriter;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author wazemaki
 */
public class SolverGridFXMLController implements Initializable{
    
    @FXML
    public Canvas grid;
    @FXML
    public Button closeBtn, saveImageBtn;
    
    private PuzzleDrawer drawer;
    
    public PuzzleDrawer getDrawer(){
        return this.drawer;
    }
    
    public void closeWindow(){
        Stage stage = (Stage) this.closeBtn.getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
    
    public void saveImageAction(ActionEvent e){
        FileChooser fileChooser = new FileChooser();
                 
        FileChooser.ExtensionFilter filter = new FileChooser
                .ExtensionFilter("Minden támogatott fájltípus", "*.png", "*.jpg", "*.jpeg", "*.bmp");
        FileChooser.ExtensionFilter pngFilter = new FileChooser
                .ExtensionFilter("PNG fájlok (*.png)", "*.png");
        FileChooser.ExtensionFilter jpgFilter = new FileChooser
                .ExtensionFilter("JPG fájlok (*.jpg, *.jpeg)", "*.jpg", "*.jpeg");
        FileChooser.ExtensionFilter bmpFilter = new FileChooser
                .ExtensionFilter("Bitmap fájlok (*.bmp)", "*.bmp");
        
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.getExtensionFilters().add(pngFilter);
        fileChooser.getExtensionFilters().add(jpgFilter);
        fileChooser.getExtensionFilters().add(bmpFilter);

        File file = fileChooser.showSaveDialog(((Node) e.getSource()).getScene().getWindow());
        
        if(file != null) {
            try {
                WritableImage writableImage = new WritableImage((int) this.grid.getWidth(), (int) this.grid.getHeight());
                this.grid.snapshot(null, writableImage);
                ImageFileWriter fw = new ImageFileWriter(writableImage);
                fw.write(file);
            } catch (Exception ex) {
            }
        }
    }
    
    public void setCanvasSizes(int w, int h){
        this.grid.setWidth(w);
        this.grid.setHeight(h);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.drawer = new PuzzleDrawer(this.grid.getGraphicsContext2D());
    }
}
