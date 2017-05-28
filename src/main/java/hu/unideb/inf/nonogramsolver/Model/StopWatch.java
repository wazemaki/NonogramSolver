package hu.unideb.inf.nonogramsolver.Model;

/**
 * A rejtvények fejtési idejének méréséhez használatos egyszerű stopper.
 * @author wazemaki
 */
public class StopWatch {
    /**
     * A legutólsó indítás időpontját tárolja MS-ben.
     */
    private long lastStartTime;
    /**
     * A legutólsó futás idejét tárolja MS-ben.
     */
    private long lastPassedTime;
    
    /**
     * Elindítja a stoppert.
     */
    public void start(){
        this.lastStartTime = System.currentTimeMillis();
    }
    
    /**
     * Megállítja a stoppert. Az utolsó indítás óta eltelt idő eltárolódik.
     */
    public void stop(){
        this.lastPassedTime = System.currentTimeMillis() - this.lastStartTime;
    }
    
    /**
     * Visszaadja a legutóbbi futási időt.
     * @return A futási idő másodpercben.
     */
    public double getPassedTime(){
        return this.lastPassedTime / 1000.00;
    }
}
