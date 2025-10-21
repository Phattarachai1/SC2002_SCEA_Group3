package sc2002_grpproject;

public class InternshipApplication {
    private static int nextId = 1;
    private String applicationId;
    private Student student;
    private Internship internship;
    private ApplicationStatus status;

    public enum ApplicationStatus { PENDING, SUCCESSFUL, UNSUCCESSFUL, WITHDRAWN }

    public InternshipApplication(Student student, Internship internship) {
        this.applicationId = "APP" + String.format("%04d", nextId++);
        this.student = student;
        this.internship = internship;
        this.status = ApplicationStatus.PENDING;
    }
    
    // Getters
    public String getApplicationId() { return applicationId; }
    public Student getStudent() { return student; }
    public Internship getInternship() { return internship; }
    public ApplicationStatus getStatus() { return status; }
    
    // Setter
    public void setStatus(ApplicationStatus status) { this.status = status; }
}