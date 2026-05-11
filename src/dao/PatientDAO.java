package dao;

import db.DbConnection;
import model.Gender;
import model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO Patients (FName,LName,Gender,Date_Of_Birth,contact_No,pt_Address)" + "VALUES (?,?,?,?,?,?)";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement p = con.prepareStatement(sql)) {
            p.setString(1, patient.getFname());
            p.setString(2, patient.getLname());
            p.setString(3, String.valueOf(patient.getGender()));
            p.setDate(4, java.sql.Date.valueOf(patient.getData_birth()));
            p.setString(5, patient.getContact());
            p.setString(6, patient.getAddress());
            return p.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePatient(Patient p) {
        String sql = "UPDATE Patients SET Fname=?, Lname=?, Gender=?, Contact=?, Address=? " +
                "WHERE patient_Id=?";
        try (Connection con = DbConnection.getConnection();
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
        String sql = "DELETE FROM Patients WHERE Patient_id = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Patient findById(int id) {
        String sql = "SELECT * FROM Patients WHERE Patient_id = ?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int countNurse() {
        String sql = "Select count(*) from Patients";
        try (Connection con = DbConnection.getConnection();
             Statement statement = con.createStatement();
             ResultSet resultSet = statement.executeQuery(sql);
        ) {
            if (resultSet.next()) return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Patient> findAll() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM Patients ORDER BY Fname, Lname";

        try (Connection con = DbConnection.getConnection();
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
        String sql = "SELECT * FROM Patients WHERE Fname LIKE ? OR Lname LIKE ? OR Patient_id LIKE ?";
        try (Connection con = DbConnection.getConnection();
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
                rs.getInt("Patient_id"),
                rs.getString("Fname"),
                rs.getString("Lname"),
                Gender.valueof(rs.getString("Gender")),
                rs.getDate("Date_of_birth").toLocalDate(),
                rs.getString("Contact_no"),
                rs.getString("Address"));
    }

}
