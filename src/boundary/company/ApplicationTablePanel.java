package sc2002_grpproject.boundary.company;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.*;
import sc2002_grpproject.boundary.RoundedButton;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationTablePanel extends JPanel {
    private CompanyRepresentative rep;
    private List<Internship> internships;
    private List<InternshipApplication> applications;
    
    private JTable applicationTable;
    private DefaultTableModel applicationModel;
    private CompanyActionHandler actionHandler;

    public ApplicationTablePanel(CompanyRepresentative rep, List<Internship> internships,
                                 List<InternshipApplication> applications,
                                 CompanyActionHandler actionHandler) {
        this.rep = rep;
        this.internships = internships;
        this.applications = applications;
        this.actionHandler = actionHandler;
        
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 255, 250));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initializeComponents();
    }

    private void initializeComponents() {
        // Top panel with info and filters
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(new Color(245, 255, 250));
        
        JLabel infoLabel = new JLabel("<html><b>Applications to My Internships</b> - Review and approve/reject PENDING applications</html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        infoLabel.setForeground(new Color(0, 80, 0));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.add(infoLabel, BorderLayout.NORTH);
        
        // Filter panel
        JPanel filterPanel = createFilterPanel();
        topPanel.add(filterPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Table
        createTable();
        JScrollPane scrollPane = new JScrollPane(applicationTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = createButtonPanel();
        add(btnPanel, BorderLayout.SOUTH);
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(new Color(245, 255, 250));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
        
        filterPanel.add(new JLabel("Internship Name:"));
        JTextField internshipNameField = new JTextField(15);
        internshipNameField.setFont(new Font("Arial", Font.PLAIN, 12));
        filterPanel.add(internshipNameField);
        
        filterPanel.add(new JLabel("Status:"));
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All", "PENDING", "SUCCESSFUL", "UNSUCCESSFUL", "WITHDRAWN"});
        statusFilter.setFont(new Font("Arial", Font.PLAIN, 12));
        filterPanel.add(statusFilter);
        
        filterPanel.add(new JLabel("Year:"));
        JComboBox<String> yearFilter = new JComboBox<>(new String[]{"All", "1", "2", "3", "4"});
        yearFilter.setFont(new Font("Arial", Font.PLAIN, 12));
        filterPanel.add(yearFilter);
        
        filterPanel.add(new JLabel("Confirmed:"));
        JComboBox<String> confirmedFilter = new JComboBox<>(new String[]{"All", "Confirmed", "Not Confirmed"});
        confirmedFilter.setFont(new Font("Arial", Font.PLAIN, 12));
        filterPanel.add(confirmedFilter);
        
        JButton applyFilterBtn = createButton("Apply Filters");
        applyFilterBtn.addActionListener(e -> {
            String internshipName = internshipNameField.getText().trim();
            String status = (String) statusFilter.getSelectedItem();
            String year = (String) yearFilter.getSelectedItem();
            String confirmed = (String) confirmedFilter.getSelectedItem();
            refreshWithFilters(internshipName, status, year, confirmed);
        });
        filterPanel.add(applyFilterBtn);
        
        JButton clearFilterBtn = createButton("Clear Filters");
        clearFilterBtn.addActionListener(e -> {
            internshipNameField.setText("");
            statusFilter.setSelectedIndex(0);
            yearFilter.setSelectedIndex(0);
            confirmedFilter.setSelectedIndex(0);
            refresh();
        });
        filterPanel.add(clearFilterBtn);
        
        return filterPanel;
    }

    private void createTable() {
        String[] columns = {"App ID", "Internship", "Student", "Major", "Year", "Status", "Confirmed"};
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
    }

    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(235, 250, 235));
        
        JButton approveBtn = createButton("Approve Selected");
        JButton rejectBtn = createButton("Reject Selected");
        JButton refreshBtn = createButton("Refresh");
        
        approveBtn.addActionListener(e -> {
            int selectedRow = applicationTable.getSelectedRow();
            if (selectedRow != -1) {
                String appId = (String) applicationModel.getValueAt(selectedRow, 0);
                actionHandler.processApplication(appId, true);
                refresh();
            } else {
                actionHandler.showError("Please select an application");
            }
        });
        
        rejectBtn.addActionListener(e -> {
            int selectedRow = applicationTable.getSelectedRow();
            if (selectedRow != -1) {
                String appId = (String) applicationModel.getValueAt(selectedRow, 0);
                actionHandler.processApplication(appId, false);
                refresh();
            } else {
                actionHandler.showError("Please select an application");
            }
        });
        
        refreshBtn.addActionListener(e -> refresh());
        
        btnPanel.add(approveBtn);
        btnPanel.add(rejectBtn);
        btnPanel.add(refreshBtn);
        
        return btnPanel;
    }

    public void refresh() {
        applicationModel.setRowCount(0);
        
        List<Internship> myInternships = internships.stream()
            .filter(i -> i.getCompanyRepInCharge().getUserID().equals(rep.getUserID()))
            .collect(Collectors.toList());

        List<InternshipApplication> relevantApps = applications.stream()
            .filter(app -> myInternships.stream()
                .anyMatch(i -> i.getInternshipID().equals(app.getInternship().getInternshipID())))
            .sorted((a1, a2) -> {
                if (a1.getStatus() == ApplicationStatus.PENDING) return -1;
                if (a2.getStatus() == ApplicationStatus.PENDING) return 1;
                return 0;
            })
            .collect(Collectors.toList());

        for (InternshipApplication app : relevantApps) {
            String statusText = "<html><b>" + app.getStatus() + "</b></html>";
            String confirmedText = app.isPlacementConfirmed() ? "Confirmed" : "Not Confirmed";
            applicationModel.addRow(new Object[]{
                app.getApplicationId(),
                app.getInternship().getTitle(),
                app.getStudent().getName(),
                app.getStudent().getMajor(),
                app.getStudent().getYearOfStudy(),
                statusText,
                confirmedText
            });
        }
        
        applyRowColors();
    }
    
    public void refreshWithFilters(String internshipName, String status, String year, String confirmed) {
        applicationModel.setRowCount(0);
        
        List<Internship> myInternships = internships.stream()
            .filter(i -> i.getCompanyRepInCharge().getUserID().equals(rep.getUserID()))
            .collect(Collectors.toList());

        List<InternshipApplication> relevantApps = applications.stream()
            .filter(app -> myInternships.stream()
                .anyMatch(i -> i.getInternshipID().equals(app.getInternship().getInternshipID())))
            .collect(Collectors.toList());

        if (internshipName != null && !internshipName.isEmpty()) {
            relevantApps = relevantApps.stream()
                .filter(app -> app.getInternship().getTitle().toLowerCase().contains(internshipName.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (status != null && !status.equals("All")) {
            relevantApps = relevantApps.stream()
                .filter(app -> app.getStatus().toString().equals(status))
                .collect(Collectors.toList());
        }
        
        if (year != null && !year.equals("All")) {
            int yearNum = Integer.parseInt(year);
            relevantApps = relevantApps.stream()
                .filter(app -> app.getStudent().getYearOfStudy() == yearNum)
                .collect(Collectors.toList());
        }
        
        if (confirmed != null && !confirmed.equals("All")) {
            boolean isConfirmed = confirmed.equals("Confirmed");
            relevantApps = relevantApps.stream()
                .filter(app -> app.isPlacementConfirmed() == isConfirmed)
                .collect(Collectors.toList());
        }

        relevantApps = relevantApps.stream()
            .sorted((a1, a2) -> {
                if (a1.getStatus() == ApplicationStatus.PENDING) return -1;
                if (a2.getStatus() == ApplicationStatus.PENDING) return 1;
                return 0;
            })
            .collect(Collectors.toList());

        for (InternshipApplication app : relevantApps) {
            String statusText = "<html><b>" + app.getStatus() + "</b></html>";
            String confirmedText = app.isPlacementConfirmed() ? "Confirmed" : "Not Confirmed";
            applicationModel.addRow(new Object[]{
                app.getApplicationId(),
                app.getInternship().getTitle(),
                app.getStudent().getName(),
                app.getStudent().getMajor(),
                app.getStudent().getYearOfStudy(),
                statusText,
                confirmedText
            });
        }
        
        applyRowColors();
        
        if (relevantApps.isEmpty()) {
            actionHandler.showInfo("No applications found matching your filter criteria.");
        }
    }

    private void applyRowColors() {
        applicationTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(new Color(220, 220, 220));
                } else {
                    String st = applicationModel.getValueAt(row, 5).toString();
                    if (st.contains("SUCCESSFUL")) {
                        c.setBackground(new Color(220, 255, 220));
                    } else if (st.contains("PENDING")) {
                        c.setBackground(new Color(255, 245, 200));
                    } else if (st.contains("UNSUCCESSFUL")) {
                        c.setBackground(new Color(255, 220, 220));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                c.setForeground(Color.BLACK);
                return c;
            }
        });
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
