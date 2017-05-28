package unit;

import hu.unideb.inf.nonogramsolver.Model.PuzzleRawData;
import hu.unideb.inf.nonogramsolver.Model.Readers.XMLReader;
import hu.unideb.inf.nonogramsolver.Model.Solver.PuzzleBackup;
import hu.unideb.inf.nonogramsolver.Model.Solver.Solver;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author wazemaki
 */
public class SolverTest {
    
    private final Solver solver;
    
    public SolverTest() throws Exception {
        XMLReader reader = new XMLReader(new File(getClass().getResource("/tester.xml").toURI()));
        PuzzleRawData rawData = reader.read();
        this.solver = new Solver(rawData.getCols(), rawData.getRows(), null, true, true);
    }
    
    @BeforeClass
    public static void setUpClass(){
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.solver.setUpSlaveArrays();
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void initializeTest() {
        solver.setIsRow(true);
        assertEquals("Empty initialized Row", "---------", solver.print(5, null));
    }
    
    @Test
    public void fixRowsTest() {
        this.solver.fixAll();
        solver.setIsRow(true);
        assertEquals("Fixed Row - 1", "IIIIIIIII", solver.print(1, null));
        assertEquals("Fixed Row - 2", "II-IIIIII", solver.print(2, null));
        assertEquals("Fixed Row - 3", "I-I-I-I-I", solver.print(3, null));
        assertEquals("Fixed Row - 4", "----I----", solver.print(7, null));

    //    assertArrayEquals("Fixed Row - 7", new int[]{-1,-1,-1,-1,1,-1,-1,-1,-1}, solver.getLine(7, -2, -2).getData());
    
    }
    
    @Test
    public void priorSelectTest() {
        this.solver.fixAll();
        solver.setIsRow(true);
        assertEquals("Priority - with emptyRows", 0, solver.selectByPrior(false, true));
        assertEquals("Priority - without emptyRows", 2, solver.selectByPrior(false, false));
    }
    
    @Test
    public void findBlocksInRowsTest() {
        solver.setIsRow(true);
        solver.findBlocksInRow(0);
        assertEquals("Full White Row", "OOOOOOOOO", solver.print(0, null));
        solver.findBlocksInRow(1);
        assertEquals("Full Black Row", "IIIIIIIII", solver.print(1, null));
        solver.findBlocksInRow(2);
        assertEquals("Row with one white", "IIOIIIIII", solver.print(2, null));
        solver.findBlocksInRow(3);
        assertEquals("Checked Row", "IOIOIOIOI", solver.print(3, null));
        solver.findBlocksInRow(6);
        assertEquals("Failed Row", "---------", solver.print(6, null));
        solver.findBlocksInRow(7);
        assertEquals("Just a little bit", "----I----", solver.print(7, null));
    }
    
    @Test
    public void findBlocksInColsTest() {
        solver.fixAll();
        solver.setIsRow(false);
        solver.findBlocksInCol(0);
        assertEquals("First number", "OIIIO---", solver.print(0, null));
        solver.findBlocksInCol(1);
        assertEquals("First number - 2", "OIIO----", solver.print(1, null));
        solver.findBlocksInCol(2);
        assertEquals("First two number", "OIOIO---", solver.print(2, null));
    }
    
    @Test
    public void backUpTest() {
        solver.fixAll();
        solver.setIsRow(true);
        assertEquals("Fixed", "II-IIIIII", solver.print(2, null));
        solver.backUp = new PuzzleBackup(solver);
        solver.clearGrid();
        assertEquals("Cleaned empty", "---------", solver.print(2, null));
        solver.backUpCopy();
        assertEquals("Restored", "II-IIIIII", solver.print(2, null));
    }
    
    @Test
    public void runTest() {
        solver.run();
        
        solver.setIsRow(true);
        assertEquals("Final Row - 4", "OIOIOIOIO", solver.print(4, null));
        assertEquals("Final Row - 5", "OOOOOOIOI", solver.print(5, null));
        assertEquals("Final Row - 6", "IOOOOOOIO", solver.print(6, null));
        assertEquals("Final Row - 7", "OIIIIIOOO", solver.print(7, null));
    }
    
}
