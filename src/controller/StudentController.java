package sc2002_grpproject.controller;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.InternshipLevel;
import sc2002_grpproject.enums.InternshipStatus;
import sc2002_grpproject.enums.ApplicationStatus;
import sc2002_grpproject.controller.result.StudentApplicationResult;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


public class StudentController {
    

    public static List<Internship> getAvailableInternships(Student student, List<Internship> allInternships) {
        return allInternships.stream()
            .filter(i -> i.isVisible())
            .filter(i -> i.getStatus() == InternshipStatus.APPROVED)
            .filter(i -> LocalDate.now().isAfter(i.getOpeningDate().minusDays(1)))
            .filter(i -> LocalDate.now().isBefore(i.getClosingDate().plusDays(1)))
            .filter(i -> i.getConfirmedPlacements() < i.getSlots())
            .filter(i -> isEligible(student, i))
            .collect(Collectors.toList());
    }
    
    /**
     * Filter internships by level
     */
    public static List<Internship> filterByLevel(List<Internship> internships, InternshipLevel level) {
        return internships.stream()
            .filter(i -> i.getLevel() == level)
            .collect(Collectors.toList());
    }
    
    /**
     * Filter internships by status
     */
    public static List<Internship> filterByStatus(List<Internship> internships, InternshipStatus status) {
        return internships.stream()
            .filter(i -> i.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    /**
     * Filter internships by closing date (before a given date)
     */
    public static List<Internship> filterByClosingDate(List<Internship> internships, LocalDate beforeDate) {
        return internships.stream()
            .filter(i -> i.getClosingDate().isBefore(beforeDate.plusDays(1)))
            .collect(Collectors.toList());
    }
    
    /**
     * Check if a student is eligible for an internship
     */
    public static boolean isEligible(Student student, Internship internship) {
        // Year 1-2 students can only apply for BASIC internships
        if (student.getYearOfStudy() <= 2 && internship.getLevel() != InternshipLevel.BASIC) {
            return false;
        }
        return true;
    }
    
    /**
     * Apply for an internship
     */
    public static StudentApplicationResult applyForInternship(Student student, Internship internship, 
                                                      List<InternshipApplication> allApplications) {
        // Check if internship is FILLED
        if (internship.getStatus() == InternshipStatus.FILLED) {
            return new StudentApplicationResult(false, "This internship is already filled.");
        }
        
        // Check if past closing date
        if (LocalDate.now().isAfter(internship.getClosingDate())) {
            return new StudentApplicationResult(false, "This internship has passed its closing date.");
        }
        
        // Check if already applied
        boolean alreadyApplied = allApplications.stream()
            .anyMatch(app -> app.getStudent().getUserID().equals(student.getUserID()) 
                          && app.getInternship().getInternshipID().equals(internship.getInternshipID()));
        
        if (alreadyApplied) {
            return new StudentApplicationResult(false, "You have already applied for this internship.");
        }
        
        // Check if student has an accepted placement
        boolean hasAcceptedPlacement = allApplications.stream()
            .anyMatch(app -> app.getStudent().getUserID().equals(student.getUserID())
                          && app.getStatus() == ApplicationStatus.SUCCESSFUL
                          && app.isPlacementConfirmed());
        
        if (hasAcceptedPlacement) {
            return new StudentApplicationResult(false, "You have already accepted a placement and cannot apply to other internships.");
        }
        
        // Check if already has 5 pending applications
        long pendingCount = allApplications.stream()
            .filter(app -> app.getStudent().getUserID().equals(student.getUserID()))
            .filter(app -> app.getStatus() == ApplicationStatus.PENDING)
            .count();
        
        if (pendingCount >= 5) {
            return new StudentApplicationResult(false, "You have reached the maximum of 5 pending applications.");
        }
        
        // Check if internship is still available
        if (internship.getConfirmedPlacements() >= internship.getSlots()) {
            return new StudentApplicationResult(false, "This internship is already filled.");
        }
        
        // Check eligibility
        if (!isEligible(student, internship)) {
            return new StudentApplicationResult(false, "You are not eligible for this internship level.");
        }
        
        // Create new application
        InternshipApplication newApp = new InternshipApplication(student, internship);
        allApplications.add(newApp);
        
        return new StudentApplicationResult(true, "Application submitted successfully!");
    }
    
    /**
     * Get all applications for a student
     */
    public static List<InternshipApplication> getStudentApplications(Student student, 
                                                                     List<InternshipApplication> allApplications) {
        return allApplications.stream()
            .filter(app -> app.getStudent().getUserID().equals(student.getUserID()))
            .collect(Collectors.toList());
    }
    
    /**
     * Accept an internship placement
     */
    public static StudentApplicationResult acceptPlacement(Student student, InternshipApplication application,
                                                   List<InternshipApplication> allApplications) {
        // Check if application is successful
        if (application.getStatus() != ApplicationStatus.SUCCESSFUL) {
            return new StudentApplicationResult(false, "Can only accept SUCCESSFUL placements.");
        }
        
        // Check if already accepted
        if (application.isPlacementConfirmed()) {
            return new StudentApplicationResult(false, "You have already accepted this placement.");
        }
        
        // Check if student has another accepted placement
        boolean hasOtherAcceptedPlacement = allApplications.stream()
            .anyMatch(app -> app.getStudent().getUserID().equals(student.getUserID())
                          && !app.getApplicationId().equals(application.getApplicationId())
                          && app.getStatus() == ApplicationStatus.SUCCESSFUL
                          && app.isPlacementConfirmed());
        
        if (hasOtherAcceptedPlacement) {
            return new StudentApplicationResult(false, "You have already accepted another placement.");
        }
        
        // Accept the placement
        application.setPlacementConfirmed(true);
        
        // Update internship confirmed placements
        application.getInternship().incrementConfirmedPlacements();
        
        return new StudentApplicationResult(true, "Placement accepted successfully!");
    }
    
    /**
     * Request withdrawal from an application
     */
    public static StudentApplicationResult requestWithdrawal(Student student, InternshipApplication application) {
        // Check if application can be withdrawn
        if (application.getStatus() == ApplicationStatus.WITHDRAWN) {
            return new StudentApplicationResult(false, "This application is already withdrawn.");
        }
        
        if (application.getStatus() == ApplicationStatus.UNSUCCESSFUL) {
            return new StudentApplicationResult(false, "Cannot withdraw an unsuccessful application.");
        }
        
        // Check if withdrawal status prevents new withdrawal request
        String withdrawalStatus = application.getWithdrawalStatus();
        if (withdrawalStatus != null && !withdrawalStatus.equals("-")) {
            if (withdrawalStatus.equals("PENDING")) {
                return new StudentApplicationResult(false, "A withdrawal request is already pending.");
            } else if (withdrawalStatus.equals("APPROVED")) {
                return new StudentApplicationResult(false, "Withdrawal has already been approved.");
            } else if (withdrawalStatus.equals("REJECTED")) {
                return new StudentApplicationResult(false, "Your previous withdrawal request was rejected. You cannot request withdrawal again.");
            }
        }
        
        // Request withdrawal - set status to PENDING, do NOT change application status
        application.setWithdrawalStatus("PENDING");
        
        return new StudentApplicationResult(true, "Withdrawal request submitted successfully!");
    }
    
    /**
     * Check for pending notifications (successful applications)
     */
    public static boolean hasPendingNotifications(Student student, List<InternshipApplication> allApplications) {
        return allApplications.stream()
            .anyMatch(app -> app.getStudent().getUserID().equals(student.getUserID())
                          && app.getStatus() == ApplicationStatus.SUCCESSFUL
                          && !app.isPlacementConfirmed());
    }
    
    /**
     * Get count of successful placements awaiting acceptance
     */
    public static long getSuccessfulPlacementsCount(Student student, List<InternshipApplication> allApplications) {
        return allApplications.stream()
            .filter(app -> app.getStudent().getUserID().equals(student.getUserID()))
            .filter(app -> app.getStatus() == ApplicationStatus.SUCCESSFUL)
            .filter(app -> !app.isPlacementConfirmed())
            .count();
    }
}
