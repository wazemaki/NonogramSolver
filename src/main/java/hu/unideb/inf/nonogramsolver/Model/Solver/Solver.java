package hu.unideb.inf.nonogramsolver.Model.Solver;

import hu.unideb.inf.nonogramsolver.Controller.SolverController;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author wazemaki
 */
public class Solver implements Runnable{
    private final SolverController controller;
    
    protected int width,

    /**
     *
     */
    height;

    /**
     *
     */
    protected boolean isEnd;
    
    /**
     *
     */
    public int active;
    
    private final Row slaveRow;
    private final Row fixBrow, fixWrow;
    private final Row slaveCol;
    private final Row fixBcol, fixWcol;
    private final boolean[] changedCols, changedRows;

    /**
     *
     */
    protected PuzzleBackup back;
    protected int colTip,

    /**
     *
     */
    rowTip;
    
    private final List<List<Integer> > puzzleCols, puzzleRows; //a feladvanyban szereplo oszlopok, sorok szamai
    
    /**
     *
     */
    public boolean isRow;
    private boolean error;
    private final boolean enableBackup;
    private final boolean enablePrior;
    private String errorMessage;
    private boolean isStopped = false;
    
    /**
     *
     */
    protected int[][] grid;
    /**
     * A prioritas szamolasahoz szuksegesek!
    */
    private final int[] colDif, rowDif;
    private final float[] colAverage, rowAverage;
    // --------------------------------
    
    /**
     *
     * @param cols
     * @param rows
     * @param c
     * @param enBackup
     * @param enPrior
     */
    public Solver(List<List<Integer> > cols, List<List<Integer> > rows, SolverController c, boolean enBackup, boolean enPrior){
        this.controller = c;
        
        this.enableBackup = enBackup;
        this.enablePrior = enPrior;
        this.isEnd = false;
        this.error = false;
        
        this.puzzleCols = cols;
        this.puzzleRows = rows;
        
        this.height = this.puzzleRows.size();
        this.width = this.puzzleCols.size();
        
        this.grid = new int[this.width][this.height];
        
        this.slaveCol = new Row(this.height);
        this.slaveRow = new Row(this.width);
        
        this.fixBcol = new Row(this.height);
        this.fixBrow = new Row(this.width);
        this.fixWcol = new Row(this.height);
        this.fixWrow = new Row(this.width);
        
        this.rowAverage = new float[this.height];
        this.colAverage = new float[this.width];
        this.rowDif = new int[this.height];
        this.colDif = new int[this.width];
        
        this.changedCols = new boolean[this.width];
        this.changedRows = new boolean[this.height];
        this.back = null;
        
        for(int i = 0; i < this.width; i++){
            this.changedCols[i] = true;
            for(int j = 0; j < this.height; j++){
                this.set(j, i, -1);
            }
        }
        for(int i = 0; i < this.height; i++){
            this.changedRows[i] = true;
        }
    
    }
       
    /**
     *
     * @param row
     * @param col
     * @return
     */
    public int get(int row,int col){ // egyetlen kocka szinet adja
        if(row < this.height && col < this.width){
            return this.grid[col][row];
        } else {
            return -1;
        }
    }
       
    /**
     *
     * @return
     */
    public int[][] getGrid(){
        return this.grid;
    }
    
    private boolean set(int row, int col, int color){ //egyetlen kockat allit, visszatér igazzal, ha válzotott a kocka
        int orig = -1;
        if(row < this.height && col < this.width){
            orig = this.grid[col][row];
            this.grid[col][row] = color;
        }
        return (orig != color);
    }
    
    private Row getRC(int index, int color, int outColor){ //visszaad egy sort vagy oszlopot, egySor formaban
        int len = (this.isRow) ? this.width : this.height;
        Row row = new Row(len);
        for(int i = 0; i < len; i++){
            int orig = (this.isRow) ? this.get(index,i) : this.get(i,index);
            if(outColor == -2){
                outColor = orig; //ha nem hataroztunk meg kimeneti szint, akkor siman mindent csak masolunk...
            } 
            if(color == -2 || color == orig){  //ha meghataroztunk szurest, akkor csak az azonos szinueket nyomjuk
                row.set(i,1,outColor);
            } else {
                row.set(i,1,-1);
            }
        }
        return row;
    }

    private void setRC(int index, Row row){ // egesz sort/oszlopot allit
        int len = (this.isRow) ? this.width : this.height;
        for(int i = 0; i < len; i++){
            if(row.get(i) > -1){
                if(this.isRow) {
                    if(this.set(index,i,row.get(i))){
                        this.changedCols[i] = true;
                    }
                } else {
                    if(set(i,index,row.get(i))){
                        this.changedRows[i] = true;
                    }
                }
            }
        }
    }
    
    private int count(int index, int color, boolean inv){ //megszamolja, az adott sorban/oszlopban mennyi "szin" mező van
        int sum = 0;
        int len = (this.isRow)?this.width:this.height;
        for(int i = 0; i < len; i++){
            if(!inv){
                if(this.isRow && this.get(index,i) == color) {
                    sum++;
                } else if(!this.isRow && this.get(i,index) == color){
                    sum++;
                }
            } else {
                if(this.isRow && this.get(index,i) != color) {
                    sum++;
                } else if(!this.isRow && this.get(i,index) != color){
                    sum++;
                }
            }
        }
        return sum;
    }
    
    private boolean compareRC(int index, Row row, int len){ //megallapitja, hogy a megadott sorreszlet egy lehetseges megoldas-e
        if(len == 0) len = row.getLength();
        for(int i = 0; i < len; i++){            
            if(this.isRow && this.grid[i][index] > -1 && this.grid[i][index] != row.get(i)){
                return false;
            }
            if(!this.isRow && this.grid[index][i] > -1 && this.grid[index][i] != row.get(i)){
                
                return false;
            }
        }
        return true;
    }
    
    private int selectPrior(boolean onlyChanged, boolean emptyRows){
        float maxprior = -2;
        float prior;
        int index = -1;
        int pcs;
        boolean isR = true;
        this.isRow = true;
        for(int i = 0; i < this.height && !this.isStopped; i++){
            pcs = this.count(i,-1,true);
            if( (!onlyChanged || this.changedRows[i]) && pcs < this.width && (emptyRows || pcs > 0)){
                if(this.rowAverage[i] == 0){
                    this.isRow = true;
                    return i;
                }
                prior = (this.rowAverage[i] + pcs) / (this.width + this.rowDif[i]);
                if(maxprior < prior) {
                    maxprior = prior;
                    index = i;
                    isR = true;
                }
            }
        }
        this.isRow = false;
        for(int i = 0; i < this.width && !this.isStopped; i++){
            pcs = this.count(i,-1,true);
            if( (!onlyChanged || this.changedCols[i]) && pcs < this.height && (emptyRows || pcs > 0)){
                if(this.colAverage[i] == 0){
                    this.isRow = false;
                    return i;
                }
                prior =  (this.colAverage[i] + pcs) / (this.height + this.colDif[i]);
                if(maxprior < prior) {
                    maxprior = prior;
                    index = i;
                    isR = false;
                }
            }
        }
        this.isRow = isR;
        return index;
    }
    
    private boolean potentialRow(int row, int dif, int recur){
        int slaveRowIndex = this.slaveRow.getIndex(); //azert kell, hogy minden ciklusmagban ugyanonnan kezdje feltolteni
        int whites = (recur == 0) ? 0 : 1;
        int numBlocks = this.puzzleRows.get(row).size();
        boolean ok = true;
        for(int i = dif; i >= 0 && ok && !this.isStopped; i--){
            this.slaveRow.setIndex(slaveRowIndex)
                    .append(i + whites, 0)
                    .append(this.puzzleRows.get(row).get(recur),1)
                    .set(-1,-1,0);

            if(ok && recur < numBlocks-1 && this.compareRC(row,this.slaveRow,this.slaveRow.getIndex())){
                ok = this.potentialRow(row, dif - i, recur + 1);
            }
            if(recur >= numBlocks - 1 && this.compareRC(row,this.slaveRow,0)){
                this.error = false;
                this.fixBrow.logic_AND(this.slaveRow,1);
                this.fixWrow.logic_AND(this.slaveRow,0);
                if(this.fixBrow.getDeficit(false) == 0 && this.fixWrow.getDeficit(false) == 0){
                    return false;
                }
            }
        }
        return ok;
    }
    
    private boolean potentialCol(int col, int dif, int recur){
        int slaveColIndex = this.slaveCol.getIndex(); //azert kell, hogy minden ciklusmagban ugyanonnan kezdje feltolteni
        int whites = (recur == 0) ? 0 : 1;
        int numBlocks = this.puzzleCols.get(col).size();
        boolean ok = true;
        for(int i = dif; i >= 0 && ok && !this.isStopped; i--){
            this.slaveCol.setIndex(slaveColIndex)
                    .append(i + whites, 0)
                    .append(this.puzzleCols.get(col).get(recur),1)
                    .set(-1,-1,0);
            if(ok && recur < numBlocks - 1 && this.compareRC(col,this.slaveCol,this.slaveCol.getIndex())){
                ok = this.potentialCol(col, dif - i, recur + 1);
            }
            if(recur >= numBlocks - 1 && this.compareRC(col,this.slaveCol,0)){
                this.error = false;
                this.fixBcol.logic_AND(this.slaveCol,1);
                this.fixWcol.logic_AND(this.slaveCol,0);
                if(this.fixBcol.getDeficit(false) == 0 && this.fixWcol.getDeficit(false) == 0){
                    return false;
                }
            }
        }
        return ok;
    }
    
    private void fix(int index){ // Meghatarozza azokat a mezoket, amik biztosan feketek, es kitolti a 'kulonsegek' es 'atlag' tombot.
        int sum = 0;
        List<Integer> a = (this.isRow) ? this.puzzleRows.get(index) : this.puzzleCols.get(index);
        int pcs = a.size();
        int len = (this.isRow) ? this.width : this.height;
        
        for(int i : a){
            sum += i;
        }
        
        int dif = len - (sum + pcs) + 1;
        if(this.isRow){
            this.rowDif[index] = dif;
            this.rowAverage[index] = sum / pcs;
        } else {
            this.colDif[index] = dif;
            this.colAverage[index] = sum / pcs;
        }
        if(dif >= 0){
            Row begin = new Row(len);
            Row end = new Row(len);
            Row slave = new Row(len);
            end.setIndex(dif);
            for(int i : a){
                begin.append(i, 1)
                        .stepIndex();
                end.append(i, 1)
                        .stepIndex();
                begin.logic_AND(end, 1);
                slave.logic_OR(begin, 1);
                begin.set(0,-1,-1);
                end.set(0,-1,-1);
            }
            this.setRC(index,slave);
        } else {
            this.isEnd = true;
        }
    }
    
    private void backUpCopy(PuzzleBackup bk){
        this.isEnd = bk.isEnd;
        this.error = false;
        this.back = bk.back;
        this.colTip = bk.colTip;
        this.rowTip = bk.rowTip;
        
        for(int i = 0; i < this.width; i++){
            this.grid[i] = Arrays.copyOf(bk.grid[i], this.height);
            this.changedCols[i] = false;
        }
        for(int i = 0; i < height; i++){
            this.changedRows[i] = false;
        }
    }
    
    private boolean backUp(){
        if(this.back != null){
            this.backUpCopy(this.back); //visszaallitjuk
            this.set(this.rowTip,this.colTip,1);
            this.changedCols[this.colTip] = true;
            this.changedRows[this.rowTip] = true;
            this.isEnd = false;
            this.error = false;
            return true;
        }
        return false;
    }
    
    private boolean isComplete(){
        this.isRow = true;
        for(int i = 0; i < this.height; i++){
            if(this.count(i, -1, false) > 0){
                return false;
            }
        }
        this.isRow = false;
        for(int i = 0; i < this.width; i++){
            if(this.count(i, -1, false) > 0){
                return false;
            }
        }
        return true;
    }
        
    private boolean takeTip(){
        int index = this.selectPrior(false,true);
        if(this.isRow){
            for(int i = 0; i < this.width; i++){
                if(this.grid[i][index] == -1) {
                    this.colTip = i;
                    this.rowTip = index;
                    return true;
                }
            }
        } else {
            for(int i = 0; i < this.height; i++){
                if(this.grid[index][i] == -1) {
                    this.colTip = index; 
                    this.rowTip= i;
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void run(){
        this.controller.callOnStart("");
        int index = 0;
        this.isRow = true;
        for(int i = 0; i < this.height; i++) { //a fix oszlopok meghatarozasa, amibol kiindulhatunk
            fix(i);
        }
        this.isRow = false;
        for(int i = 0; i < this.width; i++) { //a fix oszlopok meghatarozasa, amibol kiindulhatunk
            fix(i);
        }
        
        while(!this.isEnd){
            this.isEnd = true;
            if(this.enablePrior){
                index = this.selectPrior(true, false);
            } else {
                if((this.isRow && ++index == this.height) || (!this.isRow && ++index == this.width)){
                    index = 0;
                    this.isRow = !this.isRow;
                }
            }
            this.active = index;
            if(index > -1){
                this.error = true;
                if(this.isRow){
                    this.slaveRow.setIndex(0);
                    this.fixBrow.setByRow( this.getRC(index,-1,1), false );
                    this.fixWrow.setByRow( this.getRC(index,-1,0), false );
                    if( this.potentialRow(index,this.rowDif[index],0) ){
                        this.setRC(index,this.fixBrow);
                        this.setRC(index,this.fixWrow);
                    }
                    this.changedRows[index] = false;
                } else {
                    this.slaveCol.setIndex(0);
                    this.fixBcol.setByRow( this.getRC(index,-1,1), false );
                    this.fixWcol.setByRow( this.getRC(index,-1,0), false );
                    if( this.potentialCol(index,this.colDif[index],0) ){
                        this.setRC(index,this.fixBcol);
                        this.setRC(index,this.fixWcol);
                    }
                    this.changedCols[index] = false;
                }
                this.isEnd = false;
            }
            if(this.isStopped){
                this.controller.callOnStopped("");
                break;
            }
            if(this.error){ //hiba, nem lehet megoldani... ha van visszalepes, azzal folytatni, ha nincs vege...
                if(!this.backUp()){
                    this.controller.callOnError("Nem lehet megfejteni...");
                    break;
                }
            }
            if(this.isEnd && !this.isComplete()){
                if(this.enableBackup && this.takeTip()){
                    this.back = new PuzzleBackup(this);
                    this.set(this.rowTip,this.colTip,0);
                    this.changedCols[this.colTip] = true;
                    this.changedRows[this.rowTip] = true;
                    this.isEnd = false;
                } else {
                    this.controller.callOnError("Próbálkozások nélkül nem lehet megfejteni");
                    break;
                }
            }
            
            this.controller.callOnRedraw();
            
        }
        if(!this.isStopped && this.isComplete()){
            this.controller.callOnComplete("");
        }
        this.controller.callOnRedraw();
        this.controller.callOnEnd("");
    }
    
    /**
     *
     */
    public void stop(){
        this.isStopped = true;
    }
    
    /**
     *
     * @return
     */
    public int getActive(){
        return this.active;
    }
    
    /**
     *
     * @param index
     * @param txt
     */
    public void print(int index, String txt){
        txt += ": ";
        int len = (this.isRow)?this.width:this.height;
        for(int i=0; i < len; i++){
            int dt = (this.isRow)?this.grid[i][index]:this.grid[index][i];
            switch (dt) {
            case -1:
                txt += "-";
                break;
            case 0:
                txt += "O";
                break;
            case 1:
                txt += "I";
                break;
            }
        }
        System.out.println(txt);
    }
}
