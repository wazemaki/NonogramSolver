package hu.unideb.inf.nonogramsolver.Model;

/**
 *
 * @author wazemaki
 */
public class SolverException extends Exception{

    /**
     *
     */
    public static final int TYPE_INFAVLIDFILE = 0;

    /**
     *
     */
    public static final int TYPE_INFAVLIDFILEFORMAT = 1;

    /**
     *
     */
    public static final int TYPE_INFAVLIDPUZZLE = 2;

    /**
     *
     */
    public static final int TYPE_SOLVERISBUSY = 3;

    /**
     *
     */
    public static final int TYPE_WEBIMPORTERROR = 4;
    
    /**
     *
     */
    public static final String ERRMSG_INFAVLIDFILE = "Érvénytelen fájl";

    /**
     *
     */
    public static final String ERRMSG_INFAVLIDFILEFORMAT = "Érvénytelen fájlformátum";

    /**
     *
     */
    public static final String ERRMSG_INFAVLIDPUZZLE = "Érvénytelen rejtvény";

    /**
     *
     */
    public static final String ERRMSG_SOLVERISBUSY = "A fejtő foglalt";

    /**
     *
     */
    public static final String ERRMSG_WEBIMPORTERROR = "Az importálás sikertelen";
    
    /**
     *
     */
    public static final String ERRMSG_UNDEFINED = "Ismeretlen hiba";
    
    private int type;

    /**
     *
     * @param message
     */
    public SolverException(String message) {
        super(message);
    }
    
    /**
     *
     * @param message
     * @param type
     */
    public SolverException(String message, int type) {
        super(message);
        this.type = type;
    }
    
    /**
     *
     * @param type
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
     *
     * @return
     */
    public int getType(){
        return this.type;
    }
    
}
