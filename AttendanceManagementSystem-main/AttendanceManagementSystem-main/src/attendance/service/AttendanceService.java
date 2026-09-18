package attendance.service;

import attendance.model.AttendanceRecord;
import attendance.model.AttendanceRecord.Status;
import attendance.util.CsvUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing attendance records backed by a CSV file.
 */
public class AttendanceService {
    private static final String FILE_PATH = "data/attendance.csv";
    private final List<AttendanceRecord> records = new ArrayList<>();

    public AttendanceService() {
        CsvUtil.ensureFileExists(FILE_PATH);
        load();
    }

    // ── Load / Save ─────────────────────────────────────────────────────────

    private void load() {
        records.clear();
        for (String line : CsvUtil.readLines(FILE_PATH)) {
            AttendanceRecord r = AttendanceRecord.fromCsv(line);
            if (r != null) records.add(r);
        }
    }

    private void save() {
        List<String> lines = records.stream()
            .map(AttendanceRecord::toCsv)
            .collect(Collectors.toList());
        CsvUtil.writeLines(FILE_PATH, lines);
    }

    // ── Mark Attendance ──────────────────────────────────────────────────────

    /**
     * Marks or updates attendance for a student on a given date.
     */
    public void markAttendance(String studentId, LocalDate date, Status status) {
        // Update existing record if found
        for (AttendanceRecord r : records) {
            if (r.getStudentId().equals(studentId) && r.getDate().equals(date)) {
                r.setStatus(status);
                save();
                return;
            }
        }
        // Otherwise add new
        records.add(new AttendanceRecord(studentId, date, status));
        save();
    }

    /**
     * Bulk-mark attendance for a list of students on a date.
     * presentIds = set of student IDs marked present; all others = absent.
     */
    public void bulkMarkAttendance(List<String> allStudentIds, Set<String> presentIds, LocalDate date) {
        for (String id : allStudentIds) {
            Status status = presentIds.contains(id) ? Status.PRESENT : Status.ABSENT;
            markAttendance(id, date, status);
        }
    }

    // ── Queries ──────────────────────────────────────────────────────────────

    /** Returns all attendance records for a specific date. */
    public List<AttendanceRecord> getRecordsForDate(LocalDate date) {
        return records.stream()
            .filter(r -> r.getDate().equals(date))
            .collect(Collectors.toList());
    }

    /** Returns all attendance records for a specific student. */
    public List<AttendanceRecord> getRecordsForStudent(String studentId) {
        return records.stream()
            .filter(r -> r.getStudentId().equals(studentId))
            .sorted(Comparator.comparing(AttendanceRecord::getDate))
            .collect(Collectors.toList());
    }

    /** Returns all distinct dates attendance was recorded, sorted descending. */
    public List<LocalDate> getAllDates() {
        return records.stream()
            .map(AttendanceRecord::getDate)
            .distinct()
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());
    }

    /**
     * Returns a summary map: studentId → [totalPresent, totalDays].
     */
    public Map<String, int[]> getSummary(List<String> studentIds) {
        Map<String, int[]> summary = new LinkedHashMap<>();
        for (String id : studentIds) {
            List<AttendanceRecord> studentRecords = getRecordsForStudent(id);
            int total   = studentRecords.size();
            int present = (int) studentRecords.stream()
                .filter(r -> r.getStatus() == Status.PRESENT).count();
            summary.put(id, new int[]{present, total});
        }
        return summary;
    }

    /**
     * Checks if attendance was already marked for a given date.
     */
    public boolean isAttendanceMarkedForDate(LocalDate date) {
        return records.stream().anyMatch(r -> r.getDate().equals(date));
    }

    /**
     * Removes all attendance records for a given student ID (called on student removal).
     */
    public void removeRecordsForStudent(String studentId) {
        records.removeIf(r -> r.getStudentId().equals(studentId));
        save();
    }
}
