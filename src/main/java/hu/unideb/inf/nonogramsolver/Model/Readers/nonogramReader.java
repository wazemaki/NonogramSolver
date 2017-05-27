package hu.unideb.inf.nonogramsolver.Model.Readers;

import hu.unideb.inf.nonogramsolver.Model.PuzzleRawData;

/**
 * Olvasó interfész.
 * @author wazemaki
 */
public interface nonogramReader {

    /**
     * Olvasás indítása.
     * @return <code>{@link PuzzleRawData}</code> objektum, mely tartalmazza a beolvasott rejtvényt.
     * @throws Exception
     */
    public PuzzleRawData read() throws Exception ;
}
