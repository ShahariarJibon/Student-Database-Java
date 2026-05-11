import java.io.Serializable;

/**
 * Student class inheriting from Person.
 * Demonstrates inheritance.
 * 
 * @author JIBON
 * @version 1.0
 */
public class Student extends Person implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String studentId;
    private String discipline;
    private String email;
    private int semester;
    private double cgpa;
    private int completedCredits;
    private String status;
    private String admissionDate;
    private String dateOfBirth;
    private String guardianName;
    private String emergencyContact;
    private String bloodGroup;
    private String notes;
    
    /**
     * Available disciplines.
     */
    public static final String[] DISCIPLINES = {
            "CSE", "ECE", "EEE", "BBA", "Mathematics", "Physics",
            "URP", "Architecture", "Civil Engineering", "Mechanical Engineering"
    };

    public static final String[] STATUSES = {
            "Active", "Probation", "Graduated", "Suspended", "Transferred", "Archived"
    };

    public static final String[] BLOOD_GROUPS = {
            "", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
    };
    
    /**
     * Default constructor.
     */
    public Student() {
        super();
        this.semester = 1;
        this.cgpa = 0.0;
        this.completedCredits = 0;
        this.status = "Active";
        this.admissionDate = "";
        this.dateOfBirth = "";
        this.guardianName = "";
        this.emergencyContact = "";
        this.bloodGroup = "";
        this.notes = "";
    }
    
    /**
     * Parameterized constructor.
     * @param studentId Student ID
     * @param name Name
     * @param discipline Discipline
     * @param email Email
     * @param phoneNumber Phone number
     * @param address Address
     */
    public Student(String studentId, String name, String discipline,
               String email, String phoneNumber, String address) {
        this(studentId, name, discipline, email, phoneNumber, address,
                1, 0.0, 0, "Active", "", "", "", "", "", "");
    }

    public Student(String studentId, String name, String discipline,
               String email, String phoneNumber, String address,
               int semester, double cgpa, int completedCredits, String status,
               String admissionDate, String dateOfBirth, String guardianName,
               String emergencyContact, String bloodGroup, String notes) {
        super(name, phoneNumber, address);
        this.studentId = studentId;
        this.discipline = discipline;
        this.email = email;
        this.semester = semester;
        this.cgpa = cgpa;
        this.completedCredits = completedCredits;
        this.status = status;
        this.admissionDate = admissionDate;
        this.dateOfBirth = dateOfBirth;
        this.guardianName = guardianName;
        this.emergencyContact = emergencyContact;
        this.bloodGroup = bloodGroup;
        this.notes = notes;
    }
    
    /**
     * Gets the student ID.
     * @return Student ID
     */
    public String getStudentId() { return studentId == null ? "" : studentId; }
    
    /**
     * Sets the student ID.
     * @param studentId Student ID to set
     */
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    /**
     * Gets the discipline.
     * @return Discipline
     */
    public String getDiscipline() {
        return discipline == null || discipline.trim().isEmpty() ? DISCIPLINES[0] : discipline;
    }
    
    /**
     * Sets the discipline.
     * @param discipline Discipline to set
     */
    public void setDiscipline(String discipline) { this.discipline = discipline; }
    
    /**
     * Gets the email.
     * @return Email
     */
    public String getEmail() { return email == null ? "" : email; }
    
    /**
     * Sets the email.
     * @param email Email to set
     */
    public void setEmail(String email) { this.email = email; }

    public int getSemester() { return semester <= 0 ? 1 : semester; }

    public void setSemester(int semester) { this.semester = semester; }

    public double getCgpa() { return cgpa; }

    public void setCgpa(double cgpa) { this.cgpa = cgpa; }

    public int getCompletedCredits() { return Math.max(0, completedCredits); }

    public void setCompletedCredits(int completedCredits) { this.completedCredits = completedCredits; }

    public String getStatus() {
        return status == null || status.trim().isEmpty() ? STATUSES[0] : status;
    }

    public void setStatus(String status) { this.status = status; }

    public String getAdmissionDate() { return admissionDate == null ? "" : admissionDate; }

    public void setAdmissionDate(String admissionDate) { this.admissionDate = admissionDate; }

    public String getDateOfBirth() { return dateOfBirth == null ? "" : dateOfBirth; }

    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGuardianName() { return guardianName == null ? "" : guardianName; }

    public void setGuardianName(String guardianName) { this.guardianName = guardianName; }

    public String getEmergencyContact() { return emergencyContact == null ? "" : emergencyContact; }

    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getBloodGroup() { return bloodGroup == null ? "" : bloodGroup; }

    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getNotes() { return notes == null ? "" : notes; }

    public void setNotes(String notes) { this.notes = notes; }

    public String getAcademicStanding() {
        if (cgpa >= 3.75) {
            return "Dean's List";
        }
        if (cgpa >= 3.00) {
            return "Good Standing";
        }
        if (cgpa >= 2.00) {
            return "Watch";
        }
        return "At Risk";
    }
    
    /**
     * Overridden toString method.
     * Demonstrates polymorphism.
     * @return String representation
     */
    @Override
    public String toString() {
        return "Student ID: " + studentId + "\nName: " + getName() +
               "\nDiscipline: " + discipline + "\nEmail: " + email +
               "\nPhone: " + getPhoneNumber() + "\nAddress: " + getAddress() +
               "\nSemester: " + semester + "\nCGPA: " + cgpa +
               "\nCredits: " + completedCredits + "\nStatus: " + status;
    }
    
    /**
     * Gets details for file storage.
     * @return Formatted string
     */
    public String getDetailsForFile() {
        return studentId + "," + getName() + "," + discipline + "," +
               email + "," + getPhoneNumber() + "," + getAddress() + "," +
               semester + "," + cgpa + "," + completedCredits + "," + status + "," +
               admissionDate + "," + dateOfBirth + "," + guardianName + "," +
               emergencyContact + "," + bloodGroup + "," + notes;
    }
}
