package sc2002_grpproject.controller.result;

import sc2002_grpproject.entity.User;

/**
 * Result class for authentication operations
 */
public class AuthResult {
    private final boolean success;
    private final User user;
    private final String message;
    
    /**
     * Constructs an AuthResult with the given parameters.
     * 
     * @param success true if authentication was successful, false otherwise
     * @param user the authenticated user object (null if authentication failed)
     * @param message descriptive message about the authentication result
     */
    public AuthResult(boolean success, User user, String message) {
        this.success = success;
        this.user = user;
        this.message = message;
    }
    
    /**
     * Checks if authentication was successful.
     * 
     * @return true if authentication succeeded, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * Gets the authenticated user.
     * 
     * @return the User object if authentication was successful, null otherwise
     */
    public User getUser() {
        return user;
    }
    
    /**
     * Gets the authentication result message.
     * 
     * @return descriptive message about the authentication result
     */
    public String getMessage() {
        return message;
    }
}
