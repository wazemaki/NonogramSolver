package hu.unideb.inf.nonogramsolver.Model.Writers;

import hu.unideb.inf.nonogramsolver.Model.PuzzleRawData;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <code>{@link PuzzleRawData}</code> objektumot ment XML fájlba.
 * @author wazemaki
 */
public class XMLFileWriter {

    private final PuzzleRawData rawData;

    /**
     * Konstruktor.
     * @param data Elmentendő <code>{@link PuzzleRawData}</code> objektum
     */
    public XMLFileWriter(PuzzleRawData data) {
        this.rawData = data;
    }

    /**
     * Fájl összeállítása és írása.
     * @param file A mentendő <code>{@link File}</code> objektum.
     * @throws IOException
     * @throws TransformerException
     * @throws ParserConfigurationException
     */
    public void write(File file) throws IOException, TransformerException, ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        Element rootElement = doc.createElement("puzzleset");
        doc.appendChild(rootElement);

        Element puzzle = doc.createElement("puzzle");
        rootElement.appendChild(puzzle);

        puzzle.setAttribute("type", "grid");
        puzzle.setAttribute("defaultcolor", "black");

        Element cols = doc.createElement("clues");
        puzzle.appendChild(cols);
        cols.setAttribute("type", "columns");

        for (List<Integer> lIter : this.rawData.getCols()) {
            Element line = doc.createElement("line");
            for (Integer number : lIter) {
                Element count = doc.createElement("count");
                count.appendChild(doc.createTextNode(number.toString()));
                line.appendChild(count);
            }
            cols.appendChild(line);
        }

        Element rows = doc.createElement("clues");
        puzzle.appendChild(rows);
        rows.setAttribute("type", "rows");

        for (List<Integer> lIter : this.rawData.getRows()) {
            Element line = doc.createElement("line");
            for (Integer number : lIter) {
                Element count = doc.createElement("count");
                count.appendChild(doc.createTextNode(number.toString()));
                line.appendChild(count);
            }
            rows.appendChild(line);
        }

        Element count = doc.createElement("count");

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    }
}
