package hu.unideb.inf.nonogramsolver.Model;

import hu.unideb.inf.nonogramsolver.Model.Drawing.DrawingData;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Nonogramot generál.
 * @author wazemaki
 */
public class NonGenerator {
    private BufferedImage MasterImage;
    private BufferedImage ResizedImage;
    private final DrawingData drawData;
    private int[][] data;
    
    private int height, width;
    private int threshold;
    private boolean invertCol;
    
    /**
     * Konstruktor.
     */
    public NonGenerator(){
        this.drawData = new DrawingData();
    }

    /**
     * A színek invertálását engedélyezi.
     * @param invertCol Igaz{@code true}: a színek invertálása.
     * Hamis{@code false}: az ereeti színek megtartása.
     */
    public void setInvertCol(boolean invertCol) {
        this.invertCol = invertCol;
    }

    /**
     * A küszöbszintet állítja be.
     * @param threshold A kívánt küszöbszint. (0 - 255)
     */
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    /**
     * A generált rejtvény szélességét adja vissza.
     * @return Szélesség
     */
    public int getHeight() {
        return height;
    }

    /**
     * A generált rejtvény magasságát adja vissza.
     * @return Magasság
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * A generálandó képet állítja be.
     * @param file A képfájl objektuma.
     * @throws IOException
     */
    public void setFile(File file) throws IOException{
        this.MasterImage = ImageIO.read(file);
    }
    
    /**
     * Átméretezett képet készít az eredeti képből.
     * @param size Az átméretezett kép maximális mérete.
     */
    public void makeResizedImageFromMaster(int size){
        if(this.MasterImage != null){
            int type = this.MasterImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : this.MasterImage.getType();
            int oldW = this.MasterImage.getWidth();
            int oldH = this.MasterImage.getHeight();
            float hProp = (float)(oldH / size);
            float wProp = (float)(oldW / size);
            
            float prefProp = (hProp < wProp) ? (int) wProp : (int) hProp;
            
            if(prefProp < 1){
                prefProp = 1;
            }
            
            this.width = (int)(oldW / prefProp);
            this.height = (int)(oldH / prefProp);
            this.data = new int[this.width][this.height];
            
            BufferedImage resizedImage = new BufferedImage(this.width, this.height, type);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(this.MasterImage, 0, 0, this.width, this.height, null);
            g.dispose();
            this.ResizedImage = resizedImage;
        }
    }
    
    /**
     * Az átméretezett képből fekete-fehér pixeleket generál.
     */
    public void makeMonochromeImageFromResized(){
        
        Raster raster = this.ResizedImage.getRaster();
        
        int[] pixels = new int[this.width];
        for (int y = 0; y < this.height; y++) {
            raster.getSamples(0, y, this.width, 1, 1, pixels);
            for (int i = 0; i < this.width; i++) {
                this.data[i][y] = invertCol?
                        ((pixels[i] < this.threshold)? 0:1):
                        ((pixels[i] < this.threshold)? 1:0);
            }
        }
    }
    
    /**
     * A kész, fekete-fehár négyzetrácsot adja vissza <code>{@link DrawingData}</code> objektum formájában.
     * @return A kész <code>{@link DrawingData}</code> objektum
     */
    public DrawingData getFinalImage(){
        this.drawData.setData(this.data, true, -1);
        return this.drawData;
    }
    
    /**
     * <code>{@link PuzzleRawData}</code> objektumot generál, ez a kész kép.
     * @return A kész <code>{@link PuzzleRawData}</code> objektum
     */
    public PuzzleRawData generateRawData(){
        PuzzleRawData rData = new PuzzleRawData();
        List<Integer> actualCol;
        List<Integer> actualRow;
        int sum;
        for (int x = 0; x < this.width; x++) { //szeltebe -> oszlopok
            actualCol = new ArrayList<>();
            sum = 0;
            for (int y = 0; y < this.height; y++) {
                if(this.data[x][y] == 0){
                    if(sum > 0){
                        actualCol.add(sum);
                        sum = 0;
                    }
                } else {
                    sum++;
                }
            }
            if(sum > 0){
                actualCol.add(sum);
            }
            if(actualCol.isEmpty()){
                actualCol.add(0);
            }
            rData.addCol(actualCol);
        }
        for (int y = 0; y < this.height; y++) { //hosszaba -> sorok
            actualRow = new ArrayList<>();
            sum = 0;
            for (int x = 0; x < this.width; x++) {
                if(this.data[x][y] == 0){
                    if(sum > 0){
                        actualRow.add(sum);
                        sum = 0;
                    }
                } else {
                    sum++;
                }
            }
            if(sum > 0){
                actualRow.add(sum);
            }
            if(actualRow.isEmpty()){
                actualRow.add(0);
            }
            rData.addRow(actualRow);
        }
        return rData;
    }
}
