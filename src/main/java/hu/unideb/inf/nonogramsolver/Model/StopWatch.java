package hu.unideb.inf.nonogramsolver.Model;

/**
 *
 * @author wazemaki
 */
public class StopWatch {
    private long lastStartTime;
    private long lastPassedTime;
    
    /**
     *
     */
    public void start(){
        this.lastStartTime = System.currentTimeMillis();
    }
    
    /**
     *
     */
    public void stop(){
        this.lastPassedTime = System.currentTimeMillis() - this.lastStartTime;
    }
    
    /**
     *
     * @return
     */
    public double getPassedTime(){
        return this.lastPassedTime / 1000.00;
    }
}
