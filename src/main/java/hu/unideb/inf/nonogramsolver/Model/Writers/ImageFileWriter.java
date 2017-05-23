package hu.unideb.inf.nonogramsolver.Model.Writers;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;

/**
 *
 * @author wazemaki
 */
public class ImageFileWriter {
    
    private WritableImage writableImage;
    
    public ImageFileWriter(){
        this.writableImage = null;
    }
    
    public ImageFileWriter(WritableImage image){
        this.writableImage = image;
    }
    
    public void setImage(WritableImage image){
        this.writableImage = image;
    }
    
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
