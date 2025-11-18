package sc2002_grpproject.controller.result;

/**
 * Result class for password change operations
 */
public class PasswordChangeResult {
    private final boolean success;
    private final String message;
    
    /**
     * Constructs a PasswordChangeResult with the given parameters.
     * 
     * @param success true if the password change was successful, false otherwise
     * @param message descriptive message about the password change result
     */
    public PasswordChangeResult(boolean success, String message) {
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
