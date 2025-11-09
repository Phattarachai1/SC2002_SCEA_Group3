package sc2002_grpproject.entity;

import sc2002_grpproject.enums.Enums.InternshipLevel;
import sc2002_grpproject.enums.Enums.InternshipStatus;
import java.time.LocalDate;

/**
 * Represents an internship posting in the system.
 * Internships are created by company representatives and must be approved by staff.
 */
public class Internship {
    private static int nextId = 1;
    private String internshipID;
    private String title;
    private String description;
    private String companyName;
    private CompanyRepresentative companyRepInCharge;
    private InternshipLevel level;
    private String preferredMajor;
    private int slots;
    private int confirmedPlacements; // Track confirmed students
    private LocalDate openingDate;
    private LocalDate closingDate;
    private InternshipStatus status;
    private boolean isVisible;

    /**
     * Constructs a new Internship with the specified details.
     * Initial status is PENDING and visibility is false.
     * 
     * @param title the internship title
     * @param description the internship description
     * @param companyName the name of the company offering the internship
     * @param rep the company representative in charge
     * @param level the required level (BASIC, INTERMEDIATE, ADVANCED)
     * @param preferredMajor the preferred major for applicants
     * @param slots the number of available positions
     * @param openingDate the date applications open
     * @param closingDate the date applications close
     */
    public Internship(String title, String description, String companyName, CompanyRepresentative rep, 
                      InternshipLevel level, String preferredMajor, int slots, 
                      LocalDate openingDate, LocalDate closingDate) {
        this.internshipID = "INT" + String.format("%04d", nextId++);
        this.title = title;
        this.description = description;
        this.companyName = companyName;
        this.companyRepInCharge = rep;
        this.level = level;
        this.preferredMajor = preferredMajor;
        this.slots = slots;
        this.confirmedPlacements = 0;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.status = InternshipStatus.PENDING;
        this.isVisible = false;
    }

    // Getters
    public String getInternshipID() { return internshipID; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCompanyName() { return companyName; }
    public CompanyRepresentative getCompanyRepInCharge() { return companyRepInCharge; }
    public InternshipLevel getLevel() { return level; }
    public String getPreferredMajor() { return preferredMajor; }
    public int getSlots() { return slots; }
    public int getConfirmedPlacements() { return confirmedPlacements; }
    public LocalDate getOpeningDate() { return openingDate; }
    public LocalDate getClosingDate() { return closingDate; }
    public InternshipStatus getStatus() { return status; }
    public boolean isVisible() { return isVisible; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLevel(InternshipLevel level) { this.level = level; }
    public void setPreferredMajor(String preferredMajor) { this.preferredMajor = preferredMajor; }
    public void setSlots(int slots) { this.slots = slots; }
    public void setOpeningDate(LocalDate openingDate) { this.openingDate = openingDate; }
    public void setClosingDate(LocalDate closingDate) { this.closingDate = closingDate; }
    public void setStatus(InternshipStatus status) { this.status = status; }
    public void setVisible(boolean visible) { this.isVisible = visible; }
    
    /**
     * Increments the number of confirmed placements.
     * If slots are filled, status is set to FILLED.
     */
    public void incrementConfirmedPlacements() {
        this.confirmedPlacements++;
        if (this.confirmedPlacements >= this.slots) {
            this.status = InternshipStatus.FILLED;
        }
    }
    
    /**
     * Decrements the number of confirmed placements.
     * If previously filled, status is changed back to APPROVED.
     */
    public void decrementConfirmedPlacements() {
        if (this.confirmedPlacements > 0) {
            this.confirmedPlacements--;
            if (this.status == InternshipStatus.FILLED) {
                this.status = InternshipStatus.APPROVED;
            }
        }
    }
}
