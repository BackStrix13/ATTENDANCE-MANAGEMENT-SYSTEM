package attendance.util;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Utility class for reading and writing CSV files.
 */
public class CsvUtil {

    /**
     * Reads all non-empty, non-comment lines from a CSV file.
     * Returns an empty list if the file doesn't exist.
     */
    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        File f = new File(filePath);
        if (!f.exists()) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath + " — " + e.getMessage());
        }
        return lines;
    }

    /**
     * Writes all lines to a CSV file, overwriting existing content.
     */
    public static void writeLines(String filePath, List<String> lines) {
        try {
            Files.createDirectories(Paths.get(filePath).getParent());
            try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, false))) {
                for (String line : lines) {
                    pw.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing file: " + filePath + " — " + e.getMessage());
        }
    }

    /**
     * Appends a single line to a CSV file.
     */
    public static void appendLine(String filePath, String line) {
        try {
            Files.createDirectories(Paths.get(filePath).getParent());
            try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, true))) {
                pw.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error appending to file: " + filePath + " — " + e.getMessage());
        }
    }

    /**
     * Ensures the data directory and file exist.
     */
    public static void ensureFileExists(String filePath) {
        try {
            Path p = Paths.get(filePath);
            Files.createDirectories(p.getParent());
            if (!Files.exists(p)) Files.createFile(p);
        } catch (IOException e) {
            System.err.println("Could not create file: " + filePath);
        }
    }
}
