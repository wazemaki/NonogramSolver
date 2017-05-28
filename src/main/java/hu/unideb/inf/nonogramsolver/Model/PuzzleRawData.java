package hu.unideb.inf.nonogramsolver.Model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Nyers puzzle-adatok; maga a feladvány. Tartalmazza a rejtvények leírását is.
 * @author wazemaki
 */
public class PuzzleRawData {
    
    /**
     * Oszlopok, sorok listája.
     */
    private final List<List<Integer>> rawCols,
            rawRows;
    /**
     * A rejtvény leírása.
     */
    private String description;
    
    /**
     * Konstruktor.
     */
    public PuzzleRawData(){
        this.rawCols = new ArrayList<>();
        this.rawRows = new ArrayList<>();
        this.description = "";
    }
    
    /**
     * Törli a rejtvény adatait.
     */
    public void clear(){
        this.rawCols.clear();
        this.rawRows.clear();
        this.description = "";
    }
    
    /**
     * Oszlopot ad hozzá a rejtvényhez.
     * @param col Az oszlop számait tartalmazó lista.
     */
    public void addCol(List<Integer> col){
        this.rawCols.add(col);
    }
    
    /**
     * Sort ad hozzá a rejtvényhez.
     * @param row A sor számait tartalmazó lista.
     */
    public void addRow(List<Integer> row){
        this.rawRows.add(row);
    }
    
    /**
     * Ellenőrzi, hogy a rejtvény üres-e. (Ha egyetlen sora, vagy oszlopa sincs, a rejtvény üres, nem megfejthető.)
     * @return Igaz{@code true}, ha a rejtvény üres.
     * Hamis{@code false}, ha a rejtvény nem üres.
     */
    public boolean isEmpty(){
        return (this.rawCols.isEmpty() || this.rawRows.isEmpty());
    }
    
    /**
     * A rejtvény szélességét(oszlopok számát) adja vissza.
     * @return Szélesség
     */
    public int getSizeX(){
        return this.rawCols.size();
    }
    
    /**
     * A rejtvény magasságát(sorok számát) adja vissza.
     * @return Magasság
     */
    public int getSizeY(){
        return this.rawRows.size();
    }
    
    /**
     * A rejtvény legtöbb számot tartalmazó sorának méretét adja vissza.
     * @return A sor számainak mennyisége
     */
    public int getMaxRowSize(){
        return this.rawRows.stream().max(Comparator.comparingInt(List::size)).get().size();
    }
    
    /**
     * A rejtvény legtöbb számot tartalmazó oszlopának méretét adja vissza.
     * @return Az oszlop számainak mennyisége
     */
    public int getMaxColSize(){
        return this.rawCols.stream().max(Comparator.comparingInt(List::size)).get().size();
    }
    
    /**
     * A rejtvény oszlopait adja vissza egymásba ágyazott listák segítségével.
     * @return Az oszlopok
     */
    public List<List<Integer>> getCols(){
        return this.rawCols;
    }
    
    /**
     * A rejtvény sorait adja vissza egymásba ágyazott listák segítségével.
     * @return A sorok
     */
    public List<List<Integer>> getRows(){
        return this.rawRows;
    }
    
    /**
     * A rejtvény leírását adja vissza.
     * @return A rejtvény leírása.
     */
    public String getDescription(){
        return this.description;
    }
    
    /**
     * A rejtvény leírását állítja be.
     * @param description A rejtvény leírása.
     */
    public void setDescription(String description){
        this.description = description;
    }
    
    /**
     * Hozzáfűz a rejtvény leírásához.
     * @param description A hozzáfűzendő leírás.
     * @param addBreak Sortörés hozzáadásának engedélyezése.
     */
    public void appendDescription(String description, boolean addBreak){
        if(addBreak && !this.description.equals("")){
            this.description += "\n";
        }
        this.description += description;
    }
    
    /**
     * Egy sor egy számát adja vissza.
     * @param row A sor indexe.
     * @param index A kívánt szám indexe a sorban.
     * @return A kívánt szám.
     */
    public Integer getNumberFromRow(int row, int index){
        try{
            return this.rawRows.get(row).get(index);
        }catch(IndexOutOfBoundsException ex){
            return 0;
        }
    }
    
    /**
     * Egy oszlop egy számát adja vissza.
     * @param col Az oszlop indexe.
     * @param index A kívánt szám indexe az oszlopban.
     * @return A kívánt szám.
     */
    public Integer getNumberFromCol(int col,int index){
        try{
            return this.rawCols.get(col).get(index);
        } catch(IndexOutOfBoundsException ex) {
            return 0;
        }
    }
}
