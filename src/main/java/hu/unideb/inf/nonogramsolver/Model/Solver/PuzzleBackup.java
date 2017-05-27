package hu.unideb.inf.nonogramsolver.Model.Solver;

import java.util.Arrays;

/**
 * Back-up objektum, amely tippelés esetén tárolja a fejtő egy korábi állapotát.
 * @author wazemaki
 */
public class PuzzleBackup {
    private int width, height;

    /**
     * A fejtés végét jelző flag.
     */
    protected boolean isEnd;

    /**
     * Az esetlegesen már korábban a fejtőben tárolt back-up objektum.
     */
    protected PuzzleBackup back;
    
    /**
     * A tippelt koordináták
     */
    protected int colTip, rowTip;

    /**
     * A négyzetrács adatai
     */
    protected int[][] grid;
    
    /**
     * Konstruktor.
     * @param solver Fejtő objektum, amelynek az állapotát menteni szükséges.
     */
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
    
    /**
     * Egy négyzet színét adja vissza.
     * @param row A négyzet függőleges koordinátája.
     * @param col A négyzet vízszintes koordinátája.
     * @return A négyzet színe.
     */
    public int get(int row,int col){
        if(row < this.height && col < this.width){
            return this.grid[col][row];
        } else {
            return -1;
        }
    }
}
