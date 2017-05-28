package hu.unideb.inf.nonogramsolver.Model.Solver;

/**
 * Egy sort/oszlopot reprezentál. A <code>{@link Solver}</code> objektum segéd-sorokként használja a fejtéshez.
 *
 * @author wazemaki
 */
public class Line {
    
    /**
     * A sor mezőit tartalmazó tömb.
     */
    private int[] data;
    /**
     * A sor hossza.
     */
    private int length;
    /**
     * A sor aktuális indexe.
     */
    private int index = 0;
    
    /**
     * Konstruktor.
     * @param length A sor hosszát határozza meg
    */
    public Line(int length) {
        this.index = 0;
        this.length = length;
        this.data = new int[this.length];
        this.set(0, -1, -1);
    }
    /**
     * Konstruktor.
     * @param other Sor, amit a konstruktor lemásol
    */
    public Line(Line other){
        this.length = other.getLength();
        this.data = new int[this.length];
        for(int i = 0; i < this.length; i++){
            this.data[i] = other.get(i);
        }
    }
    
    /**
     * A sor objektum mezőit tartalmazó tömböt adja vissza.
     * @return A mezőket tartalmazó tömb.
    */
    public int[] getData(){
        return this.data;
    }

    /**
     * Egy másik sor adatait másolja.
     * @param other Sor, amit másolunk
     * @param length Ha igaz, egy az egyben lemásoljuk a sort. Ha hamis, az eredeti sor hossza megmarad.
     * @return A {@code Line} objektum.
    */
    public Line setByRow(Line other, Boolean length){
        if (this != other) {
            if(length) {
                this.length = other.getLength();
                this.data = new int[this.length];
            }
            this.index = 0;
            for(int i = 0; i < this.length; i++){
                this.data[i] = other.get(i);
            }
        }
        return this;
    }
    
    /**
     * Egy tömb szerint állítja be az adatokat.
     * @param array Tömb, amit másolunk
     * @param length Ha igaz, egy az egyben lemásoljuk a sort. Ha hamis, az eredeti sor hossza megmarad.
     * @return A {@code Line} objektum.
    */
    public Line setByArray(int[] array, Boolean length){
        if(length) {
            this.length = array.length;
            this.data = new int[this.length];
        }
        this.index = 0;
        for(int i = 0; i < this.length; i++){
            this.data[i] = array[i];
        }
        return this;
    }

    /**
     * Egy mező színét adja vissza.
     * @param index A mező indexe
     * @return A mező színe
     */
    public int get(int index) {
        if(index < this.length){
            return this.data[index];
        } else {
            return -1;
        }
    }

    /**
     * A sor hosszát adja vissza.
     * @return A sor hossza.
     */
    public int getLength() {
        return this.length;
    }

    /**
     * A sor aktuális indexét adja vissza.
     * @return A sor indexe.
     */
    public int getIndex() {
        return this.index;
    }
        
    /**
     * Beállítja a sor egy(vagy több, egymást követő) elemének színét.
     * @param i A színezendő elemek kezdő indexe
     * @param l A színezendő elemek hossza
     * @param color A kívánt szín {@code (-1|0|1)}
     * @return A {@code Line} objektum.
    */
    public Line set(int i,int l, int color){
        if(i == -1) i = this.index;
        if(l == -1) l = this.length;
        
        for(int j = i; (j < i + l) && (j < this.length); j++){
            this.data[j] = color;
        }
        return this;
    }
  
    /**
     * A sor hosszát állítja.
     * @param length A kívánt hossz
     * @return A {@code Line} objektum.
    */
    public Line setLength(int length) {
        this.length = length;
        return this;
    }
            
    /**
     * A sor aktuális indexét állítja.
     * @param index A kívánt index
     * @return A {@code Line} objektum.
    */
    public Line setIndex(int index) {
        this.index = index;
        return this;
    }
                
    /**
     * Lépteti eggyel az indexet.
     * @return A {@code Line} objektum.
    */
    public Line stepIndex() {
        this.index++;
        return this;
    }
    
    /**
     * Egy másik {@code Line} objektummal alkotott, mezőnkénti logikai ÉS műveletet végez.
     * @param r A sor, amellyel az ÉS műveletet végezzük
     * @param color A szín, amelyet figyelünk
     * @return A {@code Line} objektum.
    */
    public Line logic_AND(Line r, int color){
        for(int i = 0; i < this.length; i++){
            this.data[i] = (r.get(i) == color && this.data[i] == color)? color : -1;
        }
        return this;
    }
        
    /**
     * Egy másik {@code Line} objektummal alkotott, elemenkénti logikai VAGY műveletet végez.
     * @param r A sor, amellyel az VAGY műveletet végezzük
     * @param color A szín, amelyet figyelünk
     * @return A {@code Line} objektum.
    */
    public Line logic_OR(Line r, int color){
        for(int i = 0; i < this.length; i++){
            if(r.get(i) == color){
                this.data[i] = color;
            }
        }
        return this;
    }
    
    /**
     * Hozzáfűz a sorhoz.
     * @param l A hozzáfűzendő elemek hossza
     * @param color A hozzáfűzendő elemek színe
     * @return A {@code Line} objektum.
    */
    public Line append(int l,int color){

        int j;
        for(j = this.index; j < this.index + l; j++){
            this.data[j] = color;
        }
        for(int i = this.index + l; i<this.length; i++){
            this.data[i] = -1;
        }
        index = j;
        return this;
    }
    
    /**
     * A sorban lévő, nem üres elemek számát adja.
     * @return Üres elemek száma.
    */
    public int getFilledCnt(){
        int sum = 0;
        for(int i = 0; i < this.length; i++){
            if(this.data[i] != -1) sum++;
        }
        return sum;
    }
        
    /**
     * Szöveggé alakít, majd kiír.
     * (fejlesztői, hibakeresési célokra)
     * @param text A sztring elején található szöveg
     * @return A kívánt sor/oszlop String-je.
    */
    public String print(String text){
        if(text != null){
            text += ": ";
        } else {
            text = "";
        }
        for(int i=0; i < this.length; i++){
            switch (this.data[i]) {
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
