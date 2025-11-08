package sc2002_grpproject.boundary.panels.student;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.Enums.*;
import sc2002_grpproject.boundary.helpers.TableStyleHelper;
import sc2002_grpproject.boundary.helpers.UIHelper;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel for browsing and applying to available internships
 */
public class InternshipBrowsePanel extends JPanel {
    private final Student student;
    private final List<Internship> internships;
    private final List<InternshipApplication> applications;
    private final InternshipActionListener actionListener;
    
    private JTable internshipTable;
    private DefaultTableModel internshipModel;
    private JComboBox<String> levelFilter;
    private JTextField closingDateField;
    
    public interface InternshipActionListener {
        void onInternshipApplied(InternshipApplication application);
    }
    
    public InternshipBrowsePanel(Student student, 
                                  List<Internship> internships,
                                  List<InternshipApplication> applications,
                                  InternshipActionListener listener) {
        this.student = student;
        this.internships = internships;
        this.applications = applications;
        this.actionListener = listener;
        
        initializeUI();
        refreshTable();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIHelper.LIGHT_MINT);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(createTopPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(UIHelper.LIGHT_MINT);
        
        // Info label
        JLabel infoLabel = new JLabel("<html><b>Available Internships</b> - Browse and apply to internships matching your major and year</html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        infoLabel.setForeground(new Color(0, 80, 0));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.add(infoLabel, BorderLayout.NORTH);
        
        // Filter panel
        topPanel.add(createFilterPanel(), BorderLayout.SOUTH);
        
        return topPanel;
    }
    
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(UIHelper.LIGHT_MINT);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
        
        filterPanel.add(new JLabel("Level:"));
        levelFilter = new JComboBox<>(new String[]{"All", "BASIC", "INTERMEDIATE", "ADVANCED"});
        levelFilter.setFont(new Font("Arial", Font.PLAIN, 12));
        filterPanel.add(levelFilter);
        
        filterPanel.add(new JLabel("Closing Date:"));
        closingDateField = new JTextField(10);
        closingDateField.setFont(new Font("Arial", Font.PLAIN, 12));
        closingDateField.setToolTipText("Format: YYYY-MM-DD (leave blank for all)");
        filterPanel.add(closingDateField);
        
        JButton applyFilterBtn = UIHelper.createStyledButton("Apply Filters");
        applyFilterBtn.addActionListener(e -> applyFilters());
        filterPanel.add(applyFilterBtn);
        
        JButton clearFilterBtn = UIHelper.createStyledButton("Clear Filters");
        clearFilterBtn.addActionListener(e -> clearFilters());
        filterPanel.add(clearFilterBtn);
        
        return filterPanel;
    }
    
    private JPanel createTablePanel() {
        String[] columns = {"ID", "Title", "Company", "Level", "Major", "Slots", "Available", "Opening", "Closing"};
        internshipModel = TableStyleHelper.createNonEditableModel(columns);
        internshipTable = new JTable(internshipModel);
        
        TableStyleHelper.applyStandardStyle(internshipTable);
        internshipTable.setRowHeight(32);
        internshipTable.setDefaultRenderer(Object.class, new TableStyleHelper.InternshipAvailabilityRenderer(6));
        
        JScrollPane scrollPane = TableStyleHelper.createStyledScrollPane(internshipTable, "Select an internship to apply");
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIHelper.LIGHT_MINT);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(new Color(235, 250, 235));
        
        JButton applyBtn = UIHelper.createStyledButton("Apply to Internship");
        JButton refreshBtn = UIHelper.createStyledButton("Refresh List");
        
        applyBtn.setToolTipText("Select an internship from the table and click to apply");
        refreshBtn.setToolTipText("Reload the latest internship listings");
        
        applyBtn.addActionListener(e -> applyForInternship());
        refreshBtn.addActionListener(e -> refreshTable());
        
        btnPanel.add(applyBtn);
        btnPanel.add(refreshBtn);
        
        return btnPanel;
    }
    
    public void refreshTable() {
        internshipModel.setRowCount(0);
        
        List<Internship> available = getAvailableInternships();
        
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
    
    private void applyFilters() {
        internshipModel.setRowCount(0);
        
        String level = (String) levelFilter.getSelectedItem();
        String closingDate = closingDateField.getText().trim();
        
        List<Internship> available = getAvailableInternships();
        
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
                UIHelper.showError(this, "Invalid date format. Please use YYYY-MM-DD");
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
            UIHelper.showInfo(this, "No internships found matching your filter criteria.");
        }
    }
    
    private void clearFilters() {
        levelFilter.setSelectedIndex(0);
        closingDateField.setText("");
        refreshTable();
    }
    
    private List<Internship> getAvailableInternships() {
        return internships.stream()
            .filter(i -> i.isVisible() 
                      && i.getStatus() == InternshipStatus.APPROVED
                      && LocalDate.now().isAfter(i.getOpeningDate().minusDays(1))  
                      && LocalDate.now().isBefore(i.getClosingDate().plusDays(1))
                      && i.getConfirmedPlacements() < i.getSlots()
                      && isEligible(i))
            .collect(Collectors.toList());
    }
    
    private boolean isEligible(Internship i) {
        if (!i.getPreferredMajor().equalsIgnoreCase(student.getMajor())) {
            return false;
        }
        if (student.getYearOfStudy() <= 2 && i.getLevel() != InternshipLevel.BASIC) {
            return false;
        }
        return true;
    }
    
    private void applyForInternship() {
        int selectedRow = internshipTable.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showError(this, "Please select an internship from the table to apply");
            return;
        }
        
        String internshipId = (String) internshipModel.getValueAt(selectedRow, 0);
        
        // Check active applications limit
        long activeApps = applications.stream()
            .filter(app -> app.getStudent().getUserID().equals(student.getUserID()))
            .filter(app -> app.getStatus() == ApplicationStatus.PENDING 
                        || app.getStatus() == ApplicationStatus.SUCCESSFUL)
            .count();
        
        if (activeApps >= 3) {
            UIHelper.showError(this, "You have reached the maximum of 3 active applications.");
            return;
        }
        
        // Find the selected internship
        Internship selected = internships.stream()
            .filter(i -> i.getInternshipID().equals(internshipId))
            .findFirst()
            .orElse(null);
        
        if (selected == null) return;
        
        // Check for duplicate application
        boolean hasActiveApp = applications.stream()
            .anyMatch(app -> app.getStudent().getUserID().equals(student.getUserID()) 
                        && app.getInternship().getInternshipID().equals(internshipId)
                        && (app.getStatus() == ApplicationStatus.PENDING
                            || app.getStatus() == ApplicationStatus.SUCCESSFUL));
        
        if (hasActiveApp) {
            UIHelper.showError(this, "You already have an active application for this internship.");
            return;
        }
        
        // Create new application
        InternshipApplication newApp = new InternshipApplication(student, selected);
        applications.add(newApp);
        
        UIHelper.showSuccess(this, "Application submitted successfully!\nApplication ID: " + newApp.getApplicationId());
        
        // Notify listener
        if (actionListener != null) {
            actionListener.onInternshipApplied(newApp);
        }
    }
}
