package sc2002_grpproject.enums;

/**
 * Approval status for company representatives
 * Used by staff to manage company representative access
 */
public enum ApprovalStatus {
    /** Registration awaiting staff approval */
    PENDING,
    /** Approved by staff, can post internships */
    APPROVED,
    /** Rejected by staff */
    REJECTED
}
