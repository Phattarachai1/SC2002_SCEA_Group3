package sc2002_grpproject.controller.result;

/**
 * Result class for company application operations
 */
public class CompanyApplicationResult {
    private final boolean success;
    private final String message;
    
    public CompanyApplicationResult(boolean success, String message) {
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
