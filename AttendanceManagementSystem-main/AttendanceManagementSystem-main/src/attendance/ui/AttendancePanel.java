package attendance.ui;

import attendance.model.AttendanceRecord.Status;
import attendance.model.Student;
import attendance.service.AttendanceService;
import attendance.service.StudentService;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

/**
 * Panel for marking daily attendance using checkboxes.
 */
public class AttendancePanel extends JPanel {

    private final StudentService    studentService;
    private final AttendanceService attendanceService;

    private JTextField        tfDate;
    private DefaultTableModel tableModel;
    private JTable            table;
    private JLabel            lblStatus;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AttendancePanel(StudentService ss, AttendanceService as) {
        this.studentService    = ss;
        this.attendanceService = as;
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildHeader(),   BorderLayout.NORTH);
        add(buildTable(),    BorderLayout.CENTER);
        add(buildFooter(),   BorderLayout.SOUTH);

        loadForDate(LocalDate.now());
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);

        JLabel title = new JLabel("Mark Attendance");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JLabel dateLbl = new JLabel("Date (yyyy-MM-dd):");
        dateLbl.setFont(UITheme.FONT_BODY);
        dateLbl.setForeground(UITheme.TEXT_MUTED);

        tfDate = new JTextField(LocalDate.now().format(FMT), 12);
        tfDate.setBackground(UITheme.BG_PANEL);
        tfDate.setForeground(UITheme.TEXT_PRIMARY);
        tfDate.setCaretColor(UITheme.ACCENT);
        tfDate.setFont(UITheme.FONT_MONO);
        tfDate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));

        RoundedButton btnLoad = new RoundedButton("Load", RoundedButton.Style.GHOST);
        btnLoad.addActionListener(e -> loadForDateFromField());

        lblStatus = new JLabel("");
        lblStatus.setFont(UITheme.FONT_SMALL);
        lblStatus.setForeground(UITheme.TEXT_MUTED);

        right.add(dateLbl);
        right.add(tfDate);
        right.add(btnLoad);
        right.add(lblStatus);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    // ── Table ─────────────────────────────────────────────────────────────────

    private JScrollPane buildTable() {
        String[] cols = {"Roll No.", "Student Name", "Course", "Present", "ID"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public Class<?> getColumnClass(int col) {
                return col == 3 ? Boolean.class : String.class;
            }
            @Override public boolean isCellEditable(int r, int c) { return c == 3; }
        };

        table = new JTable(tableModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                boolean present = Boolean.TRUE.equals(tableModel.getValueAt(row, 3));
                Color bg = row % 2 == 0 ? UITheme.BG_PANEL : UITheme.TABLE_ALT_ROW;
                c.setBackground(bg);
                if (col != 3) c.setForeground(present ? UITheme.SUCCESS : UITheme.TEXT_MUTED);
                ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return c;
            }
        };
        styleTable(table);

        // Hide ID column
        table.getColumnModel().getColumn(4).setMinWidth(0);
        table.getColumnModel().getColumn(4).setMaxWidth(0);

        // Style checkbox column
        table.getColumnModel().getColumn(3).setPreferredWidth(70);

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(UITheme.BG_PANEL);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        return sp;
    }

    // ── Footer ────────────────────────────────────────────────────────────────

    private JPanel buildFooter() {
        CardPanel card = new CardPanel();
        card.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        RoundedButton btnAll  = new RoundedButton("✔ Mark All Present",  RoundedButton.Style.SUCCESS);
        RoundedButton btnNone = new RoundedButton("✘ Mark All Absent",   RoundedButton.Style.DANGER);
        RoundedButton btnSave = new RoundedButton("💾 Save Attendance",  RoundedButton.Style.PRIMARY);

        btnAll .addActionListener(e -> setAllPresent(true));
        btnNone.addActionListener(e -> setAllPresent(false));
        btnSave.addActionListener(e -> saveAttendance());

        card.add(btnAll);
        card.add(btnNone);
        card.add(btnSave);
        return card;
    }

    // ── Logic ─────────────────────────────────────────────────────────────────

    private void loadForDateFromField() {
        try {
            LocalDate date = LocalDate.parse(tfDate.getText().trim(), FMT);
            loadForDate(date);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                "Invalid date format. Use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadForDate(LocalDate date) {
        tableModel.setRowCount(0);
        List<Student> students = studentService.getAllStudents();
        if (students.isEmpty()) {
            lblStatus.setText("No students registered.");
            return;
        }

        boolean alreadyMarked = attendanceService.isAttendanceMarkedForDate(date);
        lblStatus.setText(alreadyMarked ? "⚠ Attendance already saved for this date." : "");

        Map<String, Status> existing = new HashMap<>();
        attendanceService.getRecordsForDate(date)
            .forEach(r -> existing.put(r.getStudentId(), r.getStatus()));

        for (Student s : students) {
            Status st = existing.getOrDefault(s.getId(), Status.PRESENT);
            tableModel.addRow(new Object[]{
                s.getRollNumber(), s.getName(), s.getCourse(),
                st == Status.PRESENT, s.getId()
            });
        }
    }

    private void saveAttendance() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No students to save.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        LocalDate date;
        try {
            date = LocalDate.parse(tfDate.getText().trim(), FMT);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> allIds   = new ArrayList<>();
        Set<String>  present  = new HashSet<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String id = (String) tableModel.getValueAt(i, 4);
            allIds.add(id);
            if (Boolean.TRUE.equals(tableModel.getValueAt(i, 3))) present.add(id);
        }
        attendanceService.bulkMarkAttendance(allIds, present, date);
        lblStatus.setText("✔ Saved for " + date.format(FMT));
        lblStatus.setForeground(UITheme.SUCCESS);
        JOptionPane.showMessageDialog(this, "Attendance saved for " + date + ".", "Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setAllPresent(boolean present) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(present, i, 3);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void styleTable(JTable t) {
        t.setBackground(UITheme.BG_PANEL);
        t.setForeground(UITheme.TEXT_PRIMARY);
        t.setFont(UITheme.FONT_BODY);
        t.setRowHeight(34);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(new Color(251, 191, 36, 50));
        t.setSelectionForeground(UITheme.TEXT_PRIMARY);
        t.getTableHeader().setBackground(UITheme.BG_SIDEBAR);
        t.getTableHeader().setForeground(UITheme.TEXT_MUTED);
        t.getTableHeader().setFont(UITheme.FONT_SMALL);
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));
    }

    /** Called by main window when the panel becomes visible — refresh for today. */
    public void refresh() {
        loadForDate(LocalDate.now());
        tfDate.setText(LocalDate.now().format(FMT));
    }
}
