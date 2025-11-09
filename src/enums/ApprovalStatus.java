package sc2002_grpproject.enums;

/**
 * Approval status for company representatives
 * Used by staff to manage company representative access
 */
public enum ApprovalStatus {
    PENDING,    // Registration awaiting staff approval
    APPROVED,   // Approved by staff, can post internships
    REJECTED    // Rejected by staff
}
