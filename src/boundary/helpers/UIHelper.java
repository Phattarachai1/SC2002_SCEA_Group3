package sc2002_grpproject.boundary.helpers;

import javax.swing.*;
import java.awt.*;

/**
 * Helper class for UI utilities like creating styled buttons and showing dialogs
 */
public class UIHelper {
    
    // Color constants
    public static final Color MINT_GREEN = new Color(230, 255, 240);
    public static final Color LIGHT_MINT = new Color(245, 255, 250);
    public static final Color BUTTON_GREEN = new Color(144, 238, 144);
    public static final Color DARK_GREEN = new Color(0, 100, 0);
    public static final Color BORDER_GREEN = new Color(34, 139, 34);
    
    /**
     * Create a styled button with consistent appearance
     */
    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(BUTTON_GREEN);
        button.setForeground(DARK_GREEN);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GREEN, 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    /**
     * Show an error dialog
     */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Show an info dialog
     */
    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show a success dialog
     */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show a confirmation dialog and return user's choice
     */
    public static boolean showConfirmation(Component parent, String message) {
        int choice = JOptionPane.showConfirmDialog(parent, message, "Confirm", 
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return choice == JOptionPane.YES_OPTION;
    }
    
    /**
     * Create a styled panel with standard background
     */
    public static JPanel createStyledPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(LIGHT_MINT);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }
    
    /**
     * Create a top panel with welcome message and buttons
     */
    public static JPanel createTopPanel(String welcomeMessage) {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BUTTON_GREEN);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GREEN, 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel welcomeLabel = new JLabel(welcomeMessage);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(DARK_GREEN);
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        
        return topPanel;
    }
}
