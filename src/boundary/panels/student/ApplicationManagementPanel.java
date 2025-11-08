package sc2002_grpproject.boundary.panels.student;

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
 * Panel for managing student applications (accept placements, request withdrawals)
 */
public class ApplicationManagementPanel extends JPanel {
    private final Student student;
    private final List<Internship> internships;
    private final List<InternshipApplication> applications;
    private final ApplicationActionListener actionListener;
    
    private JTable applicationTable;
    private DefaultTableModel applicationModel;
    
    public interface ApplicationActionListener {
        void onPlacementAccepted(InternshipApplication application);
        void onWithdrawalRequested(InternshipApplication application);
    }
    
    public ApplicationManagementPanel(Student student,
                                       List<Internship> internships,
                                       List<InternshipApplication> applications,
                                       ApplicationActionListener listener) {
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
        
        add(createInfoPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIHelper.LIGHT_MINT);
        
        JLabel infoLabel = new JLabel("<html><b>My Applications</b> - Track your applications. Accept SUCCESSFUL offers or withdraw from CONFIRMED placements</html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        infoLabel.setForeground(new Color(0, 80, 0));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        panel.add(infoLabel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        String[] columns = {"App ID", "Internship", "Company", "Status", "Confirmed", "Withdrawal Requested"};
        applicationModel = TableStyleHelper.createNonEditableModel(columns);
        applicationTable = new JTable(applicationModel);
        
        TableStyleHelper.applyStandardStyle(applicationTable);
        applicationTable.setDefaultRenderer(Object.class, new TableStyleHelper.ApplicationStatusRenderer(3));
        
        JScrollPane scrollPane = new JScrollPane(applicationTable);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIHelper.LIGHT_MINT);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(new Color(235, 250, 235));
        
        JButton acceptBtn = UIHelper.createStyledButton("Accept Placement");
        JButton withdrawBtn = UIHelper.createStyledButton("Request Withdrawal");
        JButton refreshBtn = UIHelper.createStyledButton("Refresh List");
        
        acceptBtn.setToolTipText("Confirm your acceptance of a successful internship offer");
        withdrawBtn.setToolTipText("Request to withdraw from a confirmed placement");
        refreshBtn.setToolTipText("Reload your application status");
        
        acceptBtn.addActionListener(e -> acceptPlacement());
        withdrawBtn.addActionListener(e -> requestWithdrawal());
        refreshBtn.addActionListener(e -> refreshTable());
        
        btnPanel.add(acceptBtn);
        btnPanel.add(withdrawBtn);
        btnPanel.add(refreshBtn);
        
        return btnPanel;
    }
    
    public void refreshTable() {
        applicationModel.setRowCount(0);
        
        List<InternshipApplication> myApps = applications.stream()
            .filter(app -> app.getStudent().getUserID().equals(student.getUserID()))
            .sorted((a1, a2) -> {
                // Sort: SUCCESSFUL first, then PENDING, then others
                if (a1.getStatus() == ApplicationStatus.SUCCESSFUL) return -1;
                if (a2.getStatus() == ApplicationStatus.SUCCESSFUL) return 1;
                if (a1.getStatus() == ApplicationStatus.PENDING) return -1;
                if (a2.getStatus() == ApplicationStatus.PENDING) return 1;
                return 0;
            })
            .collect(Collectors.toList());
        
        for (InternshipApplication app : myApps) {
            String statusText = "<html><b>" + app.getStatus() + "</b></html>";
            applicationModel.addRow(new Object[]{
                app.getApplicationId(),
                app.getInternship().getTitle(),
                app.getInternship().getCompanyName(),
                statusText,
                app.isPlacementConfirmed() ? "Yes" : "No",
                app.isWithdrawalRequested() ? "Yes" : "No"
            });
        }
    }
    
    private void acceptPlacement() {
        int selectedRow = applicationTable.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showError(this, "Please select an application to accept");
            return;
        }
        
        String appId = (String) applicationModel.getValueAt(selectedRow, 0);
        InternshipApplication selected = applications.stream()
            .filter(app -> app.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);
        
        if (selected == null) return;
        
        if (selected.getStatus() != ApplicationStatus.SUCCESSFUL) {
            UIHelper.showError(this, "You can only accept SUCCESSFUL applications.");
            return;
        }
        
        if (selected.isPlacementConfirmed()) {
            UIHelper.showError(this, "This placement is already confirmed.");
            return;
        }
        
        boolean confirm = UIHelper.showConfirmation(this,
            "Accept placement for:\n" + selected.getInternship().getTitle() + "\n\n" +
            "This will withdraw all your other applications.\nContinue?");
        
        if (confirm) {
            selected.setPlacementConfirmed(true);
            selected.getInternship().incrementConfirmedPlacements();
            
            // Withdraw all other applications
            applications.stream()
                .filter(app -> app.getStudent().getUserID().equals(student.getUserID()))
                .filter(app -> !app.getApplicationId().equals(appId))
                .filter(app -> app.getStatus() != ApplicationStatus.WITHDRAWN)
                .forEach(app -> app.setStatus(ApplicationStatus.WITHDRAWN));
            
            UIHelper.showSuccess(this, "Placement accepted! All other applications have been withdrawn.");
            refreshTable();
            
            // Notify listener
            if (actionListener != null) {
                actionListener.onPlacementAccepted(selected);
            }
        }
    }
    
    private void requestWithdrawal() {
        int selectedRow = applicationTable.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showError(this, "Please select an application to withdraw");
            return;
        }
        
        String appId = (String) applicationModel.getValueAt(selectedRow, 0);
        InternshipApplication selected = applications.stream()
            .filter(app -> app.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);
        
        if (selected == null) return;
        
        if (selected.getStatus() == ApplicationStatus.WITHDRAWN 
            || selected.getStatus() == ApplicationStatus.UNSUCCESSFUL) {
            UIHelper.showError(this, "Cannot withdraw this application.");
            return;
        }
        
        if (selected.isWithdrawalRequested()) {
            UIHelper.showError(this, "Withdrawal already requested for this application.");
            return;
        }
        
        boolean confirm = UIHelper.showConfirmation(this,
            "Request withdrawal for:\n" + selected.getInternship().getTitle() + "\n\n" +
            "This will require staff approval.\nContinue?");
        
        if (confirm) {
            selected.setWithdrawalRequested(true);
            UIHelper.showSuccess(this, "Withdrawal request submitted.\nAwaiting Career Center Staff approval.");
            refreshTable();
            
            // Notify listener
            if (actionListener != null) {
                actionListener.onWithdrawalRequested(selected);
            }
        }
    }
}
