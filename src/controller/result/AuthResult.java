package sc2002_grpproject.controller.result;

import sc2002_grpproject.entity.User;

/**
 * Result class for authentication operations
 */
public class AuthResult {
    private final boolean success;
    private final User user;
    private final String message;
    
    public AuthResult(boolean success, User user, String message) {
        this.success = success;
        this.user = user;
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public User getUser() {
        return user;
    }
    
    public String getMessage() {
        return message;
    }
}
