package sc2002_grpproject.boundary.student;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.Enums.*;
import sc2002_grpproject.boundary.RoundedButton;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class InternshipBrowsePanel extends JPanel {
    private Student student;
    private List<Internship> internships;
    private StudentActionHandler actionHandler;
    
    private JTable internshipTable;
    private DefaultTableModel internshipModel;

    public InternshipBrowsePanel(Student student, List<Internship> internships,
                                 StudentActionHandler actionHandler) {
        this.student = student;
        this.internships = internships;
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
        
        JLabel infoLabel = new JLabel("<html><b>Available Internships</b> - Browse and apply to internships matching your major and year</html>");
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
        JScrollPane scrollPane = new JScrollPane(internshipTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(144, 238, 144), 2),
            "Select an internship to apply",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.ITALIC, 11),
            new Color(0, 100, 0)
        ));
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = createButtonPanel();
        add(btnPanel, BorderLayout.SOUTH);
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(new Color(245, 255, 250));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
        
        filterPanel.add(new JLabel("Level:"));
        JComboBox<String> levelFilter = new JComboBox<>(new String[]{"All", "BASIC", "INTERMEDIATE", "ADVANCED"});
        levelFilter.setFont(new Font("Arial", Font.PLAIN, 12));
        filterPanel.add(levelFilter);
        
        filterPanel.add(new JLabel("Closing Date:"));
        JTextField closingDateField = new JTextField(10);
        closingDateField.setFont(new Font("Arial", Font.PLAIN, 12));
        closingDateField.setToolTipText("Format: YYYY-MM-DD (leave blank for all)");
        filterPanel.add(closingDateField);
        
        JButton applyFilterBtn = createButton("Apply Filters");
        applyFilterBtn.addActionListener(e -> {
            String level = (String) levelFilter.getSelectedItem();
            String closingDate = closingDateField.getText().trim();
            refreshWithFilters(level, closingDate);
        });
        filterPanel.add(applyFilterBtn);
        
        JButton clearFilterBtn = createButton("Clear Filters");
        clearFilterBtn.addActionListener(e -> {
            levelFilter.setSelectedIndex(0);
            closingDateField.setText("");
            refresh();
        });
        filterPanel.add(clearFilterBtn);
        
        return filterPanel;
    }

    private void createTable() {
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
        internshipTable.setSelectionBackground(new Color(220, 220, 220));
        internshipTable.setSelectionForeground(Color.BLACK);
        
        // Add cell renderer for color coding
        internshipTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(new Color(220, 220, 220));
                } else {
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
    }

    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(new Color(235, 250, 235));
        
        JButton applyBtn = createButton("Apply to Internship");
        JButton refreshBtn = createButton("Refresh List");
        
        applyBtn.setToolTipText("Select an internship from the table and click to apply");
        refreshBtn.setToolTipText("Reload the latest internship listings");
        
        applyBtn.addActionListener(e -> {
            int selectedRow = internshipTable.getSelectedRow();
            if (selectedRow != -1) {
                String internshipId = (String) internshipModel.getValueAt(selectedRow, 0);
                actionHandler.applyForInternship(internshipId);
                refresh();
            } else {
                actionHandler.showError("Please select an internship from the table to apply");
            }
        });
        refreshBtn.addActionListener(e -> refresh());
        
        btnPanel.add(applyBtn);
        btnPanel.add(refreshBtn);
        
        return btnPanel;
    }

    public void refresh() {
        internshipModel.setRowCount(0);
        
        List<Internship> available = internships.stream()
            .filter(i -> i.isVisible() 
                      && i.getStatus() == InternshipStatus.APPROVED
                      && i.getStatus() != InternshipStatus.FILLED
                      && LocalDate.now().isAfter(i.getOpeningDate().minusDays(1))  
                      && LocalDate.now().isBefore(i.getClosingDate().plusDays(1))
                      && !LocalDate.now().isAfter(i.getClosingDate())
                      && i.getConfirmedPlacements() < i.getSlots()
                      && actionHandler.isEligible(student, i))
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

    public void refreshWithFilters(String level, String closingDate) {
        internshipModel.setRowCount(0);
        
        List<Internship> available = internships.stream()
            .filter(i -> i.isVisible() 
                      && i.getStatus() == InternshipStatus.APPROVED
                      && i.getStatus() != InternshipStatus.FILLED
                      && LocalDate.now().isAfter(i.getOpeningDate().minusDays(1))  
                      && LocalDate.now().isBefore(i.getClosingDate().plusDays(1))
                      && !LocalDate.now().isAfter(i.getClosingDate())
                      && i.getConfirmedPlacements() < i.getSlots()
                      && actionHandler.isEligible(student, i))
            .collect(Collectors.toList());

        // Apply level filter
        if (level != null && !level.equals("All")) {
            available = available.stream()
                .filter(i -> i.getLevel().toString().equals(level))
                .collect(Collectors.toList());
        }
        
        // Apply closing date filter
        if (closingDate != null && !closingDate.isEmpty()) {
            try {
                LocalDate filterDate = LocalDate.parse(closingDate);
                available = available.stream()
                    .filter(i -> i.getClosingDate().equals(filterDate))
                    .collect(Collectors.toList());
            } catch (Exception e) {
                actionHandler.showError("Invalid date format. Please use YYYY-MM-DD");
                return;
            }
        }
        
        // Sort alphabetically by title
        available.sort((i1, i2) -> i1.getTitle().compareToIgnoreCase(i2.getTitle()));

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
        
        if (available.isEmpty()) {
            actionHandler.showInfo("No internships found matching your filter criteria.");
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
