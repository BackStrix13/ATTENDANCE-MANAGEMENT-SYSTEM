package attendance.model;

import java.time.LocalDate;

/**
 * Represents a single attendance record for a student on a specific date.
 */
public class AttendanceRecord {
    public enum Status { PRESENT, ABSENT }

    private String studentId;
    private LocalDate date;
    private Status status;

    public AttendanceRecord(String studentId, LocalDate date, Status status) {
        this.studentId = studentId;
        this.date = date;
        this.status = status;
    }

    public String    getStudentId() { return studentId; }
    public LocalDate getDate()      { return date; }
    public Status    getStatus()    { return status; }
    public void      setStatus(Status status) { this.status = status; }

    /** CSV format: studentId,date,status */
    public String toCsv() {
        return studentId + "," + date.toString() + "," + status.name();
    }

    public static AttendanceRecord fromCsv(String line) {
        String[] parts = line.split(",", 3);
        if (parts.length < 3) return null;
        try {
            return new AttendanceRecord(
                parts[0].trim(),
                LocalDate.parse(parts[1].trim()),
                Status.valueOf(parts[2].trim())
            );
        } catch (Exception e) {
            return null;
        }
    }
}
