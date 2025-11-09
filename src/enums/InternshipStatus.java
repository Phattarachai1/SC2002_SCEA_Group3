package sc2002_grpproject.enums;

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
