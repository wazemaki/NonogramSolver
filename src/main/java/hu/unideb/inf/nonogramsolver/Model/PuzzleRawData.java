package hu.unideb.inf.nonogramsolver.Model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author wazemaki
 */
public class PuzzleRawData {
    
    private final List<List<Integer>> rawCols;
    private final List<List<Integer>> rawRows;
    private String description;
    
    public PuzzleRawData(){
        this.rawCols = new ArrayList<>();
        this.rawRows = new ArrayList<>();
        this.description = "";
    }
    
    public void clear(){
        this.rawCols.clear();
        this.rawRows.clear();
        this.description = "";
    }
    
    public void addCol(List<Integer> col){
        this.rawCols.add(col);
    }
    
    public void addRow(List<Integer> row){
        this.rawRows.add(row);
    }
    
    public boolean isEmpty(){
        return (this.rawCols.isEmpty() || this.rawRows.isEmpty());
    }
    
    public int getSizeX(){
        return this.rawCols.size();
    }
    
    public int getSizeY(){
        return this.rawRows.size();
    }
    
    public int getMaxRowSize(){
        return this.rawRows.stream().max(Comparator.comparingInt(List::size)).get().size();
    }
    
    public int getMaxColSize(){
        return this.rawCols.stream().max(Comparator.comparingInt(List::size)).get().size();
    }
    
    public List<List<Integer>> getCols(){
        return this.rawCols;
    }
    
    public List<List<Integer>> getRows(){
        return this.rawRows;
    }
    
    public String getDescription(){
        return this.description;
    }
    
    public void setDescription(String d){
        this.description = d;
    }
    
    public void appendDescription(String d, boolean brk){
        if(brk && !this.description.equals("")){
            this.description += "\n";
        }
        this.description += d;
    }
    
    public Integer getNumberFromRow(int row,int index){
        try{
            return this.rawRows.get(row).get(index);
        }catch(IndexOutOfBoundsException ex){
            return 0;
        }
    }
    
    public Integer getNumberFromCol(int col,int index){
        try{
            return this.rawCols.get(col).get(index);
        } catch(IndexOutOfBoundsException ex) {
            return 0;
        }
    }
}
