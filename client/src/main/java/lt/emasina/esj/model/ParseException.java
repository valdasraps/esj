package lt.emasina.esj.model;

/**
 * ParseException
 * @author Stasys
 */
public class ParseException extends Throwable {

    public ParseException(String message, Object... params) {
        super(String.format(message, params));
    }
    
    public ParseException(Exception ex) {
        super(ex);
    }
    
}
