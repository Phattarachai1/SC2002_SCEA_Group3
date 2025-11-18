package sc2002_grpproject.enums;

/**
 * Status of an internship posting
 * Tracks the approval and availability state
 */
public enum InternshipStatus {
    /** Awaiting staff approval */
    PENDING,
    /** Approved by staff and open for applications */
    APPROVED,
    /** Rejected by staff */
    REJECTED,
    /** All slots have been filled */
    FILLED
}
