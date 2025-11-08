package sc2002_grpproject.entity;

import sc2002_grpproject.enums.Enums.ApplicationStatus;

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
    private boolean withdrawalRequested;
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
        this.withdrawalRequested = false;
        this.placementConfirmed = false;
    }

    // Getters
    public String getApplicationId() { return applicationId; }
    public Student getStudent() { return student; }
    public Internship getInternship() { return internship; }
    public ApplicationStatus getStatus() { return status; }
    public boolean isWithdrawalRequested() { return withdrawalRequested; }
    public boolean isPlacementConfirmed() { return placementConfirmed; }

    // Setters
    public void setStatus(ApplicationStatus status) { this.status = status; }
    
    public void setWithdrawalRequested(boolean withdrawalRequested) { 
        this.withdrawalRequested = withdrawalRequested; 
    }
    
    public void setPlacementConfirmed(boolean placementConfirmed) { 
        this.placementConfirmed = placementConfirmed; 
    }
}
