package sc2002_grpproject.boundary.panels.staff;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.Enums.*;
import sc2002_grpproject.boundary.helpers.TableStyleHelper;
import sc2002_grpproject.boundary.helpers.UIHelper;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel for approving or rejecting internship postings
 */
public class InternshipApprovalPanel extends JPanel {
    private final List<Internship> internships;
    private final InternshipActionListener actionListener;
    
    private JTable internshipTable;
    private DefaultTableModel internshipModel;
    private JComboBox<String> statusFilter;
    private JComboBox<String> levelFilter;
    private JTextField companyField;
    
    public interface InternshipActionListener {
        void onInternshipApproved(Internship internship);
        void onInternshipRejected(Internship internship);
    }
    
    public InternshipApprovalPanel(List<Internship> internships, InternshipActionListener listener) {
        this.internships = internships;
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
        
        // Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UIHelper.LIGHT_MINT);
        
        JLabel titleLabel = new JLabel("Internship Approval Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(0, 80, 0));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel helpLabel = new JLabel("<html><i>Approve/Reject PENDING internships. Use filters to find specific internships.</i></html>");
        helpLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        helpLabel.setForeground(new Color(100, 100, 100));
        titlePanel.add(helpLabel, BorderLayout.SOUTH);
        
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(createFilterPanel(), BorderLayout.SOUTH);
        
        return topPanel;
    }
    
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(UIHelper.LIGHT_MINT);
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
        
        JButton applyFilterBtn = UIHelper.createStyledButton("Apply Filters");
        applyFilterBtn.addActionListener(e -> applyFilters());
        filterPanel.add(applyFilterBtn);
        
        JButton clearFilterBtn = UIHelper.createStyledButton("Clear Filters");
        clearFilterBtn.addActionListener(e -> clearFilters());
        filterPanel.add(clearFilterBtn);
        
        return filterPanel;
    }
    
    private JPanel createTablePanel() {
        String[] columns = {"ID", "Title", "Company", "Level", "Major", "Slots", "Opening", "Closing", "Status"};
        internshipModel = TableStyleHelper.createNonEditableModel(columns);
        internshipTable = new JTable(internshipModel);
        
        TableStyleHelper.applyStandardStyle(internshipTable);
        internshipTable.setDefaultRenderer(Object.class, new TableStyleHelper.ApplicationStatusRenderer(8));
        
        JScrollPane scrollPane = new JScrollPane(internshipTable);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIHelper.LIGHT_MINT);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(235, 250, 235));
        
        JButton approveBtn = UIHelper.createStyledButton("Approve Selected");
        JButton rejectIntBtn = UIHelper.createStyledButton("Reject Selected");
        JButton refreshBtn = UIHelper.createStyledButton("Refresh");
        
        approveBtn.addActionListener(e -> approveInternship());
        rejectIntBtn.addActionListener(e -> rejectInternship());
        refreshBtn.addActionListener(e -> refreshTable());
        
        btnPanel.add(approveBtn);
        btnPanel.add(rejectIntBtn);
        btnPanel.add(refreshBtn);
        
        return btnPanel;
    }
    
    public void refreshTable() {
        internshipModel.setRowCount(0);
        
        for (Internship i : internships) {
            internshipModel.addRow(new Object[]{
                i.getInternshipID(),
                i.getTitle(),
                i.getCompanyName(),
                i.getLevel(),
                i.getPreferredMajor(),
                i.getSlots(),
                i.getOpeningDate(),
                i.getClosingDate(),
                i.getStatus()
            });
        }
    }
    
    private void applyFilters() {
        internshipModel.setRowCount(0);
        
        String status = (String) statusFilter.getSelectedItem();
        String level = (String) levelFilter.getSelectedItem();
        String company = companyField.getText().trim();
        
        List<Internship> filtered = internships.stream()
            .filter(i -> status.equals("All") || i.getStatus().toString().equals(status))
            .filter(i -> level.equals("All") || i.getLevel().toString().equals(level))
            .filter(i -> company.isEmpty() || i.getCompanyName().toLowerCase().contains(company.toLowerCase()))
            .collect(Collectors.toList());
        
        for (Internship i : filtered) {
            internshipModel.addRow(new Object[]{
                i.getInternshipID(),
                i.getTitle(),
                i.getCompanyName(),
                i.getLevel(),
                i.getPreferredMajor(),
                i.getSlots(),
                i.getOpeningDate(),
                i.getClosingDate(),
                i.getStatus()
            });
        }
        
        if (filtered.isEmpty()) {
            UIHelper.showInfo(this, "No internships found matching your filter criteria.");
        }
    }
    
    private void clearFilters() {
        statusFilter.setSelectedIndex(0);
        levelFilter.setSelectedIndex(0);
        companyField.setText("");
        refreshTable();
    }
    
    private void approveInternship() {
        int selectedRow = internshipTable.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showError(this, "Please select an internship");
            return;
        }
        
        String internshipId = (String) internshipModel.getValueAt(selectedRow, 0);
        Internship selected = internships.stream()
            .filter(i -> i.getInternshipID().equals(internshipId))
            .findFirst()
            .orElse(null);
        
        if (selected == null) return;
        
        if (selected.getStatus() == InternshipStatus.APPROVED) {
            UIHelper.showError(this, "This internship is already approved");
            return;
        }
        
        boolean confirm = UIHelper.showConfirmation(this,
            "Approve internship:\n" + selected.getTitle() + "\nCompany: " + selected.getCompanyName() + "?");
        
        if (confirm) {
            selected.setStatus(InternshipStatus.APPROVED);
            UIHelper.showSuccess(this, "Internship approved successfully!");
            refreshTable();
            
            if (actionListener != null) {
                actionListener.onInternshipApproved(selected);
            }
        }
    }
    
    private void rejectInternship() {
        int selectedRow = internshipTable.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showError(this, "Please select an internship");
            return;
        }
        
        String internshipId = (String) internshipModel.getValueAt(selectedRow, 0);
        Internship selected = internships.stream()
            .filter(i -> i.getInternshipID().equals(internshipId))
            .findFirst()
            .orElse(null);
        
        if (selected == null) return;
        
        if (selected.getStatus() == InternshipStatus.REJECTED) {
            UIHelper.showError(this, "This internship is already rejected");
            return;
        }
        
        if (selected.getStatus() == InternshipStatus.APPROVED) {
            UIHelper.showError(this, "Cannot reject an approved internship");
            return;
        }
        
        boolean confirm = UIHelper.showConfirmation(this,
            "Reject internship:\n" + selected.getTitle() + "\nCompany: " + selected.getCompanyName() + "?");
        
        if (confirm) {
            selected.setStatus(InternshipStatus.REJECTED);
            UIHelper.showSuccess(this, "Internship rejected.");
            refreshTable();
            
            if (actionListener != null) {
                actionListener.onInternshipRejected(selected);
            }
        }
    }
}
