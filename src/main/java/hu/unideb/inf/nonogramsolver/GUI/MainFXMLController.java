package hu.unideb.inf.nonogramsolver.GUI;

import hu.unideb.inf.nonogramsolver.Controller.MainController;
import hu.unideb.inf.nonogramsolver.Model.Drawing.DrawingData;
import hu.unideb.inf.nonogramsolver.Model.PuzzleRawData;
import hu.unideb.inf.nonogramsolver.Model.SolverEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.fxml.Initializable;

public class MainFXMLController extends MainController implements Initializable{
    
    @FXML
    public Label label;
    @FXML
    public TextArea loggerArea;
    @FXML
    public TextField winMaxW, winMaxH, webPbnId;
    @FXML
    public CheckBox showGrid, showNumbers, enBackup, enPrior;
    
    private SolverGridFXMLController gridController;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.setLevel(Level.ALL);
        LOGGER.addHandler(new LogToTextAreaHandler(this.loggerArea));
        this.setSolverEvents();
        
        this.solverController.setEvent(SolverEvent.EVENT_REDRAW, (SolverEvent<DrawingData>) (DrawingData data) -> {
            MainFXMLController.this.gridController.getDrawer().redraw(data);
        });
    }

    @FXML
    private void openFileAction(ActionEvent event){
        File file = WindowHandler.getFileFromChooser((Node) event.getSource(), new String[]{"non","xml"},false);
        this.loadPuzzleFromFile(file);
        this.showFileData();
    }
    
    @FXML
    private void webImportAction(ActionEvent e){
        this.webImport(this.webPbnId.getText());
        this.showFileData();
    }

    @FXML
    private void startSolveAction(ActionEvent e) throws IOException {
        if( this.startSolve(this.enBackup.isSelected(), this.enPrior.isSelected()) ){
            this.createSolverWindow();
        }
    }
    
    @FXML
    private void saveAsAction(ActionEvent e) {
        File file = WindowHandler.getFileFromChooser((Node) e.getSource(), new String[]{"xml"},true);
        this.savePuzzle(file);
    }
    
    @FXML
    private void generateAction(ActionEvent e) throws IOException {

        File file = WindowHandler.getFileFromChooser
            ((Node) e.getSource(), new String[]{"jpg","jpeg","png","bmp"}, false);

        if (file != null) {
            GeneratorFXMLController genController = new GeneratorFXMLController() {
                @Override
                public void generateAction(PuzzleRawData rawData) {
                    MainFXMLController.this.setRawData(rawData, "Nem mentett rejtvény");
                    LOGGER.log(Level.INFO, "Rejtvény betöltve");
                    MainFXMLController.this.showFileData();
                }
            };
            this.createGenerateWindow(genController);
            
            genController.setFile(file);
            genController.initialShow();
        }
    }
    
    private void createSolverWindow() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SolverGrid.fxml"));
        Parent root = (Parent) loader.load();
        this.gridController = loader.<SolverGridFXMLController>getController();
        
        if (this.showNumbers.isSelected()) {
            this.gridController.getDrawer().setNumbers(this.solverController.getRawData());
        }
        
        this.initGridSizes();

        WindowHandler.showWindow(root,
                "Nonogram fejtő",
                this.gridController.getCanvasWidth(),
                this.gridController.getCanvasHeight(),
                (e) -> {
                    solverController.stopSolving();
                });
    }
    
    private void createGenerateWindow(GeneratorFXMLController genController) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Generator.fxml"));
        loader.setController(genController);

        Parent root = (Parent) loader.load();

        WindowHandler.showWindow(root,
                "Generálás",
                null,null,null);
    }
    
    private void showFileData() {
        this.label.setText(this.getFileInfo());
    }
    
    private void initGridSizes(){
        Integer maxW, maxH;
        try {
            maxW = Integer.parseInt(this.winMaxW.getText());
            maxH = Integer.parseInt(this.winMaxH.getText());
        } catch (NumberFormatException e) {
            maxW = null;
            maxH = null;
        }
        
        this.gridController.initSizes(
                this.solverController.getSizeX(),
                this.solverController.getSizeY(),
                maxW, maxH,
                this.showNumbers.isSelected(),
                this.showGrid.isSelected());
    }
}
