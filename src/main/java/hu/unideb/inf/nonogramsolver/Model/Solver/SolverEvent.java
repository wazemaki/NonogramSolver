package hu.unideb.inf.nonogramsolver.Model.Solver;

/**
 *
 * @author wazemaki
 * @param <T>
 */
public interface SolverEvent<T>{

    public void run(T data);

}
