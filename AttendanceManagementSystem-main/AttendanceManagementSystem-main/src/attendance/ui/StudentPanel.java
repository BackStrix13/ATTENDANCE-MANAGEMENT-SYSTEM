package attendance.ui;

import attendance.model.Student;
import attendance.service.AttendanceService;
import attendance.service.StudentService;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Panel for adding, viewing, and removing students.
 */
public class StudentPanel extends JPanel {

    private final StudentService   studentService;
    private final AttendanceService attendanceService;

    private DefaultTableModel tableModel;
    private JTable            table;
    private JTextField        tfName, tfRoll, tfCourse, tfSearch;

    public StudentPanel(StudentService ss, AttendanceService as) {
        this.studentService    = ss;
        this.attendanceService = as;
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildTopBar(),   BorderLayout.NORTH);
        add(buildTable(),    BorderLayout.CENTER);
        add(buildAddForm(),  BorderLayout.SOUTH);

        refreshTable(studentService.getAllStudents());
    }

    // ── Top bar with search ───────────────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setOpaque(false);

        JLabel title = new JLabel("Students");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        bar.add(title, BorderLayout.WEST);

        tfSearch = styledField("Search by name or roll…");
        tfSearch.setPreferredSize(new Dimension(220, 34));
        tfSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(tfSearch);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Table ─────────────────────────────────────────────────────────────────

    private JScrollPane buildTable() {
        String[] cols = {"Roll No.", "Name", "Course", "ID"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(row % 2 == 0 ? UITheme.BG_PANEL : UITheme.TABLE_ALT_ROW);
                c.setForeground(UITheme.TEXT_PRIMARY);
                ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return c;
            }
        };
        styleTable(table);

        // Hide ID column
        table.getColumnModel().getColumn(3).setMinWidth(0);
        table.getColumnModel().getColumn(3).setMaxWidth(0);

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(UITheme.BG_PANEL);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        return sp;
    }

    // ── Add-student form ──────────────────────────────────────────────────────

    private CardPanel buildAddForm() {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(12, 8));
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel heading = new JLabel("Add New Student");
        heading.setFont(UITheme.FONT_HEADING);
        heading.setForeground(UITheme.ACCENT);
        card.add(heading, BorderLayout.NORTH);

        // Fields row
        JPanel fields = new JPanel(new GridLayout(1, 3, 10, 0));
        fields.setOpaque(false);
        tfName   = styledField("Full Name");
        tfRoll   = styledField("Roll Number");
        tfCourse = styledField("Course / Class");
        fields.add(labeled("Name",   tfName));
        fields.add(labeled("Roll #", tfRoll));
        fields.add(labeled("Course", tfCourse));
        card.add(fields, BorderLayout.CENTER);

        // Buttons row
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);

        RoundedButton btnAdd = new RoundedButton("＋ Add Student", RoundedButton.Style.PRIMARY);
        btnAdd.addActionListener(e -> addStudent());

        RoundedButton btnRemove = new RoundedButton("✕ Remove Selected", RoundedButton.Style.DANGER);
        btnRemove.addActionListener(e -> removeSelected());

        btns.add(btnRemove);
        btns.add(btnAdd);
        card.add(btns, BorderLayout.SOUTH);

        return card;
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void addStudent() {
        String name   = tfName.getText().trim();
        String roll   = tfRoll.getText().trim();
        String course = tfCourse.getText().trim();

        if (name.isEmpty() || roll.isEmpty() || course.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }
        Student s = studentService.addStudent(name, roll, course);
        if (s == null) {
            showError("A student with roll number '" + roll + "' already exists.");
            return;
        }
        tfName.setText(""); tfRoll.setText(""); tfCourse.setText("");
        refreshTable(studentService.getAllStudents());
        showSuccess("Student '" + name + "' added successfully.");
    }

    private void removeSelected() {
        int row = table.getSelectedRow();
        if (row == -1) { showError("Select a student to remove."); return; }
        String id   = (String) tableModel.getValueAt(row, 3);
        String name = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Remove '" + name + "' and all their attendance records?",
            "Confirm Removal", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            attendanceService.removeRecordsForStudent(id);
            studentService.removeStudent(id);
            refreshTable(studentService.getAllStudents());
            showSuccess("Student removed.");
        }
    }

    private void filterTable() {
        String q = tfSearch.getText().trim().toLowerCase();
        List<Student> all = studentService.getAllStudents();
        if (q.isEmpty()) { refreshTable(all); return; }
        List<Student> filtered = all.stream()
            .filter(s -> s.getName().toLowerCase().contains(q)
                      || s.getRollNumber().toLowerCase().contains(q))
            .toList();
        refreshTable(filtered);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    void refreshTable(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student s : students) {
            tableModel.addRow(new Object[]{s.getRollNumber(), s.getName(), s.getCourse(), s.getId()});
        }
    }

    private JPanel labeled(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.TEXT_MUTED);
        p.add(lbl,   BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JTextField styledField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setBackground(UITheme.BG_DARK);
        tf.setForeground(UITheme.TEXT_PRIMARY);
        tf.setCaretColor(UITheme.ACCENT);
        tf.setFont(UITheme.FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        tf.putClientProperty("placeholder", placeholder);
        return tf;
    }

    private void styleTable(JTable t) {
        t.setBackground(UITheme.BG_PANEL);
        t.setForeground(UITheme.TEXT_PRIMARY);
        t.setFont(UITheme.FONT_BODY);
        t.setRowHeight(32);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(new Color(251, 191, 36, 60));
        t.setSelectionForeground(UITheme.TEXT_PRIMARY);
        t.getTableHeader().setBackground(UITheme.BG_SIDEBAR);
        t.getTableHeader().setForeground(UITheme.TEXT_MUTED);
        t.getTableHeader().setFont(UITheme.FONT_SMALL);
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
