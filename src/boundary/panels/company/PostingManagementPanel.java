package sc2002_grpproject.boundary.panels.company;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.Enums.*;
import sc2002_grpproject.boundary.helpers.TableStyleHelper;
import sc2002_grpproject.boundary.helpers.UIHelper;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel for creating and managing internship postings
 */
public class PostingManagementPanel extends JPanel {
    private final CompanyRepresentative rep;
    private final List<Internship> internships;
    private final PostingActionListener actionListener;
    
    private JTable internshipTable;
    private DefaultTableModel internshipModel;
    
    public interface PostingActionListener {
        void onPostingCreated(Internship internship);
        void onPostingUpdated(Internship internship);
    }
    
    public PostingManagementPanel(CompanyRepresentative rep, List<Internship> internships, PostingActionListener listener) {
        this.rep = rep;
        this.internships = internships;
        this.actionListener = listener;
        
        initializeUI();
        refreshTable();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIHelper.LIGHT_MINT);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UIHelper.LIGHT_MINT);
        
        JLabel titleLabel = new JLabel("Internship Posting Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(0, 80, 0));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel helpLabel = new JLabel("<html><i>Create and manage your company's internship postings</i></html>");
        helpLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        helpLabel.setForeground(new Color(100, 100, 100));
        titlePanel.add(helpLabel, BorderLayout.SOUTH);
        
        return titlePanel;
    }
    
    private JPanel createTablePanel() {
        String[] columns = {"ID", "Title", "Level", "Major", "Slots", "Confirmed", "Opening", "Closing", "Status", "Visible"};
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
        
        JButton createBtn = UIHelper.createStyledButton("Create New Posting");
        JButton editBtn = UIHelper.createStyledButton("Edit Selected");
        JButton toggleVisBtn = UIHelper.createStyledButton("Toggle Visibility");
        JButton refreshBtn = UIHelper.createStyledButton("Refresh");
        
        createBtn.addActionListener(e -> createPosting());
        editBtn.addActionListener(e -> editPosting());
        toggleVisBtn.addActionListener(e -> toggleVisibility());
        refreshBtn.addActionListener(e -> refreshTable());
        
        btnPanel.add(createBtn);
        btnPanel.add(editBtn);
        btnPanel.add(toggleVisBtn);
        btnPanel.add(refreshBtn);
        
        return btnPanel;
    }
    
    public void refreshTable() {
        internshipModel.setRowCount(0);
        
        List<Internship> companyInternships = internships.stream()
            .filter(i -> i.getCompanyName().equals(rep.getCompanyName()))
            .collect(Collectors.toList());
        
        for (Internship i : companyInternships) {
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
    
    private void createPosting() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBackground(new Color(220, 245, 220));
        
        JTextField titleField = new JTextField();
        JComboBox<InternshipLevel> levelBox = new JComboBox<>(InternshipLevel.values());
        JTextField majorField = new JTextField();
        JTextField slotsField = new JTextField();
        JTextField openingField = new JTextField();
        JTextField closingField = new JTextField();
        JTextArea descArea = new JTextArea(3, 20);
        
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Level:"));
        panel.add(levelBox);
        panel.add(new JLabel("Major:"));
        panel.add(majorField);
        panel.add(new JLabel("Slots:"));
        panel.add(slotsField);
        panel.add(new JLabel("Opening Date (YYYY-MM-DD):"));
        panel.add(openingField);
        panel.add(new JLabel("Closing Date (YYYY-MM-DD):"));
        panel.add(closingField);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descArea));
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Create New Internship Posting", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText().trim();
                InternshipLevel level = (InternshipLevel) levelBox.getSelectedItem();
                String major = majorField.getText().trim();
                int slots = Integer.parseInt(slotsField.getText().trim());
                LocalDate opening = LocalDate.parse(openingField.getText().trim());
                LocalDate closing = LocalDate.parse(closingField.getText().trim());
                String description = descArea.getText().trim();
                
                if (title.isEmpty() || major.isEmpty() || description.isEmpty()) {
                    UIHelper.showError(this, "Please fill in all fields");
                    return;
                }
                
                Internship newInternship = new Internship(title, description, rep.getCompanyName(), rep, 
                    level, major, slots, opening, closing);
                internships.add(newInternship);
                
                UIHelper.showSuccess(this, "Internship posting created successfully!\nID: " + newInternship.getInternshipID() +
                    "\n\nStatus: PENDING (awaiting staff approval)");
                refreshTable();
                
                if (actionListener != null) {
                    actionListener.onPostingCreated(newInternship);
                }
            } catch (NumberFormatException | DateTimeParseException e) {
                UIHelper.showError(this, "Invalid input format. Please check your entries.");
            }
        }
    }
    
    private void editPosting() {
        int selectedRow = internshipTable.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showError(this, "Please select an internship to edit");
            return;
        }
        
        String internshipId = (String) internshipModel.getValueAt(selectedRow, 0);
        Internship selected = internships.stream()
            .filter(i -> i.getInternshipID().equals(internshipId))
            .findFirst()
            .orElse(null);
        
        if (selected == null) return;
        
        if (selected.getStatus() == InternshipStatus.APPROVED) {
            UIHelper.showError(this, "Cannot edit approved internships");
            return;
        }
        
        // Edit dialog (simplified for brevity)
        UIHelper.showInfo(this, "Edit functionality: Update description or dates for pending internships");
    }
    
    private void toggleVisibility() {
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
        
        selected.setVisible(!selected.isVisible());
        UIHelper.showSuccess(this, "Visibility toggled to: " + (selected.isVisible() ? "Visible" : "Hidden"));
        refreshTable();
        
        if (actionListener != null) {
            actionListener.onPostingUpdated(selected);
        }
    }
}
