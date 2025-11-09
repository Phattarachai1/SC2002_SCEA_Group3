package sc2002_grpproject.boundary.student;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.Enums.*;
import sc2002_grpproject.boundary.RoundedButton;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class MyApplicationsPanel extends JPanel {
    private Student student;
    private List<InternshipApplication> applications;
    private StudentActionHandler actionHandler;
    
    private JTable applicationTable;
    private DefaultTableModel applicationModel;

    public MyApplicationsPanel(Student student, List<InternshipApplication> applications,
                               StudentActionHandler actionHandler) {
        this.student = student;
        this.applications = applications;
        this.actionHandler = actionHandler;
        
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 255, 250));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initializeComponents();
    }

    private void initializeComponents() {
        // Info label at top
        JLabel infoLabel = new JLabel("<html><b>My Applications</b> - Track your applications. Accept SUCCESSFUL offers or withdraw from CONFIRMED placements</html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        infoLabel.setForeground(new Color(0, 80, 0));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        add(infoLabel, BorderLayout.NORTH);

        // Table
        createTable();
        JScrollPane scrollPane = new JScrollPane(applicationTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = createButtonPanel();
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void createTable() {
        String[] columns = {"App ID", "Internship", "Company", "Status", "Confirmed", "Withdrawal Requested"};
        applicationModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        applicationTable = new JTable(applicationModel);
        applicationTable.setFont(new Font("Arial", Font.PLAIN, 12));
        applicationTable.setRowHeight(28);
        applicationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        applicationTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        applicationTable.getTableHeader().setBackground(new Color(144, 238, 144));
        applicationTable.getTableHeader().setForeground(Color.BLACK);
        applicationTable.setSelectionBackground(new Color(180, 255, 180));
        applicationTable.setSelectionForeground(Color.BLACK);
    }

    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(new Color(235, 250, 235));
        
        JButton acceptBtn = createButton("Accept Placement");
        JButton withdrawBtn = createButton("Request Withdrawal");
        JButton refreshBtn = createButton("Refresh List");
        
        acceptBtn.setToolTipText("Confirm your acceptance of a successful internship offer");
        withdrawBtn.setToolTipText("Request to withdraw from a confirmed placement");
        refreshBtn.setToolTipText("Reload your application status");
        
        acceptBtn.addActionListener(e -> {
            int selectedRow = applicationTable.getSelectedRow();
            if (selectedRow != -1) {
                String appId = (String) applicationModel.getValueAt(selectedRow, 0);
                actionHandler.acceptPlacement(appId);
                refresh();
            } else {
                actionHandler.showError("Please select an application to accept");
            }
        });
        
        withdrawBtn.addActionListener(e -> {
            int selectedRow = applicationTable.getSelectedRow();
            if (selectedRow != -1) {
                String appId = (String) applicationModel.getValueAt(selectedRow, 0);
                actionHandler.requestWithdrawal(appId);
                refresh();
            } else {
                actionHandler.showError("Please select an application to withdraw");
            }
        });
        
        refreshBtn.addActionListener(e -> refresh());
        
        btnPanel.add(acceptBtn);
        btnPanel.add(withdrawBtn);
        btnPanel.add(refreshBtn);
        
        return btnPanel;
    }

    public void refresh() {
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
        
        applyRowColors();
    }

    private void applyRowColors() {
        applicationTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(new Color(220, 220, 220));
                } else {
                    String status = applicationModel.getValueAt(row, 3).toString();
                    if (status.contains("SUCCESSFUL")) {
                        c.setBackground(new Color(220, 255, 220)); // Light green
                    } else if (status.contains("PENDING")) {
                        c.setBackground(new Color(255, 245, 200)); // Light orange/yellow
                    } else if (status.contains("UNSUCCESSFUL")) {
                        c.setBackground(new Color(255, 220, 220)); // Light red
                    } else if (status.contains("WITHDRAWN")) {
                        c.setBackground(new Color(220, 220, 220)); // Light gray
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
