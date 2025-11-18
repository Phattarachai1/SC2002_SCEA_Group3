package sc2002_grpproject.entity;

import sc2002_grpproject.enums.ApplicationStatus;

/**
 * Represents a student's application for an internship.
 * Tracks application status, withdrawal requests, and placement confirmation.
 */
public class InternshipApplication {
    private static int nextId = 1;
    private String applicationId;
    private Student student;
    private Internship internship;
    private ApplicationStatus status;
    private String withdrawalStatus;  // null/"-", "PENDING", "APPROVED", "REJECTED"
    private boolean placementConfirmed;

    /**
     * Constructs a new InternshipApplication.
     * Initial status is PENDING with no withdrawal request or placement confirmation.
     * 
     * @param student the student applying for the internship
     * @param internship the internship being applied to
     */
    public InternshipApplication(Student student, Internship internship) {
        this.applicationId = "APP" + String.format("%04d", nextId++);
        this.student = student;
        this.internship = internship;
        this.status = ApplicationStatus.PENDING;
        this.withdrawalStatus = "-";
        this.placementConfirmed = false;
    }

    // Getters
    public String getApplicationId() { return applicationId; }
    public Student getStudent() { return student; }
    public Internship getInternship() { return internship; }
    public ApplicationStatus getStatus() { return status; }
    public String getWithdrawalStatus() { return withdrawalStatus; }
    public boolean isPlacementConfirmed() { return placementConfirmed; }
    
    /**
     * Checks if a withdrawal has been requested for this application.
     * 
     * @return true if withdrawal status is "PENDING", false otherwise
     */
    public boolean isWithdrawalRequested() {
        return "PENDING".equals(withdrawalStatus);
    }

    // Setters
    public void setStatus(ApplicationStatus status) { this.status = status; }
    
    public void setWithdrawalStatus(String withdrawalStatus) { 
        this.withdrawalStatus = withdrawalStatus; 
    }
    
    /**
     * Sets whether a withdrawal has been requested.
     * 
     * @param requested true to set withdrawal status to "PENDING", false to set it to "-"
     */
    public void setWithdrawalRequested(boolean requested) {
        this.withdrawalStatus = requested ? "PENDING" : "-";
    }
    
    public void setPlacementConfirmed(boolean placementConfirmed) { 
        this.placementConfirmed = placementConfirmed; 
    }
}
