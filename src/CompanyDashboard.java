package sc2002_grpproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
    
    private JTable internshipTable;
    private JTable applicationTable;
    private DefaultTableModel internshipModel;
    private DefaultTableModel applicationModel;

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

        tabbedPane.addTab("My Internships", createInternshipPanel());
        tabbedPane.addTab("Applications", createApplicationPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
        
        refreshInternships();
        refreshApplications();
    }

    private JPanel createNotificationPanel() {
        List<Internship> myInternships = internships.stream()
            .filter(i -> i.getCompanyRepInCharge().getUserID().equals(rep.getUserID()))
            .collect(Collectors.toList());
        
        long pendingApps = applications.stream()
            .filter(app -> myInternships.stream()
                .anyMatch(i -> i.getInternshipID().equals(app.getInternship().getInternshipID())))
            .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.PENDING)
            .count();
        
        long approvedInternships = myInternships.stream()
            .filter(i -> i.getStatus() == Internship.InternshipStatus.APPROVED)
            .count();
        long rejectedInternships = myInternships.stream()
            .filter(i -> i.getStatus() == Internship.InternshipStatus.REJECTED)
            .count();
        long pendingInternships = myInternships.stream()
            .filter(i -> i.getStatus() == Internship.InternshipStatus.PENDING)
            .count();
        
        if (pendingApps == 0 && approvedInternships == 0 && rejectedInternships == 0 && pendingInternships == 0) {
            return null;
        }
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 250, 205)); // Light yellow
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

    private JPanel createInternshipPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 255, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Info panel with filters
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(new Color(245, 255, 250));
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(245, 255, 250));
        
        JLabel countLabel = new JLabel("Internships Created: " + rep.getInternshipsCreated() + "/5");
        countLabel.setFont(new Font("Arial", Font.BOLD, 14));
        countLabel.setForeground(new Color(0, 80, 0));
        infoPanel.add(countLabel, BorderLayout.WEST);
        
        JLabel helpLabel = new JLabel("<html><i>Create/Edit/Delete PENDING internships. Toggle visibility for APPROVED ones.</i></html>");
        helpLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        helpLabel.setForeground(new Color(100, 100, 100));
        infoPanel.add(helpLabel, BorderLayout.SOUTH);
        
        topPanel.add(infoPanel, BorderLayout.NORTH);
        
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
        
        filterPanel.add(new JLabel("Visibility:"));
        JComboBox<String> visibilityFilter = new JComboBox<>(new String[]{"All", "Visible", "Hidden"});
        visibilityFilter.setFont(new Font("Arial", Font.PLAIN, 12));
        filterPanel.add(visibilityFilter);
        
        JButton applyFilterBtn = createButton("Apply Filters");
        applyFilterBtn.addActionListener(e -> {
            String status = (String) statusFilter.getSelectedItem();
            String level = (String) levelFilter.getSelectedItem();
            String visibility = (String) visibilityFilter.getSelectedItem();
            refreshInternshipsWithFilters(status, level, visibility);
        });
        filterPanel.add(applyFilterBtn);
        
        JButton clearFilterBtn = createButton("Clear Filters");
        clearFilterBtn.addActionListener(e -> {
            statusFilter.setSelectedIndex(0);
            levelFilter.setSelectedIndex(0);
            visibilityFilter.setSelectedIndex(0);
            refreshInternships();
        });
        filterPanel.add(clearFilterBtn);
        
        topPanel.add(filterPanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Title", "Level", "Major", "Slots", "Confirmed", "Opening", "Closing", "Status", "Visible"};
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
        internshipTable.setSelectionBackground(new Color(220, 220, 220)); // Light grey when selected
        internshipTable.setSelectionForeground(Color.BLACK);
        
        // Add cell renderer for status-based color coding
        internshipTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(new Color(220, 220, 220)); // Light grey for selected
                } else {
                    String status = internshipModel.getValueAt(row, 8).toString(); // Status column
                    if (status.contains("APPROVED")) {
                        c.setBackground(new Color(220, 255, 220)); // Light green
                    } else if (status.contains("PENDING")) {
                        c.setBackground(new Color(255, 245, 200)); // Light orange/yellow
                    } else if (status.contains("REJECTED")) {
                        c.setBackground(new Color(255, 220, 220)); // Light red
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                c.setForeground(Color.BLACK);
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(internshipTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(235, 250, 235));
        
        JButton createBtn = createButton("Create Internship");
        JButton editBtn = createButton("Edit Selected");
        JButton deleteBtn = createButton("Delete Selected");
        JButton toggleBtn = createButton("Toggle Visibility");
        JButton refreshBtn = createButton("Refresh");
        
        createBtn.addActionListener(e -> createInternship());
        editBtn.addActionListener(e -> editInternship());
        deleteBtn.addActionListener(e -> deleteInternship());
        toggleBtn.addActionListener(e -> toggleVisibility());
        refreshBtn.addActionListener(e -> refreshInternships());
        
        btnPanel.add(createBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(toggleBtn);
        btnPanel.add(refreshBtn);
        
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createApplicationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 255, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Info label at top
        JLabel infoLabel = new JLabel("<html><b>Applications to My Internships</b> - Review and approve/reject PENDING applications</html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        infoLabel.setForeground(new Color(0, 80, 0));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        panel.add(infoLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"App ID", "Internship", "Student", "Major", "Year", "Status"};
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
        
        JButton approveBtn = createButton("Approve Selected");
        JButton rejectBtn = createButton("Reject Selected");
        JButton refreshBtn = createButton("Refresh");
        
        approveBtn.addActionListener(e -> processApplication(true));
        rejectBtn.addActionListener(e -> processApplication(false));
        refreshBtn.addActionListener(e -> refreshApplications());
        
        btnPanel.add(approveBtn);
        btnPanel.add(rejectBtn);
        btnPanel.add(refreshBtn);
        
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshInternships() {
        internshipModel.setRowCount(0);
        
        List<Internship> myInternships = internships.stream()
            .filter(i -> i.getCompanyRepInCharge().getUserID().equals(rep.getUserID()))
            .collect(Collectors.toList());

        for (Internship i : myInternships) {
            internshipModel.addRow(new Object[]{
                i.getInternshipID(),
                i.getTitle(),
                i.getLevel(),
                i.getPreferredMajor(),
                i.getSlots(),
                i.getConfirmedPlacements(),
                i.getOpeningDate(),
                i.getClosingDate(),
                i.getStatus(),
                i.isVisible() ? "Yes" : "No"
            });
        }
    }

    private void refreshInternshipsWithFilters(String status, String level, String visibility) {
        internshipModel.setRowCount(0);
        
        List<Internship> myInternships = internships.stream()
            .filter(i -> i.getCompanyRepInCharge().getUserID().equals(rep.getUserID()))
            .collect(Collectors.toList());

        // Apply status filter
        if (status != null && !status.equals("All")) {
            myInternships = myInternships.stream()
                .filter(i -> i.getStatus().toString().equals(status))
                .collect(Collectors.toList());
        }
        
        // Apply level filter
        if (level != null && !level.equals("All")) {
            myInternships = myInternships.stream()
                .filter(i -> i.getLevel().toString().equals(level))
                .collect(Collectors.toList());
        }
        
        // Apply visibility filter
        if (visibility != null && !visibility.equals("All")) {
            boolean showVisible = visibility.equals("Visible");
            myInternships = myInternships.stream()
                .filter(i -> i.isVisible() == showVisible)
                .collect(Collectors.toList());
        }

        for (Internship i : myInternships) {
            internshipModel.addRow(new Object[]{
                i.getInternshipID(),
                i.getTitle(),
                i.getLevel(),
                i.getPreferredMajor(),
                i.getSlots(),
                i.getConfirmedPlacements(),
                i.getOpeningDate(),
                i.getClosingDate(),
                i.getStatus(),
                i.isVisible() ? "Yes" : "No"
            });
        }
        
        if (myInternships.isEmpty()) {
            showInfo("No internships found matching your filter criteria.");
        }
    }

    private void refreshApplications() {
        applicationModel.setRowCount(0);
        
        List<Internship> myInternships = internships.stream()
            .filter(i -> i.getCompanyRepInCharge().getUserID().equals(rep.getUserID()))
            .collect(Collectors.toList());

        List<InternshipApplication> relevantApps = applications.stream()
            .filter(app -> myInternships.stream()
                .anyMatch(i -> i.getInternshipID().equals(app.getInternship().getInternshipID())))
            .sorted((a1, a2) -> {
                // Sort: PENDING first, then others
                if (a1.getStatus() == InternshipApplication.ApplicationStatus.PENDING) return -1;
                if (a2.getStatus() == InternshipApplication.ApplicationStatus.PENDING) return 1;
                return 0;
            })
            .collect(Collectors.toList());

        for (InternshipApplication app : relevantApps) {
            String statusText = "<html><b>" + app.getStatus() + "</b></html>";
            applicationModel.addRow(new Object[]{
                app.getApplicationId(),
                app.getInternship().getTitle(),
                app.getStudent().getName(),
                app.getStudent().getMajor(),
                app.getStudent().getYearOfStudy(),
                statusText
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
                    String status = applicationModel.getValueAt(row, 5).toString();
                    if (status.contains("SUCCESSFUL")) {
                        c.setBackground(new Color(220, 255, 220)); // Light green
                    } else if (status.contains("PENDING")) {
                        c.setBackground(new Color(255, 245, 200)); // Light orange/yellow
                    } else if (status.contains("UNSUCCESSFUL")) {
                        c.setBackground(new Color(255, 220, 220)); // Light red
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                c.setForeground(Color.BLACK);
                return c;
            }
        });
    }

    private void createInternship() {
        if (!rep.canCreateMoreInternships()) {
            showError("You have reached the maximum limit of 5 internship postings.");
            return;
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 255, 240));
        panel.setPreferredSize(new Dimension(600, 500));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        JTextField titleField = new JTextField(30);
        titleField.setFont(new Font("Arial", Font.PLAIN, 13));
        
        JTextArea descArea = new JTextArea(4, 30);
        descArea.setFont(new Font("Arial", Font.PLAIN, 13));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        
        JComboBox<String> levelCombo = new JComboBox<>(new String[]{"BASIC", "INTERMEDIATE", "ADVANCED"});
        levelCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        
        String[] majors = {
            "Computer Science", "Data Science & AI", "Computer Engineering", "Information Engineering & Media",
            "Aerospace Engineering", "Chemical & Biomolecular Engineering",
            "Engineering in Electrical & Electronic", "Mathematical Sciences",
            "Communication Studies", "Business", "Social Sciences",
            "Biological Sciences", "Arts"
        };
        JComboBox<String> majorCombo = new JComboBox<>(majors);
        majorCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        
        JSpinner slotsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        slotsSpinner.setFont(new Font("Arial", Font.PLAIN, 13));
        
        // Date pickers with spinners
        JSpinner openingYearSpinner = new JSpinner(new SpinnerNumberModel(2025, 2025, 2030, 1));
        JSpinner openingMonthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        JSpinner openingDaySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));
        
        JSpinner closingYearSpinner = new JSpinner(new SpinnerNumberModel(2025, 2025, 2030, 1));
        JSpinner closingMonthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        JSpinner closingDaySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));
        
        JPanel openingDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        openingDatePanel.setBackground(new Color(240, 255, 240));
        openingDatePanel.add(new JLabel("Year:"));
        openingDatePanel.add(openingYearSpinner);
        openingDatePanel.add(new JLabel("Month:"));
        openingDatePanel.add(openingMonthSpinner);
        openingDatePanel.add(new JLabel("Day:"));
        openingDatePanel.add(openingDaySpinner);
        
        JPanel closingDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        closingDatePanel.setBackground(new Color(240, 255, 240));
        closingDatePanel.add(new JLabel("Year:"));
        closingDatePanel.add(closingYearSpinner);
        closingDatePanel.add(new JLabel("Month:"));
        closingDatePanel.add(closingMonthSpinner);
        closingDatePanel.add(new JLabel("Day:"));
        closingDatePanel.add(closingDaySpinner);
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2;
        JLabel titleHeader = new JLabel("Create New Internship");
        titleHeader.setFont(new Font("Arial", Font.BOLD, 16));
        titleHeader.setForeground(new Color(0, 100, 0));
        panel.add(titleHeader, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Title:"), gbc);
        gbc.gridx = 1;
        panel.add(titleField, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Description:"), gbc);
        gbc.gridx = 1;
        panel.add(descScroll, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Level:"), gbc);
        gbc.gridx = 1;
        panel.add(levelCombo, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Preferred Major:"), gbc);
        gbc.gridx = 1;
        panel.add(majorCombo, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Number of Slots:"), gbc);
        gbc.gridx = 1;
        panel.add(slotsSpinner, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Opening Date:"), gbc);
        gbc.gridx = 1;
        panel.add(openingDatePanel, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Closing Date:"), gbc);
        gbc.gridx = 1;
        panel.add(closingDatePanel, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Create Internship", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText().trim();
                String desc = descArea.getText().trim();
                String levelStr = (String) levelCombo.getSelectedItem();
                String major = (String) majorCombo.getSelectedItem();
                int slots = (Integer) slotsSpinner.getValue();
                
                int openYear = (Integer) openingYearSpinner.getValue();
                int openMonth = (Integer) openingMonthSpinner.getValue();
                int openDay = (Integer) openingDaySpinner.getValue();
                LocalDate opening = LocalDate.of(openYear, openMonth, openDay);
                
                int closeYear = (Integer) closingYearSpinner.getValue();
                int closeMonth = (Integer) closingMonthSpinner.getValue();
                int closeDay = (Integer) closingDaySpinner.getValue();
                LocalDate closing = LocalDate.of(closeYear, closeMonth, closeDay);

                if (title.isEmpty() || desc.isEmpty()) {
                    showError("Please fill in all fields");
                    return;
                }

                if (closing.isBefore(opening)) {
                    showError("Closing date must be after opening date");
                    return;
                }

                Internship.InternshipLevel level = Internship.InternshipLevel.valueOf(levelStr);
                Internship newInternship = new Internship(title, desc, rep.getCompanyName(), rep,
                    level, major, slots, opening, closing);
                
                internships.add(newInternship);
                rep.incrementInternshipsCreated();

                showInfo("Internship created successfully!\nID: " + newInternship.getInternshipID() + 
                        "\nStatus: PENDING (awaiting Career Center approval)");
                refreshInternships();

            } catch (Exception e) {
                showError("Error creating internship: " + e.getMessage());
            }
        }
    }

    private void editInternship() {
        int selectedRow = internshipTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select an internship to edit");
            return;
        }

        String internshipId = (String) internshipModel.getValueAt(selectedRow, 0);
        Internship selected = internships.stream()
            .filter(i -> i.getInternshipID().equals(internshipId))
            .findFirst()
            .orElse(null);

        if (selected == null) return;

        if (selected.getStatus() == Internship.InternshipStatus.APPROVED) {
            showError("Cannot edit approved internships");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBackground(new Color(220, 245, 220));
        
        JTextField titleField = new JTextField(selected.getTitle());
        JTextArea descArea = new JTextArea(selected.getDescription(), 3, 20);
        JScrollPane descScroll = new JScrollPane(descArea);
        JComboBox<String> levelCombo = new JComboBox<>(new String[]{"BASIC", "INTERMEDIATE", "ADVANCED"});
        levelCombo.setSelectedItem(selected.getLevel().toString());
        
        String[] majors = {
            "Computer Science", "Data Science & AI", "Computer Engineering", "Information Engineering & Media",
            "Aerospace Engineering", "Chemical & Biomolecular Engineering",
            "Engineering in Electrical & Electronic", "Mathematical Sciences",
            "Communication Studies", "Business", "Social Sciences",
            "Biological Sciences", "Arts"
        };
        JComboBox<String> majorCombo = new JComboBox<>(majors);
        majorCombo.setSelectedItem(selected.getPreferredMajor());
        majorCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        
        JTextField slotsField = new JTextField(String.valueOf(selected.getSlots()));
        
        panel.add(createLabel("Title:"));
        panel.add(titleField);
        panel.add(createLabel("Description:"));
        panel.add(descScroll);
        panel.add(createLabel("Level:"));
        panel.add(levelCombo);
        panel.add(createLabel("Preferred Major:"));
        panel.add(majorCombo);
        panel.add(createLabel("Slots (1-10):"));
        panel.add(slotsField);

        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Edit Internship", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText().trim();
                String desc = descArea.getText().trim();
                String levelStr = (String) levelCombo.getSelectedItem();
                String major = (String) majorCombo.getSelectedItem();
                int slots = Integer.parseInt(slotsField.getText().trim());

                if (!title.isEmpty()) selected.setTitle(title);
                if (!desc.isEmpty()) selected.setDescription(desc);
                selected.setLevel(Internship.InternshipLevel.valueOf(levelStr));
                if (major != null && !major.isEmpty()) selected.setPreferredMajor(major);
                if (slots >= 1 && slots <= 10) selected.setSlots(slots);

                showInfo("Internship updated successfully!");
                refreshInternships();

            } catch (Exception e) {
                showError("Error updating internship: " + e.getMessage());
            }
        }
    }

    private void deleteInternship() {
        int selectedRow = internshipTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select an internship to delete");
            return;
        }

        String internshipId = (String) internshipModel.getValueAt(selectedRow, 0);
        Internship selected = internships.stream()
            .filter(i -> i.getInternshipID().equals(internshipId))
            .findFirst()
            .orElse(null);

        if (selected == null) return;

        if (selected.getStatus() == Internship.InternshipStatus.APPROVED) {
            showError("Cannot delete approved internships");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete:\n" + selected.getTitle() + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            applications.removeIf(app -> app.getInternship().getInternshipID().equals(internshipId));
            internships.remove(selected);
            rep.decrementInternshipsCreated();
            showInfo("Internship deleted successfully!");
            refreshInternships();
            refreshApplications();
        }
    }

    private void toggleVisibility() {
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

        if (selected.getStatus() != Internship.InternshipStatus.APPROVED) {
            showError("Can only toggle visibility for APPROVED internships");
            return;
        }

        selected.setVisible(!selected.isVisible());
        showInfo("Visibility toggled! New status: " + (selected.isVisible() ? "Visible" : "Hidden"));
        refreshInternships();
    }

    private void processApplication(boolean approve) {
        int selectedRow = applicationTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select an application");
            return;
        }

        String appId = (String) applicationModel.getValueAt(selectedRow, 0);
        InternshipApplication selected = applications.stream()
            .filter(app -> app.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);

        if (selected == null) return;

        if (selected.getStatus() != InternshipApplication.ApplicationStatus.PENDING) {
            showError("Can only process PENDING applications");
            return;
        }

        String action = approve ? "APPROVE" : "REJECT";
        int confirm = JOptionPane.showConfirmDialog(this,
            action + " application from:\n" + selected.getStudent().getName() + 
            "\nFor: " + selected.getInternship().getTitle() + "?",
            "Confirm " + action, JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (approve) {
                selected.setStatus(InternshipApplication.ApplicationStatus.SUCCESSFUL);
                showInfo("Application approved!");
            } else {
                selected.setStatus(InternshipApplication.ApplicationStatus.UNSUCCESSFUL);
                showInfo("Application rejected.");
            }
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

            if (!rep.checkPassword(current)) {
                showError("Incorrect current password");
                return;
            }

            if (!newPass.equals(confirm)) {
                showError("New passwords do not match");
                return;
            }

            rep.changePassword(newPass);
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
