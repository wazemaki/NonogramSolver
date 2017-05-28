package hu.unideb.inf.nonogramsolver.Model.Readers;

import hu.unideb.inf.nonogramsolver.Model.PuzzleRawData;
import hu.unideb.inf.nonogramsolver.Model.SolverException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>{@link BufferedReader}</code> objektum-olvasó.
 * @author wazemaki
 */
public class StreamReader implements nonogramReader{
    
    /**
     * Olvasó.
     */
    private final BufferedReader bReader;
    
    /**
     * Konstruktor.
     * @param br <code>{@link BufferedReader}</code> objektum, ahonnan olvas.
     */
    public StreamReader(BufferedReader br){
        this.bReader = br;
    }
    
    /**
     * A stream file olvasása <code>{@link PuzzleRawData}</code> objektumba.
     * @return <code>{@link PuzzleRawData}</code> objektum, mely tartalmazza a beolvasott rejtvényt.
     * @throws IOException Valamilyen I/O hiba lépett fel.
     * @throws SolverException Formailag hibás a rejtvény.
     */
    @Override
    public PuzzleRawData read() throws IOException, SolverException {
        PuzzleRawData puzzle = new PuzzleRawData();
        int part = 0;
        Integer intNum;
        for(String line; (line = this.bReader.readLine()) != null; ) {
            
            if(line.equals("")){
                continue;
            }
            if(line.equals("rows")){
                part = 1;
                continue;
            }
            else if(line.equals("columns")){
                part = 2;
                continue;
            }
            if(part != 0){
                List<Integer> numbers = new ArrayList<>();
                try{
                    for (String s : line.split(",")){
                        intNum = Integer.parseInt(s);
                        numbers.add(intNum);
                    }
                }catch(NumberFormatException e){
                    throw new SolverException("Nem várt karakter",0);
                }
                if(part == 2){
                    puzzle.addCol(numbers);
                }
                else {
                    puzzle.addRow(numbers);
                }
            } else {
                puzzle.appendDescription(line,true);
            }
        }
        return puzzle;
    }
}
