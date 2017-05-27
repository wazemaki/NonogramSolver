package hu.unideb.inf.nonogramsolver.Model.Drawing;

import hu.unideb.inf.nonogramsolver.Model.Solver.SolverEvent;
import java.util.concurrent.atomic.AtomicReference;
import javafx.animation.AnimationTimer;

/**
 * Újrarajzolási feladat.
 * Az <code>{@link AnimationTimer}</code> osztályt terjeszti ki, így az újrarajzolás csak akkor valósul meg,\
 * ha a timer elérte a kirajzolás időintervallum végét.
 * A <code>{@link SolverEvent}</code> osztályt implementálja, így példányai gyakorlatilag fejtő-események lesznek.
 * @author wazemaki
 */
public abstract class RedrawTask extends AnimationTimer implements SolverEvent<DrawingData>{
    private final AtomicReference<DrawingData> data = new AtomicReference<>(null);
    
    @Override
    public void handle(long now) {
        DrawingData dataToDraw = data.getAndSet(null);
        if (dataToDraw != null) {
            redraw(dataToDraw);
        }
    }

    /**
     * Rajzolási kérelmet küld.
     * @param param
     */
    @Override
    public void run(DrawingData param) {
        data.set(param);
        start();
    }

    /**
     * A rajzolási esemény.
     * @param data A rajzolandó adatok objektuma.
     */
    protected abstract void redraw(DrawingData data);
}