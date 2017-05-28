package hu.unideb.inf.nonogramsolver.Model.Readers;

import hu.unideb.inf.nonogramsolver.Model.PuzzleRawData;
import hu.unideb.inf.nonogramsolver.Model.SolverException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Nonogram (*.non) fájlformátum-olvasó.
 * @author wazemaki
 */
public class NONReader implements nonogramReader {

    /**
     * Nyers puzzle-adatok.
     */
    private final PuzzleRawData puzzle;
    /**
     * Az olvasandó <code>{@link File}</code> objektum.
     */
    private final File file;

    /**
     * Konstruktor.
     * @param file A nonogram file-objektum.
     */
    public NONReader(File file) {
        this.file = file;
        this.puzzle = new PuzzleRawData();
    }

    /**
     * A nonogram file olvasása <code>{@link PuzzleRawData}</code> objektumba.
     * @return <code>{@link PuzzleRawData}</code> objektum, mely tartalmazza a beolvasott rejtvényt.
     * @throws IOException Valamilyen I/O hiba lépett fel.
     * @throws SolverException Formailag hibás a rejtvény.
     */
    @Override
    public PuzzleRawData read() throws IOException, SolverException {
        BufferedReader is = new BufferedReader(new FileReader(this.file));
        return this.readNONStream(is, ",");
    }

    /**
     * NON formátumot tartalmazó stream olvasása <code>{@link PuzzleRawData}</code> objektumba.
     * @param br Pufferelt stream, melyből olvas.
     * @param delimiter A számokat elválasztó karakter.
     * @return <code>{@link PuzzleRawData}</code> objektum, mely tartalmazza a beolvasott rejtvényt.
     * @throws IOException Valamilyen I/O hiba lépett fel.
     * @throws SolverException Formailag hibás a rejtvény.
     */
    private PuzzleRawData readNONStream(BufferedReader br, String delimiter) throws IOException, SolverException {
        int part = 0;
        for (String line; (line = br.readLine()) != null;) {

            if (line.equals("")) {
                continue;
            }
            if (line.equals("rows")) {
                part = 1;
                continue;
            } else if (line.equals("columns")) {
                part = 2;
                continue;
            }
            if (part != 0) {
                try {
                    List<Integer> numbers = new ArrayList<>();
                    for (String s : line.split(delimiter)) {
                        numbers.add(Integer.parseInt(s));
                    }
                    if (part == 2) {
                        this.puzzle.addCol(numbers);
                    } else {
                        this.puzzle.addRow(numbers);
                    }
                } catch (NumberFormatException e) {
                    throw new SolverException("Nem várt karakter",0);
                }
            } else {
                this.puzzle.appendDescription(line, true);
            }
        }
        return this.puzzle;
    }
}
