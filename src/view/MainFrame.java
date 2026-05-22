package view;

import controller.*;
import db.DBConnection;
import dao.*;
import enums.*;
import event.*;
import model.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MainFrame extends JFrame {

    private static final Color BG         = new Color(0xF8F9FB);
    private static final Color WHITE      = Color.WHITE;
    private static final Color SIDEBAR    = new Color(0x0F172A);
    private static final Color SIDEBAR_HV = new Color(0x1E293B);
    private static final Color ACCENT     = new Color(0x3B82F6);
    private static final Color ACCENT2    = new Color(0x22C55E);
    private static final Color DANGER     = new Color(0xEF4444);
    private static final Color TEXT_DARK  = new Color(0x0F172A);
    private static final Color TEXT_MID   = new Color(0x64748B);
    private static final Color TEXT_LIGHT = new Color(0x94A3B8);
    private static final Color BORDER_CLR = new Color(0xE2E8F0);
    private static final Color ROW_ALT    = new Color(0xF8FAFC);
    private static final Color ROW_SEL    = new Color(0xEFF6FF);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font FONT_BODY  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_BOLD  = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_NAV   = new Font("Segoe UI", Font.PLAIN, 13);

    private final JPanel     contentPanel;
    private final CardLayout cardLayout;

    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO  doctorDAO  = new DoctorDAO();
    private final NurseDAO   nurseDAO   = new NurseDAO();

    private JLabel lblPatientStat, lblDoctorStat, lblBedStat,
            lblAppointStat, lblAvailBedStat, lblNurseStat;

    private JPanel activeNavItem = null;

    public MainFrame() {
        setTitle("Hospital Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1350, 850);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getRootPane().putClientProperty("apple.awt.transparentTitleBar", Boolean.TRUE);

        add(buildSidebar(), BorderLayout.WEST);

        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG);

        contentPanel.add(buildDashboard(),       "dashboard");
        contentPanel.add(buildPatientPanel(),    "patients");
        contentPanel.add(buildMedRecordPanel(),  "medrecords");
        contentPanel.add(buildBedPanel(),        "beds");
        contentPanel.add(buildWardPanel(),       "wards");
        contentPanel.add(buildAppointPanel(),    "appointments");
        contentPanel.add(buildSurgeryPanel(),    "surgery");
        contentPanel.add(buildDoctorPanel(),     "doctors");
        contentPanel.add(buildNursePanel(),      "nurses");
        contentPanel.add(buildDepartmentPanel(), "departments");



        add(contentPanel, BorderLayout.CENTER);
        updateDashboardStats();
        cardLayout.show(contentPanel, "dashboard");
    }

    // SIDEBAR
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));

        JPanel logoArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 18));
        logoArea.setBackground(SIDEBAR);
        logoArea.setMaximumSize(new Dimension(220, 64));
        JLabel cross = new JLabel("+");
        cross.setFont(new Font("Segoe UI", Font.BOLD, 22));
        cross.setForeground(ACCENT);
        JLabel logoTxt = new JLabel("HospitalMS");
        logoTxt.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logoTxt.setForeground(WHITE);
        logoArea.add(cross); logoArea.add(logoTxt);
        sidebar.add(logoArea);
        sidebar.add(sidebarDivider());
        sidebar.add(Box.createVerticalStrut(8));

        sidebar.add(navLabel("OVERVIEW"));
        JPanel dashBtn = navItem("Dashboard", "dashboard");
        sidebar.add(dashBtn);
        setNavActive(dashBtn);
        activeNavItem = dashBtn;

        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(navLabel("MANAGEMENT"));
        sidebar.add(navItem("Patients",    "patients"));
        sidebar.add(navItem("Doctors",     "doctors"));
        sidebar.add(navItem("Nurses",      "nurses"));
        sidebar.add(navItem("Departments", "departments"));
        sidebar.add(navItem("Wards",       "wards"));

        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(navLabel("RECORDS"));
        sidebar.add(navItem("Medical Records", "medrecords"));
        sidebar.add(navItem("Bed Admissions",  "beds"));
        sidebar.add(navItem("Appointments",    "appointments"));
        sidebar.add(navItem("Surgery",         "surgery"));

        sidebar.add(Box.createVerticalGlue());
        JLabel ver = new JLabel("v1.0  ·  MySQL", SwingConstants.CENTER);
        ver.setFont(FONT_SMALL); ver.setForeground(new Color(0x475569));
        ver.setAlignmentX(Component.CENTER_ALIGNMENT);
        ver.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        sidebar.add(ver);
        return sidebar;
    }

    private JPanel navItem(String label, String card) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(SIDEBAR);
        item.setMaximumSize(new Dimension(220, 40));
        item.setPreferredSize(new Dimension(220, 40));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JPanel bar = new JPanel();
        bar.setPreferredSize(new Dimension(3, 40));
        bar.setBackground(SIDEBAR);
        item.add(bar, BorderLayout.WEST);
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_NAV);
        lbl.setForeground(new Color(0x94A3B8));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
        item.add(lbl, BorderLayout.CENTER);
        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (activeNavItem != item) { item.setBackground(SIDEBAR_HV); lbl.setForeground(new Color(0xCBD5E1)); } }
            public void mouseExited(MouseEvent e)  { if (activeNavItem != item) { item.setBackground(SIDEBAR); lbl.setForeground(new Color(0x94A3B8)); } }
            public void mouseClicked(MouseEvent e) {
                if (activeNavItem != null) setNavInactive(activeNavItem);
                setNavActive(item); activeNavItem = item;
                updateDashboardStats(); cardLayout.show(contentPanel, card);
            }
        });
        return item;
    }

    private void setNavActive(JPanel item) {
        item.setBackground(SIDEBAR_HV);
        ((JPanel) item.getComponent(0)).setBackground(ACCENT);
        JLabel lbl = (JLabel) item.getComponent(1);
        lbl.setFont(FONT_BOLD); lbl.setForeground(WHITE);
    }

    private void setNavInactive(JPanel item) {
        item.setBackground(SIDEBAR);
        ((JPanel) item.getComponent(0)).setBackground(SIDEBAR);
        JLabel lbl = (JLabel) item.getComponent(1);
        lbl.setFont(FONT_NAV); lbl.setForeground(new Color(0x94A3B8));
    }

    private JLabel navLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(new Color(0x475569));
        l.setBorder(BorderFactory.createEmptyBorder(4, 20, 4, 0));
        l.setMaximumSize(new Dimension(220, 24));
        return l;
    }

    private JSeparator sidebarDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x1E293B));
        sep.setMaximumSize(new Dimension(220, 1));
        return sep;
    }

    // DASHBOARD
    private JPanel buildDashboard() {
        JPanel p = new JPanel(new BorderLayout(0, 24));
        p.setBackground(BG); p.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        JPanel header = new JPanel(new BorderLayout()); header.setBackground(BG);
        JLabel title = new JLabel("Dashboard"); title.setFont(FONT_TITLE); title.setForeground(TEXT_DARK);
        JLabel sub = new JLabel("Overview of hospital operations"); sub.setFont(FONT_BODY); sub.setForeground(TEXT_MID);
        header.add(title, BorderLayout.NORTH); header.add(sub, BorderLayout.SOUTH);
        lblPatientStat = new JLabel("0"); lblDoctorStat = new JLabel("0"); lblNurseStat = new JLabel("0");
        lblBedStat = new JLabel("0"); lblAppointStat = new JLabel("0"); lblAvailBedStat = new JLabel("0");
        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16)); grid.setBackground(BG);
        grid.add(statCard("Total Patients",  lblPatientStat,  ACCENT,              "Registered patients"));
        grid.add(statCard("Total Doctors",   lblDoctorStat,   new Color(0x8B5CF6), "Active doctors"));
        grid.add(statCard("Total Nurses",    lblNurseStat,    new Color(0x06B6D4), "Active nurses"));
        grid.add(statCard("Bed Admissions",  lblBedStat,      DANGER,              "Total admissions"));
        grid.add(statCard("Appointments",    lblAppointStat,  new Color(0xF59E0B), "Scheduled"));
        grid.add(statCard("Available Beds",  lblAvailBedStat, ACCENT2,             "Ready for admission"));
        p.add(header, BorderLayout.NORTH); p.add(grid, BorderLayout.CENTER);
        return p;
    }

    private void updateDashboardStats() {
        if (lblPatientStat == null) return;
        lblPatientStat.setText(String.valueOf(getCount("patients")));
        lblDoctorStat.setText(String.valueOf(getCount("doctor")));
        lblNurseStat.setText(String.valueOf(getCount("nurse")));
        lblBedStat.setText(String.valueOf(getCount("bedrecords")));
        lblAppointStat.setText(String.valueOf(getCountApp()));
        lblAvailBedStat.setText(String.valueOf(AdmissionController.getInstance().getAvailableBeds().size()));
    }

    private int getCount(String table) {
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private int getCountApp() {
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM appointment WHERE appointment_status = 'Scheduled'")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // PATIENT PANEL
    private JPanel buildPatientPanel() {
        JPanel p = mainPanel("Patient Management", "Browse and manage patient records");
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Patient ID","First Name","Last Name","Gender","Date of Birth","Contact No","Address"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = styledTable(model); addTableDetailSupport(table); refreshPatientTable(model);
        JTextField sf = styledSearchField("Search by ID or name…");
        JButton sb = primaryBtn("Search");
        sb.addActionListener(e -> {
            String kw = sf.getText().trim(); model.setRowCount(0);
            Integer id = tryParse(kw);
            if (id != null) { Patient f = patientDAO.findById(id); if (f != null) { model.addRow(patientRow(f)); return; } }
            patientDAO.findAll().stream().filter(pt -> pt.getFname().toLowerCase().contains(kw.toLowerCase()) || pt.getLname().toLowerCase().contains(kw.toLowerCase())).forEach(pt -> model.addRow(patientRow(pt)));
        });
        JButton sab = showAllBtn(); sab.addActionListener(e -> refreshPatientTable(model));
        JButton ab = successBtn("+ New Patient"); ab.addActionListener(ev -> showAddPatientDialog(model));
        JButton db = dangerBtn("Delete");
        db.addActionListener(ev -> { int row = table.getSelectedRow(); if (row >= 0 && confirmDelete()) { patientDAO.deletePatient((int) model.getValueAt(row, 0)); refreshPatientTable(model); updateDashboardStats(); } });
        p.add(toolbar(sf, sb, sab, ab, db), BorderLayout.NORTH);
        p.add(scrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private Object[] patientRow(Patient pt) { return new Object[]{pt.getId(), pt.getFname(), pt.getLname(), pt.getGender(), pt.getData_birth(), pt.getContact(), pt.getAddress()}; }
    private void refreshPatientTable(DefaultTableModel model) { model.setRowCount(0); patientDAO.findAll().forEach(pt -> model.addRow(patientRow(pt))); }

    private void showAddPatientDialog(DefaultTableModel tableModel) {
        JDialog dlg = dialog("Add New Patient", 450, 400);
        JTextField fn = dlgField(); JTextField ln = dlgField();
        JComboBox<Gender> gc = new JComboBox<>(Gender.values());
        JTextField db = dlgField(); JTextField co = dlgField(); JTextField ad = dlgField();
        Object[][] fds = {{"First Name *",fn},{"Last Name *",ln},{"Gender",gc},{"Birth Date (YYYY-MM-DD)",db},{"Phone",co},{"Address",ad}};
        JButton save = successBtn("Save Patient");
        save.addActionListener(e -> {
            try {
                String res = PatientController.getInstance().addPatient(new Patient(0, fn.getText(), ln.getText(), (Gender)gc.getSelectedItem(), LocalDate.parse(db.getText()), co.getText(), ad.getText()));
                if (res.startsWith("SUCCESS")) { refreshPatientTable(tableModel); updateDashboardStats(); dlg.dispose(); }
                else JOptionPane.showMessageDialog(dlg, res, "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
        });
        layoutDialog(dlg, fds, save);
    }

    // DOCTOR PANEL
    private JPanel buildDoctorPanel() {
        JPanel p = mainPanel("Doctor Management", "Browse and manage doctor records");
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Doctor ID","First Name","Last Name","Specialty","Dept ID","Office","Contact"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = styledTable(model); addTableDetailSupport(table); refreshDoctorTable(model);
        JTextField sf = styledSearchField("Search by ID or name…");
        JButton sb = primaryBtn("Search");
        sb.addActionListener(e -> {
            String kw = sf.getText().trim(); model.setRowCount(0);
            Integer id = tryParse(kw);
            if (id != null) { Doctor d = doctorDAO.findById(id); if (d != null) { model.addRow(doctorRow(d)); return; } }
            doctorDAO.findAll().stream().filter(d -> d.getFname().toLowerCase().contains(kw.toLowerCase()) || d.getLname().toLowerCase().contains(kw.toLowerCase())).forEach(d -> model.addRow(doctorRow(d)));
        });
        JButton sab = showAllBtn(); sab.addActionListener(e -> refreshDoctorTable(model));
        JButton ab = successBtn("+ New Doctor"); ab.addActionListener(ev -> showAddDoctorDialog(model));
        JButton db = dangerBtn("Delete");
        db.addActionListener(ev -> { int row = table.getSelectedRow(); if (row >= 0 && confirmDelete()) { doctorDAO.delete((int)model.getValueAt(row,0)); refreshDoctorTable(model); updateDashboardStats(); } });
        p.add(toolbar(sf, sb, sab, ab, db), BorderLayout.NORTH);
        p.add(scrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private Object[] doctorRow(Doctor d) { return new Object[]{d.getId(), d.getFname(), d.getLname(), d.getSurgeonType(), d.getDeptId(), d.getOfficeNo(), d.getContact()}; }
    private void refreshDoctorTable(DefaultTableModel model) { model.setRowCount(0); doctorDAO.findAll().forEach(d -> model.addRow(doctorRow(d))); }

    private void showAddDoctorDialog(DefaultTableModel tableModel) {
        JDialog dlg = dialog("Add New Doctor", 450, 420);
        JTextField fn=dlgField(); JTextField ln=dlgField(); JComboBox<Gender> gc=new JComboBox<>(Gender.values());
        JTextField sp=dlgField(); JTextField di=dlgField(); JTextField of=dlgField(); JTextField co=dlgField();
        Object[][] fds = {{"First Name *",fn},{"Last Name *",ln},{"Gender",gc},{"Specialty",sp},{"Dept ID",di},{"Office",of},{"Phone",co}};
        JButton save = successBtn("Save Doctor");
        save.addActionListener(e -> {
            try {
                String res = DoctorController.getInstance().addDoctor(new Doctor(0, fn.getText(), ln.getText(), (Gender)gc.getSelectedItem(), sp.getText(), Integer.parseInt(di.getText()), of.getText(), co.getText()));
                if (res.startsWith("SUCCESS")) { refreshDoctorTable(tableModel); updateDashboardStats(); dlg.dispose(); }
                else JOptionPane.showMessageDialog(dlg, res, "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
        });
        layoutDialog(dlg, fds, save);
    }

    // NURSE PANEL
    private JPanel buildNursePanel() {
        JPanel p = mainPanel("Nurse Management", "Browse and manage nurse records");
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Nurse ID","First Name","Last Name","Gender","Dept ID","Contact"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = styledTable(model); addTableDetailSupport(table); refreshNurseTable(model);
        JTextField sf = styledSearchField("Search by ID, name or dept…");
        JButton sb = primaryBtn("Search");
        sb.addActionListener(e -> {
            String kw = sf.getText().trim(); model.setRowCount(0);
            Integer id = tryParse(kw);
            if (id != null) { Nurse n = nurseDAO.findById(id); if (n != null) { model.addRow(nurseRow(n)); return; } }
            nurseDAO.findAll().stream().filter(n -> n.getFname().toLowerCase().contains(kw.toLowerCase()) || n.getLname().toLowerCase().contains(kw.toLowerCase()) || String.valueOf(n.getDeptId()).equals(kw)).forEach(n -> model.addRow(nurseRow(n)));
        });
        JButton sab = showAllBtn(); sab.addActionListener(e -> refreshNurseTable(model));
        JButton ab = successBtn("+ New Nurse"); ab.addActionListener(ev -> showAddNurseDialog(model));
        JButton db = dangerBtn("Delete");
        db.addActionListener(ev -> { int row = table.getSelectedRow(); if (row >= 0 && confirmDelete()) { NurseController.getInstance().deleteNurse((int)model.getValueAt(row,0)); refreshNurseTable(model); updateDashboardStats(); } });
        p.add(toolbar(sf, sb, sab, ab, db), BorderLayout.NORTH);
        p.add(scrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private Object[] nurseRow(Nurse n) { return new Object[]{n.getId(), n.getFname(), n.getLname(), n.getGender(), n.getDeptId(), n.getContact()}; }
    private void refreshNurseTable(DefaultTableModel model) { model.setRowCount(0); NurseController.getInstance().findAll().forEach(n -> model.addRow(nurseRow(n))); }

    private void showAddNurseDialog(DefaultTableModel tableModel) {
        JDialog dlg = dialog("Add New Nurse", 420, 360);
        JTextField fn=dlgField(); JTextField ln=dlgField(); JComboBox<Gender> gc=new JComboBox<>(Gender.values());
        JTextField di=dlgField(); JTextField co=dlgField();
        Object[][] fds = {{"First Name *",fn},{"Last Name *",ln},{"Gender",gc},{"Dept ID",di},{"Contact",co}};
        JButton save = successBtn("Save Nurse");
        save.addActionListener(e -> {
            try {
                String res = NurseController.getInstance().addNurse(new Nurse(0, Integer.parseInt(di.getText()), fn.getText(), ln.getText(), (Gender)gc.getSelectedItem(), co.getText()));
                if (res.startsWith("SUCCESS")) { refreshNurseTable(tableModel); updateDashboardStats(); dlg.dispose(); }
                else JOptionPane.showMessageDialog(dlg, res, "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
        });
        layoutDialog(dlg, fds, save);
    }

    // MEDICAL RECORDS PANEL
    private JPanel buildMedRecordPanel() {
        JPanel p = mainPanel("Medical Records", "Patient diagnoses and treatment history");
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Record ID","Patient ID","Doctor ID","Diagnosis","Date","Weight","Height","BP","Temp","Treatment"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = styledTable(model); addTableDetailSupport(table); refreshMedTable(model);
        JTextField sf = styledSearchField("Search by Record ID or Patient ID…");
        JButton sb = primaryBtn("Search");
        sb.addActionListener(e -> {
            String kw = sf.getText().trim(); model.setRowCount(0);
            Integer id = tryParse(kw);
            MedicalRecordController.getInstance().findAll().stream().filter(r -> id != null && (r.getId()==id || r.getPatientId()==id)).forEach(r -> model.addRow(medRow(r)));
        });
        JButton sab = showAllBtn(); sab.addActionListener(e -> refreshMedTable(model));
        JButton ab = successBtn("+ New Record"); ab.addActionListener(ev -> showAddMedRecordDialog(model));
        JButton db = dangerBtn("Delete");
        db.addActionListener(ev -> { int row = table.getSelectedRow(); if (row >= 0 && confirmDelete()) { new MedicalRecordDAO().delete((int)model.getValueAt(row,0)); refreshMedTable(model); } });
        p.add(toolbar(sf, sb, sab, ab, db), BorderLayout.NORTH);
        p.add(scrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private Object[] medRow(MedicalRecord r) { return new Object[]{r.getId(), r.getPatientId(), r.getDoctId(), r.getDiagnosis(), r.getDate(), r.getWeight(), r.getHeight(), r.getBloodPresure(), r.getTemp(), r.getTreatment()}; }
    private void refreshMedTable(DefaultTableModel model) { model.setRowCount(0); MedicalRecordController.getInstance().findAll().forEach(r -> model.addRow(medRow(r))); }

    private void showAddMedRecordDialog(DefaultTableModel tableModel) {
        JDialog dlg = dialog("Add Medical Record", 520, 660);
        SearchableComboBox<Patient> pp = new SearchableComboBox<>(patientDAO.findAll(), pt -> pt.getId() + " - " + pt.getFname() + " " + pt.getLname());
        SearchableComboBox<Doctor>  dp = new SearchableComboBox<>(doctorDAO.findAll(),  d  -> d.getId()  + " - " + d.getFname()  + " " + d.getLname());
        JTextField we=dlgField(); JTextField he=dlgField(); JTextField bp=dlgField(); JTextField te=dlgField();
        JTextArea dg=new JTextArea(2,20); JTextArea tr=new JTextArea(2,20);
        JTextField vd=dlgField(); vd.setText(LocalDate.now().toString());
        JTextField nv=dlgField();
        Object[][] fds = {{"Patient *",pp},{"Doctor *",dp},{"Visit Date (YYYY-MM-DD)",vd},{"Next Visit (YYYY-MM-DD)",nv},{"Weight (kg)",we},{"Height (cm)",he},{"Blood Pressure",bp},{"Temperature",te},{"Diagnosis *",new JScrollPane(dg)},{"Treatment",new JScrollPane(tr)}};
        JButton save = successBtn("Save Record");
        save.addActionListener(e -> {
            try {
                Patient sp = pp.getSelectedItem(); Doctor sd = dp.getSelectedItem();
                if (sp==null) { JOptionPane.showMessageDialog(dlg,"Please select a patient.","Validation Error",JOptionPane.WARNING_MESSAGE); return; }
                if (sd==null) { JOptionPane.showMessageDialog(dlg,"Please select a doctor.", "Validation Error",JOptionPane.WARNING_MESSAGE); return; }
                String nvt = nv.getText().trim();
                String res = MedicalRecordController.getInstance().addRecord(sp.getId(), sd.getId(), dg.getText(), tr.getText(), Integer.parseInt(we.getText()), Integer.parseInt(he.getText()), bp.getText(), Integer.parseInt(te.getText()), LocalDate.parse(vd.getText()), nvt.isEmpty()?null:LocalDate.parse(nvt));
                if (res.startsWith("SUCCESS")) { refreshMedTable(tableModel); dlg.dispose(); }
                else JOptionPane.showMessageDialog(dlg, res, "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
        });
        layoutDialog(dlg, fds, save);
    }

    // BED ADMISSIONS PANEL
    private JPanel buildBedPanel() {
        JPanel p = mainPanel("Bed Admissions", "Inpatient bed assignment and discharge");
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Adm ID","Patient ID","Nurse ID","Bed No","Ward No","Ward Name","Check-in","Check-out","Amount"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = styledTable(model); addTableDetailSupport(table); refreshBedTable(model);

        JTextField sf = styledSearchField("Search by Admission ID or Patient ID…");
        JButton sb = primaryBtn("Search");
        sb.addActionListener(e -> {
            String kw = sf.getText().trim(); model.setRowCount(0);
            Integer id = tryParse(kw);
            AdmissionController.getInstance().getAllBedRecords().stream()
                    .filter(r -> id != null && (r.getId() == id || r.getPatientId() == id))
                    .forEach(r -> model.addRow(bedRow(r)));
        });
        JButton sab = showAllBtn(); sab.addActionListener(e -> refreshBedTable(model));
        JButton ab = successBtn("+ Admit Patient"); ab.addActionListener(ev -> showAddBedDialog(model));
        JButton db = dangerBtn("Discharge");
        db.addActionListener(ev -> {
            int row = table.getSelectedRow(); if (row < 0) return;
            String res = AdmissionController.getInstance().dischargeFromBed((int) model.getValueAt(row, 0), LocalDate.now(), 0);
            JOptionPane.showMessageDialog(this, res); refreshBedTable(model); updateDashboardStats();
        });
        p.add(toolbar(sf, sb, sab, ab, db), BorderLayout.NORTH);
        p.add(scrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private Object[] bedRow(BedRecord r) {
        String wardNo   = "";
        String wardName = "";
        String sql = "SELECT b.ward_No, w.ward_Name FROM bed b " +
                "JOIN ward w ON w.ward_No = b.ward_No " +
                "WHERE b.Bed_No = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getNo());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                wardNo   = String.valueOf(rs.getInt("ward_No"));
                wardName = rs.getString("ward_Name");
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return new Object[]{
                r.getId(), r.getPatientId(), r.getNurseNo(),
                r.getNo(), wardNo, wardName,
                r.getDate(),
                r.getEndingDate() != null ? r.getEndingDate() : "Active",
                r.getAmount() + " $"
        };
    }

    private void refreshBedTable(DefaultTableModel model) {
        model.setRowCount(0);
        AdmissionController.getInstance().getAllBedRecords().forEach(r -> model.addRow(bedRow(r)));
    }
    private void showAddBedDialog(DefaultTableModel tableModel) {
        JDialog dlg = dialog("Admit Patient to Bed", 520, 500);
        SearchableComboBox<Patient> pp = new SearchableComboBox<>(patientDAO.findAll(), pt -> pt.getId()+" - "+pt.getFname()+" "+pt.getLname());
        SearchableComboBox<Nurse>   np = new SearchableComboBox<>(nurseDAO.findAll(),   n  -> n.getId() +" - "+n.getFname() +" "+n.getLname());
        JTextField bn=dlgField(); JTextField am=dlgField();
        JTextField admDate=dlgField(); admDate.setText(LocalDate.now().toString());
        JTextField disDate=dlgField();
        JComboBox<PaymentType> pt=new JComboBox<>(PaymentType.values());
        Object[][] fds = {{"Patient *",pp},{"Nurse *",np},{"Bed No *",bn},{"Admission Date (YYYY-MM-DD) *",admDate},{"Discharge Date (YYYY-MM-DD)",disDate},{"Amount *",am},{"Payment Type",pt}};
        JButton save = successBtn("Admit Patient");
        save.addActionListener(e -> {
            try {
                Patient sp=pp.getSelectedItem(); Nurse sn=np.getSelectedItem();
                if (sp==null) { JOptionPane.showMessageDialog(dlg,"Please select a patient.","Validation Error",JOptionPane.WARNING_MESSAGE); return; }
                if (sn==null) { JOptionPane.showMessageDialog(dlg,"Please select a nurse.",  "Validation Error",JOptionPane.WARNING_MESSAGE); return; }
                String res = AdmissionController.getInstance().admitToBed(sp.getId(), sn.getId(), Integer.parseInt(bn.getText()), LocalDate.parse(admDate.getText().trim()), Integer.parseInt(am.getText()), (PaymentType)pt.getSelectedItem());
                if (res.startsWith("SUCCESS")) { refreshBedTable(tableModel); updateDashboardStats(); dlg.dispose(); }
                else JOptionPane.showMessageDialog(dlg, res, "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Error: "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
        });
        layoutDialog(dlg, fds, save);
    }

    // APPOINTMENT PANEL
    private JPanel buildAppointPanel() {
        JPanel p = mainPanel("Appointments", "Schedule and manage patient appointments");

        // Tasarımın Orijinal Sütunları Tamamen Korundu + "Time" Sütunu Eklendi
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"App ID", "Patient ID", "Doctor ID", "Date", "Status", "Reason", "Amount", "Type", "Time"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = styledTable(model); addTableDetailSupport(table); refreshAppointTable(model);

        JTextField sf = styledSearchField("Search by App ID, Patient ID or Doctor ID…");
        JButton sb = primaryBtn("Search");
        sb.addActionListener(e -> {
            String kw = sf.getText().trim(); model.setRowCount(0);
            Integer id = tryParse(kw);
            AppointmentController.getInstance().findAll().stream()
                    .filter(a -> id != null && (a.getId() == id || a.getPatientId() == id || a.getDoctorId() == id))
                    .forEach(a -> model.addRow(appointRow(a)));
        });
        JButton sab = showAllBtn(); sab.addActionListener(e -> refreshAppointTable(model));
        JButton ab = successBtn("+ New Appointment"); ab.addActionListener(ev -> showAddAppointDialog(model));

        // Cancel: SCHEDULED ve NO_SHOW → CANCELED; COMPLETED iptal edilemez
        JButton cancelBtn = dangerBtn("Cancel");
        cancelBtn.addActionListener(ev -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this,"Please select an appointment.","No Selection",JOptionPane.WARNING_MESSAGE); return; }
            AppStatus status = AppStatus.valueOf(model.getValueAt(row, 4).toString());
            if (status == AppStatus.COMPLETED) { JOptionPane.showMessageDialog(this,"Completed appointments cannot be cancelled.","Cannot Cancel",JOptionPane.WARNING_MESSAGE); return; }
            if (status != AppStatus.SCHEDULED && status != AppStatus.NO_SHOW) { JOptionPane.showMessageDialog(this,"Only SCHEDULED or NO_SHOW appointments can be cancelled.","Cannot Cancel",JOptionPane.WARNING_MESSAGE); return; }
            String res = AppointmentController.getInstance().cancelAppointment((int)model.getValueAt(row,0));
            JOptionPane.showMessageDialog(this, res); refreshAppointTable(model);
        });

        // Delete: veritabanından tamamen siler
        JButton deleteBtn = dangerBtn("Delete");
        deleteBtn.addActionListener(ev -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this,"Please select an appointment.","No Selection",JOptionPane.WARNING_MESSAGE); return; }
            int appId = (int)model.getValueAt(row,0);
            if (JOptionPane.showConfirmDialog(this,"Permanently DELETE Appointment ID: "+appId+"?","Confirm Delete",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, AppointmentController.getInstance().deleteAppointment(appId));
                refreshAppointTable(model); updateDashboardStats();
            }
        });

        p.add(toolbar1(sf, sb, sab, ab, cancelBtn, deleteBtn), BorderLayout.NORTH);
        p.add(scrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private Object[] appointRow(AppointmentRecord a) {
        return new Object[]{
                a.getId(),
                a.getPatientId(),
                a.getDoctorId(),
                a.getDate(),
                a.getStatus(),
                a.getReason(),
                a.getPaymentAmount() + " $",
                a.getAppointmentType(),
                a.getTime() != null ? a.getTime().toString() : ""
        };
    }

    private void refreshAppointTable(DefaultTableModel model) {
        model.setRowCount(0);
        AppointmentController.getInstance().findAll().forEach(a -> model.addRow(appointRow(a)));
    }

    private void showAddAppointDialog(DefaultTableModel tableModel) {
        JDialog dlg = dialog("Create Appointment", 520, 560); // Alanlar için boyutu korudum
        SearchableComboBox<Patient> pp = new SearchableComboBox<>(patientDAO.findAll(), pt -> pt.getId()+" - "+pt.getFname()+" "+pt.getLname());
        SearchableComboBox<Doctor>  dp = new SearchableComboBox<>(doctorDAO.findAll(),  d  -> d.getId() +" - "+d.getFname() +" "+d.getLname());

        // Düzenleme: Sadece tarih görünüyor (Saat ve dakika otomatik gelmiyor)
        JTextField dt = dlgField(); dt.setText(LocalDate.now().toString());

        // Yeni Eklenen Saat Alanı (Varsayılan olarak anlık saati HH:mm formatında yazar)
        JTextField tm = dlgField(); tm.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        JTextField rs = dlgField(); JTextField am = dlgField();
        JComboBox<PaymentType> pc = new JComboBox<>(PaymentType.values());
        JComboBox<AppointmentType> ac = new JComboBox<>(AppointmentType.values());

        // Dizaynı bozmadan "Time" satırı eklendi
        Object[][] fds = {
                {"Patient *", pp},
                {"Doctor *", dp},
                {"Date (yyyy-MM-dd) *", dt},
                {"Time (HH:mm) *", tm},
                {"Reason", rs},
                {"Amount *", am},
                {"Payment Type", pc},
                {"Appointment Type", ac}
        };

        JButton save = successBtn("Save Appointment");
        save.addActionListener(e -> {
            try {
                Patient sp = pp.getSelectedItem(); Doctor sd = dp.getSelectedItem();
                if (sp == null) { JOptionPane.showMessageDialog(dlg,"Please select a patient.","Validation Error",JOptionPane.WARNING_MESSAGE); return; }
                if (sd == null) { JOptionPane.showMessageDialog(dlg,"Please select a doctor.", "Validation Error",JOptionPane.WARNING_MESSAGE); return; }
                if (am.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(dlg,"Amount is required.", "Validation Error",JOptionPane.WARNING_MESSAGE); return; }

                // Tarih ve Saati parse edip birleştiriyoruz
                LocalDate parsedDate = LocalDate.parse(dt.getText().trim());
                LocalTime parsedTime = LocalTime.parse(tm.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
                LocalDateTime combinedDateTime = parsedDate.atTime(parsedTime);

                // Controller'daki yeni imzaya (LocalTime parametreli) uygun çağrı yapılıyor
                String res = AppointmentController.getInstance().createAppointment(
                        sp.getId(),
                        sd.getId(),
                        combinedDateTime,
                        rs.getText(),
                        Integer.parseInt(am.getText().trim()),
                        (PaymentType) pc.getSelectedItem(),
                        (AppointmentType) ac.getSelectedItem(),
                        parsedTime
                );

                if (res.startsWith("SUCCESS")) { refreshAppointTable(tableModel); updateDashboardStats(); dlg.dispose(); }
                else JOptionPane.showMessageDialog(dlg, res, "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dlg, "Invalid Date or Time format! Use YYYY-MM-DD and HH:mm", "Format Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Error: "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        layoutDialog(dlg, fds, save);
    }

    // SURGERY PANEL
    private JPanel buildSurgeryPanel() {
        JPanel p = mainPanel("Surgery Records", "Surgical procedures and operation history");
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Surg ID","Patient ID","Surgeon ID","Nurse ID","Date","Room","Type","Notes"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = styledTable(model); // ← tek table oluşturma noktası
        addTableDetailSupport(table); refreshSurgeryTable(model);

        JTextField sf = styledSearchField("Search by Surgery ID, Patient ID or Surgeon ID…");
        JButton sb = primaryBtn("Search");
        sb.addActionListener(e -> {
            String kw = sf.getText().trim(); model.setRowCount(0);
            Integer id = tryParse(kw);
            SurgeryController.getInstance().findAll().stream().filter(s -> id!=null && (s.getId()==id||s.getPatientId()==id||s.getSurgeonId()==id)).forEach(s -> model.addRow(surgRow(s)));
        });
        JButton sab = showAllBtn(); sab.addActionListener(e -> refreshSurgeryTable(model));
        JButton ab = successBtn("+ Add Surgery"); ab.addActionListener(ev -> showAddSurgeryDialog(model));

        // FIX: table referansı doğrudan kullanılıyor
        JButton db = dangerBtn("Delete");
        db.addActionListener(ev -> {
            int row = table.getSelectedRow(); // ← artık doğru
            if (row < 0) { JOptionPane.showMessageDialog(this,"Please select a surgery record to delete.","No Selection",JOptionPane.WARNING_MESSAGE); return; }
            int surgeryId = (int)model.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this,"Are you sure you want to delete Surgery ID: "+surgeryId+"?\nThis action cannot be undone.","Confirm Delete",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, SurgeryController.getInstance().delete(surgeryId));
                refreshSurgeryTable(model); updateDashboardStats();
            }
        });

        p.add(toolbar(sf, sb, sab, ab, db), BorderLayout.NORTH);
        p.add(scrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private Object[] surgRow(SurgeryRecord s) { return new Object[]{s.getId(), s.getPatientId(), s.getSurgeonId(), s.getNurseId(), s.getDate(), s.getRoomNo(), s.getSurgeryType(), s.getNotes()}; }
    private void refreshSurgeryTable(DefaultTableModel model) { model.setRowCount(0); SurgeryController.getInstance().findAll().forEach(s -> model.addRow(surgRow(s))); }

    private void showAddSurgeryDialog(DefaultTableModel tableModel) {
        JDialog dlg = dialog("Add Surgery Record", 520, 600);
        SearchableComboBox<Patient> pp = new SearchableComboBox<>(patientDAO.findAll(), pt -> pt.getId()+" - "+pt.getFname()+" "+pt.getLname());
        SearchableComboBox<Doctor>  sp = new SearchableComboBox<>(doctorDAO.findAll(),  d  -> d.getId() +" - "+d.getFname() +" "+d.getLname());
        SearchableComboBox<Nurse>   np = new SearchableComboBox<>(nurseDAO.findAll(),   n  -> n.getId() +" - "+n.getFname() +" "+n.getLname());
        JTextField ty=dlgField(); JTextField dt=dlgField(); dt.setText(LocalDate.now().toString());
        JTextField rm=dlgField(); JTextArea nt=new JTextArea(3,20); nt.setLineWrap(true);
        Object[][] fds = {{"Patient *",pp},{"Surgeon *",sp},{"Nurse *",np},{"Surgery Type *",ty},{"Date (YYYY-MM-DD)",dt},{"Room No",rm},{"Notes",new JScrollPane(nt)}};
        JButton save = successBtn("Save Surgery");
        save.addActionListener(e -> {
            try {
                Patient selP=pp.getSelectedItem(); Doctor selS=sp.getSelectedItem(); Nurse selN=np.getSelectedItem();
                if (selP==null) { JOptionPane.showMessageDialog(dlg,"Please select a patient.","Validation Error",JOptionPane.WARNING_MESSAGE); return; }
                if (selS==null) { JOptionPane.showMessageDialog(dlg,"Please select a surgeon.","Validation Error",JOptionPane.WARNING_MESSAGE); return; }
                if (selN==null) { JOptionPane.showMessageDialog(dlg,"Please select a nurse.",  "Validation Error",JOptionPane.WARNING_MESSAGE); return; }
                String res = SurgeryController.getInstance().addSurgery(selP.getId(), selS.getId(), selN.getId(), LocalDate.parse(dt.getText()), LocalTime.of(9,0), LocalTime.of(10,0), Integer.parseInt(rm.getText()), ty.getText(), nt.getText());
                if (res.startsWith("SUCCESS")) { refreshSurgeryTable(tableModel); dlg.dispose(); }
                else JOptionPane.showMessageDialog(dlg, res, "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Error: "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
        });
        layoutDialog(dlg, fds, save);
    }

    // DEPARTMENT PANEL
    private JPanel buildDepartmentPanel() {
        JPanel outer = mainPanel("Departments", "Staff overview by department");

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BG);

        for (Department dept : DepartmentController.getInstance().findAll()) {
            int deptId     = dept.getDeptId();
            int docCount   = DepartmentController.getInstance().getDoctorCount(deptId);
            int nurseCount = DepartmentController.getInstance().getNurseCount(deptId);
            int wardCount  = WardController.getInstance().getWardCount(deptId); // YENİ

            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(WHITE);
            header.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
                    BorderFactory.createEmptyBorder(14, 18, 14, 18)
            ));
            header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel nameLabel = new JLabel(dept.getDept_Name());
            nameLabel.setFont(FONT_BOLD); nameLabel.setForeground(TEXT_DARK);

            JPanel badges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            badges.setBackground(WHITE);

            JLabel docBadge = new JLabel("Doctors: " + docCount);
            docBadge.setFont(FONT_SMALL); docBadge.setForeground(new Color(0x2563EB));
            docBadge.setBackground(new Color(0xEFF6FF)); docBadge.setOpaque(true);
            docBadge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

            JLabel nurseBadge = new JLabel("Nurses: " + nurseCount);
            nurseBadge.setFont(FONT_SMALL); nurseBadge.setForeground(new Color(0x0891B2));
            nurseBadge.setBackground(new Color(0xECFEFF)); nurseBadge.setOpaque(true);
            nurseBadge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

            // YENİ: Ward badge
            JLabel wardBadge = new JLabel("Wards: " + wardCount);
            wardBadge.setFont(FONT_SMALL); wardBadge.setForeground(new Color(0x7C3AED));
            wardBadge.setBackground(new Color(0xF5F3FF)); wardBadge.setOpaque(true);
            wardBadge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

            JLabel arrow = new JLabel("  ▼");
            arrow.setFont(FONT_SMALL); arrow.setForeground(TEXT_LIGHT);

            badges.add(docBadge); badges.add(nurseBadge); badges.add(wardBadge); badges.add(arrow);
            header.add(nameLabel, BorderLayout.WEST);
            header.add(badges,    BorderLayout.EAST);

            // Detail body
            JPanel detail = new JPanel();
            detail.setLayout(new BoxLayout(detail, BoxLayout.Y_AXIS));
            detail.setBackground(new Color(0xF8FAFC));
            detail.setBorder(BorderFactory.createEmptyBorder(8, 28, 12, 28));
            detail.setVisible(false);

            List<Doctor> deptDoctors = doctorDAO.findByDepartment(deptId);
            if (!deptDoctors.isEmpty()) {
                JLabel t = new JLabel("DOCTORS");
                t.setFont(new Font("Segoe UI", Font.BOLD, 10)); t.setForeground(TEXT_LIGHT);
                t.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));
                detail.add(t);
                for (Doctor d : deptDoctors)
                    detail.add(deptPersonRow(d.getId() + " — " + d.getFname() + " " + d.getLname(),
                            d.getSurgeonType() != null ? d.getSurgeonType() : "Doctor",
                            new Color(0xEFF6FF), new Color(0x2563EB)));
            }

            List<Nurse> deptNurses = nurseDAO.findByDepartment(deptId);
            if (!deptNurses.isEmpty()) {
                JLabel t = new JLabel("NURSES");
                t.setFont(new Font("Segoe UI", Font.BOLD, 10)); t.setForeground(TEXT_LIGHT);
                t.setBorder(BorderFactory.createEmptyBorder(10, 0, 4, 0));
                detail.add(t);
                for (Nurse n : deptNurses)
                    detail.add(deptPersonRow(n.getId() + " — " + n.getFname() + " " + n.getLname(),
                            "Nurse", new Color(0xECFEFF), new Color(0x0891B2)));
            }

            // YENİ: Ward listesi
            List<Ward> deptWards = WardController.getInstance().findByDepartment(deptId);
            if (!deptWards.isEmpty()) {
                JLabel t = new JLabel("WARDS");
                t.setFont(new Font("Segoe UI", Font.BOLD, 10)); t.setForeground(TEXT_LIGHT);
                t.setBorder(BorderFactory.createEmptyBorder(10, 0, 4, 0));
                detail.add(t);
                for (Ward w : deptWards)
                    detail.add(deptPersonRow(w.getWardNo() + " — " + w.getWardName(),
                            "Ward", new Color(0xF5F3FF), new Color(0x7C3AED)));
            }

            if (deptDoctors.isEmpty() && deptNurses.isEmpty() && deptWards.isEmpty()) {
                JLabel empty = new JLabel("No staff or wards assigned.");
                empty.setFont(FONT_SMALL); empty.setForeground(TEXT_LIGHT);
                empty.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
                detail.add(empty);
            }

            MouseAdapter toggle = new MouseAdapter() {
                boolean open = false;
                public void mouseClicked(MouseEvent e) {
                    open = !open; detail.setVisible(open);
                    arrow.setText(open ? "  ▲" : "  ▼");
                    listPanel.revalidate(); listPanel.repaint();
                }
                public void mouseEntered(MouseEvent e) { header.setBackground(ROW_ALT); badges.setBackground(ROW_ALT); }
                public void mouseExited(MouseEvent e)  { header.setBackground(WHITE);   badges.setBackground(WHITE);   }
            };
            header.addMouseListener(toggle);
            for (Component c : badges.getComponents()) c.addMouseListener(toggle);
            nameLabel.addMouseListener(toggle);

            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(WHITE);
            card.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            card.add(header); card.add(detail);

            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(10));
        }

        listPanel.add(Box.createVerticalGlue());
        JScrollPane sp = new JScrollPane(listPanel);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        outer.add(sp, BorderLayout.CENTER);
        return outer;
    }

    private JPanel deptPersonRow(String name, String role, Color bgColor, Color fgColor) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(new Color(0xF8FAFC));
        row.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        JLabel nl = new JLabel(name); nl.setFont(FONT_BODY); nl.setForeground(TEXT_DARK);
        JLabel rl = new JLabel(role); rl.setFont(FONT_SMALL); rl.setForeground(fgColor);
        rl.setBackground(bgColor); rl.setOpaque(true);
        rl.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        row.add(nl, BorderLayout.CENTER); row.add(rl, BorderLayout.EAST);
        return row;
    }

    private JPanel buildWardPanel() {
        JPanel p = mainPanel("Wards", "Hospital ward management");

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Ward No", "Ward Name", "Dept ID"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = styledTable(model); addTableDetailSupport(table); refreshWardTable(model);

        JTextField sf = styledSearchField("Search by ward name or dept ID…");
        JButton sb = primaryBtn("Search");
        sb.addActionListener(e -> {
            String kw = sf.getText().trim(); model.setRowCount(0);
            Integer id = tryParse(kw);
            WardController.getInstance().findAll().stream()
                    .filter(w -> w.getWardName().toLowerCase().contains(kw.toLowerCase())
                            || (id != null && (w.getWardNo() == id || w.getDeptId() == id)))
                    .forEach(w -> model.addRow(wardRow(w)));
        });

        JButton sab = showAllBtn(); sab.addActionListener(e -> refreshWardTable(model));

        JButton ab = successBtn("+ New Ward");
        ab.addActionListener(ev -> showAddWardDialog(model));

        JButton db = dangerBtn("Delete");
        db.addActionListener(ev -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            if (confirmDelete()) {
                String res = WardController.getInstance().deleteWard((int) model.getValueAt(row, 0));
                JOptionPane.showMessageDialog(this, res);
                refreshWardTable(model);
            }
        });

        p.add(toolbar(sf, sb, sab, ab, db), BorderLayout.NORTH);
        p.add(scrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private Object[] wardRow(Ward w) {
        return new Object[]{w.getWardNo(), w.getWardName(), w.getDeptId()};
    }

    private void refreshWardTable(DefaultTableModel model) {
        model.setRowCount(0);
        WardController.getInstance().findAll().forEach(w -> model.addRow(wardRow(w)));
    }

    private void showAddWardDialog(DefaultTableModel tableModel) {
        JDialog dlg = dialog("Add New Ward", 420, 260);
        JTextField wn = dlgField();
        JTextField di = dlgField();
        Object[][] fds = {{"Ward Name *", wn}, {"Dept ID *", di}};
        JButton save = successBtn("Save Ward");
        save.addActionListener(e -> {
            try {
                String res = WardController.getInstance().addWard(
                        wn.getText().trim(),
                        Integer.parseInt(di.getText().trim())
                );
                if (res.startsWith("SUCCESS")) { refreshWardTable(tableModel); dlg.dispose(); }
                else JOptionPane.showMessageDialog(dlg, res, "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        layoutDialog(dlg, fds, save);
    }




    // SEARCHABLE COMBO BOX
    private class SearchableComboBox<T> extends JPanel {
        private final JTextField searchField;
        private final JList<String> list;
        private final DefaultListModel<String> listModel;
        private final JPopupMenu popup;
        private final List<T> allItems;
        private final java.util.function.Function<T, String> labelFn;
        private T selectedItem = null;

        SearchableComboBox(List<T> items, java.util.function.Function<T, String> labelFn) {
            super(new BorderLayout());
            this.allItems = items; this.labelFn = labelFn;
            setBackground(WHITE); setPreferredSize(new Dimension(220, 32));
            searchField = new JTextField(); searchField.setFont(FONT_BODY);
            searchField.setForeground(TEXT_LIGHT); searchField.setText("Type to search…");
            searchField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR,1,true), BorderFactory.createEmptyBorder(5,9,5,9)));
            add(searchField, BorderLayout.CENTER);
            listModel = new DefaultListModel<>();
            list = new JList<>(listModel); list.setFont(FONT_BODY); list.setBackground(WHITE);
            list.setSelectionBackground(ROW_SEL); list.setSelectionForeground(TEXT_DARK); list.setFixedCellHeight(28);
            JScrollPane sp = new JScrollPane(list); sp.setBorder(BorderFactory.createLineBorder(BORDER_CLR)); sp.setPreferredSize(new Dimension(220,150));
            popup = new JPopupMenu(); popup.setFocusable(false); popup.add(sp); popup.setBorder(BorderFactory.createEmptyBorder());
            searchField.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) { if (searchField.getText().equals("Type to search…")) { searchField.setText(""); searchField.setForeground(TEXT_DARK); } filterList(searchField.getText()); showPopup(); }
                public void focusLost(FocusEvent e)   { SwingUtilities.invokeLater(() -> { if (!list.hasFocus()) popup.setVisible(false); }); }
            });
            searchField.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e)  { filterList(searchField.getText()); }
                public void removeUpdate(DocumentEvent e)  { filterList(searchField.getText()); }
                public void changedUpdate(DocumentEvent e) { filterList(searchField.getText()); }
            });
            list.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { selectIndex(list.locationToIndex(e.getPoint())); } });
            searchField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode()==KeyEvent.VK_DOWN) { int next=list.getSelectedIndex()+1; if(next<listModel.size()) list.setSelectedIndex(next); list.requestFocus(); }
                    else if (e.getKeyCode()==KeyEvent.VK_ENTER) { if(list.getSelectedIndex()>=0) selectIndex(list.getSelectedIndex()); }
                    else if (e.getKeyCode()==KeyEvent.VK_ESCAPE) { popup.setVisible(false); }
                }
            });
            list.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) { if(e.getKeyCode()==KeyEvent.VK_ENTER) selectIndex(list.getSelectedIndex()); else if(e.getKeyCode()==KeyEvent.VK_ESCAPE) popup.setVisible(false); }
            });
            filterList("");
        }

        private void filterList(String query) {
            listModel.clear(); String q=query.toLowerCase();
            for (T item : allItems) { String label=labelFn.apply(item); if(label.toLowerCase().contains(q)) listModel.addElement(label); }
            if (!listModel.isEmpty()) list.setSelectedIndex(0);
        }
        private void showPopup() { if (!popup.isVisible()) popup.show(searchField,0,searchField.getHeight()); }
        private void selectIndex(int idx) {
            if (idx<0||idx>=listModel.size()) return;
            String label=listModel.getElementAt(idx);
            for (T item : allItems) { if (labelFn.apply(item).equals(label)) { selectedItem=item; break; } }
            searchField.setText(label); searchField.setForeground(TEXT_DARK); popup.setVisible(false);
        }
        public T getSelectedItem() { return selectedItem; }
    }

    // SHARED UI HELPERS
    private JPanel mainPanel(String title, String subtitle) {
        JPanel p = new JPanel(new BorderLayout(0,16)); p.setBackground(BG); p.setBorder(BorderFactory.createEmptyBorder(32,32,24,32));
        JPanel hdr = new JPanel(new BorderLayout()); hdr.setBackground(BG);
        JLabel t = new JLabel(title); t.setFont(FONT_TITLE); t.setForeground(TEXT_DARK);
        JLabel s = new JLabel(subtitle); s.setFont(FONT_BODY); s.setForeground(TEXT_MID);
        hdr.add(t, BorderLayout.NORTH); hdr.add(s, BorderLayout.SOUTH);
        p.add(hdr, BorderLayout.NORTH);
        return p;
    }

    private JPanel toolbar(JTextField sf, JButton sb, JButton sab, JButton ab, JButton db) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0)); bar.setBackground(BG); bar.setBorder(BorderFactory.createEmptyBorder(0,0,12,0));
        sf.setPreferredSize(new Dimension(260,34)); bar.add(sf); bar.add(sb); bar.add(sab); bar.add(Box.createHorizontalStrut(16)); bar.add(ab); bar.add(db);
        return bar;
    }

    private JPanel toolbar1(JTextField sf, JButton sb, JButton sab, JButton ab, JButton cb, JButton db) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0)); bar.setBackground(BG); bar.setBorder(BorderFactory.createEmptyBorder(0,0,12,0));
        sf.setPreferredSize(new Dimension(260,34)); bar.add(sf); bar.add(sb); bar.add(sab); bar.add(Box.createHorizontalStrut(16)); bar.add(ab); bar.add(cb); bar.add(db);
        return bar;
    }

    private JButton showAllBtn() {
        JButton b = new JButton("Show All"); b.setFont(FONT_BODY); b.setBackground(new Color(0xF1F5F9)); b.setForeground(TEXT_MID);
        b.setFocusPainted(false); b.setBorderPainted(false); b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); b.setBorder(BorderFactory.createEmptyBorder(7,16,7,16));
        return b;
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model); table.setFont(FONT_BODY); table.setRowHeight(36); table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,0)); table.setSelectionBackground(ROW_SEL); table.setSelectionForeground(TEXT_DARK);
        table.setBackground(WHITE); table.setFillsViewportHeight(true); table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader header = table.getTableHeader(); header.setFont(FONT_BOLD); header.setBackground(BG); header.setForeground(TEXT_MID);
        header.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER_CLR)); header.setPreferredSize(new Dimension(0,40)); header.setReorderingAllowed(false);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                if (!sel) setBackground(row%2==0?WHITE:ROW_ALT);
                setForeground(sel?TEXT_DARK:(col==0?TEXT_MID:TEXT_DARK));
                if (col==0) setFont(FONT_SMALL);
                setBorder(BorderFactory.createEmptyBorder(0,14,0,14));
                return this;
            }
        });
        return table;
    }

    private JScrollPane scrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table); sp.setBorder(BorderFactory.createLineBorder(BORDER_CLR,1,true)); sp.getViewport().setBackground(WHITE); return sp;
    }

    private JTextField styledSearchField(String placeholder) {
        JTextField f = new JTextField(); f.setFont(FONT_BODY); f.setBackground(WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR,1,true), BorderFactory.createEmptyBorder(6,10,6,10)));
        f.setText(placeholder); f.setForeground(TEXT_LIGHT);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { if(f.getText().equals(placeholder)){f.setText("");f.setForeground(TEXT_DARK);} f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ACCENT,1,true),BorderFactory.createEmptyBorder(6,10,6,10))); }
            public void focusLost(FocusEvent e)   { if(f.getText().isEmpty()){f.setText(placeholder);f.setForeground(TEXT_LIGHT);} f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR,1,true),BorderFactory.createEmptyBorder(6,10,6,10))); }
        });
        return f;
    }

    private JPanel statCard(String label, JLabel valueLabel, Color color, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(0,8)); card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR,1,true),BorderFactory.createEmptyBorder(22,22,22,22)));
        JLabel top=new JLabel(label); top.setFont(FONT_SMALL); top.setForeground(TEXT_MID);
        valueLabel.setFont(new Font("Segoe UI",Font.BOLD,34)); valueLabel.setForeground(color);
        JLabel sub=new JLabel(subtitle); sub.setFont(FONT_SMALL); sub.setForeground(TEXT_LIGHT);
        JPanel bottom=new JPanel(new BorderLayout()); bottom.setBackground(WHITE); bottom.add(valueLabel,BorderLayout.CENTER); bottom.add(sub,BorderLayout.SOUTH);
        card.add(top,BorderLayout.NORTH); card.add(bottom,BorderLayout.CENTER);
        return card;
    }

    private JDialog dialog(String title, int w, int h) {
        JDialog dlg=new JDialog(this,title,true); dlg.setSize(w,h); dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(WHITE); dlg.setLayout(new GridBagLayout());
        return dlg;
    }

    private JTextField dlgField() {
        JTextField f=new JTextField(); f.setFont(FONT_BODY); f.setPreferredSize(new Dimension(220,32));
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR,1,true),BorderFactory.createEmptyBorder(5,9,5,9)));
        return f;
    }

    private void layoutDialog(JDialog dlg, Object[][] fields, JButton saveBtn) {
        GridBagConstraints gbc=new GridBagConstraints(); gbc.insets=new Insets(6,12,6,12); gbc.fill=GridBagConstraints.HORIZONTAL;
        for (int i=0; i<fields.length; i++) {
            gbc.gridx=0; gbc.gridy=i; gbc.weightx=0;
            JLabel lbl=new JLabel((String)fields[i][0]); lbl.setFont(FONT_SMALL); lbl.setForeground(TEXT_MID); dlg.add(lbl,gbc);
            gbc.gridx=1; gbc.weightx=1; dlg.add((Component)fields[i][1],gbc);
        }
        gbc.gridx=1; gbc.gridy=fields.length; gbc.insets=new Insets(14,12,12,12); dlg.add(saveBtn,gbc);
        dlg.setVisible(true);
    }

    private JButton primaryBtn(String t) { JButton b=new JButton(t); b.setFont(FONT_BODY); b.setBackground(new Color(0xEFF6FF)); b.setForeground(ACCENT); b.setFocusPainted(false); b.setBorderPainted(false); b.setOpaque(true); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); b.setBorder(BorderFactory.createEmptyBorder(7,16,7,16)); return b; }
    private JButton successBtn(String t) { JButton b=new JButton(t); b.setFont(FONT_BOLD); b.setBackground(ACCENT2); b.setForeground(WHITE); b.setFocusPainted(false); b.setBorderPainted(false); b.setOpaque(true); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); b.setBorder(BorderFactory.createEmptyBorder(8,18,8,18)); return b; }
    private JButton dangerBtn(String t)  { JButton b=new JButton(t); b.setFont(FONT_BODY); b.setBackground(new Color(0xFEF2F2)); b.setForeground(DANGER); b.setFocusPainted(false); b.setBorderPainted(false); b.setOpaque(true); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); b.setBorder(BorderFactory.createEmptyBorder(7,16,7,16)); return b; }

    private void addTableDetailSupport(JTable table) {
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==2) {
                    int row=table.getSelectedRow(), col=table.getSelectedColumn();
                    if (row!=-1&&col!=-1) { Object val=table.getValueAt(row,col); if(val!=null) {
                        JTextArea area=new JTextArea(val.toString()); area.setEditable(false); area.setFont(FONT_BODY); area.setLineWrap(true); area.setWrapStyleWord(true);
                        area.setBackground(new Color(0xF8FAFC)); area.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
                        JScrollPane sp=new JScrollPane(area); sp.setPreferredSize(new Dimension(380,180)); sp.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
                        JOptionPane.showMessageDialog(MainFrame.this, sp, table.getColumnName(col), JOptionPane.PLAIN_MESSAGE);
                    }}
                }
            }
        });
    }

    private Integer tryParse(String text) { try { return Integer.parseInt(text); } catch (NumberFormatException e) { return null; } }

    private boolean confirmDelete() {
        return JOptionPane.showConfirmDialog(this,"Are you sure you want to delete this record?\nThis action cannot be undone.","Confirm Delete",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}