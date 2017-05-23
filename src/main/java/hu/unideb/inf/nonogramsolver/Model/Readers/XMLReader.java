package hu.unideb.inf.nonogramsolver.Model.Readers;

import hu.unideb.inf.nonogramsolver.Model.PuzzleRawData;
import hu.unideb.inf.nonogramsolver.Model.Solver.SolverException;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.SAXException;

/**
 *
 * @author wazemaki
 */
public class XMLReader implements nonogramReader{
    private final PuzzleRawData puzzle;
    private final File file;
    
    private final static String ERRMSG = "Érvénytelen nonogram XML fájl";
    
    public XMLReader(File file){
        this.file = file;
        this.puzzle = new PuzzleRawData();
    }
    
    @Override
    public PuzzleRawData read() throws ParserConfigurationException, IOException, SAXException, SolverException {
        DocumentBuilderFactory dbFactory
                = DocumentBuilderFactory.newInstance();

        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Document doc = dBuilder.parse(this.file);
        doc.getDocumentElement().normalize();
        Element rootElement = doc.getDocumentElement();
        
        if(!rootElement.getNodeName().equalsIgnoreCase("puzzleset")){
            throw new SolverException(ERRMSG,0);
        }

        NodeList puzzleElems = rootElement.getElementsByTagName("puzzle");
        if(puzzleElems.getLength() != 1){
            throw new SolverException(ERRMSG,0);
        }
        NodeList cluesElements = ((Element)puzzleElems.item(0)).getElementsByTagName("clues");
        if(cluesElements.getLength() != 2){
            throw new SolverException(ERRMSG,0);
        }

        readLines((Element) cluesElements.item(0));
        readLines((Element) cluesElements.item(1));
        readDescription(rootElement);
        
        return this.puzzle;
    }
    
    private void readLines(Element cluesElement) throws SolverException{
        
        NodeList lineElements = cluesElement.getElementsByTagName("line");
        List<Integer> array;
        try{
            for(int lineIndex = 0; lineIndex < lineElements.getLength(); lineIndex++){
                NodeList countElements = ((Element)lineElements.item(lineIndex)).getElementsByTagName("count");
                array = new ArrayList<>();
                for(int countIndex = 0; countIndex < countElements.getLength(); countIndex++){
                    array.add(Integer.parseInt(((Element)countElements.item(countIndex)).getTextContent()));
                }
                if(cluesElement.getAttribute("type").equalsIgnoreCase("columns")){
                    this.puzzle.addCol(array);
                } else {
                    this.puzzle.addRow(array);
                }
            }
        }catch(NumberFormatException e){
            throw new SolverException("Nem várt karakter",0);
        }
    }
    
    private void readDescription(Element element){
        Node title = element.getElementsByTagName("title").item(0);
        if(title != null){
            this.puzzle.appendDescription("Title: " + ((Element)title).getTextContent(), true);
        }
        Node author = element.getElementsByTagName("author").item(0);
        if(author != null){
            this.puzzle.appendDescription("Author: " + ((Element)author).getTextContent(), true);
        }
        Node copyright = element.getElementsByTagName("copyright").item(0);
        if(copyright != null){
            this.puzzle.appendDescription(((Element)copyright).getTextContent(), true);
        }
        Node description = element.getElementsByTagName("description").item(0);
        if(description != null){
            this.puzzle.appendDescription(((Element)description).getTextContent(), true);
        }
        Node note = element.getElementsByTagName("note").item(0);
        if(note != null){
            this.puzzle.appendDescription(((Element)note).getTextContent(), true);
        }
    }
}
