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
import java.awt.event.*;
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

        // Paneller CardLayout'a ekleniyor
        contentPanel.add(buildDashboard(),      "dashboard");
        contentPanel.add(buildPatientPanel(),   "patients");
        contentPanel.add(buildMedRecordPanel(), "medrecords");
        contentPanel.add(buildBedPanel(),       "beds");
        contentPanel.add(buildAppointPanel(),   "appointments");
        contentPanel.add(buildSurgeryPanel(),   "surgery");
        contentPanel.add(buildDoctorPanel(),    "doctors");
        contentPanel.add(buildNursePanel(),     "nurses");

        add(contentPanel, BorderLayout.CENTER);

        updateDashboardStats();
        cardLayout.show(contentPanel, "dashboard");
    }

    // --- ÖZELLİK: UZUN METİNLERİ GÖSTEREN POPUP (Mouse Listener) ---
    private void addTableDetailSupport(JTable table) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Çift tıklama kontrolü
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();
                    if (row != -1 && col != -1) {
                        Object val = table.getValueAt(row, col);
                        if (val != null) {
                            JTextArea textArea = new JTextArea(val.toString());
                            textArea.setEditable(false);
                            textArea.setFont(BODY);
                            textArea.setLineWrap(true);
                            textArea.setWrapStyleWord(true);
                            JScrollPane scroll = new JScrollPane(textArea);
                            scroll.setPreferredSize(new Dimension(400, 200));
                            JOptionPane.showMessageDialog(MainFrame.this, scroll, "Detail View", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    private Integer tryParse(String text) {
        try { return Integer.parseInt(text); } catch (NumberFormatException e) { return null; }
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
                {"👩‍⚕️", "Nurses",           "nurses"}
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

        cards.add(statCard("Total Patients", lblPatientStat, ACCENT, "👤"));
        cards.add(statCard("Total Doctors",  lblDoctorStat,  ACCENT2, "👨‍⚕️"));
        cards.add(statCard("Total Nurses",   lblNurseStat,   new Color(0x1ABC9C), "👩‍⚕️"));
        cards.add(statCard("Bed Admissions", lblBedStat,   DANGER, "🛏"));
        cards.add(statCard("Appointments", lblAppointStat, new Color(0xF39C12), "📅"));
        cards.add(statCard("Avail. Beds", lblAvailBedStat, new Color(0x9B59B6), "🆓"));

        p.add(cards, BorderLayout.CENTER);
        return p;
    }

    private void updateDashboardStats() {
        lblPatientStat.setText(String.valueOf(getCount("patients")));
        lblDoctorStat.setText(String.valueOf(getCount("doctor")));
        lblNurseStat.setText(String.valueOf(getCount("nurse")));
        lblBedStat.setText(String.valueOf(getCount("bedrecords")));
        lblAppointStat.setText(String.valueOf(getCount("appointment")));
        lblAvailBedStat.setText(String.valueOf(AdmissionController.getInstance().getAvailableBeds().size()));
    }

    private int getCount(String table) {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // --- 1) PATIENT PANEL (Updated Column Names & Selective Search) ---
    private JPanel buildPatientPanel() {
        JPanel p = mainPanel("👤  Patient Management");
        String[] cols = {"Patient ID", "First Name", "Last Name", "Gender", "Date of Birth", "Contact No", "Address"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);
        addTableDetailSupport(table);
        refreshPatientTable(model);

        JTextField searchField = new JTextField(15);
        JButton searchBtn = accentButton("Search", ACCENT);
        searchBtn.addActionListener(e -> {
            String kw = searchField.getText().trim();
            model.setRowCount(0);
            Integer id = tryParse(kw);
            if (id != null) {
                Patient found = patientDAO.findById(id);
                if (found != null) {
                    model.addRow(new Object[]{found.getId(), found.getFname(), found.getLname(), found.getGender(), found.getData_birth(), found.getContact(), found.getAddress()});
                    return;
                }
            }
            // Selective Search: Only Name and Last Name
            patientDAO.findAll().stream()
                    .filter(pt -> pt.getFname().toLowerCase().contains(kw.toLowerCase()) || pt.getLname().toLowerCase().contains(kw.toLowerCase()))
                    .forEach(pt -> model.addRow(new Object[]{pt.getId(), pt.getFname(), pt.getLname(), pt.getGender(), pt.getData_birth(), pt.getContact(), pt.getAddress()}));
        });

        JButton addBtn = accentButton("+ New Patient", ACCENT2);
        addBtn.addActionListener(ev -> showAddPatientDialog(model));
        JButton delBtn = accentButton("Delete", DANGER);
        delBtn.addActionListener(ev -> {
            int row = table.getSelectedRow();
            if (row >= 0) { patientDAO.deletePatient((int)model.getValueAt(row, 0)); refreshPatientTable(model); updateDashboardStats(); }
        });

        p.add(createToolbar("Search (ID/Name):", searchField, searchBtn, addBtn, delBtn), BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void refreshPatientTable(DefaultTableModel model) {
        model.setRowCount(0);
        patientDAO.findAll().forEach(pt -> model.addRow(new Object[]{pt.getId(), pt.getFname(), pt.getLname(), pt.getGender(), pt.getData_birth(), pt.getContact(), pt.getAddress()}));
    }

    private void showAddPatientDialog(DefaultTableModel tableModel) {
        JDialog dlg = new JDialog(this, "Add Patient", true);
        dlg.setLayout(new GridBagLayout()); dlg.setSize(450, 480); dlg.setLocationRelativeTo(this);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(8, 12, 8, 12); gbc.fill = 2;
        JTextField fn = formField(); JTextField ln = formField(); JComboBox<Gender> gc = new JComboBox<>(Gender.values());
        JTextField db = formField(); JTextField co = formField(); JTextField ad = formField();
        String[] lb = {"First Name:", "Last Name:", "Gender:", "Birth Date:", "Phone:", "Address:"};
        Component[] cp = {fn, ln, gc, db, co, ad};
        for (int i = 0; i < lb.length; i++) { gbc.gridx = 0; gbc.gridy = i; dlg.add(new JLabel(lb[i]), gbc); gbc.gridx = 1; dlg.add(cp[i], gbc); }
        JButton s = accentButton("Save", ACCENT2); gbc.gridy = lb.length; dlg.add(s, gbc);
        s.addActionListener(e -> {
            try {
                Patient p = new Patient(0, fn.getText(), ln.getText(), (Gender)gc.getSelectedItem(), LocalDate.parse(db.getText()), co.getText(), ad.getText());
                if (PatientController.getInstance().addPatient(p).startsWith("SUCCESS")) { refreshPatientTable(tableModel); updateDashboardStats(); dlg.dispose(); }
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage()); }
        });
        dlg.setVisible(true);
    }

    // --- 2) DOCTOR PANEL ---
    private JPanel buildDoctorPanel() {
        JPanel p = mainPanel("👨‍⚕️  Doctor Management");
        String[] cols = {"Doctor ID", "First Name", "Last Name", "Specialty", "Dept ID", "Office", "Contact"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = styledTable(model);
        addTableDetailSupport(table);
        refreshDoctorTable(model);

        JTextField searchField = new JTextField(15);
        JButton searchBtn = accentButton("Search", ACCENT);
        searchBtn.addActionListener(e -> {
            String kw = searchField.getText().trim();
            model.setRowCount(0);
            Integer id = tryParse(kw);
            if (id != null) {
                Doctor d = doctorDAO.findById(id);
                if (d != null) { model.addRow(new Object[]{d.getId(), d.getFname(), d.getLname(), d.getSurgeonType(), d.getDeptId(), d.getOfficeNo(), d.getContact()}); return; }
            }
            doctorDAO.findAll().stream()
                    .filter(d -> d.getFname().toLowerCase().contains(kw.toLowerCase()) || d.getLname().toLowerCase().contains(kw.toLowerCase()))
                    .forEach(d -> model.addRow(new Object[]{d.getId(), d.getFname(), d.getLname(), d.getSurgeonType(), d.getDeptId(), d.getOfficeNo(), d.getContact()}));
        });

        JButton addBtn = accentButton("+ New Doctor", ACCENT2);
        addBtn.addActionListener(ev -> showAddDoctorDialog(model));
        JButton delBtn = accentButton("Delete", DANGER);
        delBtn.addActionListener(ev -> {
            int row = table.getSelectedRow();
            if (row >= 0) { doctorDAO.delete((int)model.getValueAt(row, 0)); refreshDoctorTable(model); updateDashboardStats(); }
        });

        p.add(createToolbar("Search (ID/Name):", searchField, searchBtn, addBtn, delBtn), BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void refreshDoctorTable(DefaultTableModel model) {
        model.setRowCount(0);
        doctorDAO.findAll().forEach(d -> model.addRow(new Object[]{d.getId(), d.getFname(), d.getLname(), d.getSurgeonType(), d.getDeptId(), d.getOfficeNo(), d.getContact()}));
    }

    private void showAddDoctorDialog(DefaultTableModel tableModel) {
        JDialog dlg = new JDialog(this, "Add Doctor", true);
        dlg.setLayout(new GridBagLayout()); dlg.setSize(400, 480); dlg.setLocationRelativeTo(this);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(5,5,5,5); gbc.fill=2;
        JTextField fn = formField(); JTextField ln = formField(); JComboBox<Gender> gc = new JComboBox<>(Gender.values());
        JTextField sp = formField(); JTextField di = formField(); JTextField of = formField(); JTextField co = formField();
        Object[][] fds = {{"First Name:", fn}, {"Last Name:", ln}, {"Gender:", gc}, {"Specialty:", sp}, {"Dept ID:", di}, {"Office:", of}, {"Phone:", co}};
        for (int i = 0; i < fds.length; i++) { gbc.gridx = 0; gbc.gridy = i; dlg.add(new JLabel((String)fds[i][0]), gbc); gbc.gridx = 1; dlg.add((Component)fds[i][1], gbc); }
        JButton s = accentButton("Save", ACCENT2); gbc.gridy = fds.length; dlg.add(s, gbc);
        s.addActionListener(e -> {
            try {
                Doctor d = new Doctor(0, fn.getText(), ln.getText(), (Gender)gc.getSelectedItem(), sp.getText(), Integer.parseInt(di.getText()), of.getText(), co.getText());
                DoctorController.getInstance().addDoctor(d); refreshDoctorTable(tableModel); updateDashboardStats(); dlg.dispose();
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage()); }
        });
        dlg.setVisible(true);
    }

    // --- 3) NURSE PANEL ---
    private JPanel buildNursePanel() {
        JPanel p = mainPanel("👩‍⚕️  Nurse Management");
        String[] cols = {"Nurse ID", "First Name", "Last Name", "Gender", "Dept ID", "Contact"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = styledTable(model);
        addTableDetailSupport(table);
        refreshNurseTable(model);

        JTextField searchField = new JTextField(15);
        JButton searchBtn = accentButton("Search", ACCENT);
        searchBtn.addActionListener(e -> {
            String kw = searchField.getText().trim();
            model.setRowCount(0);
            Integer id = tryParse(kw);
            if (id != null) {
                Nurse n = nurseDAO.findById(id);
                if (n != null) { model.addRow(new Object[]{n.getId(), n.getFname(), n.getLname(), n.getGender(), n.getDeptId(), n.getContact()}); return; }
            }
            // Selective Search: Name, Last Name or Dept ID
            nurseDAO.findAll().stream()
                    .filter(n -> n.getFname().toLowerCase().contains(kw.toLowerCase()) || n.getLname().toLowerCase().contains(kw.toLowerCase()) || String.valueOf(n.getDeptId()).equals(kw))
                    .forEach(n -> model.addRow(new Object[]{n.getId(), n.getFname(), n.getLname(), n.getGender(), n.getDeptId(), n.getContact()}));
        });

        JButton addBtn = accentButton("+ New Nurse", ACCENT2);
        addBtn.addActionListener(ev -> showAddNurseDialog(model));
        JButton delBtn = accentButton("Delete", DANGER);
        delBtn.addActionListener(ev -> {
            int row = table.getSelectedRow();
            if (row >= 0) { NurseController.getInstance().deleteNurse((int)model.getValueAt(row, 0)); refreshNurseTable(model); updateDashboardStats(); }
        });

        p.add(createToolbar("Search (ID/Name/Dept):", searchField, searchBtn, addBtn, delBtn), BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void refreshNurseTable(DefaultTableModel model) {
        model.setRowCount(0);
        NurseController.getInstance().findAll().forEach(n -> model.addRow(new Object[]{n.getId(), n.getFname(), n.getLname(), n.getGender(), n.getDeptId(), n.getContact()}));
    }

    private void showAddNurseDialog(DefaultTableModel tableModel) {
        JDialog dlg = new JDialog(this, "Add Nurse", true);
        dlg.setLayout(new GridBagLayout()); dlg.setSize(400, 450); dlg.setLocationRelativeTo(this);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(5,5,5,5); gbc.fill=2;
        JTextField fn = formField(); JTextField ln = formField(); JComboBox<Gender> gc = new JComboBox<>(Gender.values());
        JTextField di = formField(); JTextField co = formField();
        Object[][] fds = {{"First Name:", fn}, {"Last Name:", ln}, {"Gender:", gc}, {"Dept ID:", di}, {"Contact:", co}};
        for (int i = 0; i < fds.length; i++) { gbc.gridx = 0; gbc.gridy = i; dlg.add(new JLabel((String)fds[i][0]), gbc); gbc.gridx = 1; dlg.add((Component)fds[i][1], gbc); }
        JButton s = accentButton("Save", ACCENT2); gbc.gridy = fds.length; dlg.add(s, gbc);
        s.addActionListener(e -> {
            try {
                Nurse n = new Nurse(0, Integer.parseInt(di.getText()), fn.getText(), ln.getText(), (Gender)gc.getSelectedItem(), co.getText());
                if (NurseController.getInstance().addNurse(n).startsWith("SUCCESS")) { refreshNurseTable(tableModel); updateDashboardStats(); dlg.dispose(); }
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage()); }
        });
        dlg.setVisible(true);
    }

    // --- 4) MEDICAL RECORDS PANEL ---
    private JPanel buildMedRecordPanel() {
        JPanel p = mainPanel("📋  Medical Records");
        String[] cols = {"Record ID", "Patient ID", "Doctor ID", "Diagnosis", "Date", "Weight", "Height", "BP", "Temp", "Treatment"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = styledTable(model);
        addTableDetailSupport(table);
        refreshMedTable(model);

        JTextField searchField = new JTextField(12);
        JButton searchBtn = accentButton("Search", ACCENT);
        searchBtn.addActionListener(e -> {
            String kw = searchField.getText().trim(); model.setRowCount(0);
            Integer id = tryParse(kw);
            // Search by Record ID or Patient ID
            MedicalRecordController.getInstance().findAll().stream()
                    .filter(r -> (id != null && (r.getId() == id || r.getPatientId() == id)))
                    .forEach(r -> model.addRow(new Object[]{r.getId(), r.getPatientId(), r.getDoctId(), r.getDiagnosis(), r.getDate(), r.getWeight(), r.getHeight(), r.getBloodPresure(), r.getTemp(), r.getTreatment()}));
        });

        JButton addBtn = accentButton("+ New Record", ACCENT2);
        addBtn.addActionListener(ev -> showAddMedRecordDialog(model));
        JButton delBtn = accentButton("Delete", DANGER);
        delBtn.addActionListener(ev -> {
            int row = table.getSelectedRow();
            if (row >= 0) { new MedicalRecordDAO().delete((int)model.getValueAt(row, 0)); refreshMedTable(model); }
        });

        p.add(createToolbar("Search (Rec/Patient ID):", searchField, searchBtn, addBtn, delBtn), BorderLayout.NORTH);
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
        JTextField pi = formField(); JTextField di = formField(); JTextField we = formField(); JTextField he = formField();
        JTextField bp = formField(); JTextField te = formField(); JTextArea dg = new JTextArea(2, 20); JTextArea tr = new JTextArea(2, 20);
        JTextField vd = formField(); vd.setText(LocalDate.now().toString());
        Object[][] fds = {{"Patient ID:", pi}, {"Doctor ID:", di}, {"Visit Date:", vd}, {"Weight:", we}, {"Height:", he}, {"BP:", bp}, {"Temp:", te}, {"Diagnosis:", new JScrollPane(dg)}, {"Treatment:", new JScrollPane(tr)}};
        for (int i = 0; i < fds.length; i++) { gbc.gridx = 0; gbc.gridy = i; dlg.add(new JLabel((String)fds[i][0]), gbc); gbc.gridx = 1; dlg.add((Component)fds[i][1], gbc); }
        JButton s = accentButton("Save", ACCENT2); gbc.gridy = fds.length; dlg.add(s, gbc);
        s.addActionListener(e -> {
            try {
                String rs = MedicalRecordController.getInstance().addRecord(Integer.parseInt(pi.getText()), Integer.parseInt(di.getText()), dg.getText(), tr.getText(), Integer.parseInt(we.getText()), Integer.parseInt(he.getText()), bp.getText(), Integer.parseInt(te.getText()), LocalDate.parse(vd.getText()), null);
                if (rs.startsWith("SUCCESS")) { refreshMedTable(tableModel); dlg.dispose(); }
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage()); }
        });
        dlg.setVisible(true);
    }

    // --- 5) BED ADMISSIONS PANEL ---
    private JPanel buildBedPanel() {
        JPanel p = mainPanel("🛏  Bed Admissions");
        String[] cols = {"Adm ID", "Patient ID", "Nurse ID", "Bed No", "Check-in", "Check-out", "Amount"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = styledTable(model);
        addTableDetailSupport(table);
        refreshBedTable(model);

        JTextField searchField = new JTextField(10);
        JButton searchBtn = accentButton("Search", ACCENT);
        searchBtn.addActionListener(e -> {
            String kw = searchField.getText().trim(); model.setRowCount(0);
            Integer id = tryParse(kw);
            AdmissionController.getInstance().getAllBedRecords().stream()
                    .filter(r -> (id != null && (r.getId() == id || r.getPatientId() == id)))
                    .forEach(r -> model.addRow(new Object[]{r.getId(), r.getPatientId(), r.getNurseNo(), r.getNo(), r.getDate(), r.getEndingDate(), r.getAmount() + " $"}));
        });

        JButton addBtn = accentButton("+ Admit", ACCENT2);
        addBtn.addActionListener(ev -> showAddBedDialog(model));
        JButton delBtn = accentButton("Discharge", DANGER);
        p.add(createToolbar("Search (Adm/Patient ID):", searchField, searchBtn, addBtn, delBtn), BorderLayout.NORTH);
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
        JTextField pi = formField(); JTextField ni = formField(); JTextField bn = formField(); JTextField am = formField();
        JComboBox<PaymentType> pt = new JComboBox<>(PaymentType.values());
        Object[][] fds = {{"Patient ID:", pi}, {"Nurse ID:", ni}, {"Bed No:", bn}, {"Amount:", am}, {"Payment:", pt}};
        for (int i = 0; i < fds.length; i++) { gbc.gridx = 0; gbc.gridy = i; dlg.add(new JLabel((String)fds[i][0]), gbc); gbc.gridx = 1; dlg.add((Component)fds[i][1], gbc); }
        JButton s = accentButton("Save", ACCENT2); gbc.gridy = fds.length; dlg.add(s, gbc);
        s.addActionListener(e -> {
            try {
                AdmissionController.getInstance().admitToBed(Integer.parseInt(pi.getText()), Integer.parseInt(ni.getText()), Integer.parseInt(bn.getText()), LocalDate.now(), Integer.parseInt(am.getText()), (PaymentType)pt.getSelectedItem());
                refreshBedTable(tableModel); updateDashboardStats(); dlg.dispose();
            } catch(Exception ex) { JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage()); }
        });
        dlg.setVisible(true);
    }

    // --- 6) APPOINTMENT PANEL ---
    private JPanel buildAppointPanel() {
        JPanel p = mainPanel("📅  Appointments");
        String[] cols = {"App ID", "Patient ID", "Doctor ID", "Date", "Status", "Reason", "Type"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = styledTable(model);
        addTableDetailSupport(table);
        refreshAppointTable(model);

        JTextField searchField = new JTextField(12);
        JButton searchBtn = accentButton("Search", ACCENT);
        searchBtn.addActionListener(e -> {
            String kw = searchField.getText().trim(); model.setRowCount(0);
            Integer id = tryParse(kw);
            AppointmentController.getInstance().findAll().stream()
                    .filter(a -> (id != null && (a.getId() == id || a.getPatientId() == id || a.getDoctorId() == id)))
                    .forEach(a -> model.addRow(new Object[]{a.getId(), a.getPatientId(), a.getDoctorId(), a.getDate(), a.getStatus(), a.getReason(), a.getAppointmentType()}));
        });

        JButton addBtn = accentButton("+ New", ACCENT2);
        addBtn.addActionListener(ev -> showAddAppointDialog(model));
        JButton delBtn = accentButton("Cancel", DANGER);
        p.add(createToolbar("Search (ID):", searchField, searchBtn, addBtn, delBtn), BorderLayout.NORTH);
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
        JTextField pi = formField(); JTextField di = formField(); JTextField dt = formField(); JTextField rs = formField(); JTextField am = formField();
        JComboBox<PaymentType> pc = new JComboBox<>(PaymentType.values()); JComboBox<AppointmentType> ac = new JComboBox<>(AppointmentType.values());
        dt.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Object[][] fds = {{"Patient ID:", pi}, {"Doctor ID:", di}, {"Date:", dt}, {"Reason:", rs}, {"Amount:", am}, {"Payment:", pc}, {"Type:", ac}};
        for (int i = 0; i < fds.length; i++) { gbc.gridx = 0; gbc.gridy = i; dlg.add(new JLabel((String)fds[i][0]), gbc); gbc.gridx = 1; dlg.add((Component)fds[i][1], gbc); }
        JButton s = accentButton("Save", ACCENT2); gbc.gridy = fds.length; dlg.add(s, gbc);
        s.addActionListener(e -> {
            try {
                AppointmentController.getInstance().createAppointment(Integer.parseInt(pi.getText()), Integer.parseInt(di.getText()), LocalDateTime.parse(dt.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), rs.getText(), Integer.parseInt(am.getText()), (PaymentType)pc.getSelectedItem(), (AppointmentType)ac.getSelectedItem());
                refreshAppointTable(tableModel); updateDashboardStats(); dlg.dispose();
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage()); }
        });
        dlg.setVisible(true);
    }

    // --- 7) SURGERY PANEL ---
    private JPanel buildSurgeryPanel() {
        JPanel p = mainPanel("🔪  Surgery Records");
        String[] cols = {"Surg ID", "Patient ID", "Surgeon ID", "Date", "Room", "Type", "Notes"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = styledTable(model);
        addTableDetailSupport(table);
        refreshSurgeryTable(model);

        JTextField searchField = new JTextField(10);
        JButton searchBtn = accentButton("Search", ACCENT);
        searchBtn.addActionListener(e -> {
            String kw = searchField.getText().trim(); model.setRowCount(0);
            Integer id = tryParse(kw);
            SurgeryController.getInstance().findAll().stream()
                    .filter(s -> (id != null && (s.getId() == id || s.getPatientId() == id || s.getSurgeonId() == id)))
                    .forEach(s -> model.addRow(new Object[]{s.getId(), s.getPatientId(), s.getSurgeonId(), s.getDate(), s.getRoomNo(), s.getSurgeryType(), s.getNotes()}));
        });

        JButton addBtn = accentButton("+ New", ACCENT2);
        addBtn.addActionListener(ev -> showAddSurgeryDialog(model));
        JButton delBtn = accentButton("Delete", DANGER);
        p.add(createToolbar("Search (ID):", searchField, searchBtn, addBtn, delBtn), BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void refreshSurgeryTable(DefaultTableModel model) {
        model.setRowCount(0);
        SurgeryController.getInstance().findAll().forEach(s -> model.addRow(new Object[]{s.getId(), s.getPatientId(), s.getSurgeonId(), s.getDate(), s.getRoomNo(), s.getSurgeryType(), s.getNotes()}));
    }

    private void showAddSurgeryDialog(DefaultTableModel tableModel) {
        JDialog dlg = new JDialog(this, "Add Surgery", true);
        dlg.setLayout(new GridBagLayout()); dlg.setSize(450, 550); dlg.setLocationRelativeTo(this);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(5,5,5,5); gbc.fill=2;
        JTextField pi = formField(); JTextField si = formField(); JTextField ty = formField(); JTextField dt = formField(); JTextField rm = formField(); JTextArea nt = new JTextArea(3, 20);
        dt.setText(LocalDate.now().toString());
        Object[][] fds = {{"Patient ID:", pi}, {"Surgeon ID:", si}, {"Type:", ty}, {"Date:", dt}, {"Room:", rm}, {"Notes:", new JScrollPane(nt)}};
        for (int i = 0; i < fds.length; i++) { gbc.gridx = 0; gbc.gridy = i; dlg.add(new JLabel((String)fds[i][0]), gbc); gbc.gridx = 1; dlg.add((Component)fds[i][1], gbc); }
        JButton s = accentButton("Save", ACCENT2); gbc.gridy = fds.length; dlg.add(s, gbc);
        s.addActionListener(e -> {
            try {
                SurgeryController.getInstance().addSurgery(Integer.parseInt(pi.getText()), Integer.parseInt(si.getText()), 0, LocalDate.parse(dt.getText()), LocalTime.of(9,0), LocalTime.of(10,0), Integer.parseInt(rm.getText()), ty.getText(), nt.getText());
                refreshSurgeryTable(tableModel); dlg.dispose();
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage()); }
        });
        dlg.setVisible(true);
    }

    // --- REUSABLE UI HELPERS ---
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
        t.setSelectionBackground(new Color(0xD6E4FF));
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

    private JPanel createToolbar(String label, JTextField field, JButton sBtn, JButton aBtn, JButton dBtn) {
        JPanel t = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        t.setBackground(BG); t.add(new JLabel(label)); t.add(field); t.add(sBtn);
        t.add(Box.createHorizontalStrut(20)); t.add(aBtn); t.add(dBtn);
        return t;
    }

    private JPanel statCard(String label, JLabel valueLabel, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0xE0E6F0), 1, true), BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        JLabel ico = new JLabel(icon); ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 30)); valueLabel.setForeground(color);
        JLabel lbl = new JLabel(label); lbl.setFont(SMALL); lbl.setForeground(TEXT_MID);
        JPanel right = new JPanel(new BorderLayout()); right.setBackground(WHITE);
        right.add(valueLabel, BorderLayout.CENTER); right.add(lbl, BorderLayout.SOUTH);
        card.add(ico, BorderLayout.WEST); card.add(right, BorderLayout.CENTER);
        return card;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}