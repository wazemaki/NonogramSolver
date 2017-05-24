package hu.unideb.inf.nonogramsolver.Controller;

import hu.unideb.inf.nonogramsolver.Model.Solver.SolverEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wazemaki
 */
public class MainController {
    
    protected SolverController solverController = new SolverController();
    
    protected static final Logger LOGGER = Logger.getLogger( MainController.class.getName() );

    protected long lastStartTime;
    protected long lastPassedTime;
    
    public void setSolverEvents() {
        this.solverController.setEvent("start", (SolverEvent<String>) (String data) -> {
            this.lastStartTime = System.currentTimeMillis();
            LOGGER.info("Start...");
        });
        this.solverController.setEvent("end", (SolverEvent<String>) (String data) -> {
            this.lastPassedTime = System.currentTimeMillis() - this.lastStartTime;
            LOGGER.log(Level.INFO, "Futás vége. Idő: {0} másodperc", this.lastPassedTime / 1000.000);
        });
        this.solverController.setEvent("error", (SolverEvent<String>) (String data) -> {
            LOGGER.log(Level.SEVERE, "A fejtő hibát eszlelt: \n{0}", data);
        });
        this.solverController.setEvent("complete", (SolverEvent<String>) (String data) -> {
            LOGGER.log(Level.INFO, "Megfejtve.");
        });
        this.solverController.setEvent("stopped", (SolverEvent<String>) (String data) -> {
            LOGGER.log(Level.INFO,"Megállítva.");
        });
    }
}
