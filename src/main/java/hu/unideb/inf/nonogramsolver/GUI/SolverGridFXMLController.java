package hu.unideb.inf.nonogramsolver.GUI;

import hu.unideb.inf.nonogramsolver.Model.Drawing.PuzzleDrawer;
import hu.unideb.inf.nonogramsolver.Model.Writers.ImageWriter;
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
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.drawer = new PuzzleDrawer(this.grid.getGraphicsContext2D());
    }
    
    public PuzzleDrawer getDrawer(){
        return this.drawer;
    }
    
    @FXML
    private void closeWindowAction(){
        Stage stage = (Stage) this.closeBtn.getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
    
    
    @FXML
    private void saveImageAction(ActionEvent e){
        
        File file = WindowHandler.getFileFromChooser
            ((Node) e.getSource(), new String[]{"jpg","jpeg","png","bmp"}, true);
        
        if(file != null) {
            try {
                WritableImage writableImage = new WritableImage((int) this.grid.getWidth(), (int) this.grid.getHeight());
                this.grid.snapshot(null, writableImage);
                ImageWriter fw = new ImageWriter(writableImage);
                fw.write(file);
            } catch (Exception ex) {
            }
        }
    }
    
    public void initSizes(int wBlocks, int hBlocks, int maxW, int maxH, boolean showNumbers, boolean showGrid){
        if (showNumbers) {
            this.drawer.setDrawNumbers(true);
        }
        if (showGrid) {
            this.drawer.setDrawGrid(true);
        }
        
        this.drawer.setSizes(
                wBlocks,
                hBlocks,
                maxW, maxH);
        
        this.grid.setWidth(this.drawer.getFullWidth());
        this.grid.setHeight(this.drawer.getFullHeight());
    }
    
    public int getCanvasWidth(){
        return (int)this.grid.getWidth();
    }
    
    public int getCanvasHeight(){
        return (int)this.grid.getHeight();
    }
}
