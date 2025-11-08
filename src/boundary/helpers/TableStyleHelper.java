package sc2002_grpproject.boundary.helpers;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Helper class for styling tables with consistent color coding and formatting
 */
public class TableStyleHelper {
    
    // Color constants
    public static final Color LIGHT_GREEN = new Color(144, 238, 144);
    public static final Color VERY_LIGHT_GREEN = new Color(220, 255, 220);
    public static final Color LIGHT_RED = new Color(255, 220, 220);
    public static final Color LIGHT_YELLOW = new Color(255, 245, 200);
    public static final Color LIGHT_GREY = new Color(220, 220, 220);
    public static final Color DARK_GREEN = new Color(0, 100, 0);
    
    /**
     * Apply standard table styling (fonts, colors, row height)
     */
    public static void applyStandardStyle(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(LIGHT_GREEN);
        table.getTableHeader().setForeground(Color.BLACK);
        table.setSelectionBackground(new Color(180, 255, 180));
        table.setSelectionForeground(Color.BLACK);
    }
    
    /**
     * Create a non-editable table model
     */
    public static DefaultTableModel createNonEditableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
    
    /**
     * Cell renderer for internship availability (green for available, red for full)
     */
    public static class InternshipAvailabilityRenderer extends DefaultTableCellRenderer {
        private int availableColumnIndex;
        
        public InternshipAvailabilityRenderer(int availableColumnIndex) {
            this.availableColumnIndex = availableColumnIndex;
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (isSelected) {
                c.setBackground(LIGHT_GREY);
            } else {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                String available = model.getValueAt(row, availableColumnIndex).toString();
                int availableSlots = Integer.parseInt(available);
                if (availableSlots > 0) {
                    c.setBackground(VERY_LIGHT_GREEN);
                } else {
                    c.setBackground(LIGHT_RED);
                }
            }
            c.setForeground(Color.BLACK);
            return c;
        }
    }
    
    /**
     * Cell renderer for application status (color-coded by status)
     */
    public static class ApplicationStatusRenderer extends DefaultTableCellRenderer {
        private int statusColumnIndex;
        
        public ApplicationStatusRenderer(int statusColumnIndex) {
            this.statusColumnIndex = statusColumnIndex;
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (isSelected) {
                c.setBackground(LIGHT_GREY);
            } else {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                String status = model.getValueAt(row, statusColumnIndex).toString();
                if (status.contains("SUCCESSFUL") || status.contains("APPROVED")) {
                    c.setBackground(VERY_LIGHT_GREEN);
                } else if (status.contains("PENDING")) {
                    c.setBackground(LIGHT_YELLOW);
                } else if (status.contains("UNSUCCESSFUL") || status.contains("REJECTED")) {
                    c.setBackground(LIGHT_RED);
                } else if (status.contains("WITHDRAWN")) {
                    c.setBackground(LIGHT_GREY);
                } else {
                    c.setBackground(Color.WHITE);
                }
            }
            c.setForeground(Color.BLACK);
            return c;
        }
    }
    
    /**
     * Create a styled scroll pane with title border
     */
    public static JScrollPane createStyledScrollPane(JTable table, String title) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(LIGHT_GREEN, 2),
            title,
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.ITALIC, 11),
            DARK_GREEN
        ));
        return scrollPane;
    }
}
