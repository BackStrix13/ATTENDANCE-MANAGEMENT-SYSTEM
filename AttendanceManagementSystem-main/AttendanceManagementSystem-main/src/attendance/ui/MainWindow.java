package attendance.ui;

import attendance.service.AttendanceService;
import attendance.service.StudentService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Main application window — hosts the sidebar and the content panels.
 */
public class MainWindow extends JFrame {

    private final StudentService    studentService;
    private final AttendanceService attendanceService;

    private StudentPanel   studentPanel;
    private AttendancePanel attendancePanel;
    private ReportPanel    reportPanel;

    private JPanel    contentArea;
    private CardLayout cardLayout;

    private JLabel    activeNavItem;

    public MainWindow() {
        studentService    = new StudentService();
        attendanceService = new AttendanceService();

        setTitle("Attendance Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 620));
        setSize(1180, 700);
        setLocationRelativeTo(null);
        setBackground(UITheme.BG_DARK);

        buildUI();
        setVisible(true);

        // Show students panel by default
        showPanel("students");
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_DARK);

        root.add(buildSidebar(),  BorderLayout.WEST);
        root.add(buildContent(), BorderLayout.CENTER);

        setContentPane(root);
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UITheme.BORDER));

        // Logo / app name
        JPanel logoArea = new JPanel(new BorderLayout());
        logoArea.setOpaque(false);
        logoArea.setBorder(BorderFactory.createEmptyBorder(24, 20, 20, 20));
        logoArea.setMaximumSize(new Dimension(220, 90));

        JLabel appName = new JLabel("AttendanceMS");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 17));
        appName.setForeground(UITheme.ACCENT);

        JLabel subtitle = new JLabel("Management System");
        subtitle.setFont(UITheme.FONT_SMALL);
        subtitle.setForeground(UITheme.TEXT_MUTED);

        logoArea.add(appName,  BorderLayout.NORTH);
        logoArea.add(subtitle, BorderLayout.SOUTH);
        sidebar.add(logoArea);

        // Divider
        sidebar.add(divider());
        sidebar.add(Box.createVerticalStrut(8));

        // Nav items
        JLabel navStudents    = navItem("👤  Students",        "students");
        JLabel navAttendance  = navItem("✓   Mark Attendance", "attendance");
        JLabel navReports     = navItem("📊  Reports",         "reports");

        sidebar.add(navStudents);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(navAttendance);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(navReports);

        sidebar.add(Box.createVerticalGlue());

        // Footer
        JLabel version = new JLabel("v1.0 · CSV Storage");
        version.setFont(UITheme.FONT_SMALL);
        version.setForeground(UITheme.TEXT_MUTED);
        version.setBorder(BorderFactory.createEmptyBorder(0, 20, 16, 0));
        version.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(version);

        activeNavItem = navStudents;
        setNavActive(navStudents, true);

        return sidebar;
    }

    private JLabel navItem(String text, String panelKey) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_BODY);
        lbl.setForeground(UITheme.TEXT_MUTED);
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(220, 42));
        lbl.setOpaque(true);
        lbl.setBackground(UITheme.BG_SIDEBAR);

        lbl.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (activeNavItem != null) setNavActive(activeNavItem, false);
                activeNavItem = lbl;
                setNavActive(lbl, true);
                showPanel(panelKey);
            }
            @Override public void mouseEntered(MouseEvent e) {
                if (lbl != activeNavItem) lbl.setBackground(UITheme.BG_PANEL);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (lbl != activeNavItem) lbl.setBackground(UITheme.BG_SIDEBAR);
            }
        });
        return lbl;
    }

    private void setNavActive(JLabel lbl, boolean active) {
        lbl.setForeground(active ? UITheme.ACCENT : UITheme.TEXT_MUTED);
        lbl.setBackground(active ? new Color(30, 20, 5) : UITheme.BG_SIDEBAR);
    }

    private JSeparator divider() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(UITheme.BORDER);
        sep.setMaximumSize(new Dimension(220, 1));
        return sep;
    }

    // ── Content area ──────────────────────────────────────────────────────────

    private JPanel buildContent() {
        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(UITheme.BG_DARK);

        studentPanel    = new StudentPanel(studentService, attendanceService);
        attendancePanel = new AttendancePanel(studentService, attendanceService);
        reportPanel     = new ReportPanel(studentService, attendanceService);

        contentArea.add(studentPanel,    "students");
        contentArea.add(attendancePanel, "attendance");
        contentArea.add(reportPanel,     "reports");

        return contentArea;
    }

    private void showPanel(String key) {
        cardLayout.show(contentArea, key);
        if ("reports".equals(key))    reportPanel.refresh();
        if ("attendance".equals(key)) attendancePanel.refresh();
    }
}
