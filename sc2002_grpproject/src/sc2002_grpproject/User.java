package sc2002_grpproject;

public abstract class User {
    private String userID;
    private String name;
    private String password;

    public User(String userID, String name) {
        this.userID = userID;
        this.name = name;
        this.password = "password"; // Default password
    }

    public boolean checkPassword(String enteredPassword) {
        return this.password.equals(enteredPassword);
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
        System.out.println("Password changed successfully.");
    }

    // Standard Getters
    public String getUserID() { return userID; }
    public String getName() { return name; }
    public String getPassword() { return password; }
}