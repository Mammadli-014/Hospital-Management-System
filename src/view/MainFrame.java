package view;

import controller.*;
import db.DBConnection;
import dao.*;
import enums.*;
import event.*;
import model.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MainFrame extends JFrame {

    private static final Color BG        = new Color(0xF4F6FA);
    private static final Color SIDEBAR   = new Color(0x1A2B4A);
    private static final Color ACCENT    = new Color(0x2E7BF6);
    private static final Color ACCENT2   = new Color(0x27AE60);
    private static final Color DANGER    = new Color(0xE74C3C);
    private static final Color TEXT_DARK = new Color(0x1A2B4A);
    private static final Color TEXT_MID  = new Color(0x6B7A99);
    private static final Color WHITE     = Color.WHITE;
    private static final Font  HEADER    = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font  BODY      = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  SMALL     = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font  NAV_FONT  = new Font("Segoe UI", Font.BOLD, 13);

    private JPanel contentPanel;
    private CardLayout cardLayout;

    // DAO and Stat Labels
    private PatientDAO patientDAO = new PatientDAO();
    private JLabel lblPatientStat, lblDoctorStat, lblBedStat, lblAppointStat;

    public MainFrame() {
        setTitle("Hospital Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);

        cardLayout  = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG);
        contentPanel.add(buildDashboard(),      "dashboard");
        contentPanel.add(buildPatientPanel(),   "patients");
        contentPanel.add(buildMedRecordPanel(), "medrecords");
        contentPanel.add(buildBedPanel(),       "beds");
        contentPanel.add(buildAppointPanel(),   "appointments");
        contentPanel.add(buildSurgeryPanel(),   "surgery");
        contentPanel.add(buildDoctorPanel(),    "doctors");
        add(contentPanel, BorderLayout.CENTER);

        // Update stats on startup
        updateDashboardStats();
        cardLayout.show(contentPanel, "dashboard");
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel logo = new JLabel("🏥  HospitalMS", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logo.setForeground(WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        sidebar.add(logo);

        String[][] navItems = {
                {"🏠", "Dashboard",        "dashboard"},
                {"👤", "Patients",         "patients"},
                {"📋", "Medical Records",  "medrecords"},
                {"🛏", "Bed Admissions",   "beds"},
                {"📅", "Appointments",     "appointments"},
                {"🔪", "Surgery",          "surgery"},
                {"👨‍⚕️", "Doctors",          "doctors"},
        };

        for (String[] item : navItems) {
            sidebar.add(navButton(item[0] + "  " + item[1], item[2]));
        }
        sidebar.add(Box.createVerticalGlue());

        JLabel version = new JLabel("v1.0  |  MySQL", SwingConstants.CENTER);
        version.setFont(SMALL);
        version.setForeground(new Color(0x6B8ABD));
        version.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(version);
        return sidebar;
    }

    private JButton navButton(String text, String card) {
        JButton btn = new JButton(text);
        btn.setFont(NAV_FONT);
        btn.setForeground(new Color(0xB0C4DE));
        btn.setBackground(SIDEBAR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(210, 44));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            updateDashboardStats(); // Refresh stats whenever navigating to dashboard
            cardLayout.show(contentPanel, card);
        });
        return btn;
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────
    private JPanel buildDashboard() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(HEADER);
        title.setForeground(TEXT_DARK);
        p.add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 4, 18, 0));
        cards.setBackground(BG);
        cards.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));

        // Initialize labels so we can update them later
        lblPatientStat = new JLabel("0");
        lblDoctorStat  = new JLabel("0");
        lblBedStat     = new JLabel("0");
        lblAppointStat = new JLabel("0");

        cards.add(statCard("Total Patients", lblPatientStat, ACCENT,  "👤"));
        cards.add(statCard("Total Doctors",  lblDoctorStat,  ACCENT2, "👨‍⚕️"));
        cards.add(statCard("Bed Records",    lblBedStat,     DANGER,  "🛏"));
        cards.add(statCard("Appointments",   lblAppointStat, new Color(0xF39C12), "📅"));

        p.add(cards, BorderLayout.CENTER);

        JLabel sub = new JLabel("Please select a module from the sidebar to manage hospital data.", SwingConstants.LEFT);
        sub.setFont(BODY);
        sub.setForeground(TEXT_MID);
        sub.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        p.add(sub, BorderLayout.SOUTH);
        return p;
    }

    // New logic to sync stats with DB
    private void updateDashboardStats() {
        lblPatientStat.setText(String.valueOf(getCount("patients")));
        lblDoctorStat.setText(String.valueOf(getCount("doctor")));
        lblBedStat.setText(String.valueOf(getCount("bedrecords")));
        lblAppointStat.setText(String.valueOf(getCount("appointment")));
    }

    private int getCount(String table) {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Dashboard Count Error for " + table + ": " + e.getMessage());
        }
        return 0;
    }

    private JPanel statCard(String label, JLabel valueLabel, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE0E6F0), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel ico = new JLabel(icon);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(color);

        JLabel lbl = new JLabel(label);
        lbl.setFont(SMALL);
        lbl.setForeground(TEXT_MID);

        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(WHITE);
        right.add(valueLabel, BorderLayout.CENTER);
        right.add(lbl, BorderLayout.SOUTH);

        card.add(ico,   BorderLayout.WEST);
        card.add(right, BorderLayout.CENTER);
        return card;
    }

    // ── Patient Panel ────────────────────────────────────────────────────────
    private JPanel buildPatientPanel() {
        JPanel p = mainPanel("👤  Patient Management");

        String[] cols = {"ID", "First Name", "Last Name", "Gender", "Phone", "Address"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);
        refreshPatientTable(model);

        JTextField searchField = new JTextField();
        searchField.setFont(BODY);
        searchField.setPreferredSize(new Dimension(220, 32));
        JButton searchBtn = accentButton("Search", ACCENT);
        searchBtn.addActionListener(e -> {
            String kw = searchField.getText().trim();
            model.setRowCount(0);
            List<Patient> list = kw.isEmpty() ? patientDAO.findAll() : patientDAO.search(kw);
            list.forEach(pt -> model.addRow(new Object[]{
                    pt.getId(), pt.getFname(), pt.getLname(),
                    pt.getGender(), pt.getContact(), pt.getAddress()
            }));
        });

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(BG);
        toolbar.add(new JLabel("Search:"));
        toolbar.add(searchField);
        toolbar.add(searchBtn);
        toolbar.add(Box.createHorizontalStrut(16));

        JButton addBtn = accentButton("+ New Patient", ACCENT2);
        addBtn.addActionListener(e -> showAddPatientDialog(model));
        toolbar.add(addBtn);

        JButton delBtn = accentButton("Delete", DANGER);
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a patient."); return; }
            int id = (int) model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this patient?",
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                patientDAO.deletePatient(id);
                refreshPatientTable(model);
                updateDashboardStats();
            }
        });
        toolbar.add(delBtn);

        p.add(toolbar, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void refreshPatientTable(DefaultTableModel model) {
        model.setRowCount(0);
        patientDAO.findAll().forEach(pt -> model.addRow(new Object[]{
                pt.getId(), pt.getFname(), pt.getLname(),
                pt.getGender(), pt.getContact(), pt.getAddress()
        }));
    }

    private void showAddPatientDialog(DefaultTableModel tableModel) {
        JDialog dlg = new JDialog(this, "Add New Patient", true);
        dlg.setLayout(new GridBagLayout());
        dlg.setSize(450, 450);
        dlg.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField fNameField   = formField();
        JTextField lNameField   = formField();
        JComboBox<Gender> genderCombo = new JComboBox<>(Gender.values());

        JTextField dobField     = formField();
        dobField.setToolTipText("Format: YYYY-MM-DD");

        JTextField contactField = formField();
        JTextField addressField = formField();

        String[] labels = {
                "First Name *:",
                "Last Name *:",
                "Gender:",
                "Date of Birth (YYYY-MM-DD):",
                "Contact No:",
                "Address:"
        };

        Component[] comps = {fNameField, lNameField, genderCombo, dobField, contactField, addressField};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            dlg.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            dlg.add(comps[i], gbc);
        }

        JButton saveBtn = accentButton("Save Patient", ACCENT2);
        gbc.gridx = 1; gbc.gridy = labels.length; gbc.weightx = 1;
        dlg.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            try {
                if (fNameField.getText().trim().isEmpty() || lNameField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "First Name and Last Name are required!");
                    return;
                }
                LocalDate birthDate = LocalDate.parse(dobField.getText().trim());
                Patient newPatient = new Patient(0, fNameField.getText().trim(), lNameField.getText().trim(),
                        (Gender) genderCombo.getSelectedItem(), birthDate, contactField.getText().trim(), addressField.getText().trim());

                String result = PatientController.getInstance().addPatient(newPatient);
                if (result.startsWith("SUCCESS")) {
                    refreshPatientTable(tableModel);
                    updateDashboardStats();
                    dlg.dispose();
                    JOptionPane.showMessageDialog(this, result);
                } else {
                    JOptionPane.showMessageDialog(dlg, result);
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dlg, "Invalid Date Format! Use YYYY-MM-DD.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
            }
        });
        dlg.setVisible(true);
    }

    // ── Medical Record Panel ────────────────────────────────────────────────
    private JPanel buildMedRecordPanel() {
        JPanel p = mainPanel("📋  Medical Records");

        String[] cols = {"Record ID", "Patient", "Doctor", "Diagnosis", "Date", "Weight", "Height", "BP", "Temp", "Treatment", "Next Visit"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);
        refreshMedTable(model);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(BG);
        JButton addBtn = accentButton("+ New Record", ACCENT2);
        addBtn.addActionListener(e -> showAddMedRecordDialog(model));
        toolbar.add(addBtn);

        p.add(toolbar, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void refreshMedTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<MedicalRecord> records = MedicalRecordController.getInstance().findAll();
        for (MedicalRecord r : records) {
            model.addRow(new Object[]{
                    r.getId(), r.getPatientId(), r.getDoctId(), r.getDiagnosis(), r.getDate(),
                    r.getWeight() + " kg", r.getHeight() + " cm", r.getBloodPresure(), r.getTemp() + " °F",
                    r.getTreatment(), r.getNext_visit() != null ? r.getNext_visit() : "None"
            });
        }
    }

    private void showAddMedRecordDialog(DefaultTableModel tableModel) {
        JDialog dlg = new JDialog(this, "Add Medical Record", true);
        dlg.setLayout(new GridBagLayout());
        dlg.setSize(500, 600);
        dlg.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField patientId = formField();
        JTextField doctorId  = formField();
        JTextField weight    = formField();
        JTextField height    = formField();
        JTextField bloodPres = formField();
        JTextField tempF     = formField();
        JTextArea  diagnosis = new JTextArea(2, 20);
        JTextArea  treatment = new JTextArea(2, 20);
        JTextField visitDate = formField();
        JTextField nextVisit = formField();

        visitDate.setText(LocalDate.now().toString());
        diagnosis.setFont(BODY);
        treatment.setFont(BODY);

        Object[][] fields = {
                {"Patient ID *", patientId}, {"Doctor ID *", doctorId}, {"Visit Date", visitDate},
                {"Weight (kg)", weight}, {"Height (cm)", height}, {"Blood Pressure", bloodPres},
                {"Temp (°F)", tempF}, {"Diagnosis *", new JScrollPane(diagnosis)},
                {"Treatment", new JScrollPane(treatment)}, {"Next Visit", nextVisit}
        };

        for (int i = 0; i < fields.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            dlg.add(new JLabel((String) fields[i][0]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            dlg.add((Component) fields[i][1], gbc);
        }

        JButton save = accentButton("Save Record", ACCENT2);
        gbc.gridx = 1; gbc.gridy = fields.length;
        dlg.add(save, gbc);

        save.addActionListener(e -> {
            try {
                int pid = Integer.parseInt(patientId.getText().trim());
                int did = Integer.parseInt(doctorId.getText().trim());
                int w   = weight.getText().isEmpty() ? 0 : Integer.parseInt(weight.getText().trim());
                int h   = height.getText().isEmpty() ? 0 : Integer.parseInt(height.getText().trim());
                int t   = tempF.getText().isEmpty() ? 0 : Integer.parseInt(tempF.getText().trim());
                LocalDate vDate = LocalDate.parse(visitDate.getText().trim());
                LocalDate nVisit = nextVisit.getText().trim().isEmpty() ? null : LocalDate.parse(nextVisit.getText().trim());

                String result = MedicalRecordController.getInstance().addRecord(
                        pid, did, diagnosis.getText().trim(), treatment.getText().trim(),
                        w, h, bloodPres.getText().trim(), t, vDate, nVisit
                );

                if (result.startsWith("SUCCESS") || result.startsWith("OK")) {
                    refreshMedTable(tableModel);
                    dlg.dispose();
                    JOptionPane.showMessageDialog(this, "Success: Record saved.");
                } else {
                    JOptionPane.showMessageDialog(dlg, result);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
            }
        });
        dlg.setVisible(true);
    }

    // ── Bed Record Panel ──────────────────────────────────────────────────
    private JPanel buildBedPanel() {
        JPanel p = mainPanel("🛏  Bed Admissions");

        String[] cols = {"ID", "Patient", "Nurse", "Bed No", "Check-in", "Check-out"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);
        refreshBedTable(model);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(BG);
        JButton addBtn = accentButton("+ New Bed Admission", ACCENT2);
        addBtn.addActionListener(e -> showAddBedDialog(model));
        toolbar.add(addBtn);

        p.add(toolbar, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void refreshBedTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<BedRecord> records = AdmissionController.getInstance().getAllBedRecords();
        for (BedRecord r : records) {
            model.addRow(new Object[]{
                    r.getId(), r.getPatientId(), r.getNurseNo(), r.getNo(),
                    r.getDate(), r.getEndingDate() != null ? r.getEndingDate() : "Active"
            });
        }
    }

    private void showAddBedDialog(DefaultTableModel tableModel) {
        JDialog dlg = new JDialog(this, "Admit Patient to Bed", true);
        dlg.setLayout(new GridBagLayout());
        dlg.setSize(450, 450);
        dlg.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 10, 7, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField patientId = formField();
        JTextField nurseId   = formField();
        JTextField bedNo     = formField();
        JTextField admDate   = formField();
        JTextField amount    = formField();
        JComboBox<PaymentType> pTypeCombo = new JComboBox<>(PaymentType.values());

        admDate.setText(LocalDate.now().toString());

        List<Integer> avail = AdmissionController.getInstance().getAvailableBeds();
        JLabel hint = new JLabel("Available beds: " + avail.toString());
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(Color.BLUE);

        Object[][] fields = {
                {"Patient ID *", patientId}, {"Nurse ID *", nurseId}, {"Bed No *", bedNo},
                {"Admission Date", admDate}, {"Initial Amount", amount}, {"Payment Type", pTypeCombo},
                {"", hint}
        };

        for (int i = 0; i < fields.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            dlg.add(new JLabel((String) fields[i][0]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            dlg.add((Component) fields[i][1], gbc);
        }

        JButton save = accentButton("Admit Patient", ACCENT2);
        gbc.gridx = 1; gbc.gridy = fields.length;
        dlg.add(save, gbc);

        save.addActionListener(e -> {
            try {
                int pid = Integer.parseInt(patientId.getText().trim());
                int nid = Integer.parseInt(nurseId.getText().trim());
                int bno = Integer.parseInt(bedNo.getText().trim());
                int amt = amount.getText().isEmpty() ? 0 : Integer.parseInt(amount.getText().trim());
                LocalDate d = LocalDate.parse(admDate.getText().trim());
                PaymentType pt = (PaymentType) pTypeCombo.getSelectedItem();

                String result = AdmissionController.getInstance().admitToBed(pid, nid, bno, d, amt, pt);
                if (result.startsWith("SUCCESS")) {
                    refreshBedTable(tableModel);
                    updateDashboardStats();
                    dlg.dispose();
                    JOptionPane.showMessageDialog(this, result);
                } else {
                    JOptionPane.showMessageDialog(dlg, result);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
            }
        });
        dlg.setVisible(true);
    }

    // ── Appointment Panel ──────────────────────────────────────────────────
    private JPanel buildAppointPanel() {
        JPanel p = mainPanel("📅  Appointments");

        String[] cols = {"ID", "Patient", "Doctor", "Date", "Reason", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);
        refreshAppointTable(model);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(BG);
        JButton addBtn = accentButton("+ Create Appointment", ACCENT2);
        addBtn.addActionListener(e -> showAddAppointDialog(model));
        toolbar.add(addBtn);

        p.add(toolbar, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void refreshAppointTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<AppointmentRecord> records = AppointmentController.getInstance().findAll();
        for (AppointmentRecord r : records) {
            model.addRow(new Object[]{
                    r.getId(), r.getPatientId(), r.getDoctorId(), r.getDate(),
                    r.getReason(), r.getStatus()
            });
        }
    }

    private void showAddAppointDialog(DefaultTableModel tableModel) {
        JDialog dlg = new JDialog(this, "Create Appointment", true);
        dlg.setLayout(new GridBagLayout());
        dlg.setSize(450, 500);
        dlg.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField patientId = formField();
        JTextField doctorId  = formField();
        JTextField dateTime  = formField();
        JTextField reason    = formField();
        JTextField amount    = formField();
        JComboBox<PaymentType> pTypeCombo = new JComboBox<>(PaymentType.values());
        JComboBox<AppointmentType> aTypeCombo = new JComboBox<>(AppointmentType.values());

        dateTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        Object[][] fields = {
                {"Patient ID *", patientId}, {"Doctor ID *", doctorId}, {"Date (yyyy-MM-dd HH:mm:ss)", dateTime},
                {"Reason", reason}, {"Amount", amount}, {"Payment Type", pTypeCombo}, {"App. Type", aTypeCombo}
        };

        for (int i = 0; i < fields.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            dlg.add(new JLabel((String) fields[i][0]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            dlg.add((Component) fields[i][1], gbc);
        }

        JButton save = accentButton("Save Appointment", ACCENT2);
        gbc.gridx = 1; gbc.gridy = fields.length;
        dlg.add(save, gbc);

        save.addActionListener(e -> {
            try {
                int pid = Integer.parseInt(patientId.getText().trim());
                int did = Integer.parseInt(doctorId.getText().trim());
                int amt = amount.getText().isEmpty() ? 0 : Integer.parseInt(amount.getText().trim());
                LocalDateTime dt = LocalDateTime.parse(dateTime.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                PaymentType pType = (PaymentType) pTypeCombo.getSelectedItem();
                AppointmentType aType = (AppointmentType) aTypeCombo.getSelectedItem();

                String result = AppointmentController.getInstance().createAppointment(pid, did, dt, reason.getText().trim(), amt, pType, aType);
                if (result.startsWith("SUCCESS")) {
                    refreshAppointTable(tableModel);
                    updateDashboardStats();
                    dlg.dispose();
                    JOptionPane.showMessageDialog(this, result);
                } else {
                    JOptionPane.showMessageDialog(dlg, result);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
            }
        });
        dlg.setVisible(true);
    }

    // ── Surgery Panel ─────────────────────────────────────────────────────────
    private JPanel buildSurgeryPanel() {
        JPanel p = mainPanel("🔪  Surgery Records");

        String[] cols = {"ID", "Patient", "Surgeon", "Nurse", "Type", "Date", "Start", "End", "Room"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);
        refreshSurgeryTable(model);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(BG);
        JButton addBtn = accentButton("+ New Surgery", ACCENT2);
        addBtn.addActionListener(e -> showAddSurgeryDialog(model));
        toolbar.add(addBtn);

        p.add(toolbar, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void refreshSurgeryTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<SurgeryRecord> records = SurgeryController.getInstance().findAll();
        for (SurgeryRecord r : records) {
            model.addRow(new Object[]{
                    r.getId(), r.getPatientId(), r.getSurgeonId(), r.getNurseId(),
                    r.getSurgeryType(), r.getDate(), r.getStartTime(), r.getEndTime(), r.getRoomNo()
            });
        }
    }

    private void showAddSurgeryDialog(DefaultTableModel tableModel) {
        JDialog dlg = new JDialog(this, "Add Surgery Record", true);
        dlg.setLayout(new GridBagLayout());
        dlg.setSize(480, 550);
        dlg.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField patientId    = formField();
        JTextField surgeonId    = formField();
        JTextField nurseId      = formField();
        JTextField surgeryType  = formField();
        JTextField surgDate     = formField();
        JTextField startTime    = formField();
        JTextField endTime      = formField();
        JTextField roomNo       = formField();
        JTextArea notes         = new JTextArea(3, 20);

        surgDate.setText(LocalDate.now().toString());
        startTime.setText("09:00");
        endTime.setText("11:00");

        Object[][] fields = {
                {"Patient ID *", patientId}, {"Surgeon ID *", surgeonId}, {"Nurse ID *", nurseId},
                {"Type *", surgeryType}, {"Date", surgDate}, {"Start", startTime}, {"End", endTime},
                {"Room No *", roomNo}, {"Notes", new JScrollPane(notes)}
        };

        for (int i = 0; i < fields.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            dlg.add(new JLabel((String) fields[i][0]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            dlg.add((Component) fields[i][1], gbc);
        }

        JButton save = accentButton("Save Surgery", ACCENT2);
        gbc.gridx = 1; gbc.gridy = fields.length;
        dlg.add(save, gbc);

        save.addActionListener(e -> {
            try {
                int pid = Integer.parseInt(patientId.getText().trim());
                int sid = Integer.parseInt(surgeonId.getText().trim());
                int nid = Integer.parseInt(nurseId.getText().trim());
                int rno = Integer.parseInt(roomNo.getText().trim());
                LocalDate d = LocalDate.parse(surgDate.getText().trim());
                LocalTime start = LocalTime.parse(startTime.getText().trim());
                LocalTime end = LocalTime.parse(endTime.getText().trim());

                String result = SurgeryController.getInstance().addSurgery(pid, sid, nid, d, start, end, rno, surgeryType.getText().trim(), notes.getText().trim());
                if (result.startsWith("SUCCESS")) {
                    refreshSurgeryTable(tableModel);
                    dlg.dispose();
                    JOptionPane.showMessageDialog(this, result);
                } else {
                    JOptionPane.showMessageDialog(dlg, result);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
            }
        });
        dlg.setVisible(true);
    }

    // ── Doctor Panel ────────────────────────────────────────────────────────
    private JPanel buildDoctorPanel() {
        JPanel p = mainPanel("👨‍⚕️  Doctor Management");

        String[] cols = {"ID", "First Name", "Last Name", "Gender", "Specialty", "Dept ID", "Office", "Phone"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);
        refreshDoctorTable(model);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(BG);
        JButton addBtn = accentButton("+ New Doctor", ACCENT2);
        addBtn.addActionListener(e -> showAddDoctorDialog(model));
        toolbar.add(addBtn);

        p.add(toolbar, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void refreshDoctorTable(DefaultTableModel model) {
        model.setRowCount(0);
        new DoctorDAO().findAll().forEach(d -> model.addRow(new Object[]{
                d.getId(), d.getFname(), d.getLname(), d.getGender(),
                d.getSurgeonType(), d.getDeptId(), d.getOfficeNo(), d.getContact()
        }));
    }

    private void showAddDoctorDialog(DefaultTableModel tableModel) {
        JDialog dlg = new JDialog(this, "Add New Doctor", true);
        dlg.setLayout(new GridBagLayout());
        dlg.setSize(450, 450);
        dlg.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField fNameField    = formField();
        JTextField lNameField    = formField();
        JComboBox<Gender> genderCombo = new JComboBox<>(Gender.values());
        JTextField specField     = formField();
        JTextField deptIdField   = formField();
        JTextField officeField   = formField();
        JTextField contactField  = formField();

        Object[][] fields = {
                {"First Name *", fNameField}, {"Last Name *", lNameField}, {"Gender", genderCombo},
                {"Surgeon Type", specField}, {"Dept ID *", deptIdField}, {"Office No", officeField}, {"Contact No", contactField}
        };

        for (int i = 0; i < fields.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            dlg.add(new JLabel((String) fields[i][0]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            dlg.add((Component) fields[i][1], gbc);
        }

        JButton save = accentButton("Save Doctor", ACCENT2);
        gbc.gridx = 1; gbc.gridy = fields.length;
        dlg.add(save, gbc);

        save.addActionListener(e -> {
            try {
                int dId = Integer.parseInt(deptIdField.getText().trim());
                Doctor doc = new Doctor(0, fNameField.getText().trim(), lNameField.getText().trim(),
                        (Gender) genderCombo.getSelectedItem(), specField.getText().trim(),
                        dId, officeField.getText().trim(), contactField.getText().trim());

                String result = DoctorController.getInstance().addDoctor(doc);
                if (result.startsWith("SUCCESS")) {
                    refreshDoctorTable(tableModel);
                    updateDashboardStats();
                    dlg.dispose();
                    JOptionPane.showMessageDialog(this, result);
                } else {
                    JOptionPane.showMessageDialog(dlg, result);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
            }
        });
        dlg.setVisible(true);
    }

    private JPanel mainPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        JLabel lbl = new JLabel(title);
        lbl.setFont(HEADER);
        lbl.setForeground(TEXT_DARK);
        p.add(lbl, BorderLayout.NORTH);
        return p;
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(BODY);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(0xD6E4FF));
        table.setBackground(WHITE);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(0xE8EFFA));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT));
        return table;
    }

    private JButton accentButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        return btn;
    }

    private JTextField formField() {
        JTextField f = new JTextField();
        f.setFont(BODY);
        f.setPreferredSize(new Dimension(220, 30));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCBD5E1), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return f;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}