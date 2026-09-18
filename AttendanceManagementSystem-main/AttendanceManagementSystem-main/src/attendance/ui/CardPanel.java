package attendance.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A JPanel with a rounded rectangle background — used as a "card".
 */
public class CardPanel extends JPanel {
    private final Color bg;
    private final int radius;

    public CardPanel(Color bg, int radius) {
        this.bg = bg;
        this.radius = radius;
        setOpaque(false);
    }

    public CardPanel() {
        this(UITheme.BG_PANEL, UITheme.RADIUS);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
        super.paintComponent(g2);
        g2.dispose();
    }
}
