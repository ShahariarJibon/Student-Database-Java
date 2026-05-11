import java.io.Serializable;

public enum SortOption implements Serializable {
    ID_ASC("Student ID"),
    NAME_ASC("Name"),
    DISCIPLINE_ASC("Discipline"),
    SEMESTER_ASC("Semester"),
    CGPA_DESC("CGPA High to Low"),
    CREDITS_DESC("Credits High to Low"),
    STATUS_ASC("Status");

    private final String label;

    SortOption(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
