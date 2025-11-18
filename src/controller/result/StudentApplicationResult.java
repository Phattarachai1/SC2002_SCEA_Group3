package sc2002_grpproject.controller.result;

/**
 * Result class for student application operations
 */
public class StudentApplicationResult {
    private final boolean success;
    private final String message;
    
    /**
     * Constructs a StudentApplicationResult with the given parameters.
     * 
     * @param success true if the student's application operation was successful, false otherwise
     * @param message descriptive message about the application result
     */
    public StudentApplicationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }
}
