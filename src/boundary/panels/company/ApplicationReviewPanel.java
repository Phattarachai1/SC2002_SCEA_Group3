package sc2002_grpproject.boundary.panels.company;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.Enums.*;
import sc2002_grpproject.boundary.helpers.TableStyleHelper;
import sc2002_grpproject.boundary.helpers.UIHelper;
import sc2002_grpproject.controller.CompanyController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel for reviewing and managing student applications
 */
public class ApplicationReviewPanel extends JPanel {
    private final CompanyRepresentative rep;
    private final List<Internship> internships;
    private final List<InternshipApplication> applications;
    private final ApplicationActionListener actionListener;
    
    private JTable applicationTable;
    private DefaultTableModel applicationModel;
    
    public interface ApplicationActionListener {
        void onApplicationApproved(InternshipApplication application);
        void onApplicationRejected(InternshipApplication application);
    }
    
    public ApplicationReviewPanel(CompanyRepresentative rep, List<Internship> internships,
                                   List<InternshipApplication> applications, ApplicationActionListener listener) {
        this.rep = rep;
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
        
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UIHelper.LIGHT_MINT);
        
        JLabel titleLabel = new JLabel("Application Review");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(0, 80, 0));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel helpLabel = new JLabel("<html><i>Review and approve/reject student applications for your internships</i></html>");
        helpLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        helpLabel.setForeground(new Color(100, 100, 100));
        titlePanel.add(helpLabel, BorderLayout.SOUTH);
        
        return titlePanel;
    }
    
    private JPanel createTablePanel() {
        String[] columns = {"App ID", "Student", "Email", "Major", "Year", "Internship", "Status"};
        applicationModel = TableStyleHelper.createNonEditableModel(columns);
        applicationTable = new JTable(applicationModel);
        
        TableStyleHelper.applyStandardStyle(applicationTable);
        applicationTable.setDefaultRenderer(Object.class, new TableStyleHelper.ApplicationStatusRenderer(6));
        
        JScrollPane scrollPane = new JScrollPane(applicationTable);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIHelper.LIGHT_MINT);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(235, 250, 235));
        
        JButton approveBtn = UIHelper.createStyledButton("Approve Selected");
        JButton rejectBtn = UIHelper.createStyledButton("Reject Selected");
        JButton viewDetailsBtn = UIHelper.createStyledButton("View Student Details");
        JButton refreshBtn = UIHelper.createStyledButton("Refresh");
        
        approveBtn.addActionListener(e -> approveApplication());
        rejectBtn.addActionListener(e -> rejectApplication());
        viewDetailsBtn.addActionListener(e -> viewStudentDetails());
        refreshBtn.addActionListener(e -> refreshTable());
        
        btnPanel.add(approveBtn);
        btnPanel.add(rejectBtn);
        btnPanel.add(viewDetailsBtn);
        btnPanel.add(refreshBtn);
        
        return btnPanel;
    }
    
    public void refreshTable() {
        applicationModel.setRowCount(0);
        
        List<String> companyInternshipIds = internships.stream()
            .filter(i -> i.getCompanyName().equals(rep.getCompanyName()))
            .map(Internship::getInternshipID)
            .collect(Collectors.toList());
        
        List<InternshipApplication> companyApplications = applications.stream()
            .filter(app -> companyInternshipIds.contains(app.getInternship().getInternshipID()))
            .collect(Collectors.toList());
        
        for (InternshipApplication app : companyApplications) {
            applicationModel.addRow(new Object[]{
                app.getApplicationId(),
                app.getStudent().getName(),
                app.getStudent().getEmail(),
                app.getStudent().getMajor(),
                app.getStudent().getYearOfStudy(),
                app.getInternship().getTitle(),
                app.getStatus()
            });
        }
    }
    
    private void approveApplication() {
        int selectedRow = applicationTable.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showError(this, "Please select an application");
            return;
        }
        
        String appId = (String) applicationModel.getValueAt(selectedRow, 0);
        InternshipApplication selected = applications.stream()
            .filter(app -> app.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);
        
        if (selected == null) return;
        
        CompanyController.ApplicationResult result = CompanyController.approveApplication(selected, selected.getInternship());
        
        if (result.isSuccess()) {
            UIHelper.showSuccess(this, "Application approved successfully!");
            refreshTable();
            
            if (actionListener != null) {
                actionListener.onApplicationApproved(selected);
            }
        } else {
            UIHelper.showError(this, "Failed to approve: " + result.getMessage());
        }
    }
    
    private void rejectApplication() {
        int selectedRow = applicationTable.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showError(this, "Please select an application");
            return;
        }
        
        String appId = (String) applicationModel.getValueAt(selectedRow, 0);
        InternshipApplication selected = applications.stream()
            .filter(app -> app.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);
        
        if (selected == null) return;
        
        boolean confirm = UIHelper.showConfirmation(this,
            "Reject application from:\n" + selected.getStudent().getName() + 
            "\nFor: " + selected.getInternship().getTitle() + "?");
        
        if (confirm) {
            CompanyController.ApplicationResult result = CompanyController.rejectApplication(selected);
            
            if (result.isSuccess()) {
                UIHelper.showSuccess(this, "Application rejected.");
                refreshTable();
                
                if (actionListener != null) {
                    actionListener.onApplicationRejected(selected);
                }
            } else {
                UIHelper.showError(this, "Failed to reject: " + result.getMessage());
            }
        }
    }
    
    private void viewStudentDetails() {
        int selectedRow = applicationTable.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showError(this, "Please select an application");
            return;
        }
        
        String appId = (String) applicationModel.getValueAt(selectedRow, 0);
        InternshipApplication selected = applications.stream()
            .filter(app -> app.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);
        
        if (selected == null) return;
        
        Student student = selected.getStudent();
        String details = String.format(
            "STUDENT DETAILS\n\n" +
            "Name: %s\n" +
            "Email: %s\n" +
            "Major: %s\n" +
            "Year of Study: %d\n\n" +
            "Applied for: %s",
            student.getName(),
            student.getEmail(),
            student.getMajor(),
            student.getYearOfStudy(),
            selected.getInternship().getTitle()
        );
        
        UIHelper.showInfo(this, details);
    }
}
