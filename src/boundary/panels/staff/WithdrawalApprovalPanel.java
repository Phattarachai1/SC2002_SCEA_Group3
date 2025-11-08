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
 * Panel for approving student withdrawal requests
 */
public class WithdrawalApprovalPanel extends JPanel {
    private final List<InternshipApplication> applications;
    private final WithdrawalActionListener actionListener;
    
    private JTable withdrawalTable;
    private DefaultTableModel withdrawalModel;
    
    public interface WithdrawalActionListener {
        void onWithdrawalApproved(InternshipApplication application);
    }
    
    public WithdrawalApprovalPanel(List<InternshipApplication> applications, WithdrawalActionListener listener) {
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
        
        JLabel titleLabel = new JLabel("Withdrawal Request Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(0, 80, 0));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel helpLabel = new JLabel("<html><i>Approve withdrawal requests from students who need to withdraw from confirmed placements</i></html>");
        helpLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        helpLabel.setForeground(new Color(100, 100, 100));
        titlePanel.add(helpLabel, BorderLayout.SOUTH);
        
        return titlePanel;
    }
    
    private JPanel createTablePanel() {
        String[] columns = {"App ID", "Student", "Internship", "Company", "Status"};
        withdrawalModel = TableStyleHelper.createNonEditableModel(columns);
        withdrawalTable = new JTable(withdrawalModel);
        
        TableStyleHelper.applyStandardStyle(withdrawalTable);
        
        JScrollPane scrollPane = new JScrollPane(withdrawalTable);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIHelper.LIGHT_MINT);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(235, 250, 235));
        
        JButton approveBtn = UIHelper.createStyledButton("Approve Withdrawal");
        JButton refreshBtn = UIHelper.createStyledButton("Refresh");
        
        approveBtn.setToolTipText("Approve the selected withdrawal request");
        refreshBtn.setToolTipText("Reload withdrawal requests");
        
        approveBtn.addActionListener(e -> approveWithdrawal());
        refreshBtn.addActionListener(e -> refreshTable());
        
        btnPanel.add(approveBtn);
        btnPanel.add(refreshBtn);
        
        return btnPanel;
    }
    
    public void refreshTable() {
        withdrawalModel.setRowCount(0);
        
        List<InternshipApplication> withdrawals = applications.stream()
            .filter(app -> app.isWithdrawalRequested() && app.getStatus() != ApplicationStatus.WITHDRAWN)
            .collect(Collectors.toList());
        
        for (InternshipApplication app : withdrawals) {
            withdrawalModel.addRow(new Object[]{
                app.getApplicationId(),
                app.getStudent().getName(),
                app.getInternship().getTitle(),
                app.getInternship().getCompanyName(),
                app.getStatus()
            });
        }
    }
    
    private void approveWithdrawal() {
        int selectedRow = withdrawalTable.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showError(this, "Please select a withdrawal request");
            return;
        }
        
        String appId = (String) withdrawalModel.getValueAt(selectedRow, 0);
        InternshipApplication selected = applications.stream()
            .filter(app -> app.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);
        
        if (selected == null) return;
        
        boolean confirm = UIHelper.showConfirmation(this,
            "Approve withdrawal for:\nStudent: " + selected.getStudent().getName() +
            "\nInternship: " + selected.getInternship().getTitle() + "?");
        
        if (confirm) {
            selected.setStatus(ApplicationStatus.WITHDRAWN);
            selected.setWithdrawalRequested(false);
            
            if (selected.isPlacementConfirmed()) {
                selected.setPlacementConfirmed(false);
                selected.getInternship().decrementConfirmedPlacements();
            }
            
            UIHelper.showSuccess(this, "Withdrawal approved successfully!");
            refreshTable();
            
            if (actionListener != null) {
                actionListener.onWithdrawalApproved(selected);
            }
        }
    }
}
