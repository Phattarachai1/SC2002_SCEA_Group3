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
     * Authenticate a user with userID and password
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
     * Change user password
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
     * Get user type as a string
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
     * Check if user is a student
     */
    public static boolean isStudent(User user) {
        return user instanceof Student;
    }
    
    /**
     * Check if user is a company representative
     */
    public static boolean isCompanyRepresentative(User user) {
        return user instanceof CompanyRepresentative;
    }
    
    /**
     * Check if user is staff
     */
    public static boolean isStaff(User user) {
        return user instanceof CareerCenterStaff;
    }
}
