package unit;

import hu.unideb.inf.nonogramsolver.Model.Solver.Line;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author wazemaki
 */
public class LineTest {
    
    private Line line;
    
    @BeforeClass
    public static void setUpClass(){
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.line = new Line(9);
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void initializeTest() {
        assertEquals("Initialized", "---------", line.print(null));
    }
    
    @Test
    public void setUpTest() {
        line.setByArray(new int[] {1,1,1,1,1,1,1,1,1}, true);
        assertEquals("Set with blacks", "IIIIIIIII", line.print(null));
    }
    
    @Test
    public void logicAndTest() {
        line.setByArray(new int[] {-1,-1,-1,0,0,0,1,1,1}, true);
        Line line2 = new Line(9).setByArray(new int[] {-1,0,1,-1,0,1,-1,0,1}, true);
        
        assertEquals("AND whites", "----O----", line.logic_AND(line2, 0).print(null));
        line.setByArray(new int[] {-1,-1,-1,0,0,0,1,1,1}, true);
        assertEquals("AND blacks", "--------I", line.logic_AND(line2, 1).print(null));
        line.setByArray(new int[] {-1,-1,-1,0,0,0,1,1,1}, true);
        assertEquals("AND unset", "---------", line.logic_AND(line2, -1).print(null));
    }
    
    @Test
    public void logicOrTest() {
        line.setByArray(new int[] {-1,-1,-1,0,0,0,1,1,1}, true);
        Line line2 = new Line(9).setByArray(new int[] {-1,0,1,-1,0,1,-1,0,1}, true);
        
        assertEquals("OR whites", "-O-OOOIOI", line.logic_OR(line2, 0).print(null));
        line.setByArray(new int[] {-1,-1,-1,0,0,0,1,1,1}, true);
        assertEquals("OR blacks", "--IOOIIII", line.logic_OR(line2, 1).print(null));
        line.setByArray(new int[] {-1,-1,-1,0,0,0,1,1,1}, true);
        assertEquals("OR unset",  "----OO-II", line.logic_OR(line2, -1).print(null));
    }
}
