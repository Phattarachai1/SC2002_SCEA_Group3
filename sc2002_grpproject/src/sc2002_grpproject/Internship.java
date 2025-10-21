package sc2002_grpproject;

public class Internship {
    private static int nextId = 1;
    private String internshipID;
    private String title;
    private String companyName;
    private CompanyRepresentative companyRepInCharge;
    private InternshipLevel level;
    private String preferredMajor;
    private int slots;
    private InternshipStatus status;
    private boolean isVisible;

    public enum InternshipLevel { BASIC, INTERMEDIATE, ADVANCED }
    public enum InternshipStatus { PENDING, APPROVED, REJECTED, FILLED }

    public Internship(String title, String companyName, CompanyRepresentative rep, InternshipLevel level, String preferredMajor, int slots) {
        this.internshipID = "INT" + String.format("%04d", nextId++);
        this.title = title;
        this.companyName = companyName;
        this.companyRepInCharge = rep;
        this.level = level;
        this.preferredMajor = preferredMajor;
        this.slots = slots;
        this.status = InternshipStatus.PENDING;
        this.isVisible = false;
    }

    // Getters
    public String getInternshipID() { return internshipID; }
    public String getTitle() { return title; }
    public String getCompanyName() { return companyName; }
    public InternshipLevel getLevel() { return level; }
    public String getPreferredMajor() { return preferredMajor; }
    public int getSlots() { return slots; }
    public InternshipStatus getStatus() { return status; }
    public boolean isVisible() { return isVisible; }
    public CompanyRepresentative getCompanyRepInCharge() { return companyRepInCharge; }

    // Setters
    public void setStatus(InternshipStatus status) { this.status = status; }
    public void setVisible(boolean visible) { isVisible = visible; }
    public void decreaseSlots() { 
        if (this.slots > 0) this.slots--;
        if (this.slots == 0) this.status = InternshipStatus.FILLED;
    }
}