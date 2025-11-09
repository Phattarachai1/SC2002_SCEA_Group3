package sc2002_grpproject.controller.result;

/**
 * Result class for student application operations
 */
public class StudentApplicationResult {
    private final boolean success;
    private final String message;
    
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
