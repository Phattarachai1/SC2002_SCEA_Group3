package sc2002_grpproject.main;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.*;
import sc2002_grpproject.utils.DataManager;
import sc2002_grpproject.boundary.LoginFrame;
import javax.swing.*;
import java.util.List;

/**
 * GUI-based main application for the Internship Placement Management System.
 * Launches the login frame and initializes system data.
 */
public class MainAppGUI {
    public static void main(String[] args) {
        // Load data using DataManager instance
        DataManager dataManager = new DataManager();
        List<Student> students = dataManager.loadStudents("sample_student_list.csv");
        List<CareerCenterStaff> staff = dataManager.loadStaff("sample_staff_list.csv");
        List<CompanyRepresentative> companyReps = dataManager.loadCompanyReps("sample_company_representative_list.csv");
        
        // Combine into User list
        List<User> allUsers = new java.util.ArrayList<>();
        allUsers.addAll(students);
        allUsers.addAll(staff);
        allUsers.addAll(companyReps);
        
        // Initialize internships and applications
        List<Internship> internships = new java.util.ArrayList<>();
        List<InternshipApplication> applications = new java.util.ArrayList<>();

        // Set Swing look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Launch Login Frame
        SwingUtilities.invokeLater(() -> {
            new LoginFrame(allUsers, students, staff, companyReps, internships, applications);
        });
    }
}
