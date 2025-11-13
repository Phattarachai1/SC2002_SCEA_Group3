package sc2002_grpproject.controller;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.InternshipLevel;
import sc2002_grpproject.enums.InternshipStatus;
import sc2002_grpproject.enums.ApprovalStatus;
import sc2002_grpproject.controller.result.ApprovalResult;
import sc2002_grpproject.controller.stats.InternshipStats;
import sc2002_grpproject.controller.stats.RepresentativeStats;
import java.util.Comparator;
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
            .sorted(Comparator.comparing(Internship::getTitle))
            .collect(Collectors.toList());
    }
    

    public static List<Internship> filterByLevel(List<Internship> internships, InternshipLevel level) {
        return internships.stream()
            .filter(i -> i.getLevel() == level)
            .sorted(Comparator.comparing(Internship::getTitle))
            .collect(Collectors.toList());
    }
    

    public static List<Internship> filterByCompany(List<Internship> internships, String companyName) {
        return internships.stream()
            .filter(i -> i.getCompanyName().toLowerCase().contains(companyName.toLowerCase()))
            .sorted(Comparator.comparing(Internship::getTitle))
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
}
