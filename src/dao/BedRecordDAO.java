package dao;

import db.DBConnection;
import event.BedRecord;
import enums.PaymentType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BedRecordDAO {

    public boolean add(BedRecord record) {
        String sql = "INSERT INTO bedrecords (bed_No, patient_Id, nurse_Id, admission_Date, " +
                "discharge_Date, amount, mode_of_payment) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, record.getNo()); // AdmissionRecord'dan gelen yatak/oda no
            ps.setInt(2, record.getPatientId());
            ps.setInt(3, record.getNurseNo());
            ps.setDate(4, Date.valueOf(record.getDate())); // MedicalEvent'ten gelen admission_date
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
        String sql = "DELETE FROM bedrecords WHERE admission_Id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, admissionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public BedRecord findById(int admissionId) {
        String sql = "SELECT * FROM bedrecords WHERE admission_Id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, admissionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<BedRecord> findAll() {
        List<BedRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM bedrecords ORDER BY admission_Date DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<BedRecord> findByPatient(int patientId) {
        List<BedRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM bedrecords WHERE patient_Id=? ORDER BY admission_Date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<BedRecord> findActive() {
        List<BedRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM bedrecords WHERE discharge_Date IS NULL";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean isBedAvailable(int bedNo) {
        String sql = "SELECT COUNT(*) FROM bedrecords WHERE bed_No=? AND discharge_Date IS NULL";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bedNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) == 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean discharge(int admissionId, LocalDate dischargeDate, int finalAmount) {
        String sql = "UPDATE bedrecords SET discharge_Date=?, amount=? WHERE admission_Id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dischargeDate));
            ps.setInt(2, finalAmount);
            ps.setInt(3, admissionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean isPatientAdmitted(int patientId) {
        String sql = "SELECT COUNT(*) FROM bedrecords WHERE patient_Id=? AND discharge_Date IS NULL";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Integer> getAvailableBeds() {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT b.bed_No FROM Bed b " +
                "WHERE b.bed_No NOT IN (" +
                "  SELECT bed_No FROM bedrecords WHERE discharge_Date IS NULL)";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(rs.getInt(1));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private BedRecord mapRow(ResultSet rs) throws SQLException {
        Date dischargeSql = rs.getDate("discharge_Date");
        LocalDate dischargeDate = (dischargeSql != null) ? dischargeSql.toLocalDate() : null;

        PaymentType pType = PaymentType.fromMessage(rs.getString("mode_of_payment"));

        return new BedRecord(
                rs.getInt("admission_Id"),
                rs.getInt("patient_Id"),
                rs.getDate("admission_Date").toLocalDate(),
                rs.getInt("bed_No"),
                rs.getInt("nurse_Id"),
                dischargeDate,
                rs.getInt("amount"),
                PaymentType.fromMessage(rs.getString("mode_of_payment"))
        );
    }

    public void debugPaymentValues() {
        String sql = "SELECT DISTINCT mode_of_payment FROM bedrecords";

        try (Connection con = db.DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("--- Database Payment Values ---");
            while (rs.next()) {
                String value = rs.getString("mode_of_payment");
                System.out.println("Value in DB: [" + value + "]");
            }
            System.out.println("-------------------------------");

        } catch (SQLException e) {
            System.out.println("Error while reading DB: " + e.getMessage());
        }
    }
}