package hu.unideb.inf.nonogramsolver.Controller;

import hu.unideb.inf.nonogramsolver.Model.NonGenerator;
import hu.unideb.inf.nonogramsolver.Model.Drawing.PuzzleDrawer;
import hu.unideb.inf.nonogramsolver.Model.PuzzleRawData;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;

/**
 * FXML Controller class
 *
 * @author wazemaki
 */
public class GeneratorFXMLController implements Initializable{
    private NonGenerator generator;
    private PuzzleDrawer drawer;
    private MainFXMLController mainController;
    
    @FXML
    public Canvas out;
    @FXML
    public CheckBox invertColors;
    @FXML
    public Slider sizeSlider, thresholdSlider;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.drawer = new PuzzleDrawer(out.getGraphicsContext2D());
        this.drawer.setDrawGrid(true);
        this.generator = new NonGenerator();
        
        this.setListeners();
    }
    
    private void setListeners(){
        this.thresholdSlider.valueProperty().addListener(
            (ObservableValue<? extends Number> o, Number oldValue, Number newValue) -> {
                this.makeMonochromeAndRedraw(newValue.intValue());
        });
        
        this.sizeSlider.valueProperty().addListener(
            (ObservableValue<? extends Number> o, Number oldValue, Number newValue) -> {
                int size = newValue.intValue();
                this.generator.makeResizedImageFromMaster(size);
                this.setDrawerSizes();
                this.makeMonochromeAndRedraw(null);
        });
        
        this.invertColors.selectedProperty().addListener(
            (ObservableValue<? extends Boolean> o, Boolean oldValue, Boolean newValue) -> {
                this.generator.setInvertCol(newValue);
                this.makeMonochromeAndRedraw(null);
        });
    }
    
    private void setDrawerSizes(){
        this.drawer.setSizes(
                this.generator.getWidth(),
                this.generator.getHeight(),
                (int)this.out.getWidth(),
                (int)this.out.getHeight());
    }
    
    public void setFile(File file) throws IOException {
        this.generator.setFile(file);
    }
    
    public void initialShow(){
        this.generator.makeResizedImageFromMaster((int)this.sizeSlider.getValue());
        this.setDrawerSizes();
        this.makeMonochromeAndRedraw((int)this.thresholdSlider.getValue());
    }
    
    private void makeMonochromeAndRedraw(Integer threshold){
        if(threshold != null){
            this.generator.setThreshold(threshold);
        }
        this.generator.makeMonochromeImageFromResized();
        this.drawer.redraw(this.generator.getFinalImage());
    }
    
    public void setMainController(MainFXMLController controller) {
        this.mainController = controller;
    }
    
    public void generateRawData(){
        PuzzleRawData rawData = this.generator.generateRawData();
        this.mainController.setGeneratedRawData(rawData);
    }
    
}
