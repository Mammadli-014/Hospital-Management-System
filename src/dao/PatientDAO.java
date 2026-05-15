package dao;

import db.DBConnection;
import model.Gender;
import model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    public boolean addPatient(Patient p) {
        String sql = "INSERT INTO patients (FName, LName, Gender, Date_Of_Birth, contact_No, pt_Address) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getFname());
            ps.setString(2, p.getLname());
            ps.setString(3, p.getGender() != null ? p.getGender().getMessage() : null);
            ps.setDate(4, Date.valueOf(p.getData_birth()));
            ps.setString(5, p.getContact());
            ps.setString(6, p.getAddress());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePatient(Patient p) {
        String sql = "UPDATE patients SET Fname=?, Lname=?, Gender=?, Contact=?, Address=? " +
                "WHERE patient_Id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getFname());
            ps.setString(2, p.getLname());
            ps.setString(3, String.valueOf(p.getGender()));
            ps.setDate(4, java.sql.Date.valueOf(p.getData_birth()));
            ps.setString(5, p.getContact());
            ps.setString(6, p.getAddress());
            ps.setInt(7, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePatient(int id) {
        String sql = "DELETE FROM patients WHERE Patient_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Patient findById(int id) {
        String sql = "SELECT * FROM patients WHERE Patient_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Patient> findAll() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Patient> search(String keyword) {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE Fname LIKE ? OR Lname LIKE ? OR Patient_id LIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        return new Patient(
                rs.getInt("patient_Id"),
                rs.getString("FName"),
                rs.getString("LName"),
                Gender.fromMessage(rs.getString("Gender")),
                rs.getDate("Date_Of_Birth").toLocalDate(),
                rs.getString("contact_No"),
                rs.getString("pt_Address"));
    }

}
