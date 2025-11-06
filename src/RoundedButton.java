package sc2002_grpproject;

import javax.swing.*;
import java.awt.*;

// Simple rounded button with custom background painting to remove rectangular green edge
public class RoundedButton extends JButton {
    private int radius;

    public RoundedButton(String text) {
        this(text, 16);
    }

    public RoundedButton(String text, int radius) {
        super(text);
        this.radius = radius;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        setFont(new Font("Arial", Font.BOLD, 14));
        setForeground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    public void setBackground(Color bg) {
        // ensure non-null
        if (bg == null) bg = Color.LIGHT_GRAY;
        super.setBackground(bg);
    }
}
