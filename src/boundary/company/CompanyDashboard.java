package sc2002_grpproject.boundary.company;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.*;
import sc2002_grpproject.boundary.RoundedButton;
import sc2002_grpproject.boundary.LoginFrame;
import sc2002_grpproject.controller.AuthController;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class CompanyDashboard extends JFrame {
    private CompanyRepresentative rep;
    private List<Internship> internships;
    private List<InternshipApplication> applications;
    private List<Student> students;
    private List<User> allUsers;
    private List<CareerCenterStaff> staff;
    private List<CompanyRepresentative> companyReps;
    
    private InternshipTablePanel internshipPanel;
    private ApplicationTablePanel applicationPanel;
    private CompanyActionHandler actionHandler;

    public CompanyDashboard(CompanyRepresentative rep, List<Internship> internships,
                           List<InternshipApplication> applications, List<Student> students,
                           List<User> allUsers, List<CareerCenterStaff> staff,
                           List<CompanyRepresentative> companyReps) {
        this.rep = rep;
        this.internships = internships;
        this.applications = applications;
        this.students = students;
        this.allUsers = allUsers;
        this.staff = staff;
        this.companyReps = companyReps;

        // Initialize action handler
        this.actionHandler = new CompanyActionHandler(rep, internships, applications, this);

        setTitle("Company Dashboard - " + rep.getName() + " (" + rep.getCompanyName() + ")");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(220, 245, 220));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(220, 245, 220));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + rep.getName() + " - " + rep.getCompanyName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(0, 100, 0));
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(220, 245, 220));
        
        JButton changePasswordBtn = createButton("Change Password");
        JButton logoutBtn = createButton("Logout");
        
        changePasswordBtn.addActionListener(e -> changePassword());
        logoutBtn.addActionListener(e -> logout());
        
        buttonPanel.add(changePasswordBtn);
        buttonPanel.add(logoutBtn);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Create top container for header and notifications
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(new Color(220, 245, 220));
        topContainer.add(topPanel);
        
        // Add notification panel if there are notifications
        JPanel notificationPanel = createNotificationPanel();
        if (notificationPanel != null) {
            topContainer.add(Box.createVerticalStrut(10));
            topContainer.add(notificationPanel);
        }

        mainPanel.add(topContainer, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        tabbedPane.setBackground(new Color(220, 245, 220));

        // Create panels
        internshipPanel = new InternshipTablePanel(rep, internships, applications, actionHandler);
        applicationPanel = new ApplicationTablePanel(rep, internships, applications, actionHandler);

        tabbedPane.addTab("My Internships", internshipPanel);
        tabbedPane.addTab("Applications", applicationPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
        
        // Refresh panels
        internshipPanel.refresh();
        applicationPanel.refresh();
    }

    private JPanel createNotificationPanel() {
        List<Internship> myInternships = internships.stream()
            .filter(i -> i.getCompanyRepInCharge().getUserID().equals(rep.getUserID()))
            .collect(Collectors.toList());
        
        long pendingApps = applications.stream()
            .filter(app -> myInternships.stream()
                .anyMatch(i -> i.getInternshipID().equals(app.getInternship().getInternshipID())))
            .filter(app -> app.getStatus() == ApplicationStatus.PENDING)
            .count();
        
        long approvedInternships = myInternships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.APPROVED)
            .count();
        long rejectedInternships = myInternships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.REJECTED)
            .count();
        long pendingInternships = myInternships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.PENDING)
            .count();
        
        if (pendingApps == 0 && approvedInternships == 0 && rejectedInternships == 0 && pendingInternships == 0) {
            return null;
        }
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 250, 205));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 165, 0), 3),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel titleLabel = new JLabel("🔔 NOTIFICATIONS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(255, 100, 0));
        panel.add(titleLabel);
        
        if (pendingApps > 0) {
            JLabel label = new JLabel("📥 " + pendingApps + " new application(s) pending review");
            label.setFont(new Font("Arial", Font.BOLD, 13));
            label.setForeground(new Color(255, 100, 0));
            panel.add(label);
        }
        if (approvedInternships > 0) {
            JLabel label = new JLabel("✓ " + approvedInternships + " internship(s) APPROVED by staff");
            label.setFont(new Font("Arial", Font.BOLD, 13));
            label.setForeground(new Color(0, 150, 0));
            panel.add(label);
        }
        if (rejectedInternships > 0) {
            JLabel label = new JLabel("✗ " + rejectedInternships + " internship(s) REJECTED by staff");
            label.setFont(new Font("Arial", Font.BOLD, 13));
            label.setForeground(new Color(200, 0, 0));
            panel.add(label);
        }
        if (pendingInternships > 0) {
            JLabel label = new JLabel("⏳ " + pendingInternships + " internship(s) awaiting staff approval");
            label.setFont(new Font("Arial", Font.BOLD, 13));
            label.setForeground(new Color(150, 100, 0));
            panel.add(label);
        }
        
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

            if (!rep.checkPassword(current)) {
                actionHandler.showError("Incorrect current password");
                return;
            }

            if (!newPass.equals(confirm)) {
                actionHandler.showError("New passwords do not match");
                return;
            }

            rep.changePassword(newPass);
            actionHandler.showInfo("Password changed successfully!\nYou will be logged out for security.");
            logout();
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
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
