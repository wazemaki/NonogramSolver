package hu.unideb.inf.nonogramsolver.Model.Drawing;

/**
 * Példányai a <code>{@link PuzzleDrawer}</code> által ismert, rajzolható objektumok.
 * @author wazemaki
 */
public final class DrawingData {

    /**
     * Az aktuális index értéket tárolja (piros vonal rajzolásához)
     */
    public int activeIndex;

    /**
     * Annak jelzésére szolgál, hogy az aktív index sort, vagy oszlopot jelöl.
     * Igaz{@code true}, ha sort jelöl.
     * Hamis{@code false}, ha oszlopot jelöl.
     */
    public boolean activeIsRow;

    /**
     * A rajzolandó adatokat tárolja négyzetrácsonként.
     */
    public int[][] gridData;
    
    /**
     * Beállítja a rajzolandó adatokat.
     * @param gridDt Rajzolandó négyzetrács
     * @param isRow Igaz({@code true}): az aktív index sort jelöl.
     * Hamis({@code false}): az aktív index oszlopot jelöl.
     * @param activeInd Aktuális index
     */
    public void setData(int[][] gridDt, boolean isRow, int activeInd){
        this.activeIndex = activeInd;
        this.activeIsRow = isRow;
        this.gridData = gridDt;
    }
}
