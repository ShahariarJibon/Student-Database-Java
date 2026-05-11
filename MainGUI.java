import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainGUI extends JFrame {
    private static final Color APP_BG = new Color(245, 247, 251);
    private static final Color SIDEBAR_BG = new Color(27, 37, 53);
    private static final Color SIDEBAR_ACTIVE = new Color(59, 130, 246);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT = new Color(30, 41, 59);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color SUCCESS = new Color(22, 163, 74);
    private static final Color WARNING = new Color(217, 119, 6);

    private final StudentManager studentManager;
    private final FileHandler fileHandler;

    private CardLayout cardLayout;
    private JPanel cardsPanel;
    private JLabel titleLabel;
    private JLabel statusLabel;
    private JButton dashboardNav;
    private JButton studentsNav;
    private JButton analyticsNav;
    private JButton toolsNav;

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private JComboBox<String> disciplineFilter;
    private JComboBox<String> statusFilter;
    private JComboBox<String> semesterFilter;
    private JComboBox<SortOption> sortFilter;
    private JComboBox<Integer> pageSizeCombo;
    private JLabel pageInfoLabel;
    private List<Student> currentResults;
    private int currentPage;

    private JLabel totalValue;
    private JLabel activeValue;
    private JLabel cgpaValue;
    private JLabel creditsValue;
    private JPanel dashboardDistributionPanel;
    private JPanel dashboardRiskPanel;
    private JTextArea analyticsText;

    public MainGUI() {
        studentManager = new StudentManager();
        fileHandler = new FileHandler();
        currentResults = new ArrayList<>();
        currentPage = 1;

        configureWindow();
        initializeComponents();
        loadDataFromFiles();
        showView("Dashboard");
        setVisible(true);
    }

    private void configureWindow() {
        setTitle("Advanced Student Database System");
        setSize(1220, 780);
        setMinimumSize(new Dimension(1040, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(APP_BG);
    }

    private void initializeComponents() {
        add(createSidebar(), BorderLayout.WEST);
        add(createShell(), BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(235, 0));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setBorder(new EmptyBorder(22, 16, 22, 16));

        JPanel brand = new JPanel(new BorderLayout());
        brand.setOpaque(false);
        JLabel appName = new JLabel("StudentDB Pro");
        appName.setForeground(Color.WHITE);
        appName.setFont(new Font("Segoe UI", Font.BOLD, 23));
        JLabel appMeta = new JLabel("Academic records suite");
        appMeta.setForeground(new Color(148, 163, 184));
        appMeta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        brand.add(appName, BorderLayout.NORTH);
        brand.add(appMeta, BorderLayout.SOUTH);

        JPanel nav = new JPanel(new GridLayout(0, 1, 0, 10));
        nav.setOpaque(false);
        nav.setBorder(new EmptyBorder(28, 0, 0, 0));
        dashboardNav = navButton("Dashboard");
        studentsNav = navButton("Students");
        analyticsNav = navButton("Analytics");
        toolsNav = navButton("Tools");
        nav.add(dashboardNav);
        nav.add(studentsNav);
        nav.add(analyticsNav);
        nav.add(toolsNav);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        JLabel footerText = new JLabel("<html>Local Java database<br>Binary + CSV storage</html>");
        footerText.setForeground(new Color(148, 163, 184));
        footerText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.add(footerText, BorderLayout.SOUTH);

        sidebar.add(brand, BorderLayout.NORTH);
        sidebar.add(nav, BorderLayout.CENTER);
        sidebar.add(footer, BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel createShell() {
        JPanel shell = new JPanel(new BorderLayout(0, 18));
        shell.setBackground(APP_BG);
        shell.setBorder(new EmptyBorder(24, 26, 18, 26));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(TEXT);
        statusLabel = new JLabel("Ready");
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(MUTED);
        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(statusLabel, BorderLayout.EAST);

        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setOpaque(false);
        cardsPanel.add(createDashboardView(), "Dashboard");
        cardsPanel.add(createStudentsView(), "Students");
        cardsPanel.add(createAnalyticsView(), "Analytics");
        cardsPanel.add(createToolsView(), "Tools");

        shell.add(topBar, BorderLayout.NORTH);
        shell.add(cardsPanel, BorderLayout.CENTER);
        return shell;
    }

    private JPanel createDashboardView() {
        JPanel view = new JPanel(new BorderLayout(18, 18));
        view.setOpaque(false);

        JPanel statGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        statGrid.setOpaque(false);
        totalValue = new JLabel("0");
        activeValue = new JLabel("0");
        cgpaValue = new JLabel("0.00");
        creditsValue = new JLabel("0");
        statGrid.add(statCard("Total Students", totalValue, SIDEBAR_ACTIVE));
        statGrid.add(statCard("Active Records", activeValue, SUCCESS));
        statGrid.add(statCard("Average CGPA", cgpaValue, WARNING));
        statGrid.add(statCard("Completed Credits", creditsValue, new Color(124, 58, 237)));

        dashboardDistributionPanel = cardPanel(new BorderLayout());
        dashboardDistributionPanel.add(sectionTitle("Discipline Distribution"), BorderLayout.NORTH);

        dashboardRiskPanel = cardPanel(new BorderLayout());
        dashboardRiskPanel.add(sectionTitle("At-Risk Watchlist"), BorderLayout.NORTH);

        JPanel lower = new JPanel(new GridLayout(1, 2, 18, 0));
        lower.setOpaque(false);
        lower.add(dashboardDistributionPanel);
        lower.add(dashboardRiskPanel);

        view.add(statGrid, BorderLayout.NORTH);
        view.add(lower, BorderLayout.CENTER);
        return view;
    }

    private JPanel createStudentsView() {
        JPanel view = new JPanel(new BorderLayout(0, 14));
        view.setOpaque(false);

        JPanel actions = cardPanel(new BorderLayout(12, 12));
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filters.setOpaque(false);
        searchField = modernField(18);
        disciplineFilter = new JComboBox<>(withAll(Student.DISCIPLINES));
        statusFilter = new JComboBox<>(withAll(Student.STATUSES));
        semesterFilter = new JComboBox<>(semesterChoices());
        sortFilter = new JComboBox<>(SortOption.values());
        filters.add(label("Search"));
        filters.add(searchField);
        filters.add(label("Discipline"));
        filters.add(disciplineFilter);
        filters.add(label("Status"));
        filters.add(statusFilter);
        filters.add(label("Semester"));
        filters.add(semesterFilter);
        filters.add(label("Sort"));
        filters.add(sortFilter);

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        actionButtons.setOpaque(false);
        JButton addButton = primaryButton("Add Student");
        JButton editButton = secondaryButton("Edit");
        JButton deleteButton = dangerButton("Delete");
        JButton clearButton = secondaryButton("Clear Filters");
        actionButtons.add(addButton);
        actionButtons.add(editButton);
        actionButtons.add(deleteButton);
        actionButtons.add(clearButton);
        actions.add(filters, BorderLayout.CENTER);
        actions.add(actionButtons, BorderLayout.EAST);

        String[] columns = {
                "ID", "Name", "Discipline", "Semester", "CGPA",
                "Credits", "Status", "Email", "Phone", "Standing"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(38);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(TEXT);
        table.setGridColor(BORDER);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        centerColumn(3);
        centerColumn(4);
        centerColumn(5);
        centerColumn(6);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(BORDER));

        JPanel pagination = cardPanel(new BorderLayout());
        JPanel pageControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        pageControls.setOpaque(false);
        JButton firstButton = secondaryButton("First");
        JButton previousButton = secondaryButton("Previous");
        JButton nextButton = secondaryButton("Next");
        JButton lastButton = secondaryButton("Last");
        pageInfoLabel = new JLabel("Page 1 of 1");
        pageInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pageInfoLabel.setForeground(TEXT);
        pageSizeCombo = new JComboBox<>(new Integer[] {10, 20, 50, 100});
        pageControls.add(firstButton);
        pageControls.add(previousButton);
        pageControls.add(pageInfoLabel);
        pageControls.add(nextButton);
        pageControls.add(lastButton);
        pageControls.add(new JSeparator(SwingConstants.VERTICAL));
        pageControls.add(label("Rows"));
        pageControls.add(pageSizeCombo);
        pagination.add(pageControls, BorderLayout.WEST);

        view.add(actions, BorderLayout.NORTH);
        view.add(tableScroll, BorderLayout.CENTER);
        view.add(pagination, BorderLayout.SOUTH);

        searchField.addActionListener(e -> applyFilters());
        disciplineFilter.addActionListener(e -> applyFilters());
        statusFilter.addActionListener(e -> applyFilters());
        semesterFilter.addActionListener(e -> applyFilters());
        sortFilter.addActionListener(e -> applyFilters());
        pageSizeCombo.addActionListener(e -> {
            currentPage = 1;
            refreshTablePage();
        });
        addButton.addActionListener(e -> addStudent());
        editButton.addActionListener(e -> editSelectedStudent());
        deleteButton.addActionListener(e -> deleteSelectedStudent());
        clearButton.addActionListener(e -> clearFilters());
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                if (event.getClickCount() == 2) {
                    editSelectedStudent();
                }
            }
        });
        firstButton.addActionListener(e -> goToPage(1));
        previousButton.addActionListener(e -> goToPage(currentPage - 1));
        nextButton.addActionListener(e -> goToPage(currentPage + 1));
        lastButton.addActionListener(e -> goToPage(totalPages()));

        return view;
    }

    private JPanel createAnalyticsView() {
        JPanel view = new JPanel(new BorderLayout(0, 16));
        view.setOpaque(false);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        top.setOpaque(false);
        JButton refresh = primaryButton("Refresh Analytics");
        top.add(refresh);

        analyticsText = new JTextArea();
        analyticsText.setEditable(false);
        analyticsText.setFont(new Font("Consolas", Font.PLAIN, 14));
        analyticsText.setForeground(TEXT);
        analyticsText.setBackground(CARD_BG);
        analyticsText.setBorder(new EmptyBorder(16, 18, 16, 18));

        JScrollPane scroll = new JScrollPane(analyticsText);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        view.add(top, BorderLayout.NORTH);
        view.add(scroll, BorderLayout.CENTER);
        refresh.addActionListener(e -> refreshAnalytics());
        return view;
    }

    private JPanel createToolsView() {
        JPanel view = new JPanel(new GridLayout(1, 2, 18, 0));
        view.setOpaque(false);

        JPanel dataTools = cardPanel(new BorderLayout(0, 14));
        dataTools.add(sectionTitle("Data Operations"), BorderLayout.NORTH);
        JPanel dataButtons = new JPanel(new GridLayout(0, 1, 0, 12));
        dataButtons.setOpaque(false);
        JButton exportButton = primaryButton("Export CSV");
        JButton importButton = secondaryButton("Import CSV");
        JButton saveButton = secondaryButton("Save Now");
        JButton demoButton = secondaryButton("Create Demo Dataset");
        dataButtons.add(exportButton);
        dataButtons.add(importButton);
        dataButtons.add(saveButton);
        dataButtons.add(demoButton);
        dataTools.add(dataButtons, BorderLayout.CENTER);

        JPanel systemTools = cardPanel(new BorderLayout(0, 14));
        systemTools.add(sectionTitle("Database Summary"), BorderLayout.NORTH);
        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        info.setForeground(TEXT);
        info.setBackground(CARD_BG);
        info.setText("Storage: students.dat and students.txt\nBackups: backups folder\nValidation: ID, email, discipline, semester, CGPA, credits, dates\nViews: dashboard, paginated records, analytics, import/export");
        systemTools.add(info, BorderLayout.CENTER);

        exportButton.addActionListener(e -> exportCsv());
        importButton.addActionListener(e -> importCsv());
        saveButton.addActionListener(e -> {
            saveData();
            setStatus("Database saved.");
        });
        demoButton.addActionListener(e -> createDemoDataset());

        view.add(dataTools);
        view.add(systemTools);
        return view;
    }

    private void addStudent() {
        Student student = showStudentDialog(null);
        if (student == null) {
            return;
        }
        try {
            studentManager.addStudent(student);
            afterDataChange("Student added.");
        } catch (InvalidInputException ex) {
            showError(ex.getMessage());
        }
    }

    private void editSelectedStudent() {
        Student selected = getSelectedStudent();
        if (selected == null) {
            showError("Select a student first.");
            return;
        }
        Student updated = showStudentDialog(selected);
        if (updated == null) {
            return;
        }
        try {
            if (studentManager.updateStudent(selected.getStudentId(), updated)) {
                afterDataChange("Student updated.");
            }
        } catch (InvalidInputException ex) {
            showError(ex.getMessage());
        }
    }

    private void deleteSelectedStudent() {
        Student selected = getSelectedStudent();
        if (selected == null) {
            showError("Select a student first.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete " + selected.getName() + " (" + selected.getStudentId() + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION && studentManager.removeStudent(selected.getStudentId())) {
            afterDataChange("Student deleted.");
        }
    }

    private Student getSelectedStudent() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        String id = String.valueOf(tableModel.getValueAt(row, 0));
        return studentManager.searchById(id);
    }

    private void applyFilters() {
        StudentSearchCriteria criteria = new StudentSearchCriteria();
        criteria.setQuery(searchField.getText());
        criteria.setDiscipline(String.valueOf(disciplineFilter.getSelectedItem()));
        criteria.setStatus(String.valueOf(statusFilter.getSelectedItem()));
        criteria.setSemester(parseSemesterChoice());
        criteria.setSortOption((SortOption) sortFilter.getSelectedItem());
        currentResults = studentManager.search(criteria);
        currentPage = 1;
        refreshTablePage();
        setStatus(currentResults.size() + " matching student(s).");
    }

    private void clearFilters() {
        searchField.setText("");
        disciplineFilter.setSelectedIndex(0);
        statusFilter.setSelectedIndex(0);
        semesterFilter.setSelectedIndex(0);
        sortFilter.setSelectedItem(SortOption.ID_ASC);
        applyFilters();
    }

    private void refreshTablePage() {
        tableModel.setRowCount(0);
        int pageSize = (Integer) pageSizeCombo.getSelectedItem();
        int totalPages = totalPages();
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, currentResults.size());
        for (int i = start; i < end; i++) {
            Student s = currentResults.get(i);
            tableModel.addRow(new Object[] {
                    s.getStudentId(),
                    s.getName(),
                    s.getDiscipline(),
                    s.getSemester(),
                    String.format("%.2f", s.getCgpa()),
                    s.getCompletedCredits(),
                    s.getStatus(),
                    s.getEmail(),
                    s.getPhoneNumber(),
                    s.getAcademicStanding()
            });
        }
        pageInfoLabel.setText("Page " + currentPage + " of " + totalPages
                + "   Records " + (currentResults.isEmpty() ? 0 : start + 1) + "-" + end
                + " of " + currentResults.size());
    }

    private void goToPage(int page) {
        currentPage = Math.max(1, Math.min(page, totalPages()));
        refreshTablePage();
    }

    private int totalPages() {
        int pageSize = pageSizeCombo == null ? 10 : (Integer) pageSizeCombo.getSelectedItem();
        return Math.max(1, (int) Math.ceil(currentResults.size() / (double) pageSize));
    }

    private void refreshDashboard() {
        StudentStatistics stats = studentManager.getStatistics();
        totalValue.setText(String.valueOf(stats.getTotalStudents()));
        activeValue.setText(String.valueOf(stats.getActiveStudents()));
        cgpaValue.setText(String.format("%.2f", stats.getAverageCgpa()));
        creditsValue.setText(String.valueOf(stats.getTotalCredits()));

        replaceCenter(dashboardDistributionPanel, distributionList(stats.getDisciplineCounts()));
        replaceCenter(dashboardRiskPanel, riskList(studentManager.getAtRiskStudents()));
    }

    private JPanel distributionList(Map<String, Integer> counts) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 8));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(14, 0, 0, 0));
        if (counts.isEmpty()) {
            panel.add(emptyState("No discipline data yet."));
            return panel;
        }
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            panel.add(metricRow(entry.getKey(), String.valueOf(entry.getValue())));
        }
        return panel;
    }

    private JPanel riskList(List<Student> students) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 8));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(14, 0, 0, 0));
        if (students.isEmpty()) {
            panel.add(emptyState("No at-risk students."));
            return panel;
        }
        int limit = Math.min(8, students.size());
        for (int i = 0; i < limit; i++) {
            Student s = students.get(i);
            panel.add(metricRow(s.getName(), s.getStudentId() + " | CGPA " + String.format("%.2f", s.getCgpa())));
        }
        return panel;
    }

    private void refreshAnalytics() {
        StudentStatistics stats = studentManager.getStatistics();
        StringBuilder report = new StringBuilder();
        report.append("ADVANCED STUDENT DATABASE ANALYTICS\n");
        report.append("==================================\n\n");
        report.append("Total students      : ").append(stats.getTotalStudents()).append('\n');
        report.append("Active records      : ").append(stats.getActiveStudents()).append('\n');
        report.append("Average CGPA        : ").append(String.format("%.2f", stats.getAverageCgpa())).append('\n');
        report.append("Completed credits   : ").append(stats.getTotalCredits()).append("\n\n");
        report.append("DISCIPLINE BREAKDOWN\n");
        for (Map.Entry<String, Integer> entry : stats.getDisciplineCounts().entrySet()) {
            report.append(String.format("%-24s %5d%n", entry.getKey(), entry.getValue()));
        }
        report.append("\nSTATUS BREAKDOWN\n");
        for (Map.Entry<String, Integer> entry : stats.getStatusCounts().entrySet()) {
            report.append(String.format("%-24s %5d%n", entry.getKey(), entry.getValue()));
        }
        report.append("\nAT-RISK STUDENTS\n");
        List<Student> atRisk = studentManager.getAtRiskStudents();
        if (atRisk.isEmpty()) {
            report.append("None\n");
        } else {
            for (Student s : atRisk) {
                report.append(String.format("%-12s %-24s CGPA %.2f  %s%n",
                        s.getStudentId(), s.getName(), s.getCgpa(), s.getStatus()));
            }
        }
        analyticsText.setText(report.toString());
        analyticsText.setCaretPosition(0);
    }

    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("students-export.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            if (fileHandler.exportToCsv(studentManager, chooser.getSelectedFile())) {
                setStatus("CSV exported.");
            } else {
                showError("CSV export failed.");
            }
        }
    }

    private void importCsv() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                int imported = studentManager.importStudents(fileHandler.importFromCsv(chooser.getSelectedFile()), true);
                afterDataChange(imported + " student(s) imported.");
            } catch (IOException | InvalidInputException ex) {
                showError("Import failed: " + ex.getMessage());
            }
        }
    }

    private void createDemoDataset() {
        try {
            List<Student> demo = new ArrayList<>();
            demo.add(new Student("CSE-2401", "Afsana Rahman", "CSE", "afsana@university.edu", "01710000001", "Dhaka", 5, 3.86, 82, "Active", "2024-01-12", "2004-03-18", "M Rahman", "01710000009", "B+", "Scholarship candidate"));
            demo.add(new Student("EEE-2314", "Tanvir Hasan", "EEE", "tanvir@university.edu", "01710000002", "Khulna", 7, 2.18, 104, "Probation", "2023-01-10", "2003-08-09", "S Hasan", "01710000010", "O+", "Needs advising"));
            demo.add(new Student("ARC-2207", "Nadia Islam", "Architecture", "nadia@university.edu", "01710000003", "Rajshahi", 8, 3.44, 126, "Active", "2022-01-08", "2002-11-21", "K Islam", "01710000011", "A+", "Studio lead"));
            demo.add(new Student("BBA-2509", "Rafi Chowdhury", "BBA", "rafi@university.edu", "01710000004", "Sylhet", 3, 3.12, 45, "Active", "2025-01-11", "2005-06-05", "A Chowdhury", "01710000012", "AB+", ""));
            demo.add(new Student("MAT-2115", "Mithila Akter", "Mathematics", "mithila@university.edu", "01710000005", "Barishal", 10, 3.91, 151, "Graduated", "2021-01-13", "2001-09-14", "T Akter", "01710000013", "O-", "Research assistant"));
            int imported = studentManager.importStudents(demo, false);
            afterDataChange(imported + " demo student(s) added.");
        } catch (InvalidInputException ex) {
            showError(ex.getMessage());
        }
    }

    private Student showStudentDialog(Student existing) {
        JDialog dialog = new JDialog(this, existing == null ? "Add Student" : "Edit Student", true);
        dialog.setSize(700, 620);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD_BG);
        form.setBorder(new EmptyBorder(18, 18, 10, 18));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JTextField id = modernField(16);
        JTextField name = modernField(16);
        JComboBox<String> discipline = new JComboBox<>(Student.DISCIPLINES);
        JTextField email = modernField(16);
        JTextField phone = modernField(16);
        JTextField address = modernField(16);
        JSpinner semester = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        JSpinner cgpa = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 4.0, 0.01));
        JSpinner credits = new JSpinner(new SpinnerNumberModel(0, 0, 260, 1));
        JComboBox<String> status = new JComboBox<>(Student.STATUSES);
        JTextField admission = modernField(16);
        JTextField dob = modernField(16);
        JTextField guardian = modernField(16);
        JTextField emergency = modernField(16);
        JComboBox<String> blood = new JComboBox<>(Student.BLOOD_GROUPS);
        JTextArea notes = new JTextArea(4, 18);
        notes.setLineWrap(true);
        notes.setWrapStyleWord(true);
        notes.setBorder(BorderFactory.createLineBorder(BORDER));

        if (existing != null) {
            id.setText(existing.getStudentId());
            id.setEditable(false);
            name.setText(existing.getName());
            discipline.setSelectedItem(existing.getDiscipline());
            email.setText(existing.getEmail());
            phone.setText(existing.getPhoneNumber());
            address.setText(existing.getAddress());
            semester.setValue(existing.getSemester());
            cgpa.setValue(existing.getCgpa());
            credits.setValue(existing.getCompletedCredits());
            status.setSelectedItem(existing.getStatus());
            admission.setText(existing.getAdmissionDate());
            dob.setText(existing.getDateOfBirth());
            guardian.setText(existing.getGuardianName());
            emergency.setText(existing.getEmergencyContact());
            blood.setSelectedItem(existing.getBloodGroup());
            notes.setText(existing.getNotes());
        }

        addField(form, gbc, 0, "Student ID", id);
        addField(form, gbc, 1, "Full Name", name);
        addField(form, gbc, 2, "Discipline", discipline);
        addField(form, gbc, 3, "Email", email);
        addField(form, gbc, 4, "Phone", phone);
        addField(form, gbc, 5, "Address", address);
        addField(form, gbc, 6, "Semester", semester);
        addField(form, gbc, 7, "CGPA", cgpa);
        addField(form, gbc, 8, "Completed Credits", credits);
        addField(form, gbc, 9, "Status", status);
        addField(form, gbc, 10, "Admission Date", admission);
        addField(form, gbc, 11, "Date of Birth", dob);
        addField(form, gbc, 12, "Guardian", guardian);
        addField(form, gbc, 13, "Emergency Contact", emergency);
        addField(form, gbc, 14, "Blood Group", blood);
        addField(form, gbc, 15, "Notes", new JScrollPane(notes));

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        footer.setBackground(CARD_BG);
        JButton cancel = secondaryButton("Cancel");
        JButton save = primaryButton("Save");
        footer.add(cancel);
        footer.add(save);

        final Student[] result = new Student[1];
        cancel.addActionListener(e -> dialog.dispose());
        save.addActionListener(e -> {
            Student student = new Student(
                    id.getText().trim(),
                    name.getText().trim(),
                    String.valueOf(discipline.getSelectedItem()),
                    email.getText().trim(),
                    phone.getText().trim(),
                    address.getText().trim(),
                    (Integer) semester.getValue(),
                    ((Number) cgpa.getValue()).doubleValue(),
                    (Integer) credits.getValue(),
                    String.valueOf(status.getSelectedItem()),
                    admission.getText().trim(),
                    dob.getText().trim(),
                    guardian.getText().trim(),
                    emergency.getText().trim(),
                    String.valueOf(blood.getSelectedItem()),
                    notes.getText().trim()
            );
            try {
                StudentValidator.validate(student);
                result[0] = student;
                dialog.dispose();
            } catch (InvalidInputException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
        return result[0];
    }

    private void addField(JPanel form, GridBagConstraints gbc, int row, String label, java.awt.Component field) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel labelComponent = label(label);
        labelComponent.setPreferredSize(new Dimension(145, 24));
        form.add(labelComponent, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(field, gbc);
    }

    private void afterDataChange(String message) {
        saveData();
        applyFilters();
        refreshDashboard();
        refreshAnalytics();
        setStatus(message);
    }

    private void loadDataFromFiles() {
        StudentManager data = fileHandler.loadFromBinary();
        studentManager.setStudents(data.getAllStudents());
        applyFilters();
        refreshDashboard();
        refreshAnalytics();
        setStatus("Loaded " + studentManager.getSize() + " student(s).");
    }

    private void saveData() {
        Thread saveThread = new Thread(() -> {
            fileHandler.saveToBinary(studentManager);
            fileHandler.saveToText(studentManager);
        });
        saveThread.setDaemon(true);
        saveThread.start();
    }

    private void showView(String name) {
        cardLayout.show(cardsPanel, name);
        titleLabel.setText(name);
        updateNavState(name);
        if ("Dashboard".equals(name)) {
            refreshDashboard();
        } else if ("Students".equals(name)) {
            applyFilters();
        } else if ("Analytics".equals(name)) {
            refreshAnalytics();
        }
    }

    private void updateNavState(String active) {
        setNavActive(dashboardNav, "Dashboard".equals(active));
        setNavActive(studentsNav, "Students".equals(active));
        setNavActive(analyticsNav, "Analytics".equals(active));
        setNavActive(toolsNav, "Tools".equals(active));
    }

    private JButton navButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(new Color(226, 232, 240));
        button.setBackground(SIDEBAR_BG);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(12, 14, 12, 14));
        button.addActionListener(e -> showView(text));
        return button;
    }

    private void setNavActive(JButton button, boolean active) {
        button.setBackground(active ? SIDEBAR_ACTIVE : SIDEBAR_BG);
        button.setForeground(Color.WHITE);
    }

    private JPanel statCard(String label, JLabel value, Color accent) {
        JPanel panel = cardPanel(new BorderLayout(0, 8));
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelComponent.setForeground(MUTED);
        value.setFont(new Font("Segoe UI", Font.BOLD, 31));
        value.setForeground(TEXT);
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setPreferredSize(new Dimension(0, 4));
        panel.add(accentBar, BorderLayout.NORTH);
        panel.add(labelComponent, BorderLayout.CENTER);
        panel.add(value, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel cardPanel(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(16, 16, 16, 16)));
        return panel;
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 17));
        label.setForeground(TEXT);
        return label;
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(MUTED);
        return label;
    }

    private JLabel emptyState(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(MUTED);
        return label;
    }

    private JPanel metricRow(String left, String right) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel leftLabel = new JLabel(left);
        leftLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        leftLabel.setForeground(TEXT);
        JLabel rightLabel = new JLabel(right);
        rightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rightLabel.setForeground(MUTED);
        row.add(leftLabel, BorderLayout.WEST);
        row.add(rightLabel, BorderLayout.EAST);
        return row;
    }

    private JTextField modernField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(7, 9, 7, 9)));
        return field;
    }

    private JButton primaryButton(String text) {
        return styledButton(text, SIDEBAR_ACTIVE, Color.WHITE);
    }

    private JButton secondaryButton(String text) {
        return styledButton(text, new Color(241, 245, 249), TEXT);
    }

    private JButton dangerButton(String text) {
        return styledButton(text, new Color(220, 38, 38), Color.WHITE);
    }

    private JButton styledButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(9, 14, 9, 14));
        return button;
    }

    private void centerColumn(int column) {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(column).setCellRenderer(renderer);
    }

    private String[] withAll(String[] values) {
        String[] result = new String[values.length + 1];
        result[0] = "All";
        System.arraycopy(values, 0, result, 1, values.length);
        return result;
    }

    private String[] semesterChoices() {
        String[] choices = new String[13];
        choices[0] = "All";
        for (int i = 1; i <= 12; i++) {
            choices[i] = String.valueOf(i);
        }
        return choices;
    }

    private int parseSemesterChoice() {
        String selected = String.valueOf(semesterFilter.getSelectedItem());
        if ("All".equals(selected)) {
            return 0;
        }
        return Integer.parseInt(selected);
    }

    private void replaceCenter(JPanel panel, java.awt.Component component) {
        java.awt.Component north = ((BorderLayout) panel.getLayout()).getLayoutComponent(BorderLayout.NORTH);
        panel.removeAll();
        if (north != null) {
            panel.add(north, BorderLayout.NORTH);
        }
        panel.add(component, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    private void setStatus(String message) {
        statusLabel.setText(message + "  |  Total: " + studentManager.getSize());
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Student Database", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new MainGUI();
        });
    }
}
