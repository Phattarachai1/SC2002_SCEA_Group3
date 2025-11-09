package sc2002_grpproject.controller.result;

/**
 * Result class for approval operations
 */
public class ApprovalResult {
    private final boolean success;
    private final String message;
    
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
