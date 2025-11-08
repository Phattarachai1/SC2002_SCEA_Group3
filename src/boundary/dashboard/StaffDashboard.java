package sc2002_grpproject.boundary.dashboard;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.Enums.*;
import sc2002_grpproject.boundary.LoginFrame;
import sc2002_grpproject.boundary.RoundedButton;
import sc2002_grpproject.boundary.helpers.UIHelper;
import sc2002_grpproject.boundary.panels.staff.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Main dashboard for Career Center Staff - coordinates UI panels
 */
public class StaffDashboard extends JFrame {
    private final CareerCenterStaff staff;
    private final List<Internship> internships;
    private final List<InternshipApplication> applications;
    private final List<Student> students;
    private final List<User> allUsers;
    private final List<CareerCenterStaff> staffList;
    private final List<CompanyRepresentative> companyReps;
    
    private RepApprovalPanel repApprovalPanel;
    private InternshipApprovalPanel internshipApprovalPanel;
    private WithdrawalApprovalPanel withdrawalApprovalPanel;
    private ReportPanel reportPanel;

    public StaffDashboard(CareerCenterStaff staff, List<Internship> internships,
                         List<InternshipApplication> applications, List<Student> students,
                         List<User> allUsers, List<CareerCenterStaff> staffList,
                         List<CompanyRepresentative> companyReps) {
        this.staff = staff;
        this.internships = internships;
        this.applications = applications;
        this.students = students;
        this.allUsers = allUsers;
        this.staffList = staffList;
        this.companyReps = companyReps;

        setTitle("Career Center Staff Dashboard - " + staff.getName());
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(220, 245, 220));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mainPanel.add(createTopPanel(), BorderLayout.NORTH);

        // Center with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));
        tabbedPane.setBackground(new Color(200, 255, 200));
        tabbedPane.setForeground(UIHelper.DARK_GREEN);

        // Create panels with listeners
        repApprovalPanel = new RepApprovalPanel(companyReps,
            new RepApprovalPanel.RepActionListener() {
                @Override
                public void onRepApproved(CompanyRepresentative rep) {
                    // Refresh if needed
                }
                
                @Override
                public void onRepRejected(CompanyRepresentative rep) {
                    // Refresh if needed
                }
            });
        
        internshipApprovalPanel = new InternshipApprovalPanel(internships,
            new InternshipApprovalPanel.InternshipActionListener() {
                @Override
                public void onInternshipApproved(Internship internship) {
                    // Refresh if needed
                }
                
                @Override
                public void onInternshipRejected(Internship internship) {
                    // Refresh if needed
                }
            });
        
        withdrawalApprovalPanel = new WithdrawalApprovalPanel(applications,
            app -> {
                // Refresh panels after withdrawal approval
                internshipApprovalPanel.refreshTable();
            });
        
        reportPanel = new ReportPanel(students, internships, applications, companyReps);

        tabbedPane.addTab("Company Representatives", repApprovalPanel);
        tabbedPane.addTab("Internship Approvals", internshipApprovalPanel);
        tabbedPane.addTab("Withdrawal Requests", withdrawalApprovalPanel);
        tabbedPane.addTab("Reports", reportPanel);

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
        
        JLabel welcomeLabel = new JLabel("Career Center Staff Dashboard - " + staff.getName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(UIHelper.DARK_GREEN);
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UIHelper.BUTTON_GREEN);
        
        JButton changePasswordBtn = createButton("Change Password");
        JButton logoutBtn = createButton("Logout");
        
        changePasswordBtn.addActionListener(e -> changePassword());
        logoutBtn.addActionListener(e -> logout());
        
        buttonPanel.add(changePasswordBtn);
        buttonPanel.add(logoutBtn);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        return topPanel;
    }

    private void changePassword() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBackground(new Color(220, 245, 220));
        
        JPasswordField currentField = new JPasswordField();
        JPasswordField newField = new JPasswordField();
        JPasswordField confirmField = new JPasswordField();
        
        panel.add(new JLabel("Current Password:"));
        panel.add(currentField);
        panel.add(new JLabel("New Password:"));
        panel.add(newField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String current = new String(currentField.getPassword());
            String newPass = new String(newField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (!staff.checkPassword(current)) {
                UIHelper.showError(this, "Incorrect current password");
                return;
            }

            if (!newPass.equals(confirm)) {
                UIHelper.showError(this, "New passwords do not match");
                return;
            }

            staff.changePassword(newPass);
            UIHelper.showInfo(this, "Password changed successfully!");
        }
    }

    private void logout() {
        boolean confirm = UIHelper.showConfirmation(this, "Are you sure you want to logout?");
        
        if (confirm) {
            dispose();
            new LoginFrame(allUsers, students, staffList, companyReps, internships, applications);
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
}
