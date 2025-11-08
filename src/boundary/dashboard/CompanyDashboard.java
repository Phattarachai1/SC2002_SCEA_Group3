package sc2002_grpproject.boundary.dashboard;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.Enums.*;
import sc2002_grpproject.boundary.LoginFrame;
import sc2002_grpproject.boundary.RoundedButton;
import sc2002_grpproject.boundary.helpers.UIHelper;
import sc2002_grpproject.boundary.panels.company.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Main dashboard for Company Representatives - coordinates UI panels
 */
public class CompanyDashboard extends JFrame {
    private final CompanyRepresentative rep;
    private final List<Internship> internships;
    private final List<InternshipApplication> applications;
    private final List<Student> students;
    private final List<User> allUsers;
    private final List<CareerCenterStaff> staff;
    private final List<CompanyRepresentative> companyReps;
    
    private PostingManagementPanel postingManagementPanel;
    private ApplicationReviewPanel applicationReviewPanel;

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

        setTitle("Company Dashboard - " + rep.getName() + " (" + rep.getCompanyName() + ")");
        setSize(1100, 750);
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
        postingManagementPanel = new PostingManagementPanel(rep, internships,
            new PostingManagementPanel.PostingActionListener() {
                @Override
                public void onPostingCreated(Internship internship) {
                    applicationReviewPanel.refreshTable();
                }
                
                @Override
                public void onPostingUpdated(Internship internship) {
                    // Refresh if needed
                }
            });
        
        applicationReviewPanel = new ApplicationReviewPanel(rep, internships, applications,
            new ApplicationReviewPanel.ApplicationActionListener() {
                @Override
                public void onApplicationApproved(InternshipApplication application) {
                    postingManagementPanel.refreshTable();
                }
                
                @Override
                public void onApplicationRejected(InternshipApplication application) {
                    // Refresh if needed
                }
            });

        tabbedPane.addTab("My Internship Postings", postingManagementPanel);
        tabbedPane.addTab("Applications Review", applicationReviewPanel);

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
        
        JLabel welcomeLabel = new JLabel("Company Dashboard - " + rep.getName() + " (" + rep.getCompanyName() + ")");
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

            if (!rep.checkPassword(current)) {
                UIHelper.showError(this, "Incorrect current password");
                return;
            }

            if (!newPass.equals(confirm)) {
                UIHelper.showError(this, "New passwords do not match");
                return;
            }

            rep.changePassword(newPass);
            UIHelper.showInfo(this, "Password changed successfully!");
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
}
