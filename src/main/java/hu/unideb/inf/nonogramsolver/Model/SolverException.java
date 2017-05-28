package hu.unideb.inf.nonogramsolver.Model;

/**
 *
 * @author wazemaki
 */
public class SolverException extends Exception{

    /**
     * Kivételek lehetséges típusai.
     */
    public static final int TYPE_INFAVLIDFILE = 0,
            TYPE_INFAVLIDFILEFORMAT = 1,
            TYPE_INFAVLIDPUZZLE = 2,
            TYPE_SOLVERISBUSY = 3,
            TYPE_WEBIMPORTERROR = 4;
    
    /**
     * A típusoknak megfelelő üzenetek.
     */
    private static final String ERRMSG_INFAVLIDFILE = "Érvénytelen fájl",
            ERRMSG_INFAVLIDFILEFORMAT = "Érvénytelen fájlformátum",
            ERRMSG_INFAVLIDPUZZLE = "Érvénytelen rejtvény",
            ERRMSG_SOLVERISBUSY = "A fejtő foglalt",
            ERRMSG_WEBIMPORTERROR = "Az importálás sikertelen",
            ERRMSG_UNDEFINED = "Ismeretlen hiba";
    
    /**
     * A hibaüzenet típusa.
     */
    private int type;

    /**
     * Konstruktor.
     * @param message Kiegészítő üzenet a kivétel típusa mellé
     */
//    public SolverException(String message) {
//        super(message);
//    }
    
    /**
     * Konstruktor.
     * @param message Kiegészítő üzenet a kivétel típusa mellé
     * @param type A kivétel típusa
     */
    public SolverException(String message, int type) {
        super(message);
        this.type = type;
    }
    
    /**
     * Konstruktor.
     * @param type A kivétel típusa
     */
    public SolverException(int type) {
        super();
        this.type = type;
    }
    
    @Override
    public String getMessage(){
        switch(this.type){
            case SolverException.TYPE_INFAVLIDFILE:
                return SolverException.ERRMSG_INFAVLIDFILE + ": " + super.getMessage();
            case SolverException.TYPE_INFAVLIDFILEFORMAT:
                return SolverException.ERRMSG_INFAVLIDFILEFORMAT + ": " + super.getMessage();
            case SolverException.TYPE_INFAVLIDPUZZLE:
                return SolverException.ERRMSG_INFAVLIDPUZZLE + ": " + super.getMessage();
            case SolverException.TYPE_SOLVERISBUSY:
                return SolverException.ERRMSG_SOLVERISBUSY + ": " + super.getMessage();
            case SolverException.TYPE_WEBIMPORTERROR:
                return SolverException.ERRMSG_WEBIMPORTERROR + ": " + super.getMessage();
        }
        return SolverException.ERRMSG_UNDEFINED + ": " + super.getMessage();
    }
    
    /**
     * A kivétel típusát adja vissza.
     * @return Típus
     */
    public int getType(){
        return this.type;
    }
    
}
