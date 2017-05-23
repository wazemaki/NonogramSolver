package hu.unideb.inf.nonogramsolver.Model;

import hu.unideb.inf.nonogramsolver.Model.Solver.SolverException;
import hu.unideb.inf.nonogramsolver.Model.Readers.StreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author wazemaki
 */
public class WEBImporter{ 
    
    public static final String URI = "http://webpbn.com/export.cgi";
    
    public static PuzzleRawData importPuzzle(String id) throws IOException, SolverException {
        
        URL url = new URL(URI + "?go=1&fmt=ss&id=" + id);
        
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
        con.setRequestProperty( "charset", "utf-8");
        con.setDoOutput(true);
        
        InputStream is = con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StreamReader reader = new StreamReader(br);
        
        PuzzleRawData puzzle = reader.read();
        
        if(puzzle.isEmpty()){
            throw new SolverException(puzzle.getDescription(), 4);
        }
        
        return puzzle;
    }
}
