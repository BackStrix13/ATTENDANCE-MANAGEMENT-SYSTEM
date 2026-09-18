package attendance.model;

/**
 * Represents a Student in the Attendance Management System.
 */
public class Student {
    private String id;
    private String name;
    private String rollNumber;
    private String course;

    public Student(String id, String name, String rollNumber, String course) {
        this.id = id;
        this.name = name;
        this.rollNumber = rollNumber;
        this.course = course;
    }

    public String getId()         { return id; }
    public String getName()       { return name; }
    public String getRollNumber() { return rollNumber; }
    public String getCourse()     { return course; }

    public void setName(String name)             { this.name = name; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
    public void setCourse(String course)         { this.course = course; }

    /** CSV format: id,name,rollNumber,course */
    public String toCsv() {
        return id + "," + name + "," + rollNumber + "," + course;
    }

    public static Student fromCsv(String line) {
        String[] parts = line.split(",", 4);
        if (parts.length < 4) return null;
        return new Student(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim());
    }

    @Override
    public String toString() {
        return "[" + rollNumber + "] " + name + " — " + course;
    }
}
