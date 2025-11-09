package sc2002_grpproject.controller.stats;

/**
 * Statistics class for internships
 */
public class InternshipStats {
    private final long pending;
    private final long approved;
    private final long rejected;
    private final long filled;
    
    public InternshipStats(long pending, long approved, long rejected, long filled) {
        this.pending = pending;
        this.approved = approved;
        this.rejected = rejected;
        this.filled = filled;
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
    
    public long getFilled() {
        return filled;
    }
    
    public long getTotal() {
        return pending + approved + rejected + filled;
    }
}
