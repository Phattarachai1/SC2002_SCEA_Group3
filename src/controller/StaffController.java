package sc2002_grpproject.controller;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.Enums.*;
import java.util.List;
import java.util.stream.Collectors;

public class StaffController {
    

    public static List<CompanyRepresentative> getPendingRepresentatives(List<CompanyRepresentative> allReps) {
        return allReps.stream()
            .filter(rep -> rep.getStatus() == ApprovalStatus.PENDING)
            .collect(Collectors.toList());
    }
    

    public static List<CompanyRepresentative> getApprovedRepresentatives(List<CompanyRepresentative> allReps) {
        return allReps.stream()
            .filter(rep -> rep.getStatus() == ApprovalStatus.APPROVED)
            .collect(Collectors.toList());
    }
    

    public static ApprovalResult approveRepresentative(CompanyRepresentative rep) {
        if (rep.getStatus() != ApprovalStatus.PENDING) {
            return new ApprovalResult(false, "Can only approve pending representatives.");
        }
        
        rep.setStatus(ApprovalStatus.APPROVED);
        return new ApprovalResult(true, "Company representative approved successfully!");
    }
    

    public static ApprovalResult rejectRepresentative(CompanyRepresentative rep) {
        if (rep.getStatus() != ApprovalStatus.PENDING) {
            return new ApprovalResult(false, "Can only reject pending representatives.");
        }
        
        rep.setStatus(ApprovalStatus.REJECTED);
        return new ApprovalResult(true, "Company representative rejected.");
    }
    

    public static List<Internship> getPendingInternships(List<Internship> allInternships) {
        return allInternships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.PENDING)
            .collect(Collectors.toList());
    }
    

    public static List<Internship> getApprovedInternships(List<Internship> allInternships) {
        return allInternships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.APPROVED)
            .collect(Collectors.toList());
    }
    

    public static List<Internship> filterByStatus(List<Internship> internships, InternshipStatus status) {
        return internships.stream()
            .filter(i -> i.getStatus() == status)
            .collect(Collectors.toList());
    }
    

    public static List<Internship> filterByLevel(List<Internship> internships, InternshipLevel level) {
        return internships.stream()
            .filter(i -> i.getLevel() == level)
            .collect(Collectors.toList());
    }
    

    public static List<Internship> filterByCompany(List<Internship> internships, String companyName) {
        return internships.stream()
            .filter(i -> i.getCompanyName().toLowerCase().contains(companyName.toLowerCase()))
            .collect(Collectors.toList());
    }
    

    public static ApprovalResult approveInternship(Internship internship) {
        if (internship.getStatus() != InternshipStatus.PENDING) {
            return new ApprovalResult(false, "Can only approve pending internships.");
        }
        
        internship.setStatus(InternshipStatus.APPROVED);
        return new ApprovalResult(true, "Internship approved successfully!");
    }
    

    public static ApprovalResult rejectInternship(Internship internship) {
        if (internship.getStatus() != InternshipStatus.PENDING) {
            return new ApprovalResult(false, "Can only reject pending internships.");
        }
        
        internship.setStatus(InternshipStatus.REJECTED);
        return new ApprovalResult(true, "Internship rejected.");
    }
    
    /**
     * Get all applications for an internship
     */
    public static List<InternshipApplication> getInternshipApplications(Internship internship,
                                                                        List<InternshipApplication> allApplications) {
        return allApplications.stream()
            .filter(app -> app.getInternship().getInternshipID().equals(internship.getInternshipID()))
            .collect(Collectors.toList());
    }
    
    /**
     * Get statistics for internships
     */
    public static InternshipStats getInternshipStats(List<Internship> allInternships) {
        long pending = allInternships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.PENDING)
            .count();
        
        long approved = allInternships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.APPROVED)
            .count();
        
        long rejected = allInternships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.REJECTED)
            .count();
        
        long filled = allInternships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.FILLED)
            .count();
        
        return new InternshipStats(pending, approved, rejected, filled);
    }
    
    /**
     * Get statistics for company representatives
     */
    public static RepresentativeStats getRepresentativeStats(List<CompanyRepresentative> allReps) {
        long pending = allReps.stream()
            .filter(rep -> rep.getStatus() == ApprovalStatus.PENDING)
            .count();
        
        long approved = allReps.stream()
            .filter(rep -> rep.getStatus() == ApprovalStatus.APPROVED)
            .count();
        
        long rejected = allReps.stream()
            .filter(rep -> rep.getStatus() == ApprovalStatus.REJECTED)
            .count();
        
        return new RepresentativeStats(pending, approved, rejected);
    }
    
    /**
     * Result class for approval operations
     */
    public static class ApprovalResult {
        private final boolean success;
        private final String message;
        
        public ApprovalResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    /**
     * Statistics class for internships
     */
    public static class InternshipStats {
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
    
    /**
     * Statistics class for company representatives
     */
    public static class RepresentativeStats {
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
}
