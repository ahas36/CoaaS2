package au.coaas.cqc.utils.exceptions;

public class InvalidOperandsException extends Exception {
    public InvalidOperandsException() {
        super("Invalid number of entities used in the function.");
    }
}
