import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class StudentStatistics implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int totalStudents;
    private final int activeStudents;
    private final double averageCgpa;
    private final int totalCredits;
    private final Map<String, Integer> disciplineCounts;
    private final Map<String, Integer> statusCounts;

    public StudentStatistics(int totalStudents, int activeStudents, double averageCgpa,
            int totalCredits, Map<String, Integer> disciplineCounts, Map<String, Integer> statusCounts) {
        this.totalStudents = totalStudents;
        this.activeStudents = activeStudents;
        this.averageCgpa = averageCgpa;
        this.totalCredits = totalCredits;
        this.disciplineCounts = new LinkedHashMap<>(disciplineCounts);
        this.statusCounts = new LinkedHashMap<>(statusCounts);
    }

    public int getTotalStudents() { return totalStudents; }

    public int getActiveStudents() { return activeStudents; }

    public double getAverageCgpa() { return averageCgpa; }

    public int getTotalCredits() { return totalCredits; }

    public Map<String, Integer> getDisciplineCounts() {
        return Collections.unmodifiableMap(disciplineCounts);
    }

    public Map<String, Integer> getStatusCounts() {
        return Collections.unmodifiableMap(statusCounts);
    }
}
