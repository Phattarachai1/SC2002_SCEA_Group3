package sc2002_grpproject.enums;

/**
 * Status of a student's internship application
 * Tracks the application lifecycle
 */
public enum ApplicationStatus {
    /** Application submitted and awaiting company review */
    PENDING,
    /** Application approved by company representative */
    SUCCESSFUL,
    /** Application rejected by company representative */
    UNSUCCESSFUL,
    /** Application withdrawn by student */
    WITHDRAWN
}
