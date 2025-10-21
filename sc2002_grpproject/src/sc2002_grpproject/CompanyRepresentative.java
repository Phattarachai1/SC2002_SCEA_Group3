package sc2002_grpproject;

public class CompanyRepresentative extends User {
    private String companyName;
    private String department;
    private String position;
    private ApprovalStatus status;

    public enum ApprovalStatus { PENDING, APPROVED, REJECTED }

    public CompanyRepresentative(String userID, String name, String companyName, String department, String position) {
        super(userID, name); // UserID for company rep is their email
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.status = ApprovalStatus.PENDING; // Default status
    }

    // Getters and Setters
    public String getCompanyName() { return companyName; }
    public ApprovalStatus getStatus() { return status; }
    public void setStatus(ApprovalStatus status) { this.status = status; }
}