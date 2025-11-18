package sc2002_grpproject.controller;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.controller.result.AuthResult;
import sc2002_grpproject.controller.result.PasswordChangeResult;
import java.util.List;

/**
 * Controller class for handling authentication and user management
 */
public class AuthController {
    
    /**
     * Authenticate a user with userID and password.
     * Validates credentials against the user database and returns authentication result.
     * 
     * @param userID the unique identifier (email) of the user attempting to login
     * @param password the password entered by the user
     * @param allUsers the list of all users in the system
     * @return AuthResult containing success status, authenticated user object, and message
     */
    public static AuthResult authenticate(String userID, String password, List<User> allUsers) {
        User user = allUsers.stream()
            .filter(u -> u.getUserID().equals(userID) && u.getPassword().equals(password))
            .findFirst()
            .orElse(null);
        
        if (user == null) {
            return new AuthResult(false, null, "Invalid credentials.");
        }
        
        return new AuthResult(true, user, "Login successful!");
    }
    
    /**
     * Change user password with validation.
     * Validates the old password, checks new password requirements,
     * and ensures the new password is different from the old one.
     * 
     * @param user the user whose password is being changed
     * @param oldPassword the current password for verification
     * @param newPassword the new password to set (minimum 4 characters)
     * @return PasswordChangeResult indicating success or failure with appropriate message
     */
    public static PasswordChangeResult changePassword(User user, String oldPassword, String newPassword) {
        // Verify old password
        if (!user.checkPassword(oldPassword)) {
            return new PasswordChangeResult(false, "Current password is incorrect.");
        }
        
        // Validate new password
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return new PasswordChangeResult(false, "New password cannot be empty.");
        }
        
        if (newPassword.length() < 4) {
            return new PasswordChangeResult(false, "New password must be at least 4 characters long.");
        }
        
        if (newPassword.equals(oldPassword)) {
            return new PasswordChangeResult(false, "New password must be different from the old password.");
        }
        
        // Change password
        user.changePassword(newPassword);
        
        return new PasswordChangeResult(true, "Password changed successfully!");
    }
    
    /**
     * Get user type as a string.
     * Determines the role of the user (Student, Company Representative, or Career Center Staff).
     * 
     * @param user the user whose type is being determined
     * @return the user type as a string ("Student", "Company Representative", "Career Center Staff", or "Unknown")
     */
    public static String getUserType(User user) {
        if (user instanceof Student) {
            return "Student";
        } else if (user instanceof CompanyRepresentative) {
            return "Company Representative";
        } else if (user instanceof CareerCenterStaff) {
            return "Career Center Staff";
        }
        return "Unknown";
    }
    
    /**
     * Check if user is a student.
     * 
     * @param user the user to check
     * @return true if the user is a Student, false otherwise
     */
    public static boolean isStudent(User user) {
        return user instanceof Student;
    }
    
    /**
     * Check if user is a company representative.
     * 
     * @param user the user to check
     * @return true if the user is a CompanyRepresentative, false otherwise
     */
    public static boolean isCompanyRepresentative(User user) {
        return user instanceof CompanyRepresentative;
    }
    
    /**
     * Check if user is career center staff.
     * 
     * @param user the user to check
     * @return true if the user is CareerCenterStaff, false otherwise
     */
    public static boolean isStaff(User user) {
        return user instanceof CareerCenterStaff;
    }
}
