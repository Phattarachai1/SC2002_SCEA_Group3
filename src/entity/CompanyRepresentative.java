package sc2002_grpproject.entity;

import sc2002_grpproject.enums.Enums.ApprovalStatus;

/**
 * Represents a company representative in the internship placement system.
 * Company representatives can create and manage internship postings.
 */
public class CompanyRepresentative extends User {
    private String companyName;
    private String department;
    private String position;
    private ApprovalStatus status;
    private int internshipsCreated;
    private static final int MAX_INTERNSHIPS = 5;

    /**
     * Constructs a new CompanyRepresentative with the specified details.
     * Initial status is set to PENDING approval.
     * 
     * @param userID the unique identifier (typically email)
     * @param name the representative's full name
     * @param companyName the name of the company
     * @param department the department within the company
     * @param position the representative's position/title
     */
    public CompanyRepresentative(String userID, String name, String companyName, 
                                 String department, String position) {
        super(userID, name); // UserID for company rep is their email
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.status = ApprovalStatus.PENDING; // Default status
        this.internshipsCreated = 0;
    }

    /**
     * Gets the company name.
     * 
     * @return the company name
     */
    public String getCompanyName() { return companyName; }
    
    /**
     * Gets the department.
     * 
     * @return the department
     */
    public String getDepartment() { return department; }
    
    /**
     * Gets the position/title.
     * 
     * @return the position
     */
    public String getPosition() { return position; }
    
    /**
     * Gets the approval status.
     * 
     * @return the approval status
     */
    public ApprovalStatus getStatus() { return status; }
    
    /**
     * Gets the number of internships created.
     * 
     * @return the count of internships created
     */
    public int getInternshipsCreated() { return internshipsCreated; }
    
    /**
     * Sets the approval status.
     * 
     * @param status the new approval status
     */
    public void setStatus(ApprovalStatus status) { this.status = status; }
    
    /**
     * Checks if the representative can create more internships.
     * 
     * @return true if under the MAX_INTERNSHIPS limit, false otherwise
     */
    public boolean canCreateMoreInternships() {
        return internshipsCreated < MAX_INTERNSHIPS;
    }
    
    /**
     * Increments the count of internships created.
     * Only increments if under the maximum limit.
     */
    public void incrementInternshipsCreated() {
        if (internshipsCreated < MAX_INTERNSHIPS) {
            internshipsCreated++;
        }
    }
    
    /**
     * Decrements the count of internships created.
     * Only decrements if greater than zero.
     */
    public void decrementInternshipsCreated() {
        if (internshipsCreated > 0) {
            internshipsCreated--;
        }
    }
}
