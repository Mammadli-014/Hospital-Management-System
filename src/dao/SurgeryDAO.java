package dao;

import db.DBConnection;
import event.SurgeryRecord;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SurgeryDAO {

    public boolean add(SurgeryRecord record) {
        String sql = "INSERT INTO surgeryrecord (patient_Id, surgeon_Id, nurse_Id, surgery_Type, " +
                "surgery_Date, start_Time, end_Time, room_no, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, record.getPatientId());
            ps.setInt(2, record.getSurgeonId());
            ps.setInt(3, record.getNurseId());
            ps.setString(4, record.getSurgeryType());
            ps.setDate(5, Date.valueOf(record.getDate()));
            ps.setTime(6, Time.valueOf(record.getStartTime()));
            ps.setTime(7, Time.valueOf(record.getEndTime()));
            ps.setInt(8, record.getRoomNo());
            ps.setString(9, record.getNotes());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int surgeryId) {
        String sql = "DELETE FROM surgeryrecord WHERE surgery_Id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, surgeryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public SurgeryRecord findById(int surgeryId) {
        String sql = "SELECT * FROM surgeryrecord WHERE surgery_Id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, surgeryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<SurgeryRecord> findAll() {
        List<SurgeryRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM surgeryrecord";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<SurgeryRecord> findByPatient(int patientId) {
        List<SurgeryRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM surgeryrecord WHERE patient_Id=? ORDER BY surgery_Date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<SurgeryRecord> findByDoctor(int surgeonId) {
        List<SurgeryRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM surgeryrecord WHERE surgeon_Id=? ORDER BY surgery_Date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, surgeonId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<SurgeryRecord> findByDate(LocalDate date) {
        List<SurgeryRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM surgeryrecord WHERE surgery_Date=? ORDER BY start_Time ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean patientHasSurgeryOnDay(int patientId, LocalDate date) {
        String sql = "SELECT COUNT(*) FROM surgeryrecord WHERE patient_Id=? AND surgery_Date=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1,  patientId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public int countDoctorSurgeriesOnDay(int surgeonId, LocalDate date) {
        String sql = "SELECT COUNT(*) FROM surgeryrecord WHERE surgeon_Id=? AND surgery_Date=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1,  surgeonId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private SurgeryRecord mapRow(ResultSet rs) throws SQLException {
        LocalTime startTime = rs.getTime("start_Time") != null ? rs.getTime("start_Time").toLocalTime() : null;
        LocalTime endTime = rs.getTime("end_Time") != null ? rs.getTime("end_Time").toLocalTime() : null;

        return new SurgeryRecord(
                rs.getInt("surgery_Id"),
                rs.getInt("patient_Id"),
                rs.getDate("surgery_Date").toLocalDate(),
                rs.getInt("surgeon_Id"),
                rs.getInt("nurse_Id"),
                rs.getString("surgery_Type"),
                startTime,
                endTime,
                rs.getInt("room_no"),
                rs.getString("notes")
        );
    }
}