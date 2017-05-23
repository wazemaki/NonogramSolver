package hu.unideb.inf.nonogramsolver.Controller;

import hu.unideb.inf.nonogramsolver.Model.Drawing.DrawingData;
import hu.unideb.inf.nonogramsolver.Model.Drawing.PuzzleDrawer;
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
import java.nio.file.Files;
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

public class MainFXMLController {
    
    private static final int MAXH = 750;
    private static final int MAXW = 800;
    private static final int MINH = 200;
    private static final int MINW = 200;

    private SolverController solver;
    private PuzzleDrawer drawer;
//    private SolverGridFXMLController gridFxml;
    
    private long lastStartTime;
    private long lastPassedTime;

    public void setSolver(SolverController s) {
        this.solver = s;
        this.solver.setEvent("start", (SolverEvent<String>) (String data) -> {
            this.lastStartTime = System.currentTimeMillis();
            logToTextArea("Start...");
            logToTextArea(data);
        });
        this.solver.setEvent("end", (SolverEvent<String>) (String data) -> {
            this.lastPassedTime = System.currentTimeMillis() - this.lastStartTime;
            logToTextArea("Futás vége. Idő: " + this.lastPassedTime / 1000.000 + " másodperc");
            logToTextArea(data);
        });
        this.solver.setEvent("error", (SolverEvent<String>) (String data) -> {
            logToTextArea("A fejtő hibát eszlelt!");
            logToTextArea(data);
        });
        this.solver.setEvent("complete", (SolverEvent<String>) (String data) -> {
            logToTextArea("Megfejtve.");
            logToTextArea(data);
        });
        this.solver.setEvent("stopped", (SolverEvent<String>) (String data) -> {
            logToTextArea("Megállítva.");
            logToTextArea(data);
        });
        this.solver.setEvent("redraw", (SolverEvent<DrawingData>) (DrawingData data) -> {
            MainFXMLController.this.drawer.redraw(data);
        });
    }

    @FXML
    public Label label;
    @FXML
    public TextArea logger;
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
                this.setPuzzle(file);
            } catch (SolverException ex) {
                logToTextArea("A fájl megnyitása sikertelen volt [" + file.getName() + "]:\n " + ex.getMessage());
            } catch (IOException ex) {
                logToTextArea("A művelet során IO hiba történt [" + file.getName() + "]:\n " + ex.getMessage());
            } catch (Exception ex) {
                logToTextArea("Ismeretlen hiba [" + file.getName() + "]:\n " + ex.getMessage());
            }
        }
    }
    
    @FXML
    private void webImportAction(ActionEvent e){
        try{
            String id = this.webPbnId.getText();
            PuzzleRawData rawData = WEBImporter.importPuzzle(id);
            
            this.solver.setRawData(rawData);
            
            showFileData("--- WEBPBN.COM " + id + " ---"
                    + "\n< " + rawData.getSizeX() + " x " + rawData.getSizeY() + " >\n"
                    + rawData.getDescription());
            logToTextArea("File importálva: " + id);
        } catch(SolverException ex){
            logToTextArea("Importálás sikertelen\n " + ex.getMessage());
        } catch(IOException ex){
            logToTextArea("Importálás sikertelen\n " + ex.getMessage());
        }
    }

    @FXML
    private void startSolveAction(ActionEvent e) {
        try {
            this.drawer = this.showSolverWindow();
            this.solver.solve(this.enBackup.isSelected(), this.enPrior.isSelected());
        } catch (IOException | SolverException ex) {
            logToTextArea(ex.getMessage());
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
        this.solver.setRawData(data);

        showFileData("--- GENERÁLT REJTVÉNY ---"
                + "\n< " + data.getSizeX() + " x " + data.getSizeY() + " >\n");
        logToTextArea("Rejtvény importálva");
    }
    
    @FXML
    private void saveAsAction(ActionEvent e) {
    //    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Minden támogatott fájltípus", "*.xml", "*.non");
        FileChooser.ExtensionFilter xmlFilter = new FileChooser.ExtensionFilter("XML fájlok", "*.xml");
     //   FileChooser.ExtensionFilter nonFilter = new FileChooser.ExtensionFilter("Nonogram fájlok", "*.non");

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Fájl mentése...");
//        fileChooser.getExtensionFilters().add(filter);
//        fileChooser.getExtensionFilters().add(nonFilter);
        fileChooser.getExtensionFilters().add(xmlFilter);

        File file = fileChooser.showSaveDialog(((Node) e.getSource()).getScene().getWindow());

        if (file != null && this.solver.isValidPuzzle()) {
            try {
                XMLFileWriter fw = new XMLFileWriter(this.solver.getRawData());
                fw.write(file);
                logToTextArea("Fájl elmentve: " + file.getName());
            } catch (IOException | TransformerException | ParserConfigurationException ex) {
                logToTextArea("A fájl mentése sikertelen volt [" + file.getName() + "] :\n " + ex.getMessage());
            }
        }
    }

    private PuzzleDrawer showSolverWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/SolverGrid.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        SolverGridFXMLController controller = fxmlLoader.<SolverGridFXMLController>getController();
        
        PuzzleDrawer pDrawer = controller.getDrawer();
        
        if (this.showNumbers.isSelected()) {
            pDrawer.setNumbers(this.solver.getRawData());
            pDrawer.setDrawNumbers(true);
        }
        if (this.showGrid.isSelected()) {
            pDrawer.setDrawGrid(true);
        }
        
        int maxW, maxH;
        try {
            maxW = Integer.parseInt(this.winMaxW.getText());
            maxH = Integer.parseInt(this.winMaxH.getText());
        } catch (NumberFormatException e) {
            maxW = MainFXMLController.MAXW;
            maxH = MainFXMLController.MAXH;
        }
        
        if(maxW < MINW){
            maxW = MINW;
        }
        if(maxH < MINH){
            maxH = MINH;
        }
        
        pDrawer.setSizes(
                this.solver.getSizeX(),
                this.solver.getSizeY(),
                maxW, maxH);
        controller.setCanvasSizes(
                pDrawer.getFullWidth(),
                pDrawer.getFullHeight()
            );

        Stage stage = new Stage();
        stage.setTitle("Nonogram fejtő");
        Scene scene = new Scene(root, pDrawer.getFullWidth(), pDrawer.getFullHeight());
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest((e) -> {
            solver.stopSolving();
        });
        
        return pDrawer;
    }

    private void setPuzzle(File file) throws IOException, SolverException, Exception{
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
        this.solver.setRawData(rawData);
        showFileData("--- " + file.getName() + " ---"
                + "\n< " + rawData.getSizeX() + " x " + rawData.getSizeY() + " >\n"
                + rawData.getDescription());
        logToTextArea("File beolvasva: " + file.getName());
    }

    private void showFileData(String str) {
        if (str != null) {
            this.label.setText(str);
        } else {
            this.label.setText("--- Nincs megnyitva fájl ---");
        }

    }

    private void logToTextArea(String str) {
        if(str != null && !str.equals("")){
            this.logger.appendText("\n> " + str);
        }
    }
}
