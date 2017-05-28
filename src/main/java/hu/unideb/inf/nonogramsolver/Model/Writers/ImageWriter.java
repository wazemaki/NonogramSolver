package hu.unideb.inf.nonogramsolver.Model.Writers;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;

/**
 * Kép-formátumú fájlt hoz létre <code>{@link WritableImage}</code> objektumból.
 * @author wazemaki
 */
public class ImageWriter {
    
    /**
     * Az írandó kép fájl-objektuma.
     */
    private WritableImage writableImage;
    
    /**
     * Konstruktor.
     */
    public ImageWriter(){
        this.writableImage = null;
    }
    
    /**
     * Konstruktor.
     * @param image A fájlba mentendő <code>{@link WritableImage}</code> objektum.
     */
    public ImageWriter(WritableImage image){
        this.writableImage = image;
    }
    
    /**
     * Konstruktor.
     * @param image A fájlba mentendő <code>{@link WritableImage}</code> objektum.
     */
    public void setImage(WritableImage image){
        this.writableImage = image;
    }
    
    /**
     * Képfájl írása.
     * @param file Az írandó file objektuma.
     * @throws IOException Valamilyen I/O hiba lépett fel.
     */
    public void write(File file) throws IOException{

        RenderedImage renderedImage = SwingFXUtils.fromFXImage(this.writableImage, null);
        String mimeType = Files.probeContentType(file.toPath());
        switch (mimeType) {
            case "image/jpeg":
                ImageIO.write(renderedImage, "jpeg", file);
                break;
            case "image/bmp":
                ImageIO.write(renderedImage, "bmp", file);
                break;
            case "image/png":
                ImageIO.write(renderedImage, "png", file);
                break;
        
        }
    }
}
