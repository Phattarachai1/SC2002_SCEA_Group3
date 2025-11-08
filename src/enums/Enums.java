package sc2002_grpproject.enums;

/**
 * Centralized enumeration definitions for the SC2002 Group Project
 * Contains all enums used across the application
 */
public class Enums {
    
    /**
     * Status levels for internship positions
     * Defines the difficulty/year requirement for internships
     */
    public enum InternshipLevel {
        BASIC,          // For Year 1-2 students
        INTERMEDIATE,   // For Year 3+ students
        ADVANCED        // For Year 3+ students
    }
    
    /**
     * Status of an internship posting
     * Tracks the approval and availability state
     */
    public enum InternshipStatus {
        PENDING,    // Awaiting staff approval
        APPROVED,   // Approved by staff and open for applications
        REJECTED,   // Rejected by staff
        FILLED      // All slots have been filled
    }
    
    /**
     * Status of a student's internship application
     * Tracks the application lifecycle
     */
    public enum ApplicationStatus {
        PENDING,        // Application submitted, awaiting review
        SUCCESSFUL,     // Application approved by company
        UNSUCCESSFUL,   // Application rejected by company
        WITHDRAWN       // Application withdrawn by student
    }
    
    /**
     * Approval status for company representatives
     * Used by staff to manage company representative access
     */
    public enum ApprovalStatus {
        PENDING,    // Registration awaiting staff approval
        APPROVED,   // Approved by staff, can post internships
        REJECTED    // Rejected by staff
    }
}
