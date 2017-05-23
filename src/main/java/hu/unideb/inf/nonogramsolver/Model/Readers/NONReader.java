package hu.unideb.inf.nonogramsolver.Model.Readers;

import hu.unideb.inf.nonogramsolver.Model.PuzzleRawData;
import hu.unideb.inf.nonogramsolver.Model.Solver.SolverException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wazemaki
 */
public class NONReader implements nonogramReader {

    private final PuzzleRawData puzzle;
    private final File file;

    public NONReader(File file) {
        this.file = file;
        this.puzzle = new PuzzleRawData();
    }

    @Override
    public PuzzleRawData read() throws IOException, SolverException {
        BufferedReader is = new BufferedReader(new FileReader(this.file));
        return this.readNONStream(is, ",");
    }

    public PuzzleRawData readNONStream(BufferedReader br, String delimiter) throws IOException, SolverException {
        int part = 0; // 0 - nem erdekes, 1 - sorok, 2 - oszlopok
        for (String line; (line = br.readLine()) != null;) {

            if (line.equals("")) {
                continue;
            }
            if (line.equals("rows")) {
                part = 1; //sorok kovetkeznek..
                continue;
            } else if (line.equals("columns")) {
                part = 2; //oszlopok kovetkeznek
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
