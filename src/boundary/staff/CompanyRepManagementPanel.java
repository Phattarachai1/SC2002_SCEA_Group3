package sc2002_grpproject.boundary.staff;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.*;
import sc2002_grpproject.boundary.RoundedButton;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GUI panel for managing company representative accounts and approval status.
 * Provides filtering by approval status and approval/rejection functionality for staff.
 * 
 * @author SC2002_SCEA_Group3
 * @version 1.0
 */
public class CompanyRepManagementPanel extends JPanel {
    private List<CompanyRepresentative> companyReps;
    private StaffActionHandler actionHandler;
    
    private JTable companyRepTable;
    private DefaultTableModel companyRepModel;

    public CompanyRepManagementPanel(List<CompanyRepresentative> companyReps,
                                     StaffActionHandler actionHandler) {
        this.companyReps = companyReps;
        this.actionHandler = actionHandler;
        
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 255, 250));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initializeComponents();
    }

    private void initializeComponents() {
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(245, 255, 250));
        
        JLabel titleLabel = new JLabel("Company Representative Authorization");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(0, 80, 0));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel helpLabel = new JLabel("<html><i>Authorize PENDING company representatives to allow them to post internships</i></html>");
        helpLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        helpLabel.setForeground(new Color(100, 100, 100));
        titlePanel.add(helpLabel, BorderLayout.SOUTH);
        
        add(titlePanel, BorderLayout.NORTH);

        // Table
        createTable();
        JScrollPane scrollPane = new JScrollPane(companyRepTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = createButtonPanel();
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void createTable() {
        String[] columns = {"User ID", "Name", "Email", "Company", "Status"};
        companyRepModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        companyRepTable = new JTable(companyRepModel);
        companyRepTable.setFont(new Font("Arial", Font.PLAIN, 12));
        companyRepTable.setRowHeight(32);
        companyRepTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        companyRepTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        companyRepTable.getTableHeader().setBackground(new Color(144, 238, 144));
        companyRepTable.getTableHeader().setForeground(Color.BLACK);
        companyRepTable.setSelectionBackground(new Color(220, 220, 220));
        companyRepTable.setSelectionForeground(Color.BLACK);
    }

    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(235, 250, 235));
        
        JButton authorizeBtn = createButton("Authorize Selected");
        JButton rejectRepBtn = createButton("Reject Selected");
        JButton refreshBtn = createButton("Refresh");
        
        authorizeBtn.setToolTipText("Click on a PENDING representative row then click to authorize");
        rejectRepBtn.setToolTipText("Click on a PENDING representative row then click to reject");
        refreshBtn.setToolTipText("Reload the company representatives list");
        
        authorizeBtn.addActionListener(e -> {
            int selectedRow = companyRepTable.getSelectedRow();
            if (selectedRow != -1) {
                String userId = (String) companyRepModel.getValueAt(selectedRow, 0);
                actionHandler.authorizeRepresentative(userId);
                refresh();
            } else {
                actionHandler.showError("Please click on a company representative row to select it");
            }
        });
        
        rejectRepBtn.addActionListener(e -> {
            int selectedRow = companyRepTable.getSelectedRow();
            if (selectedRow != -1) {
                String userId = (String) companyRepModel.getValueAt(selectedRow, 0);
                actionHandler.rejectRepresentative(userId);
                refresh();
            } else {
                actionHandler.showError("Please click on a company representative row to select it");
            }
        });
        
        refreshBtn.addActionListener(e -> refresh());
        
        btnPanel.add(authorizeBtn);
        btnPanel.add(rejectRepBtn);
        btnPanel.add(refreshBtn);
        
        return btnPanel;
    }

    public void refresh() {
        companyRepModel.setRowCount(0);
        
        // Sort: PENDING first, then APPROVED, then REJECTED
        List<CompanyRepresentative> sortedReps = companyReps.stream()
            .sorted((r1, r2) -> {
                if (r1.getStatus() == ApprovalStatus.PENDING) return -1;
                if (r2.getStatus() == ApprovalStatus.PENDING) return 1;
                if (r1.getStatus() == ApprovalStatus.APPROVED) return -1;
                if (r2.getStatus() == ApprovalStatus.APPROVED) return 1;
                return 0;
            })
            .collect(Collectors.toList());

        for (CompanyRepresentative rep : sortedReps) {
            String statusText = "<html><b>" + rep.getStatus() + "</b></html>";
            companyRepModel.addRow(new Object[]{
                rep.getUserID(),
                rep.getName(),
                rep.getUserID(), // Email is the UserID
                rep.getCompanyName(),
                statusText
            });
        }
        
        applyRowColors();
    }

    private void applyRowColors() {
        companyRepTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String status = companyRepModel.getValueAt(row, 4).toString();
                    if (status.contains("PENDING")) {
                        c.setBackground(new Color(255, 255, 200)); // Light yellow
                    } else if (status.contains("APPROVED")) {
                        c.setBackground(new Color(220, 255, 220)); // Light green
                    } else if (status.contains("REJECTED")) {
                        c.setBackground(new Color(255, 220, 220)); // Light red
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
