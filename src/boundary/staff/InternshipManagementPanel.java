package sc2002_grpproject.boundary.staff;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.*;
import sc2002_grpproject.boundary.RoundedButton;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class InternshipManagementPanel extends JPanel {
    private List<Internship> internships;
    private StaffActionHandler actionHandler;
    
    private JTable internshipTable;
    private DefaultTableModel internshipModel;
    
    private JComboBox<String> statusFilter;
    private JComboBox<String> levelFilter;
    private JTextField companyField;

    public InternshipManagementPanel(List<Internship> internships,
                                     StaffActionHandler actionHandler) {
        this.internships = internships;
        this.actionHandler = actionHandler;
        
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 255, 250));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initializeComponents();
    }

    private void initializeComponents() {
        // Top panel with title and filters
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(new Color(245, 255, 250));
        
        // Title
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
        topPanel.add(createFilterPanel(), BorderLayout.SOUTH);
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
        statusFilter = new JComboBox<>(new String[]{"All", "PENDING", "APPROVED", "REJECTED", "FILLED"});
        statusFilter.setFont(new Font("Arial", Font.PLAIN, 12));
        filterPanel.add(statusFilter);
        
        filterPanel.add(new JLabel("Level:"));
        levelFilter = new JComboBox<>(new String[]{"All", "BASIC", "INTERMEDIATE", "ADVANCED"});
        levelFilter.setFont(new Font("Arial", Font.PLAIN, 12));
        filterPanel.add(levelFilter);
        
        filterPanel.add(new JLabel("Company:"));
        companyField = new JTextField(15);
        companyField.setFont(new Font("Arial", Font.PLAIN, 12));
        companyField.setToolTipText("Enter company name (partial match)");
        filterPanel.add(companyField);
        
        JButton applyFilterBtn = createButton("Apply Filters");
        applyFilterBtn.addActionListener(e -> {
            String status = (String) statusFilter.getSelectedItem();
            String level = (String) levelFilter.getSelectedItem();
            String company = companyField.getText().trim();
            refreshWithFilters(status, level, company);
        });
        filterPanel.add(applyFilterBtn);
        
        JButton clearFilterBtn = createButton("Clear Filters");
        clearFilterBtn.addActionListener(e -> {
            statusFilter.setSelectedIndex(0);
            levelFilter.setSelectedIndex(0);
            companyField.setText("");
            refresh();
        });
        filterPanel.add(clearFilterBtn);
        
        return filterPanel;
    }

    private void createTable() {
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
    }

    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(235, 250, 235));
        
        JButton approveBtn = createButton("Approve Selected");
        JButton rejectIntBtn = createButton("Reject Selected");
        JButton refreshBtn = createButton("Refresh");
        
        approveBtn.addActionListener(e -> {
            int selectedRow = internshipTable.getSelectedRow();
            if (selectedRow != -1) {
                String internshipId = (String) internshipModel.getValueAt(selectedRow, 0);
                actionHandler.approveInternship(internshipId);
                refresh();
            } else {
                actionHandler.showError("Please select an internship");
            }
        });
        
        rejectIntBtn.addActionListener(e -> {
            int selectedRow = internshipTable.getSelectedRow();
            if (selectedRow != -1) {
                String internshipId = (String) internshipModel.getValueAt(selectedRow, 0);
                actionHandler.rejectInternship(internshipId);
                refresh();
            } else {
                actionHandler.showError("Please select an internship");
            }
        });
        
        refreshBtn.addActionListener(e -> refresh());
        
        btnPanel.add(approveBtn);
        btnPanel.add(rejectIntBtn);
        btnPanel.add(refreshBtn);
        
        return btnPanel;
    }

    public void refresh() {
        refreshWithFilters("All", "All", "");
    }

    private void refreshWithFilters(String status, String level, String company) {
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
        
        applyRowColors();
        
        // Only show warning if filters are actually applied (not on initial load)
        if (filteredInternships.isEmpty() && 
            (!status.equals("All") || !level.equals("All") || !company.isEmpty())) {
            actionHandler.showWarning("No internships found matching your filter criteria.");
        }
    }

    private void applyRowColors() {
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
