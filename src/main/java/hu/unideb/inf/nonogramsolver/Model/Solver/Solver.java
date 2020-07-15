package hu.unideb.inf.nonogramsolver.Model.Solver;

import hu.unideb.inf.nonogramsolver.Controller.SolverController;
import hu.unideb.inf.nonogramsolver.Model.SolverEvent;
import java.util.Arrays;
import java.util.List;

/**
 * Maga a fejtő.
 * @author wazemaki
 */
public class Solver implements Runnable{
    /**
     * A fejtő kontrollere.
     */
    private final SolverController controller;
    
    /**
     * A rejtvény méretei.
     */
    protected final int width, height;

    /**
     * A fejtés végét jelző flag.
     */
    protected boolean isEnd;
    
    /**
     * Az aktív sor/oszlop indexe.
     */
    private int active;
    
    /**
     * A fejtéshez felhasznált segéd-sorok, oszlopok.
     */
    private final Line slaveRow,
            fixBrow, fixWrow,
            fixBcol, fixWcol,
            slaveCol;
    /**
     * A sorokban történt változtatásokat tartalmazza.
     */
    private final boolean[] changedCols, changedRows;

    /**
     * Back-up objektum, amely tippelés esetén tárolja a fejtő egy korábi állapotát.
     */
    public PuzzleBackup backUp;
    
    /**
     * Aktuális tippelés koordinátái.
     */
    protected int colTip, rowTip;
    
    /**
     * Nyers rejtvény-adatokat tárol.
     */
    private final List<List<Integer> > puzzleCols, puzzleRows;
    
    /**
     * A fejtő aktuális SOR / OSZLOP állapotát tárolja.
     */
    private boolean isRow;
    /**
     * Hiba flag.
     */
    private boolean error;
    /**
     * Fejtő beállításokat tároló flag-ek.
     */
    private final boolean enableBackup,
            enablePrior;
    /**
     * Megállítást jelző flag.
     */
    private boolean isStopped = false;
    
    /**
     * Négyzetrács, amely tárolja a fejtés aktuális állapotát.
     */
    protected int[][] grid;
    
    /**
     * A prioritas szamolasahoz szukseges tömb.
    */
    public final int[] colDif, rowDif;
    /**
     * A prioritas szamolasahoz szukseges tömb.
    */
    private final float[] colAverage, rowAverage;
    // --------------------------------
    
    /**
     * A fejtő Konstruktora.
     * @param cols A rejtvényben szereplő oszlopok
     * @param rows A rejtvényben szereplő sorok
     * @param controller Kontroller, melynek segítségével kezelhetjük az eseményeket
     * @param enBackup Backup-ok (próbálkozások) engedélyezése
     * @param enPrior Prioritás szerinti fejtés engedélyezése
     */
    public Solver(List<List<Integer> > cols, List<List<Integer> > rows, SolverController controller, boolean enBackup, boolean enPrior){
        this.controller = controller;
        
        this.enableBackup = enBackup;
        this.enablePrior = enPrior;
        this.isEnd = false;
        this.error = false;
        
        this.puzzleCols = cols;
        this.puzzleRows = rows;
        
        this.height = this.puzzleRows.size();
        this.width = this.puzzleCols.size();
        
        this.grid = new int[this.width][this.height];
        
        this.slaveCol = new Line(this.height);
        this.slaveRow = new Line(this.width);
        
        this.fixBcol = new Line(this.height);
        this.fixBrow = new Line(this.width);
        this.fixWcol = new Line(this.height);
        this.fixWrow = new Line(this.width);
        
        this.rowAverage = new float[this.height];
        this.colAverage = new float[this.width];
        this.rowDif = new int[this.height];
        this.colDif = new int[this.width];
        
        this.changedCols = new boolean[this.width];
        this.changedRows = new boolean[this.height];
        this.backUp = null;
        this.clearGrid();
        
        for(int i = 0; i < this.height; i++){
            this.changedRows[i] = true;
        }
    }
    
    /**
     * A négyzetrácsot feltölti üres négyzetekkel.
     */
    public void clearGrid(){
        for(int i = 0; i < this.width; i++){
            this.changedCols[i] = true;
            for(int j = 0; j < this.height; j++){
                this.set(j, i, -1);
            }
        }
    }
       
    /**
     * Egy mező aktuális színét adja vissza.
     * @param row A mező sorának indexe
     * @param col A mező oszlopának indexe
     * @return A mező színe (-1, ha még nincs megfejtve, vagy az indexek valamelyike hibás)
     */
    public int get(int row, int col){ // egyetlen kocka szinet adja
        if(row < this.height && col < this.width){
            return this.grid[col][row];
        } else {
            return -1;
        }
    }
       
    /**
     * A kétdimenziós négyzetrácsot adja vissza.
     * @return A rács
     */
    public int[][] getGrid(){
        return this.grid;
    }
    
    /**
     * A fejtő aktuális sor/oszlop állapotát adja.
     * @return Igaz({@code true}), ha az aktuális állapot SOR.
     *  Hamis({@code false}), ha az aktuális állapot OSZLOP.
     */
    public boolean getIsRow(){
        return this.isRow;
    }
    
    /**
     * A fejtő aktuális sor/oszlop állapotát állítja be.
     * @param isRow A kívánt SOR/OSZLOP állapot.
     */
    public void setIsRow(boolean isRow){
        this.isRow = isRow;
    }
    
    /**
     * Egy mező színét állítja be.
     * @param row A mező sorának indexe
     * @param col A mező oszlopának indexe
     * @param color A mező oszlopának indexe
     * @return Igaz({@code true}), ha a mező színe változott.
     * Hamis({@code false}), ha a mező színe eredeti maradt.
     */
    public boolean set(int row, int col, int color){
        int orig = -1;
        if(row < this.height && col < this.width){
            orig = this.grid[col][row];
            this.grid[col][row] = color;
        }
        return (orig != color);
    }
    
    /**
     * Egy sort/oszlopot ad vissza <code>{@link Line}</code> objektum formájában a négyízetrácsból.
     * Az <code>{@link Solver#isRow}</code> függvényében az adott indexben lévő sort, vagy oszlopot adja
     * a paraméterben megadott feltételeknek megfelelően.
     * @param index A sor/oszlop indexe
     * @param color Színek szűrése: {@code (-2|-1|0|1)} Az itt megadott színt figyeli az eredeti sorban.
     * -2 esetén az összes színt figyelembe veszi.
     * @param outColor Kimeneti szín: {@code (-2|-1|0|1)}. -2 esetén az eredeti színt másolja,
     * egyébként az itt megadott színt írja a kimeneti objektumba.
     * @return <code>{@link Line}</code> objektum, amelyet a függvény elkészít.
     */
    public Line getLine(int index, int color, int outColor){
        int len = (this.isRow) ? this.width : this.height;
        Line line = new Line(len);
        int o_color = outColor;
        for(int i = 0; i < len; i++){
            int orig = (this.isRow) ? this.get(index,i) : this.get(i,index);
            if(outColor == -2){
                o_color = orig; //ha nem hataroztunk meg kimeneti szint, akkor siman mindent csak masolunk...
            }
            if(color == -2 || color == orig){  //ha meghataroztunk szurest, akkor csak az azonos szinueket nyomjuk
                line.set(i, 1, o_color);
            } else {
                line.set(i,1,-1);
            }
        }
        return line;
    }

    /**
     * Egy sort/oszlopot állít be a négyzetrácsban egy <code>{@link Line}</code> objektumból.
     * Az <code>{@link Solver#isRow}</code> függvényében az adott indexben lévő sort, vagy oszlopot állítja.
     * @param index A sor/oszlop indexe
     * @param row <code>{@link Line}</code> objektum, amelyet beállít.
     */
    public void setLine(int index, Line row){
        int len = (this.isRow) ? this.width : this.height;
        for(int i = 0; i < len; i++){
            if(row.get(i) > -1){
                if(this.isRow) {
                    if(this.set(index, i, row.get(i))){
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
    
    /**
     * Egy sor/oszlop mezőit számolja meg a paraméterekben megadott feltételeknek megfelelően.
     * Az <code>{@link Solver#isRow}</code> függvényében az adott indexben lévő sort, vagy oszlopot számolja.
     * @param index A sor/oszlop indexe
     * @param color A szín, amelyet az adott sorban/oszlopban számol.
     * @param inv Igaz({@code true}): A {@code color} paraméterben megadott színen kívül számol.
     * Hamis({@code false}): A {@code color} paraméterben megadott színű mazőket számolja.
     * @return A kívánt mezők száma a sorban.
     */ 
    public int count(int index, int color, boolean inv){
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
    
    /**
     * Megállapitja, hogy a megadott <code>{@link Line}</code> objektum lehetséges megoldás-e.
     * Az <code>{@link Solver#isRow}</code> függvényében az adott indexben lévő sort, vagy oszlopot vizsgálja.
     * @param index A sor/oszlop indexe
     * @param line <code>{@link Line}</code> objektum, amelyet hasonlít a kívánt sorhoz/oszlophoz.
     * @param len A hossz, amelyet vizsgál. {@code 0} esetén az egész sort vizsgálja.
     * @return Igaz({@code true}), ha a sor/oszlop lehetséges megoldás.
     * Hamis({@code false}), ha nem megoldás.
     */
    public boolean compare(int index, Line line, int len){
        if(len == 0) len = line.getLength();
        for(int i = 0; i < len; i++){            
            if(this.isRow && this.grid[i][index] > -1 && this.grid[i][index] != line.get(i)){
                return false;
            }
            if(!this.isRow && this.grid[index][i] > -1 && this.grid[index][i] != line.get(i)){
                
                return false;
            }
        }
        return true;
    }
    
    /**
     * Kiválasztja prioritás szerint a legmegfelelőbb sort/oszlopot.
     * Az <code>{@link Solver#isRow}</code> flaget is ez a függvény állítja be.
     * @param onlyChanged Ha igaz({@code true}), csak a legutóbbi vizsgálat óta megváltoztatott sorokat/oszlopokat veszi figyelembe.
     * @param emptyRows Ha igaz({@code true}), az üres sorokat/oszlopokat is figyelembe veszi.
     * @return A kiválasztott sor/oszlop indexe.
     */
    public int selectByPrior(boolean onlyChanged, boolean emptyRows){
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
    
    /**
     * A <code>{@link Solver#fixBrow}</code>(fekete mezők) és <code>{@link Solver#fixWrow}</code>(fehér mezők)
     * <code>{@link Line}</code>-objektumokat állítja be. A kívánt sorban azokat a mezőket tárolja az objektumokban,
     * amelyek az összes illeszkedő, lehetséges sor-variációban előfordulnak.
     * @param row A vizsgálni kívánt sor indexe.
     * @param dif A rekurzió megvalósításához szükséges.
     * @param recur A rekurzió megvalósításához szükséges.
     * @return Igaz({@code true}), ha a segéd-objektumok valamelyike tartalmaz megfejtett mezőt.
     * Hamis({@code false}), ha a segéd-objektumok üresek. (Nem tartalmaznak megfejtett mezőt.)
     */
    public boolean potentialRow(int row, int dif, int recur){
        int slaveRowIndex = this.slaveRow.getIndex();
        int whites = (recur == 0) ? 0 : 1;
        int numBlocks = this.puzzleRows.get(row).size();
        boolean ok = true;
        for(int i = dif; i >= 0 && ok && !this.isStopped; i--){
            this.slaveRow.setIndex(slaveRowIndex)
                    .append(i + whites, 0)
                    .append(this.puzzleRows.get(row).get(recur),1)
                    .set(-1,-1,0);

            if(ok && recur < numBlocks-1 && this.compare(row,this.slaveRow,this.slaveRow.getIndex())){
                ok = this.potentialRow(row, dif - i, recur + 1);
            }
            if(recur >= numBlocks - 1 && this.compare(row,this.slaveRow,0)){
                this.error = false;
                this.fixBrow.logic_AND(this.slaveRow,1);
                this.fixWrow.logic_AND(this.slaveRow,0);
                if(this.fixBrow.getFilledCnt() == 0 && this.fixWrow.getFilledCnt() == 0){
                    return false;
                }
            }
        }
        return ok;
    }
    
    /**
     * A <code>{@link Solver#fixBcol}</code>(fekete mezők) és <code>{@link Solver#fixWcol}</code>(fehér mezők)
     * <code>{@link Line}</code>-objektumokat állítja be. A kívánt oszlopban azokat a mezőket tárolja az objektumokban,
     * amelyek az összes illeszkedő, lehetséges oszlop-variációban előfordulnak.
     * @param col A vizsgálni kívánt oszlop indexe.
     * @param dif A rekurzió megvalósításához szükséges.
     * @param recur A rekurzió megvalósításához szükséges.
     * @return Igaz({@code true}), ha a segéd-objektumok valamelyike tartalmaz megfejtett mezőt.
     * Hamis({@code false}), ha a segéd-objektumok üresek. (Nem tartalmaznak megfejtett mezőt.)
     */
    public boolean potentialCol(int col, int dif, int recur){
        int slaveColIndex = this.slaveCol.getIndex();
        int whites = (recur == 0) ? 0 : 1;
        int numBlocks = this.puzzleCols.get(col).size();
        boolean ok = true;
        for(int i = dif; i >= 0 && ok && !this.isStopped; i--){
            this.slaveCol.setIndex(slaveColIndex)
                    .append(i + whites, 0)
                    .append(this.puzzleCols.get(col).get(recur),1)
                    .set(-1,-1,0);
            if(ok && recur < numBlocks - 1 && this.compare(col,this.slaveCol,this.slaveCol.getIndex())){
                ok = this.potentialCol(col, dif - i, recur + 1);
            }
            if(recur >= numBlocks - 1 && this.compare(col,this.slaveCol,0)){
                this.error = false;
                this.fixBcol.logic_AND(this.slaveCol,1);
                this.fixWcol.logic_AND(this.slaveCol,0);
                if(this.fixBcol.getFilledCnt() == 0 && this.fixWcol.getFilledCnt() == 0){
                    return false;
                }
            }
        }
        return ok;
    }
    
    /**
     * Meghatározza azokat a mezőket, amelyek biztosan feketék.
     * Az <code>{@link Solver#isRow}</code> függvényében az adott indexben lévő sort, vagy oszlopot vizsgálja.
     * @param index A sor/oszlop indexe
     */
    public void fix(int index){
        int sum = 0;
        List<Integer> line = (this.isRow) ? this.puzzleRows.get(index) : this.puzzleCols.get(index);
        int pcs = line.size();
        int len = (this.isRow) ? this.width : this.height;
        
        for(int i : line){
            sum += i;
        }
        
        int dif = len - (sum + pcs) + 1;
        
        if(dif >= 0){
            Line begin = new Line(len);
            Line end = new Line(len);
            Line slave = new Line(len);
            end.setIndex(dif);
            for(int i : line){
                begin.append(i, 1)
                        .stepIndex();
                end.append(i, 1)
                        .stepIndex();
                begin.logic_AND(end, 1);
                slave.logic_OR(begin, 1);
                begin.set(0,-1,-1);
                end.set(0,-1,-1);
            }
            this.setLine(index, slave);
        } else {
            this.isEnd = true;
        }
    }
    
    /**
     * A fejtő <code>{@link PuzzleBackup}</code> példányát másolja vissza. (visszaállítja)
     */
    public void backUpCopy(){
        this.error = false;
        this.colTip = this.backUp.colTip;
        this.rowTip = this.backUp.rowTip;
        
        for(int i = 0; i < this.width; i++){
            this.grid[i] = Arrays.copyOf(this.backUp.grid[i], this.height);
            this.changedCols[i] = false;
        }
        for(int i = 0; i < height; i++){
            this.changedRows[i] = false;
        }
        this.backUp = this.backUp.back;
    }
    
    /**
     * A fejtő <code>{@link PuzzleBackup}</code> példányát állítja vissza, majd korrigálja a tippelési hibát.
     * @return Igaz({@code true}), ha sikeres a visszaállítás.
     * Hamis({@code false}), ha nincs eltárolva backUp objektum.
     */
    public boolean backUp(){
        if(this.backUp != null){
            this.backUpCopy();
            this.set(this.rowTip,this.colTip,1);
            this.changedCols[this.colTip] = true;
            this.changedRows[this.rowTip] = true;
            this.isEnd = false;
            this.error = false;
            return true;
        }
        return false;
    }
    
    /**
     * Megállapítja, hogy a rejtvény fejtése készen van-e.
     * @return Igaz({@code true}), ha a rejtvény kész.
     * Hamis({@code false}), ha a rejtvény nincs kész.
     */
    public boolean isComplete(){
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
        
    /**
     * Kiválaszt egy mezőt, és tippel.
     * @return Igaz({@code true}), ha talált alkalmas mezőt a tipphez.
     * Hamis({@code false}), ha nem talált alkalmas mezőt.
     */
    private boolean takeTip(){
        int index = this.selectByPrior(false,true);
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
    
    /**
     * A fix oszlopok és sorok meghatarozása, amiből kiindulhatunk.
     */
    public void fixAll(){
        this.isRow = true;
        for(int i = 0; i < this.height; i++) {
            this.fix(i);
        }
        this.isRow = false;
        for(int i = 0; i < this.width; i++) {
            this.fix(i);
        }
    }
    
    /**
     * A prioritás számolásához szükséges tömböket tölti fel.
     */
    public void setUpSlaveArrays(){
        for(int index = 0; index < this.width; index++){
            int sum = 0;
            List<Integer> line = this.puzzleCols.get(index);
            int pcs = line.size();
            for(int i : line){
                sum += i;
            }
            this.colDif[index] = this.height - (sum + pcs) + 1;
            this.colAverage[index] = sum / pcs;
        }
        for(int index = 0; index < this.height; index++){
            int sum = 0;
            List<Integer> line = this.puzzleRows.get(index);
            int pcs = line.size();
            for(int i : line){
                sum += i;
            }
            this.rowDif[index] = this.width - (sum + pcs) + 1;
            this.rowAverage[index] = sum / pcs;
        }
    }
    
    /**
     * Egy sor megoldását keresi az aktuális mezők alapján.
     * @param index A sor indexe
     */
    public void findBlocksInRow(int index) {
        this.slaveRow.setIndex(0);
        this.fixBrow.setByRow(this.getLine(index, -1, 1), false);
        this.fixWrow.setByRow(this.getLine(index, -1, 0), false);
        if (this.potentialRow(index, this.rowDif[index], 0)) {
            this.setLine(index, this.fixBrow);
            this.setLine(index, this.fixWrow);
        }
        this.changedRows[index] = false;
    }
    
    /**
     * Egy oszlop megoldását keresi az aktuális mezők alapján.
     * @param index Az oszlop indexe
     */
    public void findBlocksInCol(int index) {
        this.slaveCol.setIndex(0);
        this.fixBcol.setByRow(this.getLine(index, -1, 1), false);
        this.fixWcol.setByRow(this.getLine(index, -1, 0), false);
        if (this.potentialCol(index, this.colDif[index], 0)) {
            this.setLine(index, this.fixBcol);
            this.setLine(index, this.fixWcol);
        }
        this.changedCols[index] = false;
    }
        
    /**
     * Elindítja a fejtést.
     */
    @Override
    public void run(){
        this.callEvent(SolverEvent.EVENT_START, "");
        int index = 0;
        this.setUpSlaveArrays();
        this.fixAll();
        
        while(!this.isEnd){
            this.isEnd = true;
            if(this.enablePrior){
                index = this.selectByPrior(true, false);
            } else {
                if((this.isRow && ++index == this.height) || (!this.isRow && ++index == this.width)){
                    this.isRow = !this.isRow;
                    index = 0;
                }
            }
            this.active = index;
            if(index > -1){
                this.error = true;
                if(this.isRow){
                    this.findBlocksInRow(index);
                } else {
                    this.findBlocksInCol(index);
                }
                this.isEnd = false;
            }
            
            if(this.isStopped){
                this.callEvent(SolverEvent.EVENT_STOP, "");
                break;
            }
            
            if(this.error){ //hiba, nem lehet megoldani... ha van visszalepes, azzal folytatni, ha nincs vege...
                if(!this.backUp()){
                    this.callEvent(SolverEvent.EVENT_ERROR, "Nem lehet megfejteni...");
                    break;
                }
            }
            if(this.isEnd && !this.isComplete()){
                if(this.enableBackup && this.takeTip()){
                    this.backUp = new PuzzleBackup(this);
                    this.set(this.rowTip,this.colTip,0);
                    this.changedCols[this.colTip] = true;
                    this.changedRows[this.rowTip] = true;
                    this.isEnd = false;
                } else {
                    this.callEvent(SolverEvent.EVENT_ERROR, "Próbálkozások nélkül nem lehet megfejteni");
                    break;
                }
            }
            
            this.callEvent(SolverEvent.EVENT_REDRAW, "");
            
        }
        if(!this.isStopped && this.isComplete()){
            this.callEvent(SolverEvent.EVENT_COMPLETE, "");
        }
        this.callEvent(SolverEvent.EVENT_REDRAW, "");
        this.callEvent(SolverEvent.EVENT_END, "");
    }
    
    /**
     * Egy eseményt hív meg.
     * @param evType Az esemény típusa
     * @param data Az eseménynek átadott adat
     */
    public void callEvent(byte evType,String data){
        if(this.controller != null){
            switch(evType){
                case SolverEvent.EVENT_COMPLETE:
                    this.controller.callOnComplete(data);
                    break;
                case SolverEvent.EVENT_END:
                    this.controller.callOnEnd(data);
                    break;
                case SolverEvent.EVENT_ERROR:
                    this.controller.callOnError(data);
                    break;
                case SolverEvent.EVENT_REDRAW:
                    this.controller.callOnRedraw();
                    break;
                case SolverEvent.EVENT_START:
                    this.controller.callOnStart(data);
                    break;
                case SolverEvent.EVENT_STOP:
                    this.controller.callOnStopped(data);
                    break;
            }
        }
    }
    
    /**
     * Leállítja a fejtést.
     */
    public void stop(){
        this.isStopped = true;
    }
    
    /**
     * Az aktuálisan aktív sor/oszlop indexét adja vissza.
     * @return Aktuális sor/oszlop index.
     */
    public int getActiveLine(){
        return this.active;
    }
    
    /**
     * Szöveggé alakítja a kívánt sort/oszlopot.
     * Az <code>{@link Solver#isRow}</code> függvényében az adott indexben lévő sort, vagy oszlopot vizsgálja.
     * @param index A sor vagy oszlop indexe.
     * @param text A String elejéhez hozzáfűzendő szöveg.
     * @return A kívánt sor/oszlop String-je.
     */
    public String print(int index, String text){
        if(text != null){
            text += ": ";
        } else {
            text = "";
        }
        int len = (this.isRow)?this.width:this.height;
        for(int i=0; i < len; i++){
            int dt = (this.isRow)?this.grid[i][index]:this.grid[index][i];
            switch (dt) {
            case -1:
                text += "-";
                break;
            case 0:
                text += "O";
                break;
            case 1:
                text += "I";
                break;
            }
        }
        return text;
    }
}
