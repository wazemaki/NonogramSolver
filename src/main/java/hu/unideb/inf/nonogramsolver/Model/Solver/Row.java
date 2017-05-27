package hu.unideb.inf.nonogramsolver.Model.Solver;

/**
 * Egy sort reprezentál. A <code>{@link Solver}</code> objektum segéd-sorokként használja a fejtéshez.
 *
 * @author wazemaki
 */
public class Row {
    /**
     * Konstruktor
     *
     * @param length A sor hosszát határozza meg
    */
    public Row(int length) {
        this.index = 0;
        this.length = length;
        this.data = new int[this.length];
        this.set(0, -1, -1);
    }
    /**
     * Konstruktor
     *
     * @param other Sor, amit a konstruktor lemásol
    */
    public Row(Row other){
        this.length = other.getLength();
        this.data = new int[this.length];
        for(int i = 0; i < this.length; i++){
            this.data[i] = other.get(i);
        }
    }
    
    private int[] data;
    private int length;
    private int index = 0;

    /**
     *
     * @param i
     * @return
     */
    public int get(int i) {
        if(i < this.length){
            return this.data[i];
        } else {
            return -1;
        }
    }

    /**
     *
     * @return
     */
    public int getLength() {
        return this.length;
    }

    /**
     *
     * @return
     */
    public int getIndex() {
        return this.index;
    }
    
    /**
     * Konstruktor
     *
     * @param other Sor, amit másolunk
     * @param length Ha igaz, egy az egyben lemásoljuk a sort. Ha hamis, az eredeti sor hossza megmarad.
     * @return 
    */
    public Row setByRow(Row other, Boolean length){
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
     * Beállítja a sor egy(vagy több, egymást követő) elemének színét.
     *
     * @param i A színezendő elemek kezdő indexe
     * @param l A színezendő elemek hossza
     * @param color A kívánt szín (-1, 0, 1)
     * @return 
    */
    public Row set(int i,int l, int color){
        if(i == -1) i = this.index;
        if(l == -1) l = this.length;
//        if(color == null) color = 1;
        
        for(int j = i; (j < i + l) && (j < this.length); j++){
            this.data[j] = color;
        }
        return this;
    }
  
    /**
     * A sor hosszát állítja
     *
     * @param length A kívánt hossz
     * @return 
    */
    public Row setLength(int length) {
        this.length = length;
        return this;
    }
            
    /**
     * A sor aktuális indexét állítja
     *
     * @param index A kívánt index
     * @return 
    */
    public Row setIndex(int index) {
        this.index = index;
        return this;
    }
                
    /**
     * Lépteti egyel az indexet
     * @return 
    */
    public Row stepIndex() {
        this.index++;
        return this;
    }
    
    /**
     * Egy másik sorral alkotott, elemenkénti logikai ÉS művelet eredményét adja
     *
     * @param r A sor, amellyel az ÉS műveletet végezzük
     * @param color A szín, amelyet figyelünk
     * @return 
    */
    public Row logic_AND(Row r, int color){
        for(int i = 0; i < this.length; i++){
            this.data[i] = (r.get(i) == color && this.data[i] == color)? color : -1;
        }
        return this;
    }
        
    /**
     * Egy másik sorral alkotott, elemenkénti logikai VAGY művelet eredményét adja
     *
     * @param r A sor, amellyel az VAGY műveletet végezzük
     * @param color A szín, amelyet figyelünk
     * @return 
    */
    public Row logic_OR(Row r, int color){
        for(int i = 0; i < this.length; i++){
            if(r.get(i) == color){
                this.data[i] = color;
            }
        }
        return this;
    }
    
    /**
     * Hozzáfűz a sorhoz
     *
     * @param l A hozzáfűzendő elemek hossza
     * @param color A hozzáfűzendő elemek színe
     * @return 
    */
    public Row append(int l,int color){

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
     * A sorban lévő üres elemek számát adja
     *
     * @param empty  Ha igaz(alapért.) Az üres elemek számát adja. Ha hamis, a nem üres elemek számát adja.
     * @return 
    */
    public int getDeficit(Boolean empty){
        if(empty == null) empty = true;
        int sum = 0;
        for(int i = 0; i < this.length; i++){
            if(this.data[i] == -1) sum++;
        }
        return (empty) ? sum : this.length - sum;
    }
        
    /**
     * Szöveggé alakít
     * (fejlesztői, hibakeresési célok)
     *
     * @param txt A sztring elején található szöveg
    */
    public void print(String txt){
        txt += ": ";
        for(int i=0; i < this.length; i++){
            switch (this.data[i]) {
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
