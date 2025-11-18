package sc2002_grpproject.controller.result;

/**
 * Result class for approval operations
 */
public class ApprovalResult {
    private final boolean success;
    private final String message;
    
    /**
     * Constructs an ApprovalResult with the given parameters.
     * 
     * @param success true if the approval/rejection operation was successful, false otherwise
     * @param message descriptive message about the approval result
     */
    public ApprovalResult(boolean success, String message) {
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
