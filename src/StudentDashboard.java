package sc2002_grpproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
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
    
    private JTable internshipTable;
    private JTable applicationTable;
    private DefaultTableModel internshipModel;
    private DefaultTableModel applicationModel;

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
        mainPanel.setBackground(new Color(230, 255, 240)); // Light mint green
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top panel with welcome and buttons
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
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));
        tabbedPane.setBackground(new Color(200, 255, 200));
        tabbedPane.setForeground(new Color(0, 100, 0));

        tabbedPane.addTab("Available Internships", createInternshipPanel());
        tabbedPane.addTab("My Applications", createApplicationPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
        
        refreshInternships();
        refreshApplications();
    }

    private JPanel createNotificationPanel() {
        List<InternshipApplication> studentApps = applications.stream()
            .filter(app -> app.getStudent().getUserID().equals(student.getUserID()))
            .collect(Collectors.toList());
        
        long approved = studentApps.stream()
            .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.SUCCESSFUL)
            .count();
        long rejected = studentApps.stream()
            .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.UNSUCCESSFUL)
            .count();
        long withdrawn = studentApps.stream()
            .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.WITHDRAWN)
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

    private JPanel createInternshipPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 255, 250)); // Very light mint
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Info label at top
        JLabel infoLabel = new JLabel("<html><b>Available Internships</b> - Browse and apply to internships matching your major and year</html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        infoLabel.setForeground(new Color(0, 80, 0));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        panel.add(infoLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Title", "Company", "Level", "Major", "Slots", "Available", "Opening", "Closing"};
        internshipModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        internshipTable = new JTable(internshipModel);
        internshipTable.setFont(new Font("Arial", Font.PLAIN, 12));
        internshipTable.setRowHeight(32);
        internshipTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        internshipTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        internshipTable.getTableHeader().setBackground(new Color(144, 238, 144));
        internshipTable.getTableHeader().setForeground(Color.BLACK);
        internshipTable.setSelectionBackground(new Color(220, 220, 220)); // Light grey when selected
        internshipTable.setSelectionForeground(Color.BLACK);
        
        // Add cell renderer for color coding
        internshipTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(new Color(220, 220, 220)); // Light grey for selected
                } else {
                    // Check if slots are available (column 6 is "Available")
                    String available = internshipModel.getValueAt(row, 6).toString();
                    int availableSlots = Integer.parseInt(available);
                    if (availableSlots > 0) {
                        c.setBackground(new Color(220, 255, 220)); // Light green for available
                    } else {
                        c.setBackground(new Color(255, 220, 220)); // Light red for full
                    }
                }
                c.setForeground(Color.BLACK);
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(internshipTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(144, 238, 144), 2),
            "Select an internship to apply",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.ITALIC, 11),
            new Color(0, 100, 0)
        ));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(new Color(235, 250, 235));
        
        JButton applyBtn = createButton("Apply to Internship");
        JButton refreshBtn = createButton("Refresh List");
        
        applyBtn.setToolTipText("Select an internship from the table and click to apply");
        refreshBtn.setToolTipText("Reload the latest internship listings");
        
        applyBtn.addActionListener(e -> applyForInternship());
        refreshBtn.addActionListener(e -> refreshInternships());
        
        btnPanel.add(applyBtn);
        btnPanel.add(refreshBtn);
        
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createApplicationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 255, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Info label at top
        JLabel infoLabel = new JLabel("<html><b>My Applications</b> - Track your applications. Accept SUCCESSFUL offers or withdraw from CONFIRMED placements</html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        infoLabel.setForeground(new Color(0, 80, 0));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        panel.add(infoLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"App ID", "Internship", "Company", "Status", "Confirmed", "Withdrawal Requested"};
        applicationModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        applicationTable = new JTable(applicationModel);
        applicationTable.setFont(new Font("Arial", Font.PLAIN, 12));
        applicationTable.setRowHeight(28);
        applicationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        applicationTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        applicationTable.getTableHeader().setBackground(new Color(144, 238, 144));
        applicationTable.getTableHeader().setForeground(Color.BLACK);
        applicationTable.setSelectionBackground(new Color(180, 255, 180));
        applicationTable.setSelectionForeground(Color.BLACK);
        
        JScrollPane scrollPane = new JScrollPane(applicationTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(new Color(235, 250, 235));
        
        JButton acceptBtn = createButton("Accept Placement");
        JButton withdrawBtn = createButton("Request Withdrawal");
        JButton refreshBtn2 = createButton("Refresh List");
        
        acceptBtn.setToolTipText("Confirm your acceptance of a successful internship offer");
        withdrawBtn.setToolTipText("Request to withdraw from a confirmed placement");
        refreshBtn2.setToolTipText("Reload your application status");
        
        acceptBtn.addActionListener(e -> acceptPlacement());
        withdrawBtn.addActionListener(e -> requestWithdrawal());
        refreshBtn2.addActionListener(e -> refreshApplications());
        
        btnPanel.add(acceptBtn);
        btnPanel.add(withdrawBtn);
        btnPanel.add(refreshBtn2);
        
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshInternships() {
        internshipModel.setRowCount(0);
        
        List<Internship> available = internships.stream()
            .filter(i -> i.isVisible() 
                      && i.getStatus() == Internship.InternshipStatus.APPROVED
                      && LocalDate.now().isAfter(i.getOpeningDate().minusDays(1))  
                      && LocalDate.now().isBefore(i.getClosingDate().plusDays(1))
                      && i.getConfirmedPlacements() < i.getSlots()
                      && isEligible(student, i))
            .collect(Collectors.toList());

        for (Internship i : available) {
            internshipModel.addRow(new Object[]{
                i.getInternshipID(),
                i.getTitle(),
                i.getCompanyName(),
                i.getLevel(),
                i.getPreferredMajor(),
                i.getSlots(),
                (i.getSlots() - i.getConfirmedPlacements()),
                i.getOpeningDate(),
                i.getClosingDate()
            });
        }
    }

    private void refreshApplications() {
        applicationModel.setRowCount(0);
        
        List<InternshipApplication> myApps = applications.stream()
            .filter(app -> app.getStudent().getUserID().equals(student.getUserID()))
            .sorted((a1, a2) -> {
                // Sort: SUCCESSFUL first, then PENDING, then others
                if (a1.getStatus() == InternshipApplication.ApplicationStatus.SUCCESSFUL) return -1;
                if (a2.getStatus() == InternshipApplication.ApplicationStatus.SUCCESSFUL) return 1;
                if (a1.getStatus() == InternshipApplication.ApplicationStatus.PENDING) return -1;
                if (a2.getStatus() == InternshipApplication.ApplicationStatus.PENDING) return 1;
                return 0;
            })
            .collect(Collectors.toList());

        for (InternshipApplication app : myApps) {
            String statusText = "<html><b>" + app.getStatus() + "</b></html>";
            applicationModel.addRow(new Object[]{
                app.getApplicationId(),
                app.getInternship().getTitle(),
                app.getInternship().getCompanyName(),
                statusText,
                app.isPlacementConfirmed() ? "Yes" : "No",
                app.isWithdrawalRequested() ? "Yes" : "No"
            });
        }
        
        // Apply row colors based on status
        applicationTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(new Color(220, 220, 220)); // Light grey for selected
                } else {
                    String status = applicationModel.getValueAt(row, 3).toString();
                    if (status.contains("SUCCESSFUL")) {
                        c.setBackground(new Color(220, 255, 220)); // Light green
                    } else if (status.contains("PENDING")) {
                        c.setBackground(new Color(255, 245, 200)); // Light orange/yellow
                    } else if (status.contains("UNSUCCESSFUL")) {
                        c.setBackground(new Color(255, 220, 220)); // Light red
                    } else if (status.contains("WITHDRAWN")) {
                        c.setBackground(new Color(220, 220, 220)); // Light gray
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                c.setForeground(Color.BLACK);
                return c;
            }
        });
    }

    private boolean isEligible(Student s, Internship i) {
        if (!i.getPreferredMajor().equalsIgnoreCase(s.getMajor())) {
            return false;
        }
        if (s.getYearOfStudy() <= 2 && i.getLevel() != Internship.InternshipLevel.BASIC) {
            return false;
        }
        return true;
    }

    private void applyForInternship() {
        int selectedRow = internshipTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select an internship from the table to apply");
            return;
        }
        
        String internshipId = (String) internshipModel.getValueAt(selectedRow, 0);

        long activeApps = applications.stream()
            .filter(app -> app.getStudent().getUserID().equals(student.getUserID()))
            .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.PENDING 
                        || app.getStatus() == InternshipApplication.ApplicationStatus.SUCCESSFUL)
            .count();

        if (activeApps >= 3) {
            showError("You have reached the maximum of 3 active applications.");
            return;
        }

        Internship selected = internships.stream()
            .filter(i -> i.getInternshipID().equals(internshipId))
            .findFirst()
            .orElse(null);

        if (selected == null) return;

        boolean hasActiveApp = applications.stream()
            .anyMatch(app -> app.getStudent().getUserID().equals(student.getUserID()) 
                        && app.getInternship().getInternshipID().equals(internshipId)
                        && (app.getStatus() == InternshipApplication.ApplicationStatus.PENDING
                            || app.getStatus() == InternshipApplication.ApplicationStatus.SUCCESSFUL));

        if (hasActiveApp) {
            showError("You already have an active application for this internship.");
            return;
        }

        InternshipApplication newApp = new InternshipApplication(student, selected);
        applications.add(newApp);
        
        showInfo("Application submitted successfully!\nApplication ID: " + newApp.getApplicationId());
        refreshApplications();
    }

    private void acceptPlacement() {
        int selectedRow = applicationTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select an application to accept");
            return;
        }

        String appId = (String) applicationModel.getValueAt(selectedRow, 0);
        InternshipApplication selected = applications.stream()
            .filter(app -> app.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);

        if (selected == null) return;

        if (selected.getStatus() != InternshipApplication.ApplicationStatus.SUCCESSFUL) {
            showError("You can only accept SUCCESSFUL applications.");
            return;
        }

        if (selected.isPlacementConfirmed()) {
            showError("This placement is already confirmed.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Accept placement for:\n" + selected.getInternship().getTitle() + "\n\n" +
            "This will withdraw all your other applications.\nContinue?",
            "Confirm Acceptance", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            selected.setPlacementConfirmed(true);
            selected.getInternship().incrementConfirmedPlacements();

            applications.stream()
                .filter(app -> app.getStudent().getUserID().equals(student.getUserID()))
                .filter(app -> !app.getApplicationId().equals(appId))
                .filter(app -> app.getStatus() != InternshipApplication.ApplicationStatus.WITHDRAWN)
                .forEach(app -> app.setStatus(InternshipApplication.ApplicationStatus.WITHDRAWN));

            showInfo("Placement accepted! All other applications have been withdrawn.");
            refreshApplications();
            refreshInternships();
        }
    }

    private void requestWithdrawal() {
        int selectedRow = applicationTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select an application to withdraw");
            return;
        }

        String appId = (String) applicationModel.getValueAt(selectedRow, 0);
        InternshipApplication selected = applications.stream()
            .filter(app -> app.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);

        if (selected == null) return;

        if (selected.getStatus() == InternshipApplication.ApplicationStatus.WITHDRAWN 
            || selected.getStatus() == InternshipApplication.ApplicationStatus.UNSUCCESSFUL) {
            showError("Cannot withdraw this application.");
            return;
        }

        if (selected.isWithdrawalRequested()) {
            showError("Withdrawal already requested for this application.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Request withdrawal for:\n" + selected.getInternship().getTitle() + "\n\n" +
            "This will require staff approval.\nContinue?",
            "Confirm Withdrawal Request", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            selected.setWithdrawalRequested(true);
            showInfo("Withdrawal request submitted.\nAwaiting Career Center Staff approval.");
            refreshApplications();
        }
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
                showError("Incorrect current password");
                return;
            }

            if (!newPass.equals(confirm)) {
                showError("New passwords do not match");
                return;
            }

            student.changePassword(newPass);
            showInfo("Password changed successfully!\nYou will be logged out for security.");
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

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
