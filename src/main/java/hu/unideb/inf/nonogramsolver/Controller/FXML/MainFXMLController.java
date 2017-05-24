package hu.unideb.inf.nonogramsolver.Controller.FXML;

import hu.unideb.inf.nonogramsolver.Controller.MainController;
import hu.unideb.inf.nonogramsolver.Model.Drawing.DrawingData;
import hu.unideb.inf.nonogramsolver.Model.Drawing.PuzzleDrawer;
import hu.unideb.inf.nonogramsolver.Model.LogToTextAreaHandler;
import hu.unideb.inf.nonogramsolver.Model.Readers.NONReader;
import hu.unideb.inf.nonogramsolver.Model.PuzzleRawData;
import hu.unideb.inf.nonogramsolver.Model.Solver.SolverEvent;
import hu.unideb.inf.nonogramsolver.Model.Readers.XMLReader;
import hu.unideb.inf.nonogramsolver.Model.Readers.nonogramReader;
import hu.unideb.inf.nonogramsolver.Model.Solver.SolverException;
import hu.unideb.inf.nonogramsolver.Model.WEBImporter;
import hu.unideb.inf.nonogramsolver.Model.Writers.XMLFileWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javafx.fxml.Initializable;

public class MainFXMLController extends MainController implements Initializable{
    
    protected PuzzleDrawer drawer;
    
    @FXML
    public Label label;
    @FXML
    public TextArea loggerArea;
    @FXML
    public TextField winMaxW, winMaxH, webPbnId;
    @FXML
    public CheckBox showGrid, showNumbers, enBackup, enPrior;

    @FXML
    private void openFileChooserAction(ActionEvent event) {
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Minden támogatott fájltípus", "*.xml", "*.non");
        FileChooser.ExtensionFilter xmlFilter = new FileChooser.ExtensionFilter("XML fájlok", "*.xml");
        FileChooser.ExtensionFilter nonFilter = new FileChooser.ExtensionFilter("Nonogram fájlok", "*.non");

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Fájl megnyitása...");
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.getExtensionFilters().add(nonFilter);
        fileChooser.getExtensionFilters().add(xmlFilter);

        Node node = (Node) event.getSource();
        File file = fileChooser.showOpenDialog(node.getScene().getWindow());

        if (file != null) {
            try {
                this.loadPuzzleFromFile(file);
            } catch (SolverException ex) {
                LOGGER.log(Level.WARNING, "A fájl megnyitása sikertelen volt ["+ file.getName() +"]:\n " + ex.getMessage());
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "A művelet során IO hiba történt ["+ file.getName() +"]:\n " + ex.getMessage());
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Ismeretlen hiba ["+ file.getName() +"]:\n " + ex.getMessage());
            }
        }
    }
    
    @FXML
    private void webImportAction(ActionEvent e){
        try{
            String id = this.webPbnId.getText();
            PuzzleRawData rawData = WEBImporter.importPuzzle(id);
            
            this.solverController.setRawData(rawData);
            
            showFileData("--- WEBPBN.COM " + id + " ---"
                    + "\n< " + rawData.getSizeX() + " x " + rawData.getSizeY() + " >\n"
                    + rawData.getDescription());
            LOGGER.log(Level.CONFIG, "File importálva: " + id);
        } catch(SolverException | IOException ex){
            LOGGER.log(Level.WARNING, "Importálás sikertelen\n " + ex.getMessage());
        }
    }

    @FXML
    private void startSolveAction(ActionEvent e) {
        try {
            this.drawer = this.showSolverWindow();
            this.solverController.solve(this.enBackup.isSelected(), this.enPrior.isSelected());
        } catch (IOException | SolverException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
    }
    
    @FXML
    private void generateAction(ActionEvent e) throws IOException {

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Fájl megnyitása...");

        Node node = (Node) e.getSource();
        File file = fileChooser.showOpenDialog(node.getScene().getWindow());

        if (file != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Generator.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            GeneratorFXMLController controller = fxmlLoader.<GeneratorFXMLController>getController();

            controller.setFile(file);
            controller.setMainController(this);
            controller.initialShow();
            
            Stage stage = new Stage();
            stage.setTitle("Gen");
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/styles/Styles.css");

            stage.setScene(scene);
            stage.show();
        }
    }
    
    public void setGeneratedRawData(PuzzleRawData data){
        this.solverController.setRawData(data);

        showFileData("--- GENERÁLT REJTVÉNY ---"
                + "\n< " + data.getSizeX() + " x " + data.getSizeY() + " >\n");
        LOGGER.log(Level.CONFIG, "Rejtvény importálva");
    }
    
    @FXML
    private void saveAsAction(ActionEvent e) {
        FileChooser.ExtensionFilter xmlFilter = new FileChooser.ExtensionFilter("XML fájlok", "*.xml");

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Fájl mentése...");
        fileChooser.getExtensionFilters().add(xmlFilter);

        File file = fileChooser.showSaveDialog(((Node) e.getSource()).getScene().getWindow());

        if (file != null && this.solverController.isValidPuzzle()) {
            try {
                XMLFileWriter fw = new XMLFileWriter(this.solverController.getRawData());
                fw.write(file);
                LOGGER.log(Level.INFO, "Fájl elmentve: " + file.getName());
            } catch (IOException | TransformerException | ParserConfigurationException ex) {
                LOGGER.log(Level.WARNING, "A fájl mentése sikertelen volt ["+file.getName()+"] :\n " + ex.getMessage());
            }
        }
    }

    private PuzzleDrawer showSolverWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/SolverGrid.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        SolverGridFXMLController controller = fxmlLoader.<SolverGridFXMLController>getController();
        
        PuzzleDrawer pDrawer = controller.getDrawer();
        
        if (this.showNumbers.isSelected()) {
            pDrawer.setNumbers(this.solverController.getRawData());
            pDrawer.setDrawNumbers(true);
        }
        if (this.showGrid.isSelected()) {
            pDrawer.setDrawGrid(true);
        }
        
        Integer maxW = null, maxH = null;
        try {
            maxW = Integer.parseInt(this.winMaxW.getText());
            maxH = Integer.parseInt(this.winMaxH.getText());
        } catch (NumberFormatException e) {
        }
        
        pDrawer.setSizes(
                this.solverController.getSizeX(),
                this.solverController.getSizeY(),
                maxW, maxH);
        controller.setCanvasSizes();

        Stage stage = new Stage();
        stage.setTitle("Nonogram fejtő");
        
        Scene scene = new Scene(root, pDrawer.getFullWidth(), pDrawer.getFullHeight());
        scene.getStylesheets().add("/styles/Styles.css");
        stage.setScene(scene);
        stage.show();
        
        stage.setOnCloseRequest((e) -> {
            solverController.stopSolving();
        });
        
        return pDrawer;
    }

    private void loadPuzzleFromFile(File file) throws IOException, SolverException, Exception{
        nonogramReader reader = null;
        String mimeType = Files.probeContentType(file.toPath());
        switch (mimeType) {
            case "text/plain":
                reader = new NONReader(file);
                break;
            case "application/xml":
                reader = new XMLReader(file);
                break;
            default:
        }

        if (reader == null) {
            throw new SolverException("Nem támogatott fájltípus: " + mimeType, 1);
        }

        PuzzleRawData rawData = reader.read();
        this.solverController.setRawData(rawData);
        showFileData("--- " + file.getName() + " ---"
                + "\n< " + rawData.getSizeX() + " x " + rawData.getSizeY() + " >\n"
                + rawData.getDescription());
        LOGGER.log(Level.CONFIG, "File beolvasva: " + file.getName());
    }

    private void showFileData(String str) {
        if (str != null) {
            this.label.setText(str);
        } else {
            this.label.setText("--- Nincs megnyitva fájl ---");
        }
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.setLevel(Level.ALL);
        LOGGER.addHandler(new LogToTextAreaHandler(this.loggerArea));
        this.setSolverEvents();
        this.solverController.setEvent("redraw", (SolverEvent<DrawingData>) (DrawingData data) -> {
            MainFXMLController.this.drawer.redraw(data);
        });
    }
}
