package attendance.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A custom rounded, flat button with hover animation.
 */
public class RoundedButton extends JButton {

    public enum Style { PRIMARY, DANGER, SUCCESS, GHOST }

    private Color bgColor;
    private Color hoverColor;
    private final int radius;
    private boolean hovered = false;

    public RoundedButton(String text, Style style) {
        super(text);
        radius = 8;
        applyStyle(style);

        setFont(UITheme.FONT_BODY);
        setForeground(UITheme.TEXT_PRIMARY);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
        });
    }

    private void applyStyle(Style style) {
        switch (style) {
            case PRIMARY -> { bgColor = UITheme.ACCENT;        hoverColor = UITheme.ACCENT_HOVER; }
            case DANGER  -> { bgColor = UITheme.DANGER;        hoverColor = new Color(239, 68, 68); }
            case SUCCESS -> { bgColor = UITheme.SUCCESS;       hoverColor = new Color(16,185,129); }
            case GHOST   -> { bgColor = UITheme.BG_PANEL;      hoverColor = UITheme.BORDER; }
        }
        if (style == Style.PRIMARY) setForeground(new Color(15, 23, 42));
        else setForeground(UITheme.TEXT_PRIMARY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(hovered ? hoverColor : bgColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.width + 24, d.height + 10);
    }
}
