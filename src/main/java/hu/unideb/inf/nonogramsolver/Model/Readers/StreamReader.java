package hu.unideb.inf.nonogramsolver.Model.Readers;

import hu.unideb.inf.nonogramsolver.Model.PuzzleRawData;
import hu.unideb.inf.nonogramsolver.Model.Solver.SolverException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wazemaki
 */
public class StreamReader implements nonogramReader{
    
    private final BufferedReader bReader;
    
    public StreamReader(BufferedReader br){
        this.bReader = br;
    }
    
    @Override
    public PuzzleRawData read() throws IOException, SolverException {
        PuzzleRawData puzzle = new PuzzleRawData();
        int part = 0; // 0 - nem erdekes, 1 - sorok, 2 - oszlopok
        Integer intNum;
        for(String line; (line = this.bReader.readLine()) != null; ) {
            
            if(line.equals("")){
                continue;
            }
            if(line.equals("rows")){
                part = 1; //sorok kovetkeznek..
                continue;
            }
            else if(line.equals("columns")){
                part = 2; //oszlopok kovetkeznek
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
                    throw new SolverException("Nemvárt karakter",0);
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
