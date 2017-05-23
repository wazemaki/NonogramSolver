package hu.unideb.inf.nonogramsolver.Model.Drawing;

import hu.unideb.inf.nonogramsolver.Model.PuzzleRawData;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 *
 * @author wazemaki
 */
public class PuzzleDrawer {
    private final GraphicsContext gc;
    private boolean drawGrid;
    private boolean drawNumbers;
    
    private int width, height, gridSize, startX, startY, xOffset, yOffset;
    private int fullWidthPx, fullHeightPx, xOffsetPx, yOffsetPx;
    
    private PuzzleRawData numbers;
    
    public PuzzleDrawer(GraphicsContext gc){
        this.gc = gc;
    }
    
    public void setDrawNumbers(boolean isset){
        this.drawNumbers = isset;
    }
    
    public void setDrawGrid(boolean isset){
        this.drawGrid = isset;
    }
    
    public void setNumbers(PuzzleRawData numbers){
        this.numbers = numbers;
    }
    
    public int getFullWidth(){
        return this.fullWidthPx;
    }
    
    public int getFullHeight(){
        return this.fullHeightPx;
    }
    
    public void setSizes(int w, int h, int maxW, int maxH){
        this.width = w;
        this.height = h;
                
        if(this.drawNumbers && this.numbers != null){
            this.drawNumbers = true;
            this.xOffset = this.numbers.getMaxRowSize();
            this.yOffset = this.numbers.getMaxColSize();
        }
        
        int fullW = this.width + this.xOffset;
        int fullH = this.height + this.yOffset;
        
        float hProp = (float)(maxH / fullH);
        float wProp = (float)(maxW / fullW);
        
        this.gridSize = (hProp > wProp) ? (int) wProp : (int) hProp;
        
        this.fullWidthPx = fullW * this.gridSize;
        this.fullHeightPx = fullH * this.gridSize;
        
        this.xOffsetPx = this.xOffset * this.gridSize;
        this.yOffsetPx = this.yOffset * this.gridSize;
        
    }
    
    public boolean redraw(DrawingData toDraw) {
        gc.setFill(Color.LIGHTGREY);
        gc.fillRect(0, 0, this.gc.getCanvas().getWidth(), this.gc.getCanvas().getHeight());
        
        boolean needDraw;

        for(int x = 0; x < toDraw.gridData.length; x++){
            for(int y = 0; y < toDraw.gridData[x].length; y++){

                int coordX = startX + xOffsetPx + x * gridSize;
                int coordY = startY + yOffsetPx + y * gridSize;

                needDraw = false;
                switch(toDraw.gridData[x][y]){
                    case 0:
                        gc.setFill(Color.WHITE);
                        needDraw = true;
                        break;
                    case 1:
                        gc.setFill(Color.BLACK);
                        needDraw = true;
                        break;
                    default:
                        if((toDraw.activeIsRow && y == toDraw.activeIndex) ||
                            (!toDraw.activeIsRow && x == toDraw.activeIndex)){
                            gc.setFill(Color.RED);
                            needDraw = true;
                        }
                }
                if(needDraw){
                    gc.fillRect(coordX, coordY, gridSize, gridSize);
                }
            }
        }
        if(this.drawGrid){
            this.drawGrid();
        }
        if(this.drawNumbers && this.numbers != null){
            this.drawNumbers();
        }
        return true;
    }
    
    public void drawGrid(){
        gc.setLineWidth(1);
        gc.setStroke(Color.GREY);
        for(int i = 0; i <= this.width; i++){ //fuggo vonalak
            int coordX = this.startX + xOffsetPx + i * this.gridSize;
            gc.strokeLine(coordX, this.startY, coordX, this.startY + this.fullHeightPx);
        }
        
        for(int i = 0; i <= this.height; i++){ //vizsz vonalak
            int coordY = this.startY + yOffsetPx + i * this.gridSize;
            gc.strokeLine(this.startX, coordY, this.startX + this.fullWidthPx, coordY);
        }
    }
    
    public void drawNumbers(){
        gc.setFill(Color.BLACK);
        gc.setFont(new Font( this.gridSize * 0.7));
        
        int colSize, rowSize;
        for(int col = 0; col < this.width; col++){
            colSize = this.numbers.getCols().get(col).size();
            for(int i = 0; i < colSize; i++){
                gc.fillText(this.numbers.getNumberFromCol(col,i).toString(),
                    this.startX + this.xOffsetPx + col * this.gridSize,
                    this.startY + yOffsetPx + (i + 1 - colSize) * this.gridSize );
            }
        }
        
        for(int row = 0; row < this.height; row++){
            rowSize = this.numbers.getRows().get(row).size();
            for(int i = 0; i < rowSize; i++){
                gc.fillText(this.numbers.getNumberFromRow(row,i).toString(), 
                    this.startX + xOffsetPx + (i - rowSize) * gridSize,
                    this.startY + this.yOffsetPx + (row + 1) * this.gridSize);
            }
        }
    }
}
