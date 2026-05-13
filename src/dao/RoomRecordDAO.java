package dao;

import db.DBConnection;
import event.RoomRecord;
import enums.PaymentType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RoomRecordDAO {

    public boolean add(RoomRecord record) {
        String sql = "INSERT INTO roomrecords (room_no, patient_Id, nurse_Id, admission_Date, " +
                "discharge_Date, amount, mode_of_payment) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, record.getNo()); // AdmissionRecord'dan gelen oda no
            ps.setInt(2, record.getPatientId());
            ps.setInt(3, record.getNurseNo());
            ps.setDate(4, Date.valueOf(record.getDate())); // MedicalEvent'ten gelen tarih
            ps.setDate(5, record.getEndingDate() != null ? Date.valueOf(record.getEndingDate()) : null);
            ps.setInt(6, record.getAmount());
            ps.setString(7, record.getPaymentType() != null ? record.getPaymentType().name() : null);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int admissionId) {
        String sql = "DELETE FROM roomrecords WHERE admission_Id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, admissionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public RoomRecord findById(int admissionId) {
        String sql = "SELECT * FROM roomrecords WHERE admission_Id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, admissionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<RoomRecord> findAll() {
        List<RoomRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM roomrecords ORDER BY admission_Date DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<RoomRecord> findActive() {
        List<RoomRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM roomrecords WHERE discharge_Date IS NULL";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean isRoomAvailable(int roomNo) {
        String sql = "SELECT COUNT(*) FROM roomrecords WHERE room_no=? AND discharge_Date IS NULL";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, roomNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) == 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean discharge(int admissionId, LocalDate dischargeDate, int finalAmount) {
        String sql = "UPDATE roomrecords SET discharge_Date=?, amount=? WHERE admission_Id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dischargeDate));
            ps.setInt(2, finalAmount);
            ps.setInt(3, admissionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Integer> getAvailableRooms() {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT r.room_no FROM Room r " +
                "WHERE r.room_no NOT IN (" +
                "  SELECT room_no FROM roomrecords WHERE discharge_Date IS NULL)";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(rs.getInt(1));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }


    public boolean isPatientAdmitted(int patientId) {
        String sql = "SELECT COUNT(*) FROM roomrecords WHERE patient_Id=? AND discharge_Date IS NULL";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private RoomRecord mapRow(ResultSet rs) throws SQLException {
        Date dischargeSql = rs.getDate("discharge_Date");
        LocalDate dischargeDate = (dischargeSql != null) ? dischargeSql.toLocalDate() : null;

        return new RoomRecord(
                rs.getInt("admission_Id"),
                rs.getInt("patient_Id"),
                rs.getDate("admission_Date").toLocalDate(),
                rs.getInt("room_no"),
                rs.getInt("nurse_Id"),
                dischargeDate,
                rs.getInt("amount"),
                PaymentType.fromMessage(rs.getString("mode_of_payment"))
        );
    }
}