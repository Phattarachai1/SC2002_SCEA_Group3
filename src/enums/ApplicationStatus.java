package sc2002_grpproject.enums;

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
