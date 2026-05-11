import java.util.Arrays;
import java.util.regex.Pattern;

public final class StudentValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^$|^\\d{4}-\\d{2}-\\d{2}$");

    private StudentValidator() {}

    public static void validate(Student student) throws InvalidInputException {
        if (isBlank(student.getStudentId())) {
            throw new InvalidInputException("Student ID is required.");
        }
        if (!student.getStudentId().matches("[A-Za-z0-9-]{3,20}")) {
            throw new InvalidInputException("Student ID must be 3-20 letters, digits, or hyphens.");
        }
        if (isBlank(student.getName())) {
            throw new InvalidInputException("Name is required.");
        }
        if (isBlank(student.getEmail()) || !EMAIL_PATTERN.matcher(student.getEmail()).matches()) {
            throw new InvalidInputException("A valid email address is required.");
        }
        if (!Arrays.asList(Student.DISCIPLINES).contains(student.getDiscipline())) {
            throw new InvalidInputException("Choose a valid discipline.");
        }
        if (!Arrays.asList(Student.STATUSES).contains(student.getStatus())) {
            throw new InvalidInputException("Choose a valid student status.");
        }
        if (student.getSemester() < 1 || student.getSemester() > 12) {
            throw new InvalidInputException("Semester must be between 1 and 12.");
        }
        if (student.getCgpa() < 0.0 || student.getCgpa() > 4.0) {
            throw new InvalidInputException("CGPA must be between 0.00 and 4.00.");
        }
        if (student.getCompletedCredits() < 0 || student.getCompletedCredits() > 260) {
            throw new InvalidInputException("Completed credits must be between 0 and 260.");
        }
        if (!DATE_PATTERN.matcher(nullToEmpty(student.getAdmissionDate())).matches()
                || !DATE_PATTERN.matcher(nullToEmpty(student.getDateOfBirth())).matches()) {
            throw new InvalidInputException("Dates must use YYYY-MM-DD format.");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
