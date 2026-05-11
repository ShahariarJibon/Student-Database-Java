/**
 * Custom exception for invalid input handling.
 * Demonstrates exception handling.
 * 
 * @author JIBON
 * @version 1.0
 */
public class InvalidInputException extends Exception {
    
    /**
     * Default constructor.
     */
    public InvalidInputException() { super(); }
    
    /**
     * Constructor with message.
     * @param message Error message
     */
    public InvalidInputException(String message) {
        super(message);
    }
    
    /**
     * Constructor with message and cause.
     * @param message Error message
     * @param cause Throwable cause
     */
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}