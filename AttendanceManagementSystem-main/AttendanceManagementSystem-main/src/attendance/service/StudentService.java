package attendance.service;

import attendance.model.Student;
import attendance.util.CsvUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing student records backed by a CSV file.
 */
public class StudentService {
    private static final String FILE_PATH = "data/students.csv";
    private final Map<String, Student> students = new LinkedHashMap<>();

    public StudentService() {
        CsvUtil.ensureFileExists(FILE_PATH);
        load();
    }

    // ── Load / Save ─────────────────────────────────────────────────────────

    private void load() {
        students.clear();
        for (String line : CsvUtil.readLines(FILE_PATH)) {
            Student s = Student.fromCsv(line);
            if (s != null) students.put(s.getId(), s);
        }
    }

    private void save() {
        List<String> lines = students.values().stream()
            .map(Student::toCsv)
            .collect(Collectors.toList());
        CsvUtil.writeLines(FILE_PATH, lines);
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    /**
     * Adds a new student. Generates a unique ID automatically.
     * Returns null if a student with the same roll number already exists.
     */
    public Student addStudent(String name, String rollNumber, String course) {
        for (Student s : students.values()) {
            if (s.getRollNumber().equalsIgnoreCase(rollNumber)) return null;
        }
        String id = "STU" + System.currentTimeMillis();
        Student student = new Student(id, name, rollNumber, course);
        students.put(id, student);
        save();
        return student;
    }

    /**
     * Removes a student by ID. Returns true if found and removed.
     */
    public boolean removeStudent(String id) {
        if (students.remove(id) != null) {
            save();
            return true;
        }
        return false;
    }

    public Student getStudentById(String id) {
        return students.get(id);
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }

    public boolean hasStudents() {
        return !students.isEmpty();
    }
}
