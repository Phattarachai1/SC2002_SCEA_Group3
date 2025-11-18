package sc2002_grpproject.controller.result;

/**
 * Result class for internship posting operations
 */
public class PostingResult {
    private final boolean success;
    private final String message;
    
    public PostingResult(boolean success, String message) {
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
