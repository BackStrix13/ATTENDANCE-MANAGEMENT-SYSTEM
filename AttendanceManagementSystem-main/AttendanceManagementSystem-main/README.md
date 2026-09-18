# 📋 Attendance Management System

A desktop application for managing student attendance, built with **Java Swing** and **CSV file storage**. Designed as a clean, modern GUI tool that lets teachers or administrators register students, mark daily attendance, and view reports all without any external database.

---

## 🖥️ Screenshots

| Students | Mark Attendance | Reports |
|----------|----------------|---------|
| Add, remove, and search registered students | Checkbox-based daily attendance marking | Summary table with % and per-student detail |

---

## ✨ Features

- **Student Management** : Add students with name, roll number, and course; remove them with a single click; search by name or roll number in real-time
- **Mark Attendance** : Load any date, check/uncheck present students, save with one click; supports re-editing past dates
- **Attendance Reports** : Overall summary table showing present days, total days, and attendance percentage per student; per-student detail view with date-wise history
- **Persistent CSV Storage** : All data is saved to plain-text CSV files in a `data/` folder — no database setup required
- **Modern Dark UI** : Deep-navy + amber theme built entirely with Java Swing; no third-party UI libraries needed

---

## 🗂️ Project Structure

```
AttendanceManagementSystem/
├── src/
│   └── attendance/
│       ├── Main.java                  # Application entry point
│       ├── model/
│       │   ├── Student.java           # Student entity with CSV serialization
│       │   └── AttendanceRecord.java  # Attendance record entity
│       ├── service/
│       │   ├── StudentService.java    # CRUD operations for students
│       │   └── AttendanceService.java # Attendance marking & querying
│       ├── util/
│       │   └── CsvUtil.java           # CSV read / write / append helpers
│       └── ui/
│           ├── UITheme.java           # Centralised colours & fonts
│           ├── CardPanel.java         # Rounded card panel component
│           ├── RoundedButton.java     # Custom animated button component
│           ├── MainWindow.java        # Main window with sidebar navigation
│           ├── StudentPanel.java      # Student management panel
│           ├── AttendancePanel.java   # Attendance marking panel
│           └── ReportPanel.java       # Reports and analytics panel
├── data/                              # Auto-created on first run
│   ├── students.csv                   # Persisted student records
│   └── attendance.csv                 # Persisted attendance records
├── run.bat                            # Windows run script
├── run.sh                             # Linux / macOS run script
└── README.md
```

---

## ⚙️ Prerequisites

| Requirement | Version |
|-------------|---------|
| Java JDK    | 17 or higher |
| OS          | Windows / Linux / macOS |

Check your Java version:
```bash
java -version
```

Download JDK from: https://adoptium.net or https://www.oracle.com/java/technologies/downloads/

---

## 🚀 Getting Started

### Option 1 : Use the run scripts (easiest)

**Windows:**
```bat
run.bat
```

**Linux / macOS:**
```bash
chmod +x run.sh
./run.sh
```

---

### Option 2 : Compile and run manually

**Step 1 — Compile**

On Windows:
```bat
mkdir out
for /r src %%f in (*.java) do javac -d out "%%f"
```

On Linux / macOS:
```bash
find src -name "*.java" > sources.txt
mkdir -p out
javac -d out @sources.txt
```

**Step 2 — Run**
```bash
java -cp out attendance.Main
```

> ⚠️ Run from the project root directory so that the `data/` folder is created in the right place.

---

## 📖 How to Use

### 1. Students Tab
- Fill in **Name**, **Roll Number**, and **Course**, then click **＋ Add Student**
- Use the search bar to filter students by name or roll number
- Select a row and click **✕ Remove Selected** to delete a student (also removes all their attendance records)

### 2. Mark Attendance Tab
- The current date is pre-filled — change it if marking for a past date (format: `yyyy-MM-dd`)
- Click **Load** to populate the student list for that date
- Check the **Present** checkbox for each student who attended
- Use **✔ Mark All Present** or **✘ Mark All Absent** to quickly fill the list
- Click **💾 Save Attendance** to persist the records

### 3. Reports Tab
- The **Overall Summary** table shows total days, present days, and attendance % for every student
- Attendance % is shown in **green** if ≥ 75%, and **red** if below
- Select a student from the dropdown to see their date-wise attendance in the **Student Detail** view
- Click **⟳ Refresh** to reload the latest data

---

## 💾 Data Format

**data/students.csv**
```
id,name,rollNumber,course
STU1712345678,Alice Johnson,CS101,Computer Science
STU1712345999,Bob Smith,CS102,Computer Science
```

**data/attendance.csv**
```
studentId,date,status
STU1712345678,2024-03-15,PRESENT
STU1712345999,2024-03-15,ABSENT
```

Both files are human-readable and can be edited manually if needed.

---

## 🎨 UI Design

The application uses a custom **deep-navy + amber** theme implemented entirely in Java Swing — no external libraries. Key design components:

- `UITheme.java` — centralised colour palette and font constants
- `RoundedButton.java` — custom `JButton` subclass with hover animation
- `CardPanel.java` — rounded `JPanel` used as content cards
- Dark background (`#0F1726`) with amber accent (`#FBBF24`) for active elements
- Green (`#34D399`) for present / success, Red (`#F87171`) for absent / danger

---

## 🛠️ Technologies Used

| Technology | Purpose |
|------------|---------|
| Java 17+   | Core language |
| Java Swing | Desktop GUI framework |
| Java I/O   | CSV file read/write |
| Java Time  | `LocalDate` for date handling |
| OOP Design | Model-Service-UI layered architecture |

---

## 📚 Concepts Demonstrated

- **Object-Oriented Programming** — encapsulated model classes, service layer separation, custom Swing component inheritance
- **File I/O** — reading and writing structured CSV data using `BufferedReader` / `PrintWriter`
- **Collections & Streams** — `List`, `Map`, `Set`, Java Stream API for filtering and aggregation
- **Event-Driven Programming** — ActionListeners, DocumentListeners, MouseAdapters for UI interaction
- **MVC-inspired Architecture** — model, service, and UI layers kept cleanly separated

---

## 🤝 Contributing

This project was built as a BYOP (Bring Your Own Project) capstone submission. Feel free to fork it and extend it with features like:
- Export to PDF
- Multi-class / subject support
- Login system
- Chart-based attendance visualisation

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
