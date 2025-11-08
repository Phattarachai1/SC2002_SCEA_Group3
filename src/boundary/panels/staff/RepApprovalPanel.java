package sc2002_grpproject.boundary.panels.staff;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.Enums.*;
import sc2002_grpproject.boundary.helpers.TableStyleHelper;
import sc2002_grpproject.boundary.helpers.UIHelper;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for authorizing or rejecting company representatives
 */
public class RepApprovalPanel extends JPanel {
    private final List<CompanyRepresentative> companyReps;
    private final RepActionListener actionListener;
    
    private JTable companyRepTable;
    private DefaultTableModel companyRepModel;
    
    public interface RepActionListener {
        void onRepApproved(CompanyRepresentative rep);
        void onRepRejected(CompanyRepresentative rep);
    }
    
    public RepApprovalPanel(List<CompanyRepresentative> companyReps, RepActionListener listener) {
        this.companyReps = companyReps;
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
        
        JLabel titleLabel = new JLabel("Company Representative Authorization");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(0, 80, 0));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel helpLabel = new JLabel("<html><i>Authorize PENDING company representatives to allow them to post internships</i></html>");
        helpLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        helpLabel.setForeground(new Color(100, 100, 100));
        titlePanel.add(helpLabel, BorderLayout.SOUTH);
        
        return titlePanel;
    }
    
    private JPanel createTablePanel() {
        String[] columns = {"User ID", "Name", "Email", "Company", "Status"};
        companyRepModel = TableStyleHelper.createNonEditableModel(columns);
        companyRepTable = new JTable(companyRepModel);
        
        TableStyleHelper.applyStandardStyle(companyRepTable);
        companyRepTable.setRowHeight(32);
        companyRepTable.setDefaultRenderer(Object.class, new TableStyleHelper.ApplicationStatusRenderer(4));
        
        JScrollPane scrollPane = new JScrollPane(companyRepTable);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIHelper.LIGHT_MINT);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(235, 250, 235));
        
        JButton authorizeBtn = UIHelper.createStyledButton("Authorize Selected");
        JButton rejectRepBtn = UIHelper.createStyledButton("Reject Selected");
        JButton refreshBtn = UIHelper.createStyledButton("Refresh");
        
        authorizeBtn.setToolTipText("Click on a PENDING representative row then click to authorize");
        rejectRepBtn.setToolTipText("Click on a PENDING representative row then click to reject");
        refreshBtn.setToolTipText("Reload the company representatives list");
        
        authorizeBtn.addActionListener(e -> authorizeRep());
        rejectRepBtn.addActionListener(e -> rejectRep());
        refreshBtn.addActionListener(e -> refreshTable());
        
        btnPanel.add(authorizeBtn);
        btnPanel.add(rejectRepBtn);
        btnPanel.add(refreshBtn);
        
        return btnPanel;
    }
    
    public void refreshTable() {
        companyRepModel.setRowCount(0);
        
        for (CompanyRepresentative rep : companyReps) {
            companyRepModel.addRow(new Object[]{
                rep.getUserID(),
                rep.getName(),
                rep.getUserID(), // UserID is email for company reps
                rep.getCompanyName(),
                rep.getStatus()
            });
        }
    }
    
    private void authorizeRep() {
        int selectedRow = companyRepTable.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showError(this, "Please click on a company representative row to select it");
            return;
        }
        
        String userId = (String) companyRepModel.getValueAt(selectedRow, 0);
        CompanyRepresentative rep = companyReps.stream()
            .filter(r -> r.getUserID().equals(userId))
            .findFirst()
            .orElse(null);
        
        if (rep == null) return;
        
        if (rep.getStatus() == ApprovalStatus.APPROVED) {
            UIHelper.showError(this, "This representative is already authorized");
            return;
        }
        
        boolean confirm = UIHelper.showConfirmation(this,
            "Authorize representative:\n" + rep.getName() + "\n" + rep.getCompanyName() + "?");
        
        if (confirm) {
            rep.setStatus(ApprovalStatus.APPROVED);
            UIHelper.showSuccess(this, "Company representative authorized successfully!");
            refreshTable();
            
            if (actionListener != null) {
                actionListener.onRepApproved(rep);
            }
        }
    }
    
    private void rejectRep() {
        int selectedRow = companyRepTable.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showError(this, "Please click on a company representative row to select it");
            return;
        }
        
        String userId = (String) companyRepModel.getValueAt(selectedRow, 0);
        CompanyRepresentative rep = companyReps.stream()
            .filter(r -> r.getUserID().equals(userId))
            .findFirst()
            .orElse(null);
        
        if (rep == null) return;
        
        if (rep.getStatus() == ApprovalStatus.REJECTED) {
            UIHelper.showError(this, "This representative is already rejected");
            return;
        }
        
        if (rep.getStatus() == ApprovalStatus.APPROVED) {
            UIHelper.showError(this, "Cannot reject an approved representative");
            return;
        }
        
        boolean confirm = UIHelper.showConfirmation(this,
            "Reject representative:\n" + rep.getName() + "\n" + rep.getCompanyName() + "?");
        
        if (confirm) {
            rep.setStatus(ApprovalStatus.REJECTED);
            UIHelper.showSuccess(this, "Company representative rejected.");
            refreshTable();
            
            if (actionListener != null) {
                actionListener.onRepRejected(rep);
            }
        }
    }
}
