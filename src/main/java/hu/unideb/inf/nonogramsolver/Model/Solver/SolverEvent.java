package hu.unideb.inf.nonogramsolver.Model.Solver;

/**
 * Fejtő esemény.
 * @author wazemaki
 * @param <T> Az esemény futtatásánál átadott paraméter típusa.
 */
public interface SolverEvent<T>{
   /**
     * Az események lehetséges típusai
     */
    public static final byte 
            EVENT_START = 1,
            EVENT_END = 2,
            EVENT_ERROR = 3,
            EVENT_COMPLETE = 4,
            EVENT_STOP = 5,
            EVENT_REDRAW = 6;

    /**
     * Az esemény indítása.
     * @param data A paraméter, amely átadódik az eseménynek.
     */
    public void run(T data);

}
