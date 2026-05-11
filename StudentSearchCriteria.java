import java.io.Serializable;

public class StudentSearchCriteria implements Serializable {
    private static final long serialVersionUID = 1L;

    private String query;
    private String discipline;
    private String status;
    private int semester;
    private double minCgpa;
    private double maxCgpa;
    private SortOption sortOption;

    public StudentSearchCriteria() {
        query = "";
        discipline = "All";
        status = "All";
        semester = 0;
        minCgpa = 0.0;
        maxCgpa = 4.0;
        sortOption = SortOption.ID_ASC;
    }

    public String getQuery() { return query; }

    public void setQuery(String query) { this.query = query == null ? "" : query.trim(); }

    public String getDiscipline() { return discipline; }

    public void setDiscipline(String discipline) { this.discipline = discipline == null ? "All" : discipline; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status == null ? "All" : status; }

    public int getSemester() { return semester; }

    public void setSemester(int semester) { this.semester = semester; }

    public double getMinCgpa() { return minCgpa; }

    public void setMinCgpa(double minCgpa) { this.minCgpa = minCgpa; }

    public double getMaxCgpa() { return maxCgpa; }

    public void setMaxCgpa(double maxCgpa) { this.maxCgpa = maxCgpa; }

    public SortOption getSortOption() { return sortOption; }

    public void setSortOption(SortOption sortOption) {
        this.sortOption = sortOption == null ? SortOption.ID_ASC : sortOption;
    }
}
