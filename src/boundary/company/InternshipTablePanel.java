package sc2002_grpproject.boundary.company;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.Enums.*;
import sc2002_grpproject.boundary.RoundedButton;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class InternshipTablePanel extends JPanel {
    private CompanyRepresentative rep;
    private List<Internship> internships;
    private List<InternshipApplication> applications;
    
    private JTable internshipTable;
    private DefaultTableModel internshipModel;
    private CompanyActionHandler actionHandler;
    private JLabel countLabel;  // Added to track the count label

    public InternshipTablePanel(CompanyRepresentative rep, List<Internship> internships,
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
        // Info panel with filters
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(new Color(245, 255, 250));
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(245, 255, 250));
        
        // Create count label as a field so it can be updated
        countLabel = new JLabel();
        countLabel.setFont(new Font("Arial", Font.BOLD, 14));
        countLabel.setForeground(new Color(0, 80, 0));
        updateCountLabel();  // Set initial text
        infoPanel.add(countLabel, BorderLayout.WEST);
        
        JLabel helpLabel = new JLabel("<html><i>Create/Edit/Delete PENDING internships. Toggle visibility for APPROVED ones.</i></html>");
        helpLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        helpLabel.setForeground(new Color(100, 100, 100));
        infoPanel.add(helpLabel, BorderLayout.SOUTH);
        
        topPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Filter panel
        JPanel filterPanel = createFilterPanel();
        topPanel.add(filterPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Table
        createTable();
        JScrollPane scrollPane = new JScrollPane(internshipTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = createButtonPanel();
        add(btnPanel, BorderLayout.SOUTH);
    }

    private JPanel createFilterPanel() {
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
            refreshWithFilters(status, level, visibility);
        });
        filterPanel.add(applyFilterBtn);
        
        JButton clearFilterBtn = createButton("Clear Filters");
        clearFilterBtn.addActionListener(e -> {
            statusFilter.setSelectedIndex(0);
            levelFilter.setSelectedIndex(0);
            visibilityFilter.setSelectedIndex(0);
            refresh();
        });
        filterPanel.add(clearFilterBtn);
        
        return filterPanel;
    }

    private void createTable() {
        String[] columns = {"ID", "Title", "Level", "Major", "Slots", "Available", "Confirmed", "Opening", "Closing", "Status", "Visible"};
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
        internshipTable.setSelectionBackground(new Color(220, 220, 220));
        internshipTable.setSelectionForeground(Color.BLACK);
        
        // Add cell renderer for status-based color coding
        internshipTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(new Color(220, 220, 220));
                } else {
                    String status = internshipModel.getValueAt(row, 9).toString();
                    if (status.contains("APPROVED")) {
                        c.setBackground(new Color(220, 255, 220));
                    } else if (status.contains("PENDING")) {
                        c.setBackground(new Color(255, 245, 200));
                    } else if (status.contains("REJECTED")) {
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

    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(235, 250, 235));
        
        JButton createBtn = createButton("Create Internship");
        JButton editBtn = createButton("Edit Selected");
        JButton deleteBtn = createButton("Delete Selected");
        JButton toggleBtn = createButton("Toggle Visibility");
        JButton refreshBtn = createButton("Refresh");
        
        createBtn.addActionListener(e -> {
            actionHandler.createInternship();
            refresh();
        });
        editBtn.addActionListener(e -> {
            int selectedRow = internshipTable.getSelectedRow();
            if (selectedRow != -1) {
                String internshipId = (String) internshipModel.getValueAt(selectedRow, 0);
                actionHandler.editInternship(internshipId);
                refresh();
            } else {
                actionHandler.showError("Please select an internship to edit");
            }
        });
        deleteBtn.addActionListener(e -> {
            int selectedRow = internshipTable.getSelectedRow();
            if (selectedRow != -1) {
                String internshipId = (String) internshipModel.getValueAt(selectedRow, 0);
                actionHandler.deleteInternship(internshipId);
                refresh();
            } else {
                actionHandler.showError("Please select an internship to delete");
            }
        });
        toggleBtn.addActionListener(e -> {
            int selectedRow = internshipTable.getSelectedRow();
            if (selectedRow != -1) {
                String internshipId = (String) internshipModel.getValueAt(selectedRow, 0);
                actionHandler.toggleVisibility(internshipId);
                refresh();
            } else {
                actionHandler.showError("Please select an internship");
            }
        });
        refreshBtn.addActionListener(e -> refresh());
        
        btnPanel.add(createBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(toggleBtn);
        btnPanel.add(refreshBtn);
        
        return btnPanel;
    }

    public void refresh() {
        internshipModel.setRowCount(0);
        
        List<Internship> myInternships = internships.stream()
            .filter(i -> i.getCompanyRepInCharge().getUserID().equals(rep.getUserID()))
            .collect(Collectors.toList());

        for (Internship i : myInternships) {
            long successfulCount = applications.stream()
                .filter(app -> app.getInternship().getInternshipID().equals(i.getInternshipID()))
                .filter(app -> app.getStatus() == ApplicationStatus.SUCCESSFUL)
                .count();
            int availableSlots = i.getSlots() - (int)successfulCount;
            
            internshipModel.addRow(new Object[]{
                i.getInternshipID(),
                i.getTitle(),
                i.getLevel(),
                i.getPreferredMajor(),
                i.getSlots(),
                availableSlots,
                i.getConfirmedPlacements(),
                i.getOpeningDate(),
                i.getClosingDate(),
                i.getStatus(),
                i.isVisible() ? "Yes" : "No"
            });
        }
        
        // Update the count label after refreshing the table
        updateCountLabel();
    }

    public void refreshWithFilters(String status, String level, String visibility) {
        internshipModel.setRowCount(0);
        
        List<Internship> myInternships = internships.stream()
            .filter(i -> i.getCompanyRepInCharge().getUserID().equals(rep.getUserID()))
            .collect(Collectors.toList());

        if (status != null && !status.equals("All")) {
            myInternships = myInternships.stream()
                .filter(i -> i.getStatus().toString().equals(status))
                .collect(Collectors.toList());
        }
        
        if (level != null && !level.equals("All")) {
            myInternships = myInternships.stream()
                .filter(i -> i.getLevel().toString().equals(level))
                .collect(Collectors.toList());
        }
        
        if (visibility != null && !visibility.equals("All")) {
            boolean showVisible = visibility.equals("Visible");
            myInternships = myInternships.stream()
                .filter(i -> i.isVisible() == showVisible)
                .collect(Collectors.toList());
        }

        for (Internship i : myInternships) {
            long successfulCount = applications.stream()
                .filter(app -> app.getInternship().getInternshipID().equals(i.getInternshipID()))
                .filter(app -> app.getStatus() == ApplicationStatus.SUCCESSFUL)
                .count();
            int availableSlots = i.getSlots() - (int)successfulCount;
            
            internshipModel.addRow(new Object[]{
                i.getInternshipID(),
                i.getTitle(),
                i.getLevel(),
                i.getPreferredMajor(),
                i.getSlots(),
                availableSlots,
                i.getConfirmedPlacements(),
                i.getOpeningDate(),
                i.getClosingDate(),
                i.getStatus(),
                i.isVisible() ? "Yes" : "No"
            });
        }
        
        if (myInternships.isEmpty()) {
            actionHandler.showInfo("No internships found matching your filter criteria.");
        }
    }

    private void updateCountLabel() {
        int activeCount = rep.countActiveInternships(internships);
        int totalCount = (int) internships.stream()
            .filter(i -> i.getCompanyRepInCharge().getUserID().equals(rep.getUserID()))
            .count();
        countLabel.setText("Active Internships: " + activeCount + "/5 (Total Created: " + totalCount + ")");
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
