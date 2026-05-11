import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final String BINARY_FILE = "students.dat";
    private static final String TEXT_FILE = "students.txt";
    private static final String BACKUP_DIR = "backups";

    private static final String[] CSV_HEADER = {
            "StudentID", "Name", "Discipline", "Email", "Phone", "Address",
            "Semester", "CGPA", "Credits", "Status", "AdmissionDate", "DateOfBirth",
            "Guardian", "EmergencyContact", "BloodGroup", "Notes"
    };

    public boolean saveToBinary(StudentManager manager) {
        createBackup(BINARY_FILE);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BINARY_FILE))) {
            oos.writeObject(manager);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving binary: " + e.getMessage());
            return false;
        }
    }

    public StudentManager loadFromBinary() {
        File file = new File(BINARY_FILE);
        if (!file.exists()) {
            return loadFromText();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (StudentManager) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return loadFromText();
        }
    }

    public boolean saveToText(StudentManager manager) {
        return exportToCsv(manager, new File(TEXT_FILE));
    }

    public StudentManager loadFromText() {
        StudentManager manager = new StudentManager();
        File file = new File(TEXT_FILE);
        if (!file.exists()) {
            return manager;
        }
        try {
            manager.setStudents(readStudentsFromCsv(file));
        } catch (IOException e) {
            System.err.println("Error loading text: " + e.getMessage());
        }
        return manager;
    }

    public boolean exportToCsv(StudentManager manager, File file) {
        createBackup(file.getPath());
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            writer.println(String.join(",", CSV_HEADER));
            for (Student s : manager.getAllStudents()) {
                writer.println(toCsvLine(s));
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting CSV: " + e.getMessage());
            return false;
        }
    }

    public List<Student> importFromCsv(File file) throws IOException {
        return readStudentsFromCsv(file);
    }

    private List<Student> readStudentsFromCsv(File file) throws IOException {
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                List<String> values = parseCsv(line);
                if (values.size() >= 6) {
                    students.add(studentFromValues(values));
                }
            }
        }
        return students;
    }

    private Student studentFromValues(List<String> values) {
        return new Student(
                valueAt(values, 0),
                valueAt(values, 1),
                defaultValue(valueAt(values, 2), Student.DISCIPLINES[0]),
                valueAt(values, 3),
                valueAt(values, 4),
                valueAt(values, 5),
                parseInt(valueAt(values, 6), 1),
                parseDouble(valueAt(values, 7), 0.0),
                parseInt(valueAt(values, 8), 0),
                defaultValue(valueAt(values, 9), Student.STATUSES[0]),
                valueAt(values, 10),
                valueAt(values, 11),
                valueAt(values, 12),
                valueAt(values, 13),
                valueAt(values, 14),
                valueAt(values, 15)
        );
    }

    private String toCsvLine(Student s) {
        String[] values = {
                s.getStudentId(), s.getName(), s.getDiscipline(), s.getEmail(),
                s.getPhoneNumber(), s.getAddress(), String.valueOf(s.getSemester()),
                String.format("%.2f", s.getCgpa()), String.valueOf(s.getCompletedCredits()),
                s.getStatus(), s.getAdmissionDate(), s.getDateOfBirth(),
                s.getGuardianName(), s.getEmergencyContact(), s.getBloodGroup(), s.getNotes()
        };
        List<String> escaped = new ArrayList<>();
        for (String value : values) {
            escaped.add(escapeCsv(value));
        }
        return String.join(",", escaped);
    }

    private List<String> parseCsv(String line) {
        // Small CSV parser so names, addresses, and notes can safely contain commas.
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (ch == ',' && !quoted) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        values.add(current.toString());
        return values;
    }

    private String escapeCsv(String value) {
        String safe = value == null ? "" : value;
        if (safe.contains(",") || safe.contains("\"") || safe.contains("\n")) {
            return "\"" + safe.replace("\"", "\"\"") + "\"";
        }
        return safe;
    }

    private void createBackup(String fileName) {
        File source = new File(fileName);
        if (!source.exists()) {
            return;
        }
        try {
            File backupDirectory = new File(BACKUP_DIR);
            if (!backupDirectory.exists()) {
                backupDirectory.mkdirs();
            }
            String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            File backup = new File(backupDirectory, source.getName() + "." + stamp + ".bak");
            Files.copy(source.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Backup skipped: " + e.getMessage());
        }
    }

    private String valueAt(List<String> values, int index) {
        return index < values.size() ? values.get(index).trim() : "";
    }

    private String defaultValue(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private double parseDouble(String value, double fallback) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
