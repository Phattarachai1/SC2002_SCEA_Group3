package sc2002_grpproject.boundary.company;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.*;
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
    
    // Filter state persistence
    private JComboBox<String> statusFilter;
    private JComboBox<String> levelFilter;
    private JComboBox<String> visibilityFilter;
    private JComboBox<String> sortByFilter;
    private JComboBox<String> orderFilter;
    private String savedStatus = "All";
    private String savedLevel = "All";
    private String savedVisibility = "All";
    private String savedSortBy = "Default (Status)";
    private String savedOrder = "Ascending";

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
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filters & Sorting"));
        
        filterPanel.add(new JLabel("Status:"));
        statusFilter = new JComboBox<>(new String[]{"All", "PENDING", "APPROVED", "REJECTED", "FILLED"});
        statusFilter.setFont(new Font("Arial", Font.PLAIN, 12));
        statusFilter.setSelectedItem(savedStatus); // Restore saved filter
        filterPanel.add(statusFilter);
        
        filterPanel.add(new JLabel("Level:"));
        levelFilter = new JComboBox<>(new String[]{"All", "BASIC", "INTERMEDIATE", "ADVANCED"});
        levelFilter.setFont(new Font("Arial", Font.PLAIN, 12));
        levelFilter.setSelectedItem(savedLevel); // Restore saved filter
        filterPanel.add(levelFilter);
        
        filterPanel.add(new JLabel("Visibility:"));
        visibilityFilter = new JComboBox<>(new String[]{"All", "Visible", "Hidden"});
        visibilityFilter.setFont(new Font("Arial", Font.PLAIN, 12));
        visibilityFilter.setSelectedItem(savedVisibility); // Restore saved filter
        filterPanel.add(visibilityFilter);
        
        // Sorting options
        filterPanel.add(new JLabel("Sort By:"));
        sortByFilter = new JComboBox<>(new String[]{"Default (Status)", "ID", "Title", "Company", "Level", "Opening Date", "Closing Date"});
        sortByFilter.setFont(new Font("Arial", Font.PLAIN, 12));
        sortByFilter.setSelectedItem(savedSortBy); // Restore saved sort
        filterPanel.add(sortByFilter);
        
        filterPanel.add(new JLabel("Order:"));
        orderFilter = new JComboBox<>(new String[]{"Ascending", "Descending"});
        orderFilter.setFont(new Font("Arial", Font.PLAIN, 12));
        orderFilter.setSelectedItem(savedOrder); // Restore saved order
        filterPanel.add(orderFilter);
        
        JButton applyFilterBtn = createButton("Apply Filters");
        applyFilterBtn.addActionListener(e -> {
            savedStatus = (String) statusFilter.getSelectedItem();
            savedLevel = (String) levelFilter.getSelectedItem();
            savedVisibility = (String) visibilityFilter.getSelectedItem();
            savedSortBy = (String) sortByFilter.getSelectedItem();
            savedOrder = (String) orderFilter.getSelectedItem();
            refreshWithFilters(savedStatus, savedLevel, savedVisibility);
        });
        filterPanel.add(applyFilterBtn);
        
        JButton clearFilterBtn = createButton("Clear Filters");
        clearFilterBtn.addActionListener(e -> {
            savedStatus = "All";
            savedLevel = "All";
            savedVisibility = "All";
            savedSortBy = "Default (Status)";
            savedOrder = "Ascending";
            statusFilter.setSelectedIndex(0);
            levelFilter.setSelectedIndex(0);
            visibilityFilter.setSelectedIndex(0);
            sortByFilter.setSelectedIndex(0);
            orderFilter.setSelectedIndex(0);
            refresh();
        });
        filterPanel.add(clearFilterBtn);
        
        return filterPanel;
    }

    private void createTable() {
        String[] columns = {"ID", "Title", "Description", "Level", "Major", "Slots", "Available", "Confirmed", "Opening", "Closing", "Status", "Visible"};
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
        refreshWithFilters(savedStatus, savedLevel, savedVisibility);
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
        
        // Apply sorting
        applySorting(myInternships);

        for (Internship i : myInternships) {
            long successfulCount = applications.stream()
                .filter(app -> app.getInternship().getInternshipID().equals(i.getInternshipID()))
                .filter(app -> app.getStatus() == ApplicationStatus.SUCCESSFUL)
                .count();
            int availableSlots = i.getSlots() - (int)successfulCount;
            
            internshipModel.addRow(new Object[]{
                i.getInternshipID(),
                i.getTitle(),
                i.getDescription(),
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
        
        // Only show warning if filters are actually applied (not on initial load)
        if (myInternships.isEmpty() && 
            (!status.equals("All") || !level.equals("All") || !visibility.equals("All"))) {
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
    
    private void applySorting(List<Internship> internships) {
        java.util.Comparator<Internship> comparator = null;
        
        switch (savedSortBy) {
            case "Default (Status)":
                if ("Ascending".equals(savedOrder)) {
                    // PENDING -> APPROVED -> REJECTED -> FILLED (within each status, sort by ID)
                    comparator = (i1, i2) -> {
                        int statusOrder1 = getStatusOrder(i1.getStatus());
                        int statusOrder2 = getStatusOrder(i2.getStatus());
                        if (statusOrder1 != statusOrder2) {
                            return statusOrder1 - statusOrder2;
                        }
                        return i1.getInternshipID().compareTo(i2.getInternshipID());
                    };
                } else {
                    // APPROVED -> REJECTED -> FILLED -> PENDING (within each status, sort by ID)
                    comparator = (i1, i2) -> {
                        int statusOrder1 = getStatusOrderDescending(i1.getStatus());
                        int statusOrder2 = getStatusOrderDescending(i2.getStatus());
                        if (statusOrder1 != statusOrder2) {
                            return statusOrder1 - statusOrder2;
                        }
                        return i1.getInternshipID().compareTo(i2.getInternshipID());
                    };
                }
                break;
            case "ID":
                comparator = java.util.Comparator.comparing(Internship::getInternshipID);
                break;
            case "Title":
                comparator = java.util.Comparator.comparing(Internship::getTitle, String.CASE_INSENSITIVE_ORDER);
                break;
            case "Company":
                comparator = java.util.Comparator.comparing(Internship::getCompanyName, String.CASE_INSENSITIVE_ORDER);
                break;
            case "Level":
                comparator = java.util.Comparator.comparing(Internship::getLevel);
                break;
            case "Opening Date":
                comparator = java.util.Comparator.comparing(Internship::getOpeningDate);
                break;
            case "Closing Date":
                comparator = java.util.Comparator.comparing(Internship::getClosingDate);
                break;
            default:
                comparator = java.util.Comparator.comparing(Internship::getInternshipID);
        }
        
        // Apply descending order for non-status sorts
        if (!"Default (Status)".equals(savedSortBy) && "Descending".equals(savedOrder)) {
            comparator = comparator.reversed();
        }
        
        internships.sort(comparator);
    }
    
    private int getStatusOrder(InternshipStatus status) {
        // PENDING -> APPROVED -> REJECTED -> FILLED
        switch (status) {
            case PENDING: return 0;
            case APPROVED: return 1;
            case REJECTED: return 2;
            case FILLED: return 3;
            default: return 4;
        }
    }
    
    private int getStatusOrderDescending(InternshipStatus status) {
        // APPROVED -> REJECTED -> FILLED -> PENDING
        switch (status) {
            case APPROVED: return 0;
            case REJECTED: return 1;
            case FILLED: return 2;
            case PENDING: return 3;
            default: return 4;
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
