package hu.unideb.inf.nonogramsolver.Model.Drawing;

/**
 *
 * @author wazemaki
 */
public final class DrawingData {
    public int activeIndex;
    public boolean activeIsRow;
    public int[][] gridData;
    
    public void setData(int[][] gridDt, boolean isRow, int activeInd){
        this.activeIndex = activeInd;
        this.activeIsRow = isRow;
        this.gridData = gridDt;
    }
}
