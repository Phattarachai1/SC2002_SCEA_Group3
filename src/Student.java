package sc2002_grpproject;

public class Student extends User {
    private String major;
    private int yearOfStudy;
    private String email;

    // We will manage applications in a central list, but a student can view theirs.
    public Student(String userID, String name, String major, int yearOfStudy, String email) {
        super(userID, name);
        this.major = major;
        this.yearOfStudy = yearOfStudy;
        this.email = email;
    }

    // Getters
    public String getMajor() { 
        return major; 
    }

    public int getYearOfStudy() { 
        return yearOfStudy; 
    }

    public String getEmail() { 
        return email; 
    }
}
