package dao;

import db.DBConnection;
import enums.*;
import event.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class AppointmentDAO {
    public boolean add(AppointmentRecord app) {
        String sql = "INSERT INTO Appointment (patient_Id, doct_Id, reason, appointment_Date, " +
                "payment_amount, mode_of_payment, mode_of_appointment, appointment_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, app.getPatientId());
            ps.setInt(2, app.getDoctorId());
            ps.setString(3, app.getReason());
            ps.setTimestamp(4, Timestamp.valueOf(app.getDate().atStartOfDay()));
            ps.setInt(5, app.getPaymentAmount());
            ps.setString(6, app.getPaymentType().name());
            ps.setString(7, app.getAppointmentType().name());
            ps.setString(8, app.getStatus().name());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int appointmentId) {
        String sql = "DELETE FROM Appointment WHERE appoIntment_Id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean existsConflict(int doctorId, LocalDateTime dt) {
        String sql = "SELECT COUNT(*) FROM Appointment WHERE doct_Id=? AND appointment_Date=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setTimestamp(2, Timestamp.valueOf(dt));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean patientHasAppointmentOnDay(int patientId, int doctorId, LocalDate date) {
        String sql = "SELECT COUNT(*) FROM Appointment " +
                "WHERE patient_Id=? AND doct_Id=? AND DATE(appointment_Date)=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setDate(3, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public AppointmentRecord findById(int appointmentId) {
        String sql = "SELECT * FROM Appointment WHERE appoIntment_Id=?";
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
        String sql = "SELECT * FROM Appointment";
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
        String sql = "SELECT * FROM Appointment WHERE doct_Id=? ORDER BY Date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
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