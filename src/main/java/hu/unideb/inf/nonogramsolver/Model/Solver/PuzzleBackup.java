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
    
    public PuzzleBackup(Solver solver){
        this.height = solver.height;
        this.width = solver.width;
        this.isEnd = solver.isEnd;
        this.back = solver.backUp;
        this.colTip = solver.colTip;
        this.rowTip = solver.rowTip;

        this.grid = new int[this.width][this.height];

        for(int i = 0; i<this.width; i++){
            this.grid[i] = Arrays.copyOf(solver.grid[i], this.height);
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
