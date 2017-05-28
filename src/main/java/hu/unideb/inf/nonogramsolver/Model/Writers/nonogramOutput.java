package hu.unideb.inf.nonogramsolver.Model.Writers;

import java.io.File;

/**
 * Interfész renjtvények mentéséhez.
 * @author wazemaki
 */
public interface nonogramOutput {

    /**
     * A fájl írása.
     * @param file A mentendő <code>{@link File}</code> objektum.
     * @throws Exception Valamilyen kivétel történt.
     */
    public void write(File file) throws Exception ;
}
