package dao;

import db.DBConnection;
import model.Doctor;
import model.Gender;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    public boolean add(Doctor d) {
        String sql = "INSERT INTO Doctor (FName, LName, Gender, Surgeron_type, Dept_id, Office_no, Contact_no) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getFname());
            ps.setString(2, d.getLname());
            ps.setString(3, String.valueOf(d.getGender()));
            ps.setString(4, d.getSurgeonType());
            ps.setInt(5, d.getDeptId());
            ps.setString(6, d.getOfficeNo());
            ps.setString(7,d.getContact());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Doctor d) {
        String sql = "UPDATE Doctor SET FName=?, LName=?, Gender=?, Surgeron_type=?, " +
                "Dept_id=?, Office_no=?, Contact_no = ? WHERE Doctor_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getFname());
            ps.setString(2, d.getLname());
            ps.setString(3, String.valueOf(d.getGender().getMessage()));
            ps.setString(4, d.getSurgeonType());
            ps.setInt(5, d.getDeptId());
            ps.setString(6, d.getOfficeNo());
            ps.setInt(7, d.getId());
            ps.setString(8,d.getContact());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int doctorId) {
        String sql = "DELETE FROM Doctor WHERE Doctor_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Doctor findById(int doctorId) {
        String sql = "SELECT * FROM Doctor WHERE Doctor_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Doctor> findAll() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM Doctor ORDER BY LName, FName";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Doctor> findByDepartment(int deptId) {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM Doctor WHERE Dept_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Doctor> findBySurgeonType(String type) {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM Doctor WHERE Surgeron_type LIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + type + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Doctor mapRow(ResultSet rs) throws SQLException {
        return new Doctor(
                rs.getInt("doct_Id"),
                rs.getString("FName"),
                rs.getString("LName"),
                Gender.fromMessage(rs.getString("Gender")),
                rs.getString("surgeon_Type"),
                rs.getInt("dept_Id"),
                rs.getString("office_No"),
                rs.getString("contact_No")
        );
    }
}