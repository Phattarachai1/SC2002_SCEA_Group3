package sc2002_grpproject.boundary.staff;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.*;
import sc2002_grpproject.boundary.RoundedButton;
import sc2002_grpproject.boundary.LoginFrame;
import sc2002_grpproject.controller.AuthController;
import sc2002_grpproject.controller.result.PasswordChangeResult;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class StaffDashboard extends JFrame {
    private CareerCenterStaff staff;
    private List<Internship> internships;
    private List<InternshipApplication> applications;
    private List<Student> students;
    private List<User> allUsers;
    private List<CareerCenterStaff> staffList;
    private List<CompanyRepresentative> companyReps;
    
    private StaffActionHandler actionHandler;
    private CompanyRepManagementPanel companyRepPanel;
    private InternshipManagementPanel internshipPanel;

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

        // Create action handler
        this.actionHandler = new StaffActionHandler(internships, applications, companyReps, this);

        setTitle("Career Center Staff Dashboard - " + staff.getName());
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(220, 245, 220));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top panel
        JPanel topPanel = createTopPanel();
        
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
        JTabbedPane tabbedPane = createTabbedPane();
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
        
        // Initial refresh
        refreshAll();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(220, 245, 220));
        
        JLabel welcomeLabel = new JLabel("Career Center Staff Portal - " + staff.getName());
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
        
        return topPanel;
    }

    private JPanel createNotificationPanel() {
        long pendingReps = companyReps.stream()
            .filter(r -> r.getStatus() == ApprovalStatus.PENDING)
            .count();
        long pendingInternships = internships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.PENDING)
            .count();
        long pendingWithdrawals = applications.stream()
            .filter(app -> "PENDING".equals(app.getWithdrawalStatus()))
            .count();
        
        if (pendingReps == 0 && pendingInternships == 0 && pendingWithdrawals == 0) {
            return null;
        }
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 250, 205));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 165, 0), 3),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel titleLabel = new JLabel("🔔 PENDING APPROVALS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(255, 100, 0));
        panel.add(titleLabel);
        
        if (pendingReps > 0) {
            JLabel label = new JLabel("⚠ " + pendingReps + " company representative(s) awaiting authorization");
            label.setFont(new Font("Arial", Font.BOLD, 13));
            label.setForeground(new Color(200, 100, 0));
            panel.add(label);
        }
        if (pendingInternships > 0) {
            JLabel label = new JLabel("⚠ " + pendingInternships + " internship(s) awaiting approval");
            label.setFont(new Font("Arial", Font.BOLD, 13));
            label.setForeground(new Color(200, 100, 0));
            panel.add(label);
        }
        if (pendingWithdrawals > 0) {
            JLabel label = new JLabel("⚠ " + pendingWithdrawals + " withdrawal request(s) pending");
            label.setFont(new Font("Arial", Font.BOLD, 13));
            label.setForeground(new Color(200, 100, 0));
            panel.add(label);
        }
        
        return panel;
    }

    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        tabbedPane.setBackground(new Color(220, 245, 220));

        // Create panels
        companyRepPanel = new CompanyRepManagementPanel(companyReps, actionHandler);
        internshipPanel = new InternshipManagementPanel(internships, actionHandler);

        tabbedPane.addTab("Company Reps", companyRepPanel);
        tabbedPane.addTab("Internships", internshipPanel);
        tabbedPane.addTab("Applications", createSimpleApplicationsPanel());
        tabbedPane.addTab("Withdrawals", createSimpleWithdrawalsPanel());
        tabbedPane.addTab("Reports", createSimpleReportsPanel());

        return tabbedPane;
    }

    // Simplified panels for Applications, Withdrawals, and Reports
    // These can be extracted to separate files later if needed

    private JPanel createSimpleApplicationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(235, 250, 235));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("All Applications Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(0, 80, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"App ID", "Student", "Internship", "Company", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(28);
        
        // Populate
        for (InternshipApplication app : applications) {
            model.addRow(new Object[]{
                app.getApplicationId(),
                app.getStudent().getName(),
                app.getInternship().getTitle(),
                app.getInternship().getCompanyName(),
                app.getStatus()
            });
        }
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSimpleWithdrawalsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(235, 250, 235));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Withdrawal Requests");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(0, 80, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"App ID", "Student", "Internship", "Company", "Status", "Confirmed"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(28);
        
        // Populate withdrawals
        List<InternshipApplication> withdrawals = applications.stream()
            .filter(app -> "PENDING".equals(app.getWithdrawalStatus()))
            .collect(Collectors.toList());
            
        for (InternshipApplication app : withdrawals) {
            model.addRow(new Object[]{
                app.getApplicationId(),
                app.getStudent().getName(),
                app.getInternship().getTitle(),
                app.getInternship().getCompanyName(),
                app.getStatus(),
                app.isPlacementConfirmed() ? "Yes" : "No"
            });
        }
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton approveBtn = createButton("Approve Selected");
        approveBtn.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel != -1) {
                String appId = (String) model.getValueAt(sel, 0);
                actionHandler.approveWithdrawal(appId);
                // Refresh
                model.setRowCount(0);
                List<InternshipApplication> w = applications.stream()
                    .filter(app -> "PENDING".equals(app.getWithdrawalStatus()))
                    .collect(Collectors.toList());
                for (InternshipApplication app : w) {
                    model.addRow(new Object[]{
                        app.getApplicationId(),
                        app.getStudent().getName(),
                        app.getInternship().getTitle(),
                        app.getInternship().getCompanyName(),
                        app.getStatus(),
                        app.isPlacementConfirmed() ? "Yes" : "No"
                    });
                }
            } else {
                actionHandler.showError("Please select a withdrawal request");
            }
        });
        btnPanel.add(approveBtn);
        
        JButton rejectBtn = createButton("Reject Selected");
        rejectBtn.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel != -1) {
                String appId = (String) model.getValueAt(sel, 0);
                actionHandler.rejectWithdrawal(appId);
                // Refresh
                model.setRowCount(0);
                List<InternshipApplication> w = applications.stream()
                    .filter(app -> "PENDING".equals(app.getWithdrawalStatus()))
                    .collect(Collectors.toList());
                for (InternshipApplication app : w) {
                    model.addRow(new Object[]{
                        app.getApplicationId(),
                        app.getStudent().getName(),
                        app.getInternship().getTitle(),
                        app.getInternship().getCompanyName(),
                        app.getStatus(),
                        app.isPlacementConfirmed() ? "Yes" : "No"
                    });
                }
            } else {
                actionHandler.showError("Please select a withdrawal request");
            }
        });
        btnPanel.add(rejectBtn);
        
        centerPanel.add(btnPanel, BorderLayout.SOUTH);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSimpleReportsPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 20, 20));
        panel.setBackground(new Color(235, 250, 235));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(createReportButton("Students Report", this::showStudentsReport));
        panel.add(createReportButton("Internships Report", this::showInternshipsReport));
        panel.add(createReportButton("Applications Report", this::showApplicationsReport));
        panel.add(createReportButton("Companies Report", this::showCompaniesReport));
        panel.add(createReportButton("Pending Internships", this::showPendingInternships));
        panel.add(createReportButton("Pending Withdrawals", this::showPendingWithdrawals));
        panel.add(createReportButton("Approved Internships", this::showApprovedInternships));
        panel.add(createReportButton("Full System Report", this::showFullReport));

        return panel;
    }

    private JButton createReportButton(String text, Runnable action) {
        JButton btn = createButton(text);
        btn.addActionListener(e -> action.run());
        return btn;
    }

    public void refreshAll() {
        if (companyRepPanel != null) {
            companyRepPanel.refresh();
        }
        if (internshipPanel != null) {
            internshipPanel.refresh();
        }
    }

    // Report methods (kept from original)
    private void showStudentsReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== STUDENTS REPORT ===\n\n");
        report.append("Total Students: ").append(students.size()).append("\n\n");
        
        for (Student s : students) {
            report.append("ID: ").append(s.getUserID()).append("\n");
            report.append("Name: ").append(s.getName()).append("\n");
            report.append("Major: ").append(s.getMajor()).append("\n");
            report.append("Year: ").append(s.getYearOfStudy()).append("\n");
            long appCount = applications.stream()
                .filter(app -> app.getStudent().getUserID().equals(s.getUserID()))
                .count();
            report.append("Applications: ").append(appCount).append("\n---\n");
        }
        showReport("Students Report", report.toString());
    }

    private void showInternshipsReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== INTERNSHIPS REPORT ===\n\n");
        report.append("Total: ").append(internships.size()).append("\n");
        report.append("Pending: ").append(internships.stream().filter(i -> i.getStatus() == InternshipStatus.PENDING).count()).append("\n");
        report.append("Approved: ").append(internships.stream().filter(i -> i.getStatus() == InternshipStatus.APPROVED).count()).append("\n\n");
        
        for (Internship i : internships) {
            report.append("ID: ").append(i.getInternshipID()).append("\n");
            report.append("Title: ").append(i.getTitle()).append("\n");
            report.append("Company: ").append(i.getCompanyName()).append("\n");
            report.append("Status: ").append(i.getStatus()).append("\n---\n");
        }
        showReport("Internships Report", report.toString());
    }

    private void showApplicationsReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== APPLICATIONS REPORT ===\n\n");
        report.append("Total: ").append(applications.size()).append("\n");
        report.append("Pending: ").append(applications.stream().filter(app -> app.getStatus() == ApplicationStatus.PENDING).count()).append("\n");
        report.append("Successful: ").append(applications.stream().filter(app -> app.getStatus() == ApplicationStatus.SUCCESSFUL).count()).append("\n");
        report.append("Confirmed: ").append(applications.stream().filter(app -> app.isPlacementConfirmed()).count()).append("\n");
        showReport("Applications Report", report.toString());
    }

    private void showCompaniesReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== COMPANIES REPORT ===\n\n");
        report.append("Total Reps: ").append(companyReps.size()).append("\n\n");
        
        for (CompanyRepresentative rep : companyReps) {
            report.append("Name: ").append(rep.getName()).append("\n");
            report.append("Company: ").append(rep.getCompanyName()).append("\n");
            report.append("Status: ").append(rep.getStatus()).append("\n---\n");
        }
        showReport("Companies Report", report.toString());
    }

    private void showPendingInternships() {
        List<Internship> pending = internships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.PENDING)
            .collect(Collectors.toList());
        StringBuilder report = new StringBuilder();
        report.append("=== PENDING INTERNSHIPS ===\n\nCount: ").append(pending.size()).append("\n\n");
        for (Internship i : pending) {
            report.append("ID: ").append(i.getInternshipID()).append("\n");
            report.append("Title: ").append(i.getTitle()).append("\n");
            report.append("Company: ").append(i.getCompanyName()).append("\n---\n");
        }
        showReport("Pending Internships", report.toString());
    }

    private void showPendingWithdrawals() {
        List<InternshipApplication> withdrawals = applications.stream()
            .filter(app -> "PENDING".equals(app.getWithdrawalStatus()))
            .collect(Collectors.toList());
        StringBuilder report = new StringBuilder();
        report.append("=== PENDING WITHDRAWALS ===\n\nCount: ").append(withdrawals.size()).append("\n\n");
        for (InternshipApplication app : withdrawals) {
            report.append("Student: ").append(app.getStudent().getName()).append("\n");
            report.append("Internship: ").append(app.getInternship().getTitle()).append("\n---\n");
        }
        showReport("Pending Withdrawals", report.toString());
    }

    private void showApprovedInternships() {
        List<Internship> approved = internships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.APPROVED)
            .collect(Collectors.toList());
        StringBuilder report = new StringBuilder();
        report.append("=== APPROVED INTERNSHIPS ===\n\nCount: ").append(approved.size()).append("\n\n");
        for (Internship i : approved) {
            report.append("Title: ").append(i.getTitle()).append("\n");
            report.append("Company: ").append(i.getCompanyName()).append("\n---\n");
        }
        showReport("Approved Internships", report.toString());
    }

    private void showFullReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== FULL SYSTEM REPORT ===\n\n");
        report.append("STUDENTS: ").append(students.size()).append("\n");
        report.append("COMPANY REPS: ").append(companyReps.size()).append("\n");
        report.append("INTERNSHIPS: ").append(internships.size()).append("\n");
        report.append("APPLICATIONS: ").append(applications.size()).append("\n\n");
        report.append("Pending Reps: ").append(companyReps.stream().filter(r -> r.getStatus() == ApprovalStatus.PENDING).count()).append("\n");
        report.append("Pending Internships: ").append(internships.stream().filter(i -> i.getStatus() == InternshipStatus.PENDING).count()).append("\n");
        report.append("Withdrawal Requests: ").append(applications.stream().filter(app -> "PENDING".equals(app.getWithdrawalStatus())).count()).append("\n");
        showReport("Full System Report", report.toString());
    }

    private void showReport(String title, String content) {
        JTextArea textArea = new JTextArea(content);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        JOptionPane.showMessageDialog(this, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void changePassword() {
        JPasswordField currentField = new JPasswordField();
        JPasswordField newField = new JPasswordField();
        JPasswordField confirmField = new JPasswordField();
        
        Object[] message = {
            "Current Password:", currentField,
            "New Password:", newField,
            "Confirm Password:", confirmField
        };

        int result = JOptionPane.showConfirmDialog(this, message, 
            "Change Password", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String current = new String(currentField.getPassword());
            String newPass = new String(newField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (!newPass.equals(confirm)) {
                actionHandler.showError("New passwords do not match");
                return;
            }

            PasswordChangeResult pwdResult = 
                AuthController.changePassword(staff, current, newPass);

            if (pwdResult.isSuccess()) {
                actionHandler.showSuccess("Password changed successfully!");
            } else {
                actionHandler.showError(pwdResult.getMessage());
            }
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
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
