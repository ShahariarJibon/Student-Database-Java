# Advanced Student Database System

A modern Java Swing student database application with dashboard navigation, paginated student records, analytics, validation, CSV import/export, and local binary/text persistence.

## Features

- Modern Swing UI with sidebar navigation
- Dashboard cards for total students, active records, average CGPA, and credits
- Paginated student list with search, filters, sorting, and page-size controls
- Advanced student profile fields including semester, CGPA, credits, status, guardian, emergency contact, blood group, and notes
- Student validation for ID, email, dates, CGPA, credits, status, and discipline
- Analytics view with discipline/status breakdowns and at-risk student reporting
- CSV import/export
- Binary and text persistence with automatic backups
- Demo dataset generator

## Requirements

- Java JDK 8 or newer

## Compile

```powershell
javac *.java
```

## Run

```powershell
java Main
```

## Main Files

- `Main.java` - application entry point
- `MainGUI.java` - modern Swing user interface
- `Student.java` - student model
- `StudentManager.java` - indexed database operations
- `FileHandler.java` - persistence and CSV import/export
- `StudentValidator.java` - validation rules
- `StudentStatistics.java` - analytics data model
- `StudentSearchCriteria.java` - search/filter criteria
- `SortOption.java` - student sorting options
"# Student-Database-Java" 
