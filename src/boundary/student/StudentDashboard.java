package sc2002_grpproject.boundary.student;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.*;
import sc2002_grpproject.boundary.RoundedButton;
import sc2002_grpproject.boundary.LoginFrame;
import sc2002_grpproject.controller.AuthController;
import sc2002_grpproject.controller.result.PasswordChangeResult;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class StudentDashboard extends JFrame {
    private Student student;
    private List<Internship> internships;
    private List<InternshipApplication> applications;
    private List<User> allUsers;
    private List<Student> students;
    private List<CareerCenterStaff> staff;
    private List<CompanyRepresentative> companyReps;
    
    private InternshipBrowsePanel internshipBrowsePanel;
    private MyApplicationsPanel myApplicationsPanel;
    private StudentActionHandler actionHandler;

    public StudentDashboard(Student student, List<Internship> internships, 
                           List<InternshipApplication> applications,
                           List<User> allUsers, List<Student> students,
                           List<CareerCenterStaff> staff, List<CompanyRepresentative> companyReps) {
        this.student = student;
        this.internships = internships;
        this.applications = applications;
        this.allUsers = allUsers;
        this.students = students;
        this.staff = staff;
        this.companyReps = companyReps;

        // Create action handler
        this.actionHandler = new StudentActionHandler(student, internships, applications, this);

        setTitle("Student Dashboard - " + student.getName());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(230, 255, 240)); // Light mint green
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top panel with welcome and buttons
        JPanel topPanel = createTopPanel();
        
        // Create top container for header and notifications
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(new Color(230, 255, 240));
        topContainer.add(topPanel);
        
        // Add notification panel if there are notifications
        JPanel notificationPanel = createNotificationPanel();
        if (notificationPanel != null) {
            topContainer.add(Box.createVerticalStrut(10));
            topContainer.add(notificationPanel);
        }

        mainPanel.add(topContainer, BorderLayout.NORTH);

        // Center with tabs
        JTabbedPane tabbedPane = createTabbedPane();
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
        
        // Initial refresh
        refreshAll();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(144, 238, 144));
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(34, 139, 34), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + student.getName() + " (" + student.getUserID() + ")");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(0, 100, 0));
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(144, 238, 144));
        
        JButton changePasswordBtn = createButton("Change Password");
        JButton logoutBtn = createButton("Logout");
        
        changePasswordBtn.setToolTipText("Update your account password");
        logoutBtn.setToolTipText("Sign out from the system");
        
        changePasswordBtn.addActionListener(e -> changePassword());
        logoutBtn.addActionListener(e -> logout());
        
        buttonPanel.add(changePasswordBtn);
        buttonPanel.add(logoutBtn);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        return topPanel;
    }

    private JPanel createNotificationPanel() {
        List<InternshipApplication> studentApps = applications.stream()
            .filter(app -> app.getStudent().getUserID().equals(student.getUserID()))
            .collect(Collectors.toList());
        
        long approved = studentApps.stream()
            .filter(app -> app.getStatus() == ApplicationStatus.SUCCESSFUL)
            .count();
        long rejected = studentApps.stream()
            .filter(app -> app.getStatus() == ApplicationStatus.UNSUCCESSFUL)
            .count();
        long withdrawn = studentApps.stream()
            .filter(app -> app.getStatus() == ApplicationStatus.WITHDRAWN)
            .count();
        
        if (approved == 0 && rejected == 0 && withdrawn == 0) {
            return null;
        }
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 250, 205)); // Light yellow
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 165, 0), 3),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel titleLabel = new JLabel("🔔 APPLICATION STATUS UPDATES");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(255, 100, 0));
        panel.add(titleLabel);
        
        if (approved > 0) {
            JLabel approvedLabel = new JLabel("✓ " + approved + " application(s) APPROVED");
            approvedLabel.setFont(new Font("Arial", Font.BOLD, 13));
            approvedLabel.setForeground(new Color(0, 150, 0));
            panel.add(approvedLabel);
        }
        if (rejected > 0) {
            JLabel rejectedLabel = new JLabel("✗ " + rejected + " application(s) REJECTED");
            rejectedLabel.setFont(new Font("Arial", Font.BOLD, 13));
            rejectedLabel.setForeground(new Color(200, 0, 0));
            panel.add(rejectedLabel);
        }
        if (withdrawn > 0) {
            JLabel withdrawnLabel = new JLabel("⊗ " + withdrawn + " application(s) WITHDRAWN");
            withdrawnLabel.setFont(new Font("Arial", Font.BOLD, 13));
            withdrawnLabel.setForeground(new Color(150, 75, 0));
            panel.add(withdrawnLabel);
        }
        
        JLabel infoLabel = new JLabel("→ Check 'My Applications' tab for details");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setForeground(new Color(100, 100, 100));
        panel.add(infoLabel);
        
        return panel;
    }

    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));
        tabbedPane.setBackground(new Color(200, 255, 200));
        tabbedPane.setForeground(new Color(0, 100, 0));

        // Create panels
        internshipBrowsePanel = new InternshipBrowsePanel(student, internships, actionHandler);
        myApplicationsPanel = new MyApplicationsPanel(student, applications, actionHandler);

        tabbedPane.addTab("Available Internships", internshipBrowsePanel);
        tabbedPane.addTab("My Applications", myApplicationsPanel);

        return tabbedPane;
    }

    public void refreshAll() {
        if (internshipBrowsePanel != null) {
            internshipBrowsePanel.refresh();
        }
        if (myApplicationsPanel != null) {
            myApplicationsPanel.refresh();
        }
    }

    private void changePassword() {
        JPasswordField oldPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        
        Object[] message = {
            "Enter old password:", oldPasswordField,
            "Enter new password:", newPasswordField,
            "Confirm new password:", confirmPasswordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Change Password", 
                                                   JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "New passwords do not match!", 
                                             "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            PasswordChangeResult result = 
                AuthController.changePassword(student, oldPassword, newPassword);
            
            if (result.isSuccess()) {
                JOptionPane.showMessageDialog(this, result.getMessage());
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), 
                                             "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame(allUsers, students, staff, companyReps, internships, applications);
        }
    }

    private JButton createButton(String text) {
        RoundedButton button = new RoundedButton(text, 20);
        button.setBackground(new Color(200, 200, 200));
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}
