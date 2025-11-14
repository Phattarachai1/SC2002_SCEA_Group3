package sc2002_grpproject.controller;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.InternshipLevel;
import sc2002_grpproject.enums.InternshipStatus;
import sc2002_grpproject.enums.ApplicationStatus;
import sc2002_grpproject.controller.result.PostingResult;
import sc2002_grpproject.controller.result.CompanyApplicationResult;
import sc2002_grpproject.utils.IInternshipFilter;
import sc2002_grpproject.utils.InternshipFilterService;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class for handling company representative business logic
 */
public class CompanyController {
    
    private static final IInternshipFilter filterService = new InternshipFilterService();
    
    /**
     * Get all internships posted by a company representative
     */
    public static List<Internship> getCompanyInternships(CompanyRepresentative rep, List<Internship> allInternships) {
        return allInternships.stream()
            .filter(i -> i.getCompanyRepInCharge().getUserID().equals(rep.getUserID()))
            .collect(Collectors.toList());
    }
    
    /**
     * Filter internships by status
     */
    public static List<Internship> filterByStatus(List<Internship> internships, InternshipStatus status) {
        return filterService.filterByStatus(internships, status);
    }
    
    /**
     * Filter internships by level
     */
    public static List<Internship> filterByLevel(List<Internship> internships, InternshipLevel level) {
        return filterService.filterByLevel(internships, level);
    }
    
    /**
     * Filter internships by visibility
     */
    public static List<Internship> filterByVisibility(List<Internship> internships, boolean visible) {
        return filterService.filterByVisibility(internships, visible);
    }
    
    /**
     * Create a new internship posting
     */
    public static PostingResult createInternship(CompanyRepresentative rep, List<Internship> allInternships,
                                                String title, String description, int slots,
                                                InternshipLevel level, String preferredMajor,
                                                LocalDate openingDate, LocalDate closingDate) {
        // Validate dates
        if (openingDate.isAfter(closingDate)) {
            return new PostingResult(false, "Opening date must be before closing date.");
        }
        
        if (closingDate.isBefore(LocalDate.now())) {
            return new PostingResult(false, "Closing date must be in the future.");
        }
        
        // Validate slots
        if (slots <= 0) {
            return new PostingResult(false, "Number of slots must be positive.");
        }
        
        if (slots > 10) {
            return new PostingResult(false, "Maximum 10 slots allowed per internship.");
        }
        
        // Check if rep can create more internships using active count
        if (!rep.canCreateMoreInternships(allInternships)) {
            return new PostingResult(false, "You have reached the maximum of 5 active internships.");
        }
        
        // Create internship
        Internship newInternship = new Internship(
            title,
            description,
            rep.getCompanyName(),
            rep,
            level,
            preferredMajor,
            slots,
            openingDate,
            closingDate
        );
        
        allInternships.add(newInternship);
        rep.incrementInternshipsCreated();
        
        return new PostingResult(true, "Internship posted successfully! Awaiting staff approval.");
    }
    
    /**
     * Update an existing internship
     */
    public static PostingResult updateInternship(Internship internship, String title, String description,
                                                int slots, InternshipLevel level,
                                                String preferredMajor,
                                                LocalDate openingDate, LocalDate closingDate) {
        // Check if internship can be edited
        if (internship.getStatus() == InternshipStatus.APPROVED) {
            return new PostingResult(false, "Cannot edit approved internships.");
        }
        
        if (internship.getStatus() == InternshipStatus.REJECTED) {
            return new PostingResult(false, "Cannot edit rejected internships.");
        }
        
        if (internship.getStatus() == InternshipStatus.FILLED) {
            return new PostingResult(false, "Cannot edit filled internships.");
        }
        
        // Validate dates
        if (openingDate.isAfter(closingDate)) {
            return new PostingResult(false, "Opening date must be before closing date.");
        }
        
        // Validate slots
        if (slots <= 0) {
            return new PostingResult(false, "Number of slots must be positive.");
        }
        
        // Update internship
        internship.setTitle(title);
        internship.setDescription(description);
        internship.setSlots(slots);
        internship.setLevel(level);
        internship.setPreferredMajor(preferredMajor);
        internship.setOpeningDate(openingDate);
        internship.setClosingDate(closingDate);
        
        return new PostingResult(true, "Internship updated successfully!");
    }
    
    /**
     * Delete an internship posting
     */
    public static PostingResult deleteInternship(Internship internship, List<Internship> allInternships,
                                                List<InternshipApplication> allApplications) {
        // Check if internship can be deleted
        if (internship.getStatus() == InternshipStatus.APPROVED) {
            return new PostingResult(false, "Cannot delete approved internships.");
        }
        
        if (internship.getStatus() == InternshipStatus.FILLED) {
            return new PostingResult(false, "Cannot delete filled internships.");
        }
        
        // Check if there are applications
        boolean hasApplications = allApplications.stream()
            .anyMatch(app -> app.getInternship().getInternshipID().equals(internship.getInternshipID()));
        
        if (hasApplications) {
            return new PostingResult(false, "Cannot delete internship with applications.");
        }
        
        // Delete internship
        allInternships.remove(internship);
        internship.getCompanyRepInCharge().decrementInternshipsCreated();
        
        return new PostingResult(true, "Internship deleted successfully!");
    }
    
    /**
     * Toggle internship visibility
     */
    public static PostingResult toggleVisibility(Internship internship) {
        internship.setVisible(!internship.isVisible());
        String status = internship.isVisible() ? "visible" : "hidden";
        return new PostingResult(true, "Internship is now " + status + ".");
    }
    
    /**
     * Get applications for a specific internship
     */
    public static List<InternshipApplication> getInternshipApplications(Internship internship,
                                                                        List<InternshipApplication> allApplications) {
        return allApplications.stream()
            .filter(app -> app.getInternship().getInternshipID().equals(internship.getInternshipID()))
            .collect(Collectors.toList());
    }
    
    /**
     * Approve an application
     */
    public static CompanyApplicationResult approveApplication(InternshipApplication application, Internship internship,
                                                       List<InternshipApplication> allApplications) {
        // Check if application is pending
        if (application.getStatus() != ApplicationStatus.PENDING) {
            return new CompanyApplicationResult(false, "Can only approve pending applications.");
        }
        
        // Calculate available slots = total slots - SUCCESSFUL applications (regardless of confirmation)
        long successfulApps = allApplications.stream()
            .filter(app -> app.getInternship().getInternshipID().equals(internship.getInternshipID()))
            .filter(app -> app.getStatus() == ApplicationStatus.SUCCESSFUL)
            .count();
        
        long availableSlots = internship.getSlots() - successfulApps;
        
        if (availableSlots <= 0) {
            return new CompanyApplicationResult(false, "No available slots for this internship.");
        }
        
        // Approve application
        application.setStatus(ApplicationStatus.SUCCESSFUL);
        
        return new CompanyApplicationResult(true, "Application approved successfully!");
    }
    
    /**
     * Reject an application
     */
    public static CompanyApplicationResult rejectApplication(InternshipApplication application) {
        // Check if application is pending
        if (application.getStatus() != ApplicationStatus.PENDING) {
            return new CompanyApplicationResult(false, "Can only reject pending applications.");
        }
        
        // Reject application
        application.setStatus(ApplicationStatus.UNSUCCESSFUL);
        
        return new CompanyApplicationResult(true, "Application rejected.");
    }
}
