package sc2002_grpproject.entity;

/**
 * Abstract base class representing a user in the internship placement system.
 * All users (Students, Staff, Company Representatives) extend this class.
 */
public abstract class User {
    private String userID;
    private String name;
    private String password;

    /**
     * Constructs a new User with the specified ID and name.
     * Default password is set to "password".
     * 
     * @param userID the unique identifier for the user
     * @param name the user's full name
     */
    public User(String userID, String name) {
        this.userID = userID;
        this.name = name;
        this.password = "password"; // Default password
    }

    /**
     * Verifies if the entered password matches the user's password.
     * 
     * @param enteredPassword the password to check
     * @return true if the password matches, false otherwise
     */
    public boolean checkPassword(String enteredPassword) {
        return this.password.equals(enteredPassword);
    }

    /**
     * Changes the user's password to a new password.
     * 
     * @param newPassword the new password to set
     */
    public void changePassword(String newPassword) {
        this.password = newPassword;
        System.out.println("Password changed successfully.");
    }

    /**
     * Gets the user's unique identifier.
     * 
     * @return the user ID
     */
    public String getUserID() { return userID; }
    
    /**
     * Gets the user's name.
     * 
     * @return the user's full name
     */
    public String getName() { return name; }
    
    /**
     * Gets the user's password.
     * 
     * @return the password
     */
    public String getPassword() { return password; }
}
