package sc2002_grpproject;

public class CareerCenterStaff extends User {
    private String staffDepartment;

    public CareerCenterStaff(String userID, String name, String staffDepartment) {
        super(userID, name);
        this.staffDepartment = staffDepartment;
    }
    
    // Getter
    public String getStaffDepartment() { return staffDepartment; }
}