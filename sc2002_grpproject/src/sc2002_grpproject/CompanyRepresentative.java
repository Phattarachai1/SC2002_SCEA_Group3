package sc2002_grpproject;

public class CompanyRepresentative extends User {
    private String companyName;
    private String department;
    private String position;
    private ApprovalStatus status;
    private int internshipsCreated;
    private static final int MAX_INTERNSHIPS = 5;

    public enum ApprovalStatus { PENDING, APPROVED, REJECTED }

    public CompanyRepresentative(String userID, String name, String companyName, 
                                 String department, String position) {
        super(userID, name); // UserID for company rep is their email
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.status = ApprovalStatus.PENDING; // Default status
        this.internshipsCreated = 0;
    }

    // Getters and Setters
    public String getCompanyName() { return companyName; }
    public String getDepartment() { return department; }
    public String getPosition() { return position; }
    public ApprovalStatus getStatus() { return status; }
    public int getInternshipsCreated() { return internshipsCreated; }
    
    public void setStatus(ApprovalStatus status) { this.status = status; }
    
    public boolean canCreateMoreInternships() {
        return internshipsCreated < MAX_INTERNSHIPS;
    }
    
    public void incrementInternshipsCreated() {
        if (internshipsCreated < MAX_INTERNSHIPS) {
            internshipsCreated++;
        }
    }
    
    public void decrementInternshipsCreated() {
        if (internshipsCreated > 0) {
            internshipsCreated--;
        }
    }
}
