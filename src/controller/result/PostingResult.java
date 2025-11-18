package sc2002_grpproject.controller.result;

/**
 * Result class for internship posting operations
 */
public class PostingResult {
    private final boolean success;
    private final String message;
    
    /**
     * Constructs a PostingResult with the given parameters.
     * 
     * @param success true if the internship posting operation was successful, false otherwise
     * @param message descriptive message about the operation result
     */
    public PostingResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    /**
     * Checks if the posting operation was successful.
     * 
     * @return true if operation succeeded, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * Gets the result message.
     * 
     * @return descriptive message about the operation result
     */
    public String getMessage() {
        return message;
    }
}
