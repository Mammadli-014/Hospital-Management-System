package dao;

import db.DBConnection;
import event.MedicalRecord;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordDAO {

    public boolean add(MedicalRecord record) {
        String sql = "INSERT INTO medicalrecord (patient_Id, visit_Date, doct_Id, curr_Weight, " +
                "curr_height, curr_Blood_Pressure, curr_Temp_F, diagnosis, treatment, next_Visit) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, record.getPatientId());
            ps.setDate(2, Date.valueOf(record.getDate()));
            ps.setInt(3, record.getDoctId());
            ps.setInt(4, record.getWeight());
            ps.setInt(5, record.getHeight());
            ps.setString(6, record.getBloodPresure());
            ps.setInt(7, record.getTemp());
            ps.setString(8, record.getDiagnosis());
            ps.setString(9, record.getTreatment());
            ps.setDate(10, record.getNext_visit() != null ? Date.valueOf(record.getNext_visit()) : null);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int recordId) {
        String sql = "DELETE FROM medicalrecord WHERE record_Id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, recordId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public MedicalRecord findById(int recordId) {
        String sql = "SELECT * FROM medicalrecord WHERE record_Id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, recordId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<MedicalRecord> findAll() {
        List<MedicalRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM medicalrecord ORDER BY visit_Date DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<MedicalRecord> findByPatient(int patientId) {
        List<MedicalRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM medicalrecord WHERE patient_Id=? ORDER BY visit_Date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             ps.setInt(1, patientId);
             ResultSet rs = ps.executeQuery();
             while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<MedicalRecord> findByDoctor(int doctorId) {
        List<MedicalRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM medicalrecord WHERE doct_Id=? ORDER BY visit_Date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private MedicalRecord mapRow(ResultSet rs) throws SQLException {
        Date nextVisitSql = rs.getDate("next_Visit");
        LocalDate nextVisit = (nextVisitSql != null) ? nextVisitSql.toLocalDate() : null;

        return new MedicalRecord(
                rs.getInt("record_Id"),
                rs.getInt("patient_Id"),
                rs.getDate("visit_Date").toLocalDate(),
                rs.getInt("doct_Id"),
                rs.getInt("curr_Weight"),
                rs.getInt("curr_height"),
                rs.getString("curr_Blood_Pressure"),
                rs.getInt("curr_Temp_F"),
                rs.getString("diagnosis"),
                rs.getString("treatment"),
                nextVisit
        );
    }
}