package hu.unideb.inf.nonogramsolver.Controller.GUI;

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
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author wazemaki
 */
public abstract class GeneratorFXMLController implements Initializable{
    private NonGenerator generator;
    private PuzzleDrawer drawer;
    
    @FXML
    public Canvas out;
    @FXML
    public Label sizeLabel;
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
    
    @FXML
    private void generateRawDataAction(){
        PuzzleRawData rawData = this.generator.generateRawData();
        this.generateAction(rawData);
        Stage stage = (Stage) this.out.getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
    
    private void setListeners(){
        this.thresholdSlider.valueProperty().addListener(
            (ObservableValue<? extends Number> o, Number oldValue, Number newValue) -> {
                this.makeMonochrome(newValue.intValue());
                this.redraw();
        });
        
        this.sizeSlider.valueProperty().addListener(
            (ObservableValue<? extends Number> o, Number oldValue, Number newValue) -> {
                int size = newValue.intValue();
                this.generator.makeResizedImageFromMaster(size);
                this.setDrawerSizes();
                this.makeMonochrome(null);
                this.redraw();
                this.setSizeLabelText();
        });
        
        this.invertColors.selectedProperty().addListener(
            (ObservableValue<? extends Boolean> o, Boolean oldValue, Boolean newValue) -> {
                this.generator.setInvertCol(newValue);
                this.makeMonochrome(null);
                this.redraw();
        });
    }
    
    public void setFile(File file) throws IOException {
        this.generator.setFile(file);
    }
    
    private void setDrawerSizes(){
        this.drawer.setSizes(
                this.generator.getWidth(),
                this.generator.getHeight(),
                (int)this.out.getWidth(),
                (int)this.out.getHeight());
    }
    
    public void initialShow(){
        this.generator.makeResizedImageFromMaster((int)this.sizeSlider.getValue());
        this.setDrawerSizes();
        this.makeMonochrome((int)this.thresholdSlider.getValue());
        this.setSizeLabelText();
        this.redraw();
    }
    
    private void makeMonochrome(Integer threshold){
        if(threshold != null){
            this.generator.setThreshold(threshold);
        }
        this.generator.makeMonochromeImageFromResized();
    }
    
    private void redraw(){
        this.drawer.redraw(this.generator.getFinalImage());
    }
    
    private void setSizeLabelText(){
        this.sizeLabel.setText(this.generator.getWidth() + " x " + this.generator.getHeight());
    }
    
    public abstract void generateAction(PuzzleRawData rawData);
    
}
