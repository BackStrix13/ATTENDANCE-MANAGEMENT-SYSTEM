package attendance.ui;

import attendance.model.AttendanceRecord;
import attendance.model.AttendanceRecord.Status;
import attendance.model.Student;
import attendance.service.AttendanceService;
import attendance.service.StudentService;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Reports panel — shows attendance summary for all students,
 * and a per-student detail view.
 */
public class ReportPanel extends JPanel {

    private final StudentService    studentService;
    private final AttendanceService attendanceService;

    private DefaultTableModel summaryModel;
    private DefaultTableModel detailModel;
    private JComboBox<String> studentCombo;
    private JLabel            lblTotal, lblPresent, lblPercent;

    public ReportPanel(StudentService ss, AttendanceService as) {
        this.studentService    = ss;
        this.attendanceService = as;
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildTitle(),   BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    // ── Title bar ─────────────────────────────────────────────────────────────

    private JPanel buildTitle() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lbl = new JLabel("Attendance Reports");
        lbl.setFont(UITheme.FONT_TITLE);
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        p.add(lbl, BorderLayout.WEST);

        RoundedButton btnRefresh = new RoundedButton("⟳ Refresh", RoundedButton.Style.GHOST);
        btnRefresh.addActionListener(e -> refresh());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(btnRefresh);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    // ── Main content: summary on left, detail on right ────────────────────────

    private JSplitPane buildContent() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildSummary(), buildDetail());
        split.setDividerLocation(480);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setBackground(UITheme.BG_DARK);
        split.setOpaque(false);
        return split;
    }

    // ── Summary table ─────────────────────────────────────────────────────────

    private JPanel buildSummary() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setOpaque(false);

        JLabel heading = new JLabel("Overall Summary");
        heading.setFont(UITheme.FONT_HEADING);
        heading.setForeground(UITheme.ACCENT);
        p.add(heading, BorderLayout.NORTH);

        String[] cols = {"Roll No.", "Name", "Present", "Total Days", "Percentage"};
        summaryModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(summaryModel);

        // Colour the percentage column
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                try {
                    double pct = Double.parseDouble(val.toString().replace("%",""));
                    setForeground(pct >= 75 ? UITheme.SUCCESS : UITheme.DANGER);
                } catch (Exception ignored) { setForeground(UITheme.TEXT_PRIMARY); }
                setBackground(row % 2 == 0 ? UITheme.BG_PANEL : UITheme.TABLE_ALT_ROW);
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                setFont(UITheme.FONT_BODY);
                return this;
            }
        });

        JScrollPane sp = scrollPane(table);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    // ── Per-student detail ────────────────────────────────────────────────────

    private JPanel buildDetail() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

        JLabel heading = new JLabel("Student Detail");
        heading.setFont(UITheme.FONT_HEADING);
        heading.setForeground(UITheme.ACCENT);
        p.add(heading, BorderLayout.NORTH);

        // Stats cards row
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 10, 0));
        statsRow.setOpaque(false);
        lblTotal   = statCard("Total Days", "0");
        lblPresent = statCard("Present",    "0");
        lblPercent = statCard("Attendance", "0%");
        statsRow.add(wrapStat("Total Days", lblTotal));
        statsRow.add(wrapStat("Present",    lblPresent));
        statsRow.add(wrapStat("Attendance", lblPercent));

        // Student selector
        JPanel selector = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        selector.setOpaque(false);
        JLabel lbl = new JLabel("Student:");
        lbl.setFont(UITheme.FONT_BODY);
        lbl.setForeground(UITheme.TEXT_MUTED);
        studentCombo = new JComboBox<>();
        studentCombo.setBackground(UITheme.BG_PANEL);
        studentCombo.setForeground(UITheme.TEXT_PRIMARY);
        studentCombo.setFont(UITheme.FONT_BODY);
        studentCombo.setPreferredSize(new Dimension(240, 32));
        studentCombo.addActionListener(e -> loadDetail());
        selector.add(lbl);
        selector.add(studentCombo);

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        top.add(statsRow, BorderLayout.NORTH);
        top.add(selector, BorderLayout.SOUTH);
        p.add(top, BorderLayout.NORTH);

        // Detail table
        String[] cols = {"Date", "Status"};
        detailModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable detailTable = styledTable(detailModel);

        // Colour status column
        detailTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setForeground("PRESENT".equals(val) ? UITheme.SUCCESS : UITheme.DANGER);
                setBackground(row % 2 == 0 ? UITheme.BG_PANEL : UITheme.TABLE_ALT_ROW);
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                setFont(UITheme.FONT_BODY);
                return this;
            }
        });

        p.add(scrollPane(detailTable), BorderLayout.CENTER);
        return p;
    }

    // ── Refresh data ──────────────────────────────────────────────────────────

    public void refresh() {
        List<Student> students = studentService.getAllStudents();

        // Summary table
        summaryModel.setRowCount(0);
        List<String> ids = students.stream().map(Student::getId).toList();
        Map<String, int[]> summary = attendanceService.getSummary(ids);
        for (Student s : students) {
            int[] data = summary.getOrDefault(s.getId(), new int[]{0, 0});
            int present = data[0], total = data[1];
            String pct = total == 0 ? "N/A" : String.format("%.1f%%", (present * 100.0 / total));
            summaryModel.addRow(new Object[]{s.getRollNumber(), s.getName(), present, total, pct});
        }

        // Combo box
        String selected = (String) studentCombo.getSelectedItem();
        studentCombo.removeAllItems();
        for (Student s : students) {
            studentCombo.addItem("[" + s.getRollNumber() + "] " + s.getName() + "|" + s.getId());
        }
        if (selected != null) studentCombo.setSelectedItem(selected);
        loadDetail();
    }

    private void loadDetail() {
        detailModel.setRowCount(0);
        Object sel = studentCombo.getSelectedItem();
        if (sel == null) return;

        String id = sel.toString().split("\\|")[1];
        List<AttendanceRecord> records = attendanceService.getRecordsForStudent(id);
        int total   = records.size();
        int present = (int) records.stream().filter(r -> r.getStatus() == Status.PRESENT).count();
        String pct  = total == 0 ? "N/A" : String.format("%.1f%%", (present * 100.0 / total));

        lblTotal.setText(String.valueOf(total));
        lblPresent.setText(String.valueOf(present));
        lblPercent.setText(pct);
        lblPercent.setForeground(total == 0 ? UITheme.TEXT_MUTED
            : (present * 100.0 / total >= 75 ? UITheme.SUCCESS : UITheme.DANGER));

        for (AttendanceRecord r : records) {
            detailModel.addRow(new Object[]{r.getDate().toString(), r.getStatus().name()});
        }
    }

    // ── UI helpers ────────────────────────────────────────────────────────────

    private JLabel statCard(String label, String value) {
        JLabel lbl = new JLabel(value, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(UITheme.ACCENT);
        return lbl;
    }

    private JPanel wrapStat(String label, JLabel valueLbl) {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(0, 4));
        card.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.TEXT_MUTED);
        card.add(lbl,      BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);
        return card;
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(row % 2 == 0 ? UITheme.BG_PANEL : UITheme.TABLE_ALT_ROW);
                c.setForeground(UITheme.TEXT_PRIMARY);
                ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return c;
            }
        };
        t.setBackground(UITheme.BG_PANEL);
        t.setForeground(UITheme.TEXT_PRIMARY);
        t.setFont(UITheme.FONT_BODY);
        t.setRowHeight(30);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(new Color(251, 191, 36, 50));
        t.setSelectionForeground(UITheme.TEXT_PRIMARY);
        t.getTableHeader().setBackground(UITheme.BG_SIDEBAR);
        t.getTableHeader().setForeground(UITheme.TEXT_MUTED);
        t.getTableHeader().setFont(UITheme.FONT_SMALL);
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));
        return t;
    }

    private JScrollPane scrollPane(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.getViewport().setBackground(UITheme.BG_PANEL);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        return sp;
    }
}
