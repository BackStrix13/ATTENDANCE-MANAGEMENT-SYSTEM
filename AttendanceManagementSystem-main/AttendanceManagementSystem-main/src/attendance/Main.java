package attendance;

import attendance.ui.MainWindow;
import attendance.ui.UITheme;

import javax.swing.*;

/**
 * Entry point for the Attendance Management System.
 */
public class Main {
    public static void main(String[] args) {
        // Set system look and feel as a base, then override with custom theme
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Global Swing defaults
        UIManager.put("OptionPane.background",           UITheme.BG_PANEL);
        UIManager.put("Panel.background",                UITheme.BG_PANEL);
        UIManager.put("OptionPane.messageForeground",    UITheme.TEXT_PRIMARY);
        UIManager.put("Button.background",               UITheme.BG_PANEL);
        UIManager.put("Button.foreground",               UITheme.TEXT_PRIMARY);
        UIManager.put("ComboBox.background",             UITheme.BG_PANEL);
        UIManager.put("ComboBox.foreground",             UITheme.TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground",    UITheme.ACCENT);
        UIManager.put("ComboBox.selectionForeground",    UITheme.BG_DARK);
        UIManager.put("ScrollBar.background",            UITheme.BG_DARK);
        UIManager.put("ScrollBar.thumb",                 UITheme.BORDER);
        UIManager.put("ScrollBar.track",                 UITheme.BG_DARK);
        UIManager.put("SplitPane.background",            UITheme.BG_DARK);
        UIManager.put("SplitPaneDivider.background",     UITheme.BORDER);

        SwingUtilities.invokeLater(MainWindow::new);
    }
}
