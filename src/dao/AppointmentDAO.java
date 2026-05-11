package dao;

import db.DBConnection;
import enums.AppStatus;
import enums.AppointmentType;
import event.AppointmentRecord;
import enums.PaymentType;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    public boolean add(int patientId, int doctorId, LocalDateTime dt) {
        String sql = "INSERT INTO Appointment (Patient_id, Doct_id, Date) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1,       patientId);
            ps.setInt(2,       doctorId);
            ps.setTimestamp(3, Timestamp.valueOf(dt));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int appointmentId) {
        String sql = "DELETE FROM Appointment WHERE App_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public AppointmentRecord findById(int appointmentId) {
        String sql = "SELECT * FROM Appointment WHERE App_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<AppointmentRecord> findAll() {
        List<AppointmentRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM Appointment ORDER BY Date DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<AppointmentRecord> findByPatient(int patientId) {
        List<AppointmentRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM Appointment WHERE Patient_id=? ORDER BY Date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<AppointmentRecord> findByDoctor(int doctorId) {
        List<AppointmentRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM Appointment WHERE Doct_id=? ORDER BY Date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Aynı doktora 30 dakika içinde başka randevu var mı? */
    public boolean existsConflict(int doctorId, LocalDateTime dt) {
        String sql = "SELECT COUNT(*) FROM Appointment " +
                "WHERE Doct_id=? AND ABS(TIMESTAMPDIFF(MINUTE, Date, ?)) < 30";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1,       doctorId);
            ps.setTimestamp(2, Timestamp.valueOf(dt));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Hasta aynı gün aynı doktora randevu almış mı? */
    public boolean patientHasAppointmentOnDay(int patientId, int doctorId, LocalDate date) {
        String sql = "SELECT COUNT(*) FROM Appointment " +
                "WHERE Patient_id=? AND Doct_id=? AND DATE(Date)=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1,  patientId);
            ps.setInt(2,  doctorId);
            ps.setDate(3, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private AppointmentRecord mapRow(ResultSet rs) throws SQLException {
        return new AppointmentRecord(
                rs.getInt("appointment_Id"),
                rs.getInt("patient_Id"),
                rs.getDate("appointment_Date").toLocalDate(),
                rs.getInt("doct_Id"),
                rs.getString("reason"),
                rs.getInt("payment_amount"),
                AppStatus.fromMessage(rs.getString("appointment_status")),
                PaymentType.fromMessage(rs.getString("mode_of_payment")),
                AppointmentType.fromMessage(rs.getString("mode_of_appointment"))
                );
    }
}