import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Indexed manager for student database operations.
 */
public class StudentManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Student> studentsById;
    private List<Student> students;

    public StudentManager() {
        studentsById = new LinkedHashMap<>();
    }

    public void addStudent(Student student) throws InvalidInputException {
        StudentValidator.validate(student);
        String key = normalizeId(student.getStudentId());
        if (studentsById.containsKey(key)) {
            throw new InvalidInputException("Student ID already exists.");
        }
        studentsById.put(key, student);
    }

    public int importStudents(Collection<Student> students, boolean replaceExisting) throws InvalidInputException {
        int imported = 0;
        for (Student student : students) {
            StudentValidator.validate(student);
            String key = normalizeId(student.getStudentId());
            if (!studentsById.containsKey(key) || replaceExisting) {
                studentsById.put(key, student);
                imported++;
            }
        }
        return imported;
    }

    public boolean removeStudent(String studentId) {
        return studentsById.remove(normalizeId(studentId)) != null;
    }

    public Student searchById(String studentId) {
        return studentsById.get(normalizeId(studentId));
    }

    public List<Student> searchByName(String name) {
        String query = safeLower(name);
        return studentsById.values().stream()
                .filter(s -> safeLower(s.getName()).contains(query))
                .sorted(Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public boolean updateStudent(String studentId, Student updatedStudent) throws InvalidInputException {
        String key = normalizeId(studentId);
        if (!studentsById.containsKey(key)) {
            return false;
        }
        updatedStudent.setStudentId(studentId);
        StudentValidator.validate(updatedStudent);
        studentsById.put(key, updatedStudent);
        return true;
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(studentsById.values());
    }

    public int getSize() {
        return studentsById.size();
    }

    public void setStudents(List<Student> students) {
        studentsById = new LinkedHashMap<>();
        for (Student student : students) {
            if (student != null && student.getStudentId() != null) {
                studentsById.put(normalizeId(student.getStudentId()), student);
            }
        }
        this.students = null;
    }

    public List<Student> filterByDiscipline(String discipline) {
        StudentSearchCriteria criteria = new StudentSearchCriteria();
        criteria.setDiscipline(discipline);
        return search(criteria);
    }

    public List<Student> search(StudentSearchCriteria criteria) {
        StudentSearchCriteria activeCriteria = criteria == null ? new StudentSearchCriteria() : criteria;
        String query = safeLower(activeCriteria.getQuery());

        return studentsById.values().stream()
                .filter(s -> matchesQuery(s, query))
                .filter(s -> matchesChoice(s.getDiscipline(), activeCriteria.getDiscipline()))
                .filter(s -> matchesChoice(s.getStatus(), activeCriteria.getStatus()))
                .filter(s -> activeCriteria.getSemester() == 0 || s.getSemester() == activeCriteria.getSemester())
                .filter(s -> s.getCgpa() >= activeCriteria.getMinCgpa() && s.getCgpa() <= activeCriteria.getMaxCgpa())
                .sorted(comparatorFor(activeCriteria.getSortOption()))
                .collect(Collectors.toList());
    }

    public List<Student> getAtRiskStudents() {
        return studentsById.values().stream()
                .filter(s -> s.getCgpa() < 2.0 || "Probation".equalsIgnoreCase(s.getStatus()))
                .sorted(Comparator.comparingDouble(Student::getCgpa))
                .collect(Collectors.toList());
    }

    public StudentStatistics getStatistics() {
        int total = studentsById.size();
        int active = 0;
        int credits = 0;
        double cgpaSum = 0.0;
        Map<String, Integer> disciplineCounts = new LinkedHashMap<>();
        Map<String, Integer> statusCounts = new LinkedHashMap<>();

        for (Student student : studentsById.values()) {
            if ("Active".equalsIgnoreCase(student.getStatus())) {
                active++;
            }
            credits += student.getCompletedCredits();
            cgpaSum += student.getCgpa();
            increment(disciplineCounts, student.getDiscipline());
            increment(statusCounts, student.getStatus());
        }

        double average = total == 0 ? 0.0 : cgpaSum / total;
        return new StudentStatistics(total, active, average, credits, disciplineCounts, statusCounts);
    }

    private boolean matchesQuery(Student student, String query) {
        if (query.isEmpty()) {
            return true;
        }
        return safeLower(student.getStudentId()).contains(query)
                || safeLower(student.getName()).contains(query)
                || safeLower(student.getEmail()).contains(query)
                || safeLower(student.getPhoneNumber()).contains(query)
                || safeLower(student.getGuardianName()).contains(query);
    }

    private boolean matchesChoice(String actual, String selected) {
        return selected == null || selected.equals("All") || selected.equalsIgnoreCase(actual);
    }

    private Comparator<Student> comparatorFor(SortOption option) {
        SortOption sortOption = option == null ? SortOption.ID_ASC : option;
        switch (sortOption) {
            case NAME_ASC:
                return Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER);
            case DISCIPLINE_ASC:
                return Comparator.comparing(Student::getDiscipline, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Student::getName, String.CASE_INSENSITIVE_ORDER);
            case SEMESTER_ASC:
                return Comparator.comparingInt(Student::getSemester)
                        .thenComparing(Student::getName, String.CASE_INSENSITIVE_ORDER);
            case CGPA_DESC:
                return Comparator.comparingDouble(Student::getCgpa).reversed()
                        .thenComparing(Student::getName, String.CASE_INSENSITIVE_ORDER);
            case CREDITS_DESC:
                return Comparator.comparingInt(Student::getCompletedCredits).reversed()
                        .thenComparing(Student::getName, String.CASE_INSENSITIVE_ORDER);
            case STATUS_ASC:
                return Comparator.comparing(Student::getStatus, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Student::getName, String.CASE_INSENSITIVE_ORDER);
            case ID_ASC:
            default:
                return Comparator.comparing(Student::getStudentId, String.CASE_INSENSITIVE_ORDER);
        }
    }

    private void increment(Map<String, Integer> counts, String key) {
        String safeKey = key == null || key.trim().isEmpty() ? "Unknown" : key;
        counts.put(safeKey, counts.getOrDefault(safeKey, 0) + 1);
    }

    private String normalizeId(String studentId) {
        return studentId == null ? "" : studentId.trim().toLowerCase(Locale.ROOT);
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();
        if (studentsById == null) {
            studentsById = new LinkedHashMap<>();
        }
        if (students != null && !students.isEmpty()) {
            setStudents(students);
        }
    }
}
