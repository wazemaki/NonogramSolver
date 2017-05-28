package hu.unideb.inf.nonogramsolver.Model;

import hu.unideb.inf.nonogramsolver.Model.Readers.StreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Internetről importál rejtvényeket.
 * A rejtvényt a <a href="http://webpbn.com">webpbn.com</a> oldalról importálja,
 * egy kifejezettem erre alkalmas felületen, GET-kérések segítségével.
 * Az oldalról {@code .non} formában kerülnek letöltésre a rejtvények.
 * Egy-egy rejtvény azonosítójának megszerzéséhez látogasson el a feltüntetett oldalra.
 * A WEbPBN exportáló felülete, további információkkal együtt itt található:
 * <a href="http://webpbn.com/export.cgi">webpbn.com/export.cgi</a>
 * Jelen program semmilyen formában nem terjeszti; nem adja tovább az innen importált rejtvényeket.
 * Az importálás kizárólag tanulmányi célra van használva!
 * @author wazemaki
 */
public class WEBImporter{ 
    
    /**
     * Az importáló statikus címe.
     */
    private static final String URI = "http://webpbn.com/export.cgi";
    
    /**
     * Importál.
     * @param id A WebPBN adatbázis-beli azonosító
     * @return <code>{@link PuzzleRawData}</code> objektum, amelyet az importáló készít a webes adatok alapján.
     * @throws IOException I/O hiba lépett fel.
     * @throws SolverException Amennyiben nem sikerül az importálás, az oldal hibaüzenetei a kivételben olvashatók.
     */
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