package sc2002_grpproject;

import javax.swing.*;
import java.util.List;

public class MainAppGUI {
    public static void main(String[] args) {
        // Load data
        List<Student> students = DataManager.loadStudents("sample_student_list.csv");
        List<CareerCenterStaff> staff = DataManager.loadStaff("sample_staff_list.csv");
        List<CompanyRepresentative> companyReps = DataManager.loadCompanyReps("sample_company_representative_list.csv");
        
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
