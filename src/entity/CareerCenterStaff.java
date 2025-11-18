package sc2002_grpproject.entity;

/**
 * Represents a career center staff member in the internship placement system.
 * Staff members can approve company representatives, internships, and manage withdrawals.
 */
public class CareerCenterStaff extends User {
    private String staffDepartment;

    /**
     * Constructs a new CareerCenterStaff with the specified details.
     * 
     * @param userID the unique identifier for the staff member
     * @param name the staff member's full name
     * @param staffDepartment the department the staff member belongs to
     */
    public CareerCenterStaff(String userID, String name, String staffDepartment) {
        super(userID, name);
        this.staffDepartment = staffDepartment;
    }
    
    /**
     * Gets the staff member's department.
     * 
     * @return the staff department
     */
    public String getStaffDepartment() { return staffDepartment; }
}
