package hu.unideb.inf.nonogramsolver.Controller.GUI;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.logging.LogRecord;
import javafx.scene.control.TextArea;

/**
 * A grafikus felhasználói felület <code>{@link TextArea}</code> elemébe író log handler.
 * @author wazemaki
 */
public class LogToTextAreaHandler extends java.util.logging.Handler {

    private final TextArea textArea;
    
    /**
     * Konstruktor.
     * @param ta <code>{@link TextArea}</code> elem, amelybe a handler ír.
     */
    public LogToTextAreaHandler(TextArea ta){
        this.textArea = ta;
    }

    @Override
    public void publish(final LogRecord record) {
        StringWriter text = new StringWriter();
        PrintWriter out = new PrintWriter(text);
        out.printf("\n > [%s]: %s", record.getLevel(), MessageFormat.format(record.getMessage(), record.getParameters()));
        this.textArea.appendText(text.toString());
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}
