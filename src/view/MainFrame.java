package view;

import controller.*;
import db.DBConnection;
import dao.*;
import enums.AppointmentType;
import enums.PaymentType;
import model.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
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

    private final JPanel contentPanel;
    private final CardLayout cardLayout;

    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO doctorDAO   = new DoctorDAO();
    private final NurseDAO nurseDAO     = new NurseDAO();

    private JLabel lblPatientStat, lblDoctorStat, lblBedStat, lblAppointStat, lblAvailBedStat, lblNurseStat;

    public MainFrame() {
        setTitle("Hospital Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1350, 850);
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
        contentPanel.add(buildNursePanel(),     "nurses"); // Yeni Panel

        add(contentPanel, BorderLayout.CENTER);

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
                {"👩‍⚕️", "Nurses",           "nurses"}, // Yeni Nav
        };

        for (String[] item : navItems) {
            sidebar.add(navButton(item[0] + "  " + item[1], item[2]));
        }
        sidebar.add(Box.createVerticalGlue());
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
            updateDashboardStats();
            cardLayout.show(contentPanel, card);
        });
        return btn;
    }

    private JPanel buildDashboard() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel cards = new JPanel(new GridLayout(2, 3, 20, 20));
        cards.setBackground(BG);

        lblPatientStat  = new JLabel("0");
        lblDoctorStat   = new JLabel("0");
        lblNurseStat    = new JLabel("0");
        lblBedStat      = new JLabel("0");
        lblAppointStat  = new JLabel("0");
        lblAvailBedStat = new JLabel("0");

        cards.add(statCard("Patients", lblPatientStat, ACCENT, "👤"));
        cards.add(statCard("Doctors",  lblDoctorStat,  ACCENT2, "👨‍⚕️"));
        cards.add(statCard("Nurses",   lblNurseStat,   new Color(0x1ABC9C), "👩‍⚕️"));
        cards.add(statCard("Admissions", lblBedStat,   DANGER, "🛏"));
        cards.add(statCard("Appointments", lblAppointStat, new Color(0xF39C12), "📅"));
        cards.add(statCard("Avail. Beds", lblAvailBedStat, new Color(0x9B59B6), "🆓"));

        p.add(cards, BorderLayout.CENTER);
        return p;
    }

    private void updateDashboardStats() {
        lblPatientStat.setText(String.valueOf(getCount("patients")));
        lblDoctorStat.setText(String.valueOf(getCount("doctor")));
        lblNurseStat.setText(String.valueOf(getCount("nurse"))); // Veritabanı tablo adın "nurse" olmalı
        lblBedStat.setText(String.valueOf(getCount("bedrecords")));
        lblAppointStat.setText(String.valueOf(getCount("appointment")));
        lblAvailBedStat.setText(String.valueOf(AdmissionController.getInstance().getAvailableBeds().size()));
    }


    private int getCount(String table) {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { /* ignore */ }
        return 0;
    }

    private JPanel statCard(String label, JLabel valueLabel, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE0E6F0), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        JLabel ico = new JLabel(icon);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valueLabel.setForeground(color);
        JLabel lbl = new JLabel(label);
        lbl.setFont(SMALL);
        lbl.setForeground(TEXT_MID);
        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(WHITE);
        right.add(valueLabel, BorderLayout.CENTER);
        right.add(lbl, BorderLayout.SOUTH);
        card.add(ico, BorderLayout.WEST);
        card.add(right, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildAppointPanel() {
        JPanel p = mainPanel("📅  Appointments");
        String[] cols = {"ID", "Patient", "Doctor", "Date", "Status", "Reason", "Type"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = styledTable(model);
        refreshAppointTable(model);

        JTextField searchField = new JTextField(12);
        JButton searchBtn = accentButton("Search", ACCENT);
        searchBtn.addActionListener(e -> {
            model.setRowCount(0);
            AppointmentController.getInstance().findAll().stream()
                    .filter(a -> String.valueOf(a.getPatientId()).equals(searchField.getText()))
                    .forEach(a -> model.addRow(new Object[]{a.getId(), a.getPatientId(), a.getDoctorId(), a.getDate(), a.getStatus(), a.getReason(), a.getAppointmentType()}));
        });

        JButton addBtn = accentButton("+ New", ACCENT2);
        addBtn.addActionListener(e -> showAddAppointDialog(model));

        JButton delBtn = accentButton("Cancel", DANGER);
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) { refreshAppointTable(model); updateDashboardStats(); }
        });

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        toolbar.setBackground(BG);
        toolbar.add(new JLabel("Patient ID:")); toolbar.add(searchField); toolbar.add(searchBtn);
        toolbar.add(Box.createHorizontalStrut(20)); toolbar.add(addBtn); toolbar.add(delBtn);

        p.add(toolbar, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void refreshAppointTable(DefaultTableModel model) {
        model.setRowCount(0);
        AppointmentController.getInstance().findAll().forEach(a -> model.addRow(new Object[]{a.getId(), a.getPatientId(), a.getDoctorId(), a.getDate(), a.getStatus(), a.getReason(), a.getAppointmentType()}));
    }

    private void showAddAppointDialog(DefaultTableModel tableModel) {
        JDialog dlg = new JDialog(this, "Create Appointment", true);
        dlg.setLayout(new GridBagLayout()); dlg.setSize(450, 500); dlg.setLocationRelativeTo(this);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(5,5,5,5); gbc.fill=2;

        JTextField pid = formField(); JTextField did = formField(); JTextField dt = formField();
        JTextField reason = formField(); JTextField amt = formField();
        JComboBox<PaymentType> pt = new JComboBox<>(PaymentType.values());
        JComboBox<AppointmentType> at = new JComboBox<>(AppointmentType.values());
        dt.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        Object[][] fields = {{"Patient ID:", pid}, {"Doctor ID:", did}, {"Date:", dt}, {"Reason:", reason}, {"Amount:", amt}, {"Payment:", pt}, {"Type:", at}};
        for (int i = 0; i < fields.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; dlg.add(new JLabel((String)fields[i][0]), gbc);
            gbc.gridx = 1; dlg.add((Component)fields[i][1], gbc);
        }
        JButton save = accentButton("Save", ACCENT2); gbc.gridy = fields.length; dlg.add(save, gbc);
        save.addActionListener(e -> {
            try {
                AppointmentController.getInstance().createAppointment(Integer.parseInt(pid.getText()), Integer.parseInt(did.getText()), LocalDateTime.parse(dt.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), reason.getText(), Integer.parseInt(amt.getText()), (PaymentType)pt.getSelectedItem(), (AppointmentType)at.getSelectedItem());
                refreshAppointTable(tableModel); updateDashboardStats(); dlg.dispose();
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage()); }
        });
        dlg.setVisible(true);
    }

    private JPanel buildSurgeryPanel() {
        JPanel p = mainPanel("🔪  Surgery Records");
        String[] cols = {"ID", "Patient", "Surgeon", "Date", "Room", "Type"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = styledTable(model);
        refreshSurgeryTable(model);

        JTextField searchField = new JTextField(10);
        JButton searchBtn = accentButton("Search", ACCENT);
        searchBtn.addActionListener(e -> {
            model.setRowCount(0);
            SurgeryController.getInstance().findAll().stream()
                    .filter(s -> String.valueOf(s.getPatientId()).equals(searchField.getText()))
                    .forEach(s -> model.addRow(new Object[]{s.getId(), s.getPatientId(), s.getSurgeonId(), s.getDate(), s.getRoomNo(), s.getSurgeryType()}));
        });

        JButton addBtn = accentButton("+ New", ACCENT2);
        addBtn.addActionListener(e -> showAddSurgeryDialog(model));

        JButton delBtn = accentButton("Delete", DANGER);
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) { refreshSurgeryTable(model); }
        });

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        toolbar.setBackground(BG);
        toolbar.add(new JLabel("Patient ID:")); toolbar.add(searchField); toolbar.add(searchBtn);
        toolbar.add(Box.createHorizontalStrut(20)); toolbar.add(addBtn); toolbar.add(delBtn);

        p.add(toolbar, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void refreshSurgeryTable(DefaultTableModel model) {
        model.setRowCount(0);
        SurgeryController.getInstance().findAll().forEach(s -> model.addRow(new Object[]{s.getId(), s.getPatientId(), s.getSurgeonId(), s.getDate(), s.getRoomNo(), s.getSurgeryType()}));
    }

    private void showAddSurgeryDialog(DefaultTableModel tableModel) {
        JDialog dlg = new JDialog(this, "Add Surgery", true);
        dlg.setLayout(new GridBagLayout()); dlg.setSize(450, 550); dlg.setLocationRelativeTo(this);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(5,5,5,5); gbc.fill=2;

        JTextField pid = formField(); JTextField sid = formField(); JTextField type = formField();
        JTextField date = formField(); date.setText(LocalDate.now().toString());
        JTextField room = formField(); JTextArea notes = new JTextArea(3, 20);

        Object[][] fields = {{"Patient ID:", pid}, {"Surgeon ID:", sid}, {"Type:", type}, {"Date:", date}, {"Room:", room}, {"Notes:", new JScrollPane(notes)}};
        for (int i = 0; i < fields.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; dlg.add(new JLabel((String)fields[i][0]), gbc);
            gbc.gridx = 1; dlg.add((Component)fields[i][1], gbc);
        }
        JButton save = accentButton("Save", ACCENT2); gbc.gridy = fields.length; dlg.add(save, gbc);
        save.addActionListener(e -> {
            try {
                SurgeryController.getInstance().addSurgery(Integer.parseInt(pid.getText()), Integer.parseInt(sid.getText()), 0, LocalDate.parse(date.getText()), LocalTime.of(9,0), LocalTime.of(10,0), Integer.parseInt(room.getText()), type.getText(), notes.getText());
                refreshSurgeryTable(tableModel); dlg.dispose();
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage()); }
        });
        dlg.setVisible(true);
    }

    private JPanel buildNursePanel() {
        JPanel p = mainPanel("👩‍⚕️  Nurse Management");
        String[] cols = {"nurse_Id", "FName", "LName", "Gender", "Dept ID", "Contact"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);
        refreshNurseTable(model);

        // Search Logic
        JTextField searchField = new JTextField(15);
        JButton searchBtn = accentButton("Search", ACCENT);
        searchBtn.addActionListener(e -> {
            String kw = searchField.getText().trim();
            model.setRowCount(0);
            NurseController.getInstance().findAll().stream()
                    .filter(n -> n.getFname().toLowerCase().contains(kw.toLowerCase()) ||
                            n.getLname().toLowerCase().contains(kw.toLowerCase()) ||
                            String.valueOf(n.getDeptId()).equals(kw))
                    .forEach(n -> model.addRow(new Object[]{
                            n.getId(), n.getFname(), n.getLname(), n.getGender(), n.getDeptId(), n.getContact()
                    }));
        });

        JButton addBtn = accentButton("+ New Nurse", ACCENT2);
        addBtn.addActionListener(e -> showAddNurseDialog(model));

        JButton delBtn = accentButton("Delete", DANGER);
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a nurse."); return; }
            int id = (int) model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete Nurse ID: " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String result = NurseController.getInstance().deleteNurse(id);
                if (result.startsWith("SUCCESS")) {
                    refreshNurseTable(model);
                    updateDashboardStats();
                }
                JOptionPane.showMessageDialog(this, result);
            }
        });

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        toolbar.setBackground(BG);
        toolbar.add(new JLabel("Search (Name/Dept):")); toolbar.add(searchField); toolbar.add(searchBtn);
        toolbar.add(Box.createHorizontalStrut(20)); toolbar.add(addBtn); toolbar.add(delBtn);

        p.add(toolbar, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void refreshNurseTable(DefaultTableModel model) {
        model.setRowCount(0);
        NurseController.getInstance().findAll().forEach(n -> model.addRow(new Object[]{
                n.getId(), n.getFname(), n.getLname(), n.getGender(), n.getDeptId(), n.getContact()
        }));
    }

    private void showAddNurseDialog(DefaultTableModel tableModel) {
        JDialog dlg = new JDialog(this, "Add New Nurse", true);
        dlg.setLayout(new GridBagLayout()); dlg.setSize(400, 450); dlg.setLocationRelativeTo(this);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(8, 12, 8, 12); gbc.fill = 2;

        JTextField fName = formField();
        JTextField lName = formField();
        JComboBox<Gender> genderCombo = new JComboBox<>(Gender.values());
        JTextField deptId = formField();
        JTextField contact = formField();

        Object[][] fields = {
                {"First Name:", fName}, {"Last Name:", lName}, {"Gender:", genderCombo},
                {"Dept ID:", deptId}, {"Contact:", contact}
        };

        for (int i = 0; i < fields.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; dlg.add(new JLabel((String)fields[i][0]), gbc);
            gbc.gridx = 1; dlg.add((Component)fields[i][1], gbc);
        }

        JButton save = accentButton("Save", ACCENT2); gbc.gridy = fields.length; dlg.add(save, gbc);

        save.addActionListener(e -> {
            try {
                int dId = Integer.parseInt(deptId.getText().trim());
                Nurse n = new Nurse(0, dId, fName.getText().trim(), lName.getText().trim(),
                        (Gender)genderCombo.getSelectedItem(), contact.getText().trim());

                String res = NurseController.getInstance().addNurse(n);
                if (res.startsWith("SUCCESS")) {
                    refreshNurseTable(tableModel);
                    updateDashboardStats();
                    dlg.dispose();
                }
                JOptionPane.showMessageDialog(this, res);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Dept ID must be a number!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
            }
        });
        dlg.setVisible(true);
    }

    private JPanel buildPatientPanel() {
        JPanel p = mainPanel("👤  Patient Management");
        String[] cols = {"patient_Id", "FName", "LName", "Gender", "Date_Of_Birth", "contact_No", "pt_Address"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);
        refreshPatientTable(model);

        JTextField searchField = new JTextField(15);
        JButton searchBtn = accentButton("Search", ACCENT);
        searchBtn.addActionListener(e -> {
            String kw = searchField.getText().trim();
            model.setRowCount(0);
            List<Patient> list = kw.isEmpty() ? patientDAO.findAll() : patientDAO.search(kw);
            list.forEach(pt -> model.addRow(new Object[]{pt.getId(), pt.getFname(), pt.getLname(), pt.getGender(), pt.getData_birth(), pt.getContact(), pt.getAddress()}));
        });

        JButton addBtn = accentButton("+ New Patient", ACCENT2);
        addBtn.addActionListener(e -> showAddPatientDialog(model));

        JButton delBtn = accentButton("Delete", DANGER);
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            int id = (int) model.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "Delete Patient ID: " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION) == 0) {
                patientDAO.deletePatient(id);
                refreshPatientTable(model);
                updateDashboardStats();
            }
        });

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        toolbar.setBackground(BG);
        toolbar.add(new JLabel("Search:")); toolbar.add(searchField); toolbar.add(searchBtn);
        toolbar.add(Box.createHorizontalStrut(20)); toolbar.add(addBtn); toolbar.add(delBtn);

        p.add(toolbar, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void refreshPatientTable(DefaultTableModel model) {
        model.setRowCount(0);
        patientDAO.findAll().forEach(pt -> model.addRow(new Object[]{pt.getId(), pt.getFname(), pt.getLname(), pt.getGender(), pt.getData_birth(), pt.getContact(), pt.getAddress()}));
    }

    private void showAddPatientDialog(DefaultTableModel tableModel) {
        JDialog dlg = new JDialog(this, "Add New Patient", true);
        dlg.setLayout(new GridBagLayout()); dlg.setSize(450, 480); dlg.setLocationRelativeTo(this);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(8, 12, 8, 12); gbc.fill = 2;

        JTextField fName = formField(); JTextField lName = formField();
        JComboBox<Gender> genderCombo = new JComboBox<>(Gender.values());
        JTextField dob = formField(); JTextField contact = formField(); JTextField address = formField();

        String[] labels = {"First Name:", "Last Name:", "Gender:", "Birth Date (YYYY-MM-DD):", "Phone:", "Address:"};
        Component[] comps = {fName, lName, genderCombo, dob, contact, address};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; dlg.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; dlg.add(comps[i], gbc);
        }

        JButton saveBtn = accentButton("Save", ACCENT2);
        gbc.gridx = 1; gbc.gridy = labels.length; dlg.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            try {
                Patient pt = new Patient(0, fName.getText().trim(), lName.getText().trim(),
                        (Gender) genderCombo.getSelectedItem(), LocalDate.parse(dob.getText().trim()),
                        contact.getText().trim(), address.getText().trim());
                String result = PatientController.getInstance().addPatient(pt);
                if (result.startsWith("SUCCESS")) {
                    refreshPatientTable(tableModel);
                    updateDashboardStats();
                    dlg.dispose();
                }
                JOptionPane.showMessageDialog(this, result);
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage()); }
        });
        dlg.setVisible(true);
    }

    private JPanel buildBedPanel() {
        JPanel p = mainPanel("🛏  Bed Admissions");
        String[] cols = {"ID", "Patient", "Nurse", "Bed No", "In", "Out", "Amount"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = styledTable(model);
        refreshBedTable(model);

        JTextField searchField = new JTextField(10);
        JButton searchBtn = accentButton("Search", ACCENT);
        searchBtn.addActionListener(e -> {
            model.setRowCount(0);
            AdmissionController.getInstance().getAllBedRecords().stream()
                    .filter(r -> String.valueOf(r.getPatientId()).contains(searchField.getText()))
                    .forEach(r -> model.addRow(new Object[]{r.getId(), r.getPatientId(), r.getNurseNo(), r.getNo(), r.getDate(), r.getEndingDate(), r.getAmount() + " $"}));
        });

        JButton addBtn = accentButton("+ Admit", ACCENT2);
        addBtn.addActionListener(e -> showAddBedDialog(model));

        JButton delBtn = accentButton("Discharge", DANGER);
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) { refreshBedTable(model); updateDashboardStats(); }
        });

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        toolbar.setBackground(BG);
        toolbar.add(new JLabel("Patient ID:")); toolbar.add(searchField); toolbar.add(searchBtn);
        toolbar.add(Box.createHorizontalStrut(20)); toolbar.add(addBtn); toolbar.add(delBtn);

        p.add(toolbar, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void refreshBedTable(DefaultTableModel model) {
        model.setRowCount(0);
        AdmissionController.getInstance().getAllBedRecords().forEach(r -> model.addRow(new Object[]{r.getId(), r.getPatientId(), r.getNurseNo(), r.getNo(), r.getDate(), r.getEndingDate() != null ? r.getEndingDate() : "Active", r.getAmount() + " $"}));
    }

    private void showAddBedDialog(DefaultTableModel tableModel) {
        JDialog dlg = new JDialog(this, "Bed Admission", true);
        dlg.setLayout(new GridBagLayout()); dlg.setSize(450, 450); dlg.setLocationRelativeTo(this);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(7, 10, 7, 10); gbc.fill=2;

        JTextField pid = formField(); JTextField nid = formField(); JTextField bno = formField();
        JTextField amt = formField(); JComboBox<PaymentType> pt = new JComboBox<>(PaymentType.values());

        dlg.add(new JLabel("Patient ID:"), gbc); gbc.gridx=1; dlg.add(pid, gbc); gbc.gridx=0; gbc.gridy=1;
        dlg.add(new JLabel("Nurse ID:"), gbc); gbc.gridx=1; dlg.add(nid, gbc); gbc.gridx=0; gbc.gridy=2;
        dlg.add(new JLabel("Bed No:"), gbc); gbc.gridx=1; dlg.add(bno, gbc); gbc.gridx=0; gbc.gridy=3;
        dlg.add(new JLabel("Amount:"), gbc); gbc.gridx=1; dlg.add(amt, gbc); gbc.gridx=0; gbc.gridy=4;
        dlg.add(new JLabel("Payment:"), gbc); gbc.gridx=1; dlg.add(pt, gbc);

        JButton save = accentButton("Admit", ACCENT2); gbc.gridy=5; dlg.add(save, gbc);
        save.addActionListener(e -> {
            try {
                AdmissionController.getInstance().admitToBed(Integer.parseInt(pid.getText()), Integer.parseInt(nid.getText()), Integer.parseInt(bno.getText()), LocalDate.now(), Integer.parseInt(amt.getText()), (PaymentType)pt.getSelectedItem());
                refreshBedTable(tableModel); updateDashboardStats(); dlg.dispose();
            } catch(Exception ex) { JOptionPane.showMessageDialog(dlg, ex.getMessage()); }
        });
        dlg.setVisible(true);
    }

    private JPanel buildMedRecordPanel() {
        JPanel p = mainPanel("📋  Medical Records");
        String[] cols = {"Record ID", "Patient", "Doctor", "Diagnosis", "Date", "Weight", "Height", "BP", "Temp", "Treatment"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = styledTable(model);
        refreshMedTable(model);

        JTextField searchField = new JTextField(12);
        JButton searchBtn = accentButton("Search", ACCENT);
        searchBtn.addActionListener(e -> {
            model.setRowCount(0);
            new MedicalRecordDAO().findByPatient(Integer.parseInt(searchField.getText())).forEach(r -> model.addRow(new Object[]{r.getId(), r.getPatientId(), r.getDoctId(), r.getDiagnosis(), r.getDate(), r.getWeight(), r.getHeight(), r.getBloodPresure(), r.getTemp(), r.getTreatment()}));
        });

        JButton addBtn = accentButton("+ New", ACCENT2);
        addBtn.addActionListener(e -> showAddMedRecordDialog(model));

        JButton delBtn = accentButton("Delete", DANGER);
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) { new MedicalRecordDAO().delete((int)model.getValueAt(row, 0)); refreshMedTable(model); }
        });

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        toolbar.setBackground(BG);
        toolbar.add(new JLabel("Patient ID:")); toolbar.add(searchField); toolbar.add(searchBtn);
        toolbar.add(Box.createHorizontalStrut(20)); toolbar.add(addBtn); toolbar.add(delBtn);

        p.add(toolbar, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }
    private void refreshMedTable(DefaultTableModel model) {
        model.setRowCount(0);
        MedicalRecordController.getInstance().findAll().forEach(r -> model.addRow(new Object[]{r.getId(), r.getPatientId(), r.getDoctId(), r.getDiagnosis(), r.getDate(), r.getWeight(), r.getHeight(), r.getBloodPresure(), r.getTemp(), r.getTreatment()}));
    }

    private void showAddMedRecordDialog(DefaultTableModel tableModel) {
        JDialog dlg = new JDialog(this, "Add Medical Record", true);
        dlg.setLayout(new GridBagLayout()); dlg.setSize(500, 600); dlg.setLocationRelativeTo(this);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(5, 10, 5, 10); gbc.fill = 2;

        JTextField pid = formField(); JTextField did = formField(); JTextField w = formField(); JTextField h = formField();
        JTextField bp = formField(); JTextField t = formField(); JTextArea diag = new JTextArea(2, 20);
        JTextArea treat = new JTextArea(2, 20); JTextField vDate = formField(); JTextField nVisit = formField();
        vDate.setText(LocalDate.now().toString());

        Object[][] fields = {{"Patient ID:", pid}, {"Doctor ID:", did}, {"Visit Date:", vDate}, {"Weight:", w}, {"Height:", h},
                {"BP:", bp}, {"Temp:", t}, {"Diagnosis:", new JScrollPane(diag)}, {"Treatment:", new JScrollPane(treat)}, {"Next Visit:", nVisit}};

        for (int i = 0; i < fields.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; dlg.add(new JLabel((String)fields[i][0]), gbc);
            gbc.gridx = 1; dlg.add((Component)fields[i][1], gbc);
        }
        JButton save = accentButton("Save", ACCENT2); gbc.gridy = fields.length; dlg.add(save, gbc);
        save.addActionListener(e -> {
            try {
                MedicalRecordController.getInstance().addRecord(Integer.parseInt(pid.getText()), Integer.parseInt(did.getText()),
                        diag.getText(), treat.getText(), Integer.parseInt(w.getText()), Integer.parseInt(h.getText()), bp.getText(),
                        Integer.parseInt(t.getText()), LocalDate.parse(vDate.getText()), nVisit.getText().isEmpty() ? null : LocalDate.parse(nVisit.getText()));
                refreshMedTable(tableModel); dlg.dispose();
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage()); }
        });
        dlg.setVisible(true);
    }
    private void refreshDoctorTable(DefaultTableModel model) {
        model.setRowCount(0);
        doctorDAO.findAll().forEach(d -> model.addRow(new Object[]{d.getId(), d.getFname(), d.getLname(), d.getSurgeonType(), d.getDeptId()}));
    }

    private JPanel buildDoctorPanel() {
        JPanel p = mainPanel("👨‍⚕️  Doctor Management");
        String[] cols = {"ID", "FName", "LName", "Specialty", "Dept ID"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = styledTable(model);
        refreshDoctorTable(model);

        JTextField searchField = new JTextField(15);
        JButton searchBtn = accentButton("Search", ACCENT);
        searchBtn.addActionListener(e -> {
            model.setRowCount(0);
            doctorDAO.findAll().stream()
                    .filter(d -> d.getFname().toLowerCase().contains(searchField.getText().toLowerCase()) || d.getLname().toLowerCase().contains(searchField.getText().toLowerCase()))
                    .forEach(d -> model.addRow(new Object[]{d.getId(), d.getFname(), d.getLname(), d.getSurgeonType(), d.getDeptId()}));
        });

        JButton addBtn = accentButton("+ New Doctor", ACCENT2);
        addBtn.addActionListener(e -> showAddDoctorDialog(model));

        JButton delBtn = accentButton("Delete", DANGER);
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) model.getValueAt(row, 0);
                if(doctorDAO.delete(id)) { refreshDoctorTable(model); updateDashboardStats(); }
            }
        });

        p.add(createToolbar("Name:", searchField, searchBtn, addBtn, delBtn), BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void showAddDoctorDialog(DefaultTableModel tableModel) {
        JDialog dlg = new JDialog(this, "Add Doctor", true);
        dlg.setLayout(new GridBagLayout()); dlg.setSize(400, 480); dlg.setLocationRelativeTo(this);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(5,5,5,5); gbc.fill=2;
        JTextField fn = formField(); JTextField ln = formField(); JComboBox<Gender> g = new JComboBox<>(Gender.values());
        JTextField spec = formField(); JTextField did = formField();
        dlg.add(new JLabel("First Name:"), gbc); gbc.gridx=1; dlg.add(fn, gbc); gbc.gridx=0; gbc.gridy=1;
        dlg.add(new JLabel("Last Name:"), gbc); gbc.gridx=1; dlg.add(ln, gbc); gbc.gridx=0; gbc.gridy=2;
        dlg.add(new JLabel("Gender:"), gbc); gbc.gridx=1; dlg.add(g, gbc); gbc.gridx=0; gbc.gridy=3;
        dlg.add(new JLabel("Specialty:"), gbc); gbc.gridx=1; dlg.add(spec, gbc); gbc.gridx=0; gbc.gridy=4;
        dlg.add(new JLabel("Dept ID:"), gbc); gbc.gridx=1; dlg.add(did, gbc);
        JButton save = accentButton("Save", ACCENT2); gbc.gridy=5; dlg.add(save, gbc);
        save.addActionListener(e -> {
            try {
                Doctor d = new Doctor(0, fn.getText(), ln.getText(), (Gender)g.getSelectedItem(), spec.getText(), Integer.parseInt(did.getText()), "", "");
                DoctorController.getInstance().addDoctor(d); refreshDoctorTable(tableModel); updateDashboardStats(); dlg.dispose();
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, ex.getMessage()); }
        });
        dlg.setVisible(true);
    }

    private JPanel mainPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG); p.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        JLabel lbl = new JLabel(title); lbl.setFont(HEADER); lbl.setForeground(TEXT_DARK);
        p.add(lbl, BorderLayout.NORTH);
        return p;
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model); t.setFont(BODY); t.setRowHeight(32);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        return t;
    }

    private JButton accentButton(String text, Color color) {
        JButton b = new JButton(text); b.setBackground(color); b.setForeground(WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12)); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JTextField formField() {
        JTextField f = new JTextField(); f.setPreferredSize(new Dimension(200, 30));
        return f;
    }

    private JPanel createToolbar(String searchLabel, JTextField searchField, JButton searchBtn, JButton addBtn, JButton delBtn) {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        toolbar.setBackground(BG);
        toolbar.add(new JLabel(searchLabel)); toolbar.add(searchField); toolbar.add(searchBtn);
        toolbar.add(Box.createHorizontalStrut(20)); toolbar.add(addBtn); toolbar.add(delBtn);
        return toolbar;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }

}