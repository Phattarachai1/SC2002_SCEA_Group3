package sc2002_grpproject;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private String major;
    private int yearOfStudy;
    // We will manage applications in a central list, but a student can view theirs.

    public Student(String userID, String name, String major, int yearOfStudy) {
        super(userID, name);
        this.major = major;
        this.yearOfStudy = yearOfStudy;
    }

    // Getters
    public String getMajor() { return major; }
    public int getYearOfStudy() { return yearOfStudy; }
}