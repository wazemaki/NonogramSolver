package hu.unideb.inf.nonogramsolver.Model.Drawing;

import hu.unideb.inf.nonogramsolver.Model.Drawing.DrawingData;
import hu.unideb.inf.nonogramsolver.Model.Solver.SolverEvent;
import java.util.concurrent.atomic.AtomicReference;
import javafx.animation.AnimationTimer;

/**
 *
 * @author wazemaki
 */
public abstract class CanvasRedrawTask extends AnimationTimer implements SolverEvent<DrawingData>{
    private final AtomicReference<DrawingData> data = new AtomicReference<>(null);
    
    @Override
    public void handle(long now) {
        DrawingData dataToDraw = data.getAndSet(null);
        if (dataToDraw != null) {
            redraw(dataToDraw);
        }
    }

    @Override
    public void run(DrawingData param) {
        data.set(param);
        start();
    }

    protected abstract void redraw(DrawingData data);
}