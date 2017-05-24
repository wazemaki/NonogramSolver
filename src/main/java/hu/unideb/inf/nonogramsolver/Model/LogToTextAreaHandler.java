package hu.unideb.inf.nonogramsolver.Model;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.LogRecord;
import javafx.scene.control.TextArea;

/**
 *
 * @author wazemaki
 */
public class LogToTextAreaHandler extends java.util.logging.Handler {

    private final TextArea textArea;
    
    public LogToTextAreaHandler(TextArea ta){
        this.textArea = ta;
    }

    @Override
    public void publish(final LogRecord record) {
        StringWriter text = new StringWriter();
        PrintWriter out = new PrintWriter(text);
        out.printf("\n > [%s]: %s", record.getLevel(), record.getMessage());
        this.textArea.appendText(text.toString());
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}
