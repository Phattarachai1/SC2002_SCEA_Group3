package sc2002_grpproject.controller.result;

/**
 * Result class for company application operations
 */
public class CompanyApplicationResult {
    private final boolean success;
    private final String message;
    
    /**
     * Constructs a CompanyApplicationResult with the given parameters.
     * 
     * @param success true if the company's application processing was successful, false otherwise
     * @param message descriptive message about the result
     */
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
