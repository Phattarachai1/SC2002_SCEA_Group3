package sc2002_grpproject.entity;

/**
 * Represents a student user in the internship placement system.
 * Students can apply for internships and manage their applications.
 */
public class Student extends User {
    private String major;
    private int yearOfStudy;
    private String email;

    /**
     * Constructs a new Student with the specified details.
     * 
     * @param userID the unique identifier for the student
     * @param name the student's full name
     * @param major the student's major field of study
     * @param yearOfStudy the student's current year of study
     * @param email the student's email address
     */
    public Student(String userID, String name, String major, int yearOfStudy, String email) {
        super(userID, name);
        this.major = major;
        this.yearOfStudy = yearOfStudy;
        this.email = email;
    }

    /**
     * Gets the student's major.
     * 
     * @return the major field of study
     */
    public String getMajor() { 
        return major; 
    }

    /**
     * Gets the student's year of study.
     * 
     * @return the year of study
     */
    public int getYearOfStudy() { 
        return yearOfStudy; 
    }

    /**
     * Gets the student's email address.
     * 
     * @return the email address
     */
    public String getEmail() { 
        return email; 
    }
}
