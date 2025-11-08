package sc2002_grpproject.boundary.dashboard;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.Enums.*;
import sc2002_grpproject.boundary.LoginFrame;
import sc2002_grpproject.boundary.RoundedButton;
import sc2002_grpproject.boundary.helpers.UIHelper;
import sc2002_grpproject.boundary.panels.student.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main dashboard for students - coordinates UI panels
 */
public class StudentDashboard extends JFrame {
    private final Student student;
    private final List<Internship> internships;
    private final List<InternshipApplication> applications;
    private final List<User> allUsers;
    private final List<Student> students;
    private final List<CareerCenterStaff> staff;
    private final List<CompanyRepresentative> companyReps;
    
    private InternshipBrowsePanel internshipBrowsePanel;
    private ApplicationManagementPanel applicationManagementPanel;

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

        setTitle("Student Dashboard - " + student.getName());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(UIHelper.MINT_GREEN);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top panel with welcome and buttons
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(UIHelper.MINT_GREEN);
        topContainer.add(createTopPanel());
        
        // Add notification panel if there are notifications
        JPanel notificationPanel = createNotificationPanel();
        if (notificationPanel != null) {
            topContainer.add(Box.createVerticalStrut(10));
            topContainer.add(notificationPanel);
        }

        mainPanel.add(topContainer, BorderLayout.NORTH);

        // Center with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));
        tabbedPane.setBackground(new Color(200, 255, 200));
        tabbedPane.setForeground(UIHelper.DARK_GREEN);

        // Create panels with listeners
        internshipBrowsePanel = new InternshipBrowsePanel(
            student, internships, applications,
            app -> applicationManagementPanel.refreshTable()
        );
        
        applicationManagementPanel = new ApplicationManagementPanel(
            student, internships, applications,
            new ApplicationManagementPanel.ApplicationActionListener() {
                @Override
                public void onPlacementAccepted(InternshipApplication application) {
                    internshipBrowsePanel.refreshTable();
                }
                
                @Override
                public void onWithdrawalRequested(InternshipApplication application) {
                    // No additional action needed
                }
            }
        );

        tabbedPane.addTab("Available Internships", internshipBrowsePanel);
        tabbedPane.addTab("My Applications", applicationManagementPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIHelper.BUTTON_GREEN);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIHelper.BORDER_GREEN, 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + student.getName() + " (" + student.getUserID() + ")");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(UIHelper.DARK_GREEN);
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UIHelper.BUTTON_GREEN);
        
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

    private void changePassword() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBackground(new Color(220, 245, 220));
        
        JPasswordField currentField = new JPasswordField();
        JPasswordField newField = new JPasswordField();
        JPasswordField confirmField = new JPasswordField();
        
        panel.add(createLabel("Current Password:"));
        panel.add(currentField);
        panel.add(createLabel("New Password:"));
        panel.add(newField);
        panel.add(createLabel("Confirm Password:"));
        panel.add(confirmField);

        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Change Password", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String current = new String(currentField.getPassword());
            String newPass = new String(newField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (!student.checkPassword(current)) {
                UIHelper.showError(this, "Incorrect current password");
                return;
            }

            if (!newPass.equals(confirm)) {
                UIHelper.showError(this, "New passwords do not match");
                return;
            }

            student.changePassword(newPass);
            UIHelper.showInfo(this, "Password changed successfully!\nYou will be logged out for security.");
            logout();
        }
    }

    private void logout() {
        boolean confirm = UIHelper.showConfirmation(this, "Are you sure you want to logout?");
        
        if (confirm) {
            dispose();
            new LoginFrame(allUsers, students, staff, companyReps, internships, applications);
        }
    }

    private JButton createButton(String text) {
        RoundedButton button = new RoundedButton(text, 20);
        button.setBackground(new Color(200, 200, 200));
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(0, 80, 0));
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }
}
