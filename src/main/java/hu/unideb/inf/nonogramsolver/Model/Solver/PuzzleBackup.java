package hu.unideb.inf.nonogramsolver.Model.Solver;

import java.util.Arrays;

/**
 *
 * @author wazemaki
 */
public class PuzzleBackup {
    private int width, height;
    protected boolean isEnd;
    protected PuzzleBackup back;
    protected int colTip, rowTip;
    protected int[][] grid;
    private boolean isRow, error;
    
    public PuzzleBackup(Solver p){
        this.height = p.height;
        this.width = p.width;
        this.isEnd = p.isEnd;
        this.error = false;
        this.back = p.back;
        this.colTip = p.colTip;
        this.rowTip = p.rowTip;

        this.grid = new int[this.width][this.height];

        for(int i = 0; i<this.width; i++){
            this.grid[i] = Arrays.copyOf(p.grid[i], this.height);
        }
    }
    
    public int get(int row,int col){ // egyetlen kocka szinet adja
        if(row < this.height && col < this.width){
            return this.grid[col][row];
        } else {
            return -1;
        }
    }
}
