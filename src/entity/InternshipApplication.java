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

    // Setters
    public void setStatus(ApplicationStatus status) { this.status = status; }
    
    public void setWithdrawalStatus(String withdrawalStatus) { 
        this.withdrawalStatus = withdrawalStatus; 
    }
    
    public void setPlacementConfirmed(boolean placementConfirmed) { 
        this.placementConfirmed = placementConfirmed; 
    }
}
