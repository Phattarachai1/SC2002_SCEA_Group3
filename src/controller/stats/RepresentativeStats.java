package sc2002_grpproject.controller.stats;

/**
 * Statistics class for company representatives
 */
public class RepresentativeStats {
    private final long pending;
    private final long approved;
    private final long rejected;
    
    public RepresentativeStats(long pending, long approved, long rejected) {
        this.pending = pending;
        this.approved = approved;
        this.rejected = rejected;
    }
    
    public long getPending() {
        return pending;
    }
    
    public long getApproved() {
        return approved;
    }
    
    public long getRejected() {
        return rejected;
    }
    
    public long getTotal() {
        return pending + approved + rejected;
    }
}
