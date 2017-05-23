package hu.unideb.inf.nonogramsolver.Controller;

import hu.unideb.inf.nonogramsolver.Model.Drawing.CanvasRedrawTask;
import hu.unideb.inf.nonogramsolver.Model.Drawing.DrawingData;
import hu.unideb.inf.nonogramsolver.Model.Solver.Solver;
import hu.unideb.inf.nonogramsolver.Model.PuzzleRawData;
import hu.unideb.inf.nonogramsolver.Model.Solver.SolverEvent;
import hu.unideb.inf.nonogramsolver.Model.Solver.SolverException;
/**
 *
 * @author wazemaki
 */
public class SolverController {
    
    private SolverEvent<String> startEvent;
    private SolverEvent<String> endEvent;
    private SolverEvent<String> errorEvent;
    private SolverEvent<String> completeEvent;
    private SolverEvent<String> stoppedEvent;
    private CanvasRedrawTask redrawEvent;
    
    private final DrawingData drawData;
    private Solver puzzle;
    private Thread thread;
    private PuzzleRawData rawData;
    
    public SolverController(){
        this.drawData = new DrawingData();
        this.rawData = new PuzzleRawData();
    }
    
    public boolean setEvent(String eventName, SolverEvent event){
        switch(eventName){
            case "start":
                this.startEvent = event;
                return true;
            case "end":
                this.endEvent = event;
                return true;
            case "error":
                this.errorEvent = event;
                return true;
            case "complete":
                this.completeEvent = event;
                return true;
            case "stopped":
                this.stoppedEvent = event;
                return true;
            case "redraw":
                this.redrawEvent = new CanvasRedrawTask() {
                    @Override
                    protected void redraw(DrawingData data) {
                        event.run(data);
                    }
                };
                return true;
        }
        return false;
    }
    
    public void setRawData(PuzzleRawData rawData){
        this.rawData = rawData;
    }
    
    public boolean isValidPuzzle(){
        return !(this.rawData == null || this.rawData.isEmpty());
    }
    
    public PuzzleRawData getRawData(){
        return this.rawData;
    }
    
    public int getSizeX(){
        return this.rawData.getSizeX();
    }
    
    public int getSizeY(){
        return this.rawData.getSizeY();
    }
    
    public void solve(boolean enBackup, boolean enPrior) throws SolverException{
        if(!this.checkIsFree()){
            throw new SolverException("A fejtő foglalt.\n  Várjuk meg, amíg befejezi, vagy állítsuk meg!",3);
        }
        if(!this.isValidPuzzle()){
            throw new SolverException("Érvénytelen rejtvény, valószínűleg sorok,vagy oszlopok hiányoznak.",2);
        }
        this.puzzle = new Solver(this.rawData.getCols(),this.rawData.getRows(),this,enBackup,enPrior);
        this.thread = new Thread (this.puzzle, "Solving");
        this.thread.start();
    }
    
    public Solver getSolver(){
        return this.puzzle;
    }
    
    public boolean checkIsFree(){
        return (this.thread == null || !this.thread.isAlive());
    }
    
    public void stopSolving(){
        if(this.thread != null && this.puzzle != null){
            this.puzzle.stop();
        }
        this.thread = null;
    }
    
    public void callOnComplete(String p){
        if(this.completeEvent != null){
            this.completeEvent.run(p);
        }
    }
    
    public void callOnError(String p){
        if(this.errorEvent != null){
            this.errorEvent.run(p);
        }
    }
    
    public void callOnStart(String p){
        if(this.startEvent != null){
            this.startEvent.run(p);
        }
    }
    
    public void callOnStopped(String p){
        if(this.stoppedEvent != null){
            this.stoppedEvent.run(p);
        }
    }
    
    public void callOnRedraw(){
        if(this.redrawEvent != null && this.puzzle != null){
            this.drawData.setData(this.puzzle.getGrid(), this.puzzle.isRow, this.puzzle.getActive());
            this.redrawEvent.run(this.drawData);
        }
    }
    
    public void callOnEnd(String p){
        if(this.endEvent != null){
            this.endEvent.run(p);
        }
        this.thread = null;
    }
}
