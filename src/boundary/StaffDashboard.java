package sc2002_grpproject.boundary;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.Enums.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class StaffDashboard extends JFrame {
    private CareerCenterStaff staff;
    private List<Internship> internships;
    private List<InternshipApplication> applications;
    private List<Student> students;
    private List<User> allUsers;
    private List<CareerCenterStaff> staffList;
    private List<CompanyRepresentative> companyReps;
    
    private JTable companyRepTable;
    private JTable internshipTable;
    private JTable applicationTable;
    private JTable withdrawalTable;
    private DefaultTableModel companyRepModel;
    private DefaultTableModel internshipModel;
    private DefaultTableModel applicationModel;
    private DefaultTableModel withdrawalModel;

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

        // Top panel
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

        tabbedPane.addTab("Company Reps", createCompanyRepPanel());
        tabbedPane.addTab("Internships", createInternshipPanel());
        tabbedPane.addTab("Applications", createApplicationPanel());
        tabbedPane.addTab("Withdrawals", createWithdrawalPanel());
        tabbedPane.addTab("Reports", createReportPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
        
        refreshCompanyReps();
        refreshInternships();
        refreshApplications();
        refreshWithdrawals();
    }

    private JPanel createNotificationPanel() {
        long pendingReps = companyReps.stream()
            .filter(rep -> rep.getStatus() == ApprovalStatus.PENDING)
            .count();
        
        long pendingInternships = internships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.PENDING)
            .count();
        
        long pendingWithdrawals = applications.stream()
            .filter(app -> app.isWithdrawalRequested())
            .count();
        
        if (pendingReps == 0 && pendingInternships == 0 && pendingWithdrawals == 0) {
            return null;
        }
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 230, 230)); // Light red/pink
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 100, 100), 3),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel titleLabel = new JLabel("🔔 PENDING ACTIONS REQUIRED");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(200, 0, 0));
        panel.add(titleLabel);
        
        if (pendingReps > 0) {
            JLabel label = new JLabel("👥 " + pendingReps + " company representative(s) awaiting authorization");
            label.setFont(new Font("Arial", Font.BOLD, 13));
            label.setForeground(new Color(150, 50, 0));
            panel.add(label);
        }
        if (pendingInternships > 0) {
            JLabel label = new JLabel("📋 " + pendingInternships + " internship(s) awaiting approval");
            label.setFont(new Font("Arial", Font.BOLD, 13));
            label.setForeground(new Color(150, 50, 0));
            panel.add(label);
        }
        if (pendingWithdrawals > 0) {
            JLabel label = new JLabel("🔄 " + pendingWithdrawals + " withdrawal request(s) pending");
            label.setFont(new Font("Arial", Font.BOLD, 13));
            label.setForeground(new Color(150, 50, 0));
            panel.add(label);
        }
        
        return panel;
    }

    private JPanel createCompanyRepPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 255, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(245, 255, 250));
        
        JLabel titleLabel = new JLabel("Company Representative Authorization");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(0, 80, 0));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel helpLabel = new JLabel("<html><i>Authorize PENDING company representatives to allow them to post internships</i></html>");
        helpLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        helpLabel.setForeground(new Color(100, 100, 100));
        titlePanel.add(helpLabel, BorderLayout.SOUTH);
        
        panel.add(titlePanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"User ID", "Name", "Email", "Company", "Status"};
        companyRepModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        companyRepTable = new JTable(companyRepModel);
        companyRepTable.setFont(new Font("Arial", Font.PLAIN, 12));
        companyRepTable.setRowHeight(32);
        companyRepTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        companyRepTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        companyRepTable.getTableHeader().setBackground(new Color(144, 238, 144));
        companyRepTable.getTableHeader().setForeground(Color.BLACK);
        companyRepTable.setSelectionBackground(new Color(220, 220, 220)); // Light grey when selected
        companyRepTable.setSelectionForeground(Color.BLACK);
        
        JScrollPane scrollPane = new JScrollPane(companyRepTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(235, 250, 235));
        
        JButton authorizeBtn = createButton("Authorize Selected");
        JButton rejectRepBtn = createButton("Reject Selected");
        JButton refreshBtn = createButton("Refresh");
        
        authorizeBtn.setToolTipText("Click on a PENDING representative row then click to authorize");
        rejectRepBtn.setToolTipText("Click on a PENDING representative row then click to reject");
        refreshBtn.setToolTipText("Reload the company representatives list");
        
        authorizeBtn.addActionListener(e -> authorizeRep());
        rejectRepBtn.addActionListener(e -> rejectRep());
        refreshBtn.addActionListener(e -> refreshCompanyReps());
        
        btnPanel.add(authorizeBtn);
        btnPanel.add(rejectRepBtn);
        btnPanel.add(refreshBtn);
        
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createInternshipPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 255, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel with title and filters
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(new Color(245, 255, 250));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(245, 255, 250));
        
        JLabel titleLabel = new JLabel("Internship Approval Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(0, 80, 0));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel helpLabel = new JLabel("<html><i>Approve/Reject PENDING internships. Use filters to find specific internships.</i></html>");
        helpLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        helpLabel.setForeground(new Color(100, 100, 100));
        titlePanel.add(helpLabel, BorderLayout.SOUTH);
        
        topPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(new Color(245, 255, 250));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
        
        filterPanel.add(new JLabel("Status:"));
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All", "PENDING", "APPROVED", "REJECTED", "FILLED"});
        statusFilter.setFont(new Font("Arial", Font.PLAIN, 12));
        filterPanel.add(statusFilter);
        
        filterPanel.add(new JLabel("Level:"));
        JComboBox<String> levelFilter = new JComboBox<>(new String[]{"All", "BASIC", "INTERMEDIATE", "ADVANCED"});
        levelFilter.setFont(new Font("Arial", Font.PLAIN, 12));
        filterPanel.add(levelFilter);
        
        filterPanel.add(new JLabel("Company:"));
        JTextField companyField = new JTextField(15);
        companyField.setFont(new Font("Arial", Font.PLAIN, 12));
        companyField.setToolTipText("Enter company name (partial match)");
        filterPanel.add(companyField);
        
        JButton applyFilterBtn = createButton("Apply Filters");
        applyFilterBtn.addActionListener(e -> {
            String status = (String) statusFilter.getSelectedItem();
            String level = (String) levelFilter.getSelectedItem();
            String company = companyField.getText().trim();
            refreshInternshipsWithFilters(status, level, company);
        });
        filterPanel.add(applyFilterBtn);
        
        JButton clearFilterBtn = createButton("Clear Filters");
        clearFilterBtn.addActionListener(e -> {
            statusFilter.setSelectedIndex(0);
            levelFilter.setSelectedIndex(0);
            companyField.setText("");
            refreshInternships();
        });
        filterPanel.add(clearFilterBtn);
        
        topPanel.add(filterPanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Title", "Company", "Level", "Major", "Slots", "Opening", "Closing", "Status"};
        internshipModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        internshipTable = new JTable(internshipModel);
        internshipTable.setFont(new Font("Arial", Font.PLAIN, 12));
        internshipTable.setRowHeight(28);
        internshipTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        internshipTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        internshipTable.getTableHeader().setBackground(new Color(144, 238, 144));
        internshipTable.getTableHeader().setForeground(Color.BLACK);
        internshipTable.setSelectionBackground(new Color(180, 255, 180));
        internshipTable.setSelectionForeground(Color.BLACK);
        
        JScrollPane scrollPane = new JScrollPane(internshipTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(235, 250, 235));
        
        JButton approveBtn = createButton("Approve Selected");
        JButton rejectIntBtn = createButton("Reject Selected");
        JButton refreshBtn = createButton("Refresh");
        
        approveBtn.addActionListener(e -> approveInternship());
        rejectIntBtn.addActionListener(e -> rejectInternship());
        refreshBtn.addActionListener(e -> refreshInternships());
        
        btnPanel.add(approveBtn);
        btnPanel.add(rejectIntBtn);
        btnPanel.add(refreshBtn);
        
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createApplicationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(235, 250, 235));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("All Applications Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(0, 80, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"App ID", "Student", "Internship", "Company", "Status"};
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

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(235, 250, 235));
        
        JButton refreshBtn = createButton("Refresh");
        refreshBtn.addActionListener(e -> refreshApplications());
        btnPanel.add(refreshBtn);
        
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createWithdrawalPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(235, 250, 235));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Withdrawal Requests");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(0, 80, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"App ID", "Student", "Internship", "Company", "Request Date", "Status"};
        withdrawalModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        withdrawalTable = new JTable(withdrawalModel);
        withdrawalTable.setFont(new Font("Arial", Font.PLAIN, 12));
        withdrawalTable.setRowHeight(28);
        withdrawalTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        withdrawalTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        withdrawalTable.getTableHeader().setBackground(new Color(144, 238, 144));
        withdrawalTable.getTableHeader().setForeground(Color.BLACK);
        withdrawalTable.setSelectionBackground(new Color(180, 255, 180));
        withdrawalTable.setSelectionForeground(Color.BLACK);
        
        JScrollPane scrollPane = new JScrollPane(withdrawalTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(235, 250, 235));
        
        JButton approveBtn = createButton("Approve Withdrawal");
        JButton refreshBtn = createButton("Refresh");
        
        approveBtn.addActionListener(e -> approveWithdrawal());
        refreshBtn.addActionListener(e -> refreshWithdrawals());
        
        btnPanel.add(approveBtn);
        btnPanel.add(refreshBtn);
        
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 20, 20));
        panel.setBackground(new Color(235, 250, 235));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

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
        RoundedButton button = new RoundedButton(text, 20);
        button.setBackground(new Color(200, 200, 200));
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());
        return button;
    }

    private void refreshCompanyReps() {
        companyRepModel.setRowCount(0);
        
        // Sort: PENDING first
        List<CompanyRepresentative> sortedReps = companyReps.stream()
            .sorted((r1, r2) -> {
                if (r1.getStatus() == ApprovalStatus.PENDING) return -1;
                if (r2.getStatus() == ApprovalStatus.PENDING) return 1;
                return 0;
            })
            .collect(Collectors.toList());
        
        for (CompanyRepresentative rep : sortedReps) {
            String statusText = rep.getStatus() == ApprovalStatus.APPROVED ? 
                "<html><b>Authorized</b></html>" : "<html><b style='color:#D2691E;'>Pending</b></html>";
            companyRepModel.addRow(new Object[]{
                rep.getUserID(),
                rep.getName(),
                rep.getUserID(), // UserID is email for company reps
                rep.getCompanyName(),
                statusText
            });
        }
        
        // Apply row colors
        companyRepTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(new Color(220, 220, 220)); // Light grey when selected
                } else {
                    String status = companyRepModel.getValueAt(row, 4).toString();
                    if (status.contains("Pending")) {
                        c.setBackground(new Color(255, 245, 200)); // Light orange/yellow for pending
                    } else {
                        c.setBackground(new Color(220, 255, 220)); // Light green for approved
                    }
                }
                return c;
            }
        });
    }

    private void refreshInternships() {
        internshipModel.setRowCount(0);
        
        // Sort: PENDING first
        List<Internship> sortedInternships = internships.stream()
            .sorted((i1, i2) -> {
                if (i1.getStatus() == InternshipStatus.PENDING) return -1;
                if (i2.getStatus() == InternshipStatus.PENDING) return 1;
                return 0;
            })
            .collect(Collectors.toList());
        
        for (Internship i : sortedInternships) {
            String statusText = "<html><b>" + i.getStatus() + "</b></html>";
            internshipModel.addRow(new Object[]{
                i.getInternshipID(),
                i.getTitle(),
                i.getCompanyName(),
                i.getLevel(),
                i.getPreferredMajor(),
                i.getSlots(),
                i.getOpeningDate(),
                i.getClosingDate(),
                statusText
            });
        }
        
        // Apply row colors
        internshipTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String status = internshipModel.getValueAt(row, 8).toString();
                    if (status.contains("PENDING")) {
                        c.setBackground(new Color(255, 255, 200)); // Light yellow
                    } else if (status.contains("APPROVED")) {
                        c.setBackground(new Color(200, 255, 200)); // Light green
                    } else if (status.contains("REJECTED")) {
                        c.setBackground(new Color(255, 200, 200)); // Light red
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });
    }

    private void refreshApplications() {
        applicationModel.setRowCount(0);
        for (InternshipApplication app : applications) {
            applicationModel.addRow(new Object[]{
                app.getApplicationId(),
                app.getStudent().getName(),
                app.getInternship().getTitle(),
                app.getInternship().getCompanyName(),
                app.getStatus()
            });
        }
    }

    private void refreshWithdrawals() {
        withdrawalModel.setRowCount(0);
        List<InternshipApplication> withdrawals = applications.stream()
            .filter(app -> app.isWithdrawalRequested())
            .collect(Collectors.toList());

        for (InternshipApplication app : withdrawals) {
            withdrawalModel.addRow(new Object[]{
                app.getApplicationId(),
                app.getStudent().getName(),
                app.getInternship().getTitle(),
                app.getInternship().getCompanyName(),
                "Requested",
                "Pending Approval"
            });
        }
    }

    private void authorizeRep() {
        int selectedRow = companyRepTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please click on a company representative row to select it");
            return;
        }

        String userId = (String) companyRepModel.getValueAt(selectedRow, 0);
        CompanyRepresentative rep = companyReps.stream()
            .filter(r -> r.getUserID().equals(userId))
            .findFirst()
            .orElse(null);

        if (rep == null) return;

        if (rep.getStatus() == ApprovalStatus.APPROVED) {
            showError("This representative is already authorized");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Authorize representative:\n" + rep.getName() + "\n" + rep.getCompanyName() + "?",
            "Confirm Authorization", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            rep.setStatus(ApprovalStatus.APPROVED);
            showInfo("Company representative authorized successfully!");
            refreshCompanyReps();
        }
    }

    private void approveInternship() {
        int selectedRow = internshipTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select an internship");
            return;
        }

        String internshipId = (String) internshipModel.getValueAt(selectedRow, 0);
        Internship selected = internships.stream()
            .filter(i -> i.getInternshipID().equals(internshipId))
            .findFirst()
            .orElse(null);

        if (selected == null) return;

        if (selected.getStatus() == InternshipStatus.APPROVED) {
            showError("This internship is already approved");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Approve internship:\n" + selected.getTitle() + "\nCompany: " + 
            selected.getCompanyName() + "?",
            "Confirm Approval", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            selected.setStatus(InternshipStatus.APPROVED);
            showInfo("Internship approved successfully!");
            refreshInternships();
        }
    }

    private void rejectRep() {
        int selectedRow = companyRepTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please click on a company representative row to select it");
            return;
        }

        String userId = (String) companyRepModel.getValueAt(selectedRow, 0);
        CompanyRepresentative rep = companyReps.stream()
            .filter(r -> r.getUserID().equals(userId))
            .findFirst()
            .orElse(null);

        if (rep == null) return;

        if (rep.getStatus() == ApprovalStatus.REJECTED) {
            showError("This representative is already rejected");
            return;
        }

        if (rep.getStatus() == ApprovalStatus.APPROVED) {
            showError("Cannot reject an approved representative");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Reject representative:\n" + rep.getName() + "\n" + rep.getCompanyName() + "?",
            "Confirm Rejection", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            rep.setStatus(ApprovalStatus.REJECTED);
            showInfo("Company representative rejected.");
            refreshCompanyReps();
        }
    }

    private void rejectInternship() {
        int selectedRow = internshipTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select an internship");
            return;
        }

        String internshipId = (String) internshipModel.getValueAt(selectedRow, 0);
        Internship selected = internships.stream()
            .filter(i -> i.getInternshipID().equals(internshipId))
            .findFirst()
            .orElse(null);

        if (selected == null) return;

        if (selected.getStatus() == InternshipStatus.REJECTED) {
            showError("This internship is already rejected");
            return;
        }

        if (selected.getStatus() == InternshipStatus.APPROVED) {
            showError("Cannot reject an approved internship");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Reject internship:\n" + selected.getTitle() + "\nCompany: " + 
            selected.getCompanyName() + "?",
            "Confirm Rejection", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            selected.setStatus(InternshipStatus.REJECTED);
            showInfo("Internship rejected.");
            refreshInternships();
        }
    }

    private void refreshInternshipsWithFilters(String status, String level, String company) {
        internshipModel.setRowCount(0);
        
        List<Internship> filteredInternships = internships.stream()
            .sorted((i1, i2) -> {
                if (i1.getStatus() == InternshipStatus.PENDING) return -1;
                if (i2.getStatus() == InternshipStatus.PENDING) return 1;
                return 0;
            })
            .collect(Collectors.toList());

        // Apply status filter
        if (status != null && !status.equals("All")) {
            filteredInternships = filteredInternships.stream()
                .filter(i -> i.getStatus().toString().equals(status))
                .collect(Collectors.toList());
        }
        
        // Apply level filter
        if (level != null && !level.equals("All")) {
            filteredInternships = filteredInternships.stream()
                .filter(i -> i.getLevel().toString().equals(level))
                .collect(Collectors.toList());
        }
        
        // Apply company filter
        if (company != null && !company.isEmpty()) {
            filteredInternships = filteredInternships.stream()
                .filter(i -> i.getCompanyName().toLowerCase().contains(company.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        for (Internship i : filteredInternships) {
            String statusText = "<html><b>" + i.getStatus() + "</b></html>";
            internshipModel.addRow(new Object[]{
                i.getInternshipID(),
                i.getTitle(),
                i.getCompanyName(),
                i.getLevel(),
                i.getPreferredMajor(),
                i.getSlots(),
                i.getOpeningDate(),
                i.getClosingDate(),
                statusText
            });
        }
        
        // Apply row colors
        internshipTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String statusStr = internshipModel.getValueAt(row, 8).toString();
                    if (statusStr.contains("PENDING")) {
                        c.setBackground(new Color(255, 255, 200)); // Light yellow
                    } else if (statusStr.contains("APPROVED")) {
                        c.setBackground(new Color(200, 255, 200)); // Light green
                    } else if (statusStr.contains("REJECTED")) {
                        c.setBackground(new Color(255, 200, 200)); // Light red
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });
        
        if (filteredInternships.isEmpty()) {
            showInfo("No internships found matching your filter criteria.");
        }
    }

    private void approveWithdrawal() {
        int selectedRow = withdrawalTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a withdrawal request");
            return;
        }

        String appId = (String) withdrawalModel.getValueAt(selectedRow, 0);
        InternshipApplication selected = applications.stream()
            .filter(app -> app.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);

        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Approve withdrawal request:\nStudent: " + selected.getStudent().getName() +
            "\nInternship: " + selected.getInternship().getTitle() + "?",
            "Confirm Withdrawal", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            selected.setStatus(ApplicationStatus.WITHDRAWN);
            selected.setWithdrawalRequested(false);
            Internship internship = selected.getInternship();
            internship.decrementConfirmedPlacements();
            showInfo("Withdrawal approved. Placement slot released.");
            refreshWithdrawals();
            refreshApplications();
        }
    }

    private void showStudentsReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== STUDENTS REPORT ===\n\n");
        report.append("Total Students: ").append(students.size()).append("\n\n");
        
        for (Student s : students) {
            report.append("ID: ").append(s.getUserID()).append("\n");
            report.append("Name: ").append(s.getName()).append("\n");
            report.append("Email: ").append(s.getEmail()).append("\n");
            report.append("Major: ").append(s.getMajor()).append("\n");
            report.append("Year: ").append(s.getYearOfStudy()).append("\n");
            
            long appCount = applications.stream()
                .filter(app -> app.getStudent().getUserID().equals(s.getUserID()))
                .count();
            report.append("Applications: ").append(appCount).append("\n");
            report.append("---\n");
        }

        showReport("Students Report", report.toString());
    }

    private void showInternshipsReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== INTERNSHIPS REPORT ===\n\n");
        report.append("Total Internships: ").append(internships.size()).append("\n");
        
        long pending = internships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.PENDING)
            .count();
        long approved = internships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.APPROVED)
            .count();
        
        report.append("Pending: ").append(pending).append("\n");
        report.append("Approved: ").append(approved).append("\n\n");
        
        for (Internship i : internships) {
            report.append("ID: ").append(i.getInternshipID()).append("\n");
            report.append("Title: ").append(i.getTitle()).append("\n");
            report.append("Company: ").append(i.getCompanyName()).append("\n");
            report.append("Status: ").append(i.getStatus()).append("\n");
            report.append("Slots: ").append(i.getConfirmedPlacements()).append("/").append(i.getSlots()).append("\n");
            report.append("---\n");
        }

        showReport("Internships Report", report.toString());
    }

    private void showApplicationsReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== APPLICATIONS REPORT ===\n\n");
        report.append("Total Applications: ").append(applications.size()).append("\n\n");
        
        long pending = applications.stream()
            .filter(app -> app.getStatus() == ApplicationStatus.PENDING)
            .count();
        long successful = applications.stream()
            .filter(app -> app.getStatus() == ApplicationStatus.SUCCESSFUL)
            .count();
        long placed = applications.stream()
            .filter(app -> app.isPlacementConfirmed())
            .count();
        
        report.append("Pending: ").append(pending).append("\n");
        report.append("Successful: ").append(successful).append("\n");
        report.append("Placement Confirmed: ").append(placed).append("\n\n");

        showReport("Applications Report", report.toString());
    }

    private void showCompaniesReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== COMPANIES REPORT ===\n\n");
        report.append("Total Company Representatives: ").append(companyReps.size()).append("\n\n");
        
        for (CompanyRepresentative rep : companyReps) {
            report.append("Name: ").append(rep.getName()).append("\n");
            report.append("Company: ").append(rep.getCompanyName()).append("\n");
            report.append("Email: ").append(rep.getUserID()).append("\n");
            report.append("Status: ").append(rep.getStatus()).append("\n");
            report.append("Internships Created: ").append(rep.getInternshipsCreated()).append("/5\n");
            report.append("---\n");
        }

        showReport("Companies Report", report.toString());
    }

    private void showPendingInternships() {
        List<Internship> pending = internships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.PENDING)
            .collect(Collectors.toList());

        StringBuilder report = new StringBuilder();
        report.append("=== PENDING INTERNSHIPS ===\n\n");
        report.append("Count: ").append(pending.size()).append("\n\n");
        
        for (Internship i : pending) {
            report.append("ID: ").append(i.getInternshipID()).append("\n");
            report.append("Title: ").append(i.getTitle()).append("\n");
            report.append("Company: ").append(i.getCompanyName()).append("\n");
            report.append("---\n");
        }

        showReport("Pending Internships", report.toString());
    }

    private void showPendingWithdrawals() {
        List<InternshipApplication> withdrawals = applications.stream()
            .filter(app -> app.isWithdrawalRequested())
            .collect(Collectors.toList());

        StringBuilder report = new StringBuilder();
        report.append("=== PENDING WITHDRAWALS ===\n\n");
        report.append("Count: ").append(withdrawals.size()).append("\n\n");
        
        for (InternshipApplication app : withdrawals) {
            report.append("Student: ").append(app.getStudent().getName()).append("\n");
            report.append("Internship: ").append(app.getInternship().getTitle()).append("\n");
            report.append("Company: ").append(app.getInternship().getCompanyName()).append("\n");
            report.append("---\n");
        }

        showReport("Pending Withdrawals", report.toString());
    }

    private void showApprovedInternships() {
        List<Internship> approved = internships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.APPROVED)
            .collect(Collectors.toList());

        StringBuilder report = new StringBuilder();
        report.append("=== APPROVED INTERNSHIPS ===\n\n");
        report.append("Count: ").append(approved.size()).append("\n\n");
        
        for (Internship i : approved) {
            report.append("ID: ").append(i.getInternshipID()).append("\n");
            report.append("Title: ").append(i.getTitle()).append("\n");
            report.append("Company: ").append(i.getCompanyName()).append("\n");
            report.append("Slots: ").append(i.getConfirmedPlacements()).append("/").append(i.getSlots()).append("\n");
            report.append("---\n");
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
        
        report.append("Authorized Reps: ").append(companyReps.stream()
            .filter(r -> r.getStatus() == ApprovalStatus.APPROVED)
            .count()).append("\n");
        report.append("Pending Reps: ").append(companyReps.stream()
            .filter(r -> r.getStatus() == ApprovalStatus.PENDING)
            .count()).append("\n\n");
        
        report.append("Approved Internships: ").append(internships.stream().filter(i -> i.getStatus() == InternshipStatus.APPROVED).count()).append("\n");
        report.append("Pending Internships: ").append(internships.stream().filter(i -> i.getStatus() == InternshipStatus.PENDING).count()).append("\n\n");
        
        report.append("Pending Applications: ").append(applications.stream().filter(app -> app.getStatus() == ApplicationStatus.PENDING).count()).append("\n");
        report.append("Successful Applications: ").append(applications.stream().filter(app -> app.getStatus() == ApplicationStatus.SUCCESSFUL).count()).append("\n");
        report.append("Confirmed Placements: ").append(applications.stream().filter(app -> app.isPlacementConfirmed()).count()).append("\n");
        report.append("Withdrawal Requests: ").append(applications.stream().filter(app -> app.isWithdrawalRequested()).count()).append("\n");

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

            if (!staff.checkPassword(current)) {
                showError("Incorrect current password");
                return;
            }

            if (!newPass.equals(confirm)) {
                showError("New passwords do not match");
                return;
            }

            staff.changePassword(newPass);
            showInfo("Password changed successfully!\nYou will be logged out for security.");
            logout();
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
