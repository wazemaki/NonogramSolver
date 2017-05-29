package hu.unideb.inf.nonogramsolver.Controller;

import hu.unideb.inf.nonogramsolver.Model.PuzzleRawData;
import hu.unideb.inf.nonogramsolver.Model.Readers.NONReader;
import hu.unideb.inf.nonogramsolver.Model.Readers.XMLReader;
import hu.unideb.inf.nonogramsolver.Model.Readers.nonogramReader;
import hu.unideb.inf.nonogramsolver.Model.SolverEvent;
import hu.unideb.inf.nonogramsolver.Model.SolverException;
import hu.unideb.inf.nonogramsolver.Model.StopWatch;
import hu.unideb.inf.nonogramsolver.Model.WEBImporter;
import hu.unideb.inf.nonogramsolver.Model.Writers.XMLFileWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * Main kontroller.
 * @author wazemaki
 */
public class MainController {
    
    /**
     * A fejtő vezérlőjének példánya.
     */
    protected SolverController solverController = new SolverController();
    
    /**
     * Logger példány.
     */
    protected static final Logger LOGGER = Logger.getLogger( MainController.class.getName() );

    /**
     * Stopper a futási idők méréséhez.
     */
    private final StopWatch stopwatch = new StopWatch();
    
    /**
     * A betöltött fájl információja.
     */
    private String fileInfo;
    
    /**
     * A betöltött nyers rejtvény.
     */
    private PuzzleRawData rawData;
    
    /**
     * A fejtő eseményeit állítja be.
     */
    protected void setSolverEvents() {
        this.solverController.setEvent(SolverEvent.EVENT_START, (SolverEvent<String>) (String data) -> {
            this.stopwatch.start();
            LOGGER.info("Start...");
        });
        this.solverController.setEvent(SolverEvent.EVENT_END, (SolverEvent<String>) (String data) -> {
            this.stopwatch.stop();
            LOGGER.log(Level.INFO, "Futás vége. Idő: {0} másodperc", this.stopwatch.getPassedTime());
        });
        this.solverController.setEvent(SolverEvent.EVENT_ERROR, (SolverEvent<String>) (String data) -> {
            LOGGER.log(Level.WARNING, "A fejtő hibát eszlelt: \n{0}", data);
        });
        this.solverController.setEvent(SolverEvent.EVENT_COMPLETE, (SolverEvent<String>) (String data) -> {
            LOGGER.log(Level.INFO, "Megfejtve.");
        });
        this.solverController.setEvent(SolverEvent.EVENT_STOP, (SolverEvent<String>) (String data) -> {
            LOGGER.log(Level.INFO,"Megállítva.");
        });
    }
    
    /**
     * Fájlból tölt be egy rejtvényt.
     * @param file A rejtvényt tartalmazó fájl objektuma
     */
    protected void loadPuzzleFromFile(File file){
        PuzzleRawData raw = null;
        String title = null;
        if (file != null) {
            try {
                nonogramReader reader = null;
                String mimeType = Files.probeContentType(file.toPath());
                switch (mimeType) {
                    case "text/plain":
                        reader = new NONReader(file);
                        break;
                    case "application/xml":
                    case "text/xml":
                        reader = new XMLReader(file);
                        break;
                    default:
                }

                if (reader == null) {
                    throw new SolverException("Nem támogatott fájltípus: " + mimeType, 1);
                }

                raw = reader.read();
                title = file.getName();
                
                LOGGER.log(Level.INFO, "File beolvasva: {0}", file.getName());
                
            } catch (SolverException ex) {
                LOGGER.log(Level.WARNING, "A fájl megnyitása sikertelen volt [{0}]:\n {1}", new Object[]{file.getName(), ex.getMessage()});
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "A művelet során IO hiba történt [{0}]:\n {1}", new Object[]{file.getName(), ex.getMessage()});
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Ismeretlen hiba [{0}]:\n {1}", new Object[]{file.getName(), ex.getMessage()});
            }
        }
        this.setRawData(raw, title);
    }
    
    /**
     * Internetről importál egy rejtvényt.
     * A rejtvényt a <a href="http://webpbn.com">webpbn.com</a> oldalról importálja.
     * @param id A <a href="http://webpbn.com">webpbn.com</a> azonosító
     */
    protected void webImport(String id) {
        PuzzleRawData raw = null;
        try {
            raw = WEBImporter.importPuzzle(id);
            LOGGER.log(Level.CONFIG, "File importálva: {0}", id);
        } catch (SolverException | IOException ex) {
            LOGGER.log(Level.WARNING, "Importálás sikertelen\n {0}", ex.getMessage());
        }
        this.setRawData(raw,"WEBPBN.COM " + id);
    }
    
    /**
     * A fájl információit tárolja szövegként.
     * @param title A rejtvény címe.
     */
    private void setFileInfo(String title) {
        if(this.rawData != null){
            this.fileInfo = "--- " + title + " ---"
                    + "\n< " + this.rawData.getSizeX() + " x " + this.rawData.getSizeY() + " >\n"
                    + this.rawData.getDescription();
        } else {
            this.fileInfo = "< Nincs megnyitva fájl >";
        }
    }
    
    /**
     * Az aktuálisan betöltött fájl információit adja.
     * Név, cím, méret, leírás, stb.
     * @return A fájl információi
     */
    protected String getFileInfo(){
        return this.fileInfo;
    }
    
    /**
     * A rejtvény objektumot állítja be.
     * @param rdata A rejtvény objektuma
     * @param title A rejtvény címe
     */
    protected void setRawData(PuzzleRawData rdata, String title){
        this.rawData = rdata;
        this.setFileInfo(title);
    }
    
    /**
     * A fejtés megkezdése.
     * @param enBackup Visszalépés(tippelés) engedélyezése
     * @param enPrior Prioritás szerinti sorválasztás engedélyezése.
     * @return Igaz({@code true}), ha a fejtő elindult.
     * Hamis({@code false}), ha valamilyen probléma lépett fel.
     */
    protected boolean startSolve(boolean enBackup, boolean enPrior){
        try{
            this.solverController.setRawData(this.rawData);
            this.solverController.solve(enBackup, enPrior);
        } catch (SolverException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
            return false;
        }
        return true;
    }
    
    /**
     * A betöltött rejtvény mentése.
     * @param file A fájl objektum, ahová mentünk.
     */
    protected void savePuzzle(File file){
        if (file != null && !this.rawData.isEmpty()) {
            try {
                XMLFileWriter fw = new XMLFileWriter(this.rawData);
                fw.write(file);
                LOGGER.log(Level.INFO, "Fájl elmentve: {0}", file.getName());
            } catch (IOException | TransformerException | ParserConfigurationException ex) {
                LOGGER.log(Level.WARNING, "A fájl mentése sikertelen volt [{0}] :\n {1}", new String[]{file.getName(), ex.getMessage()});
            }
        }
    }
    
}
