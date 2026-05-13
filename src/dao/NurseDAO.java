package dao;

import db.DBConnection;
import model.Gender;
import model.Nurse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NurseDAO {

    public boolean add(Nurse nurse) {
        String sql = "INSERT INTO nurse (dept_Id, FName, LName, Gender, contact_No) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement p = connection.prepareStatement(sql)) {
            p.setInt(1, nurse.getDeptId());
            p.setString(2, nurse.getFname());
            p.setString(3, nurse.getLname());
            p.setString(4, nurse.getGender() != null ? nurse.getGender().name() : null);
            p.setString(5, nurse.getContact());
            return p.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Nurse nurse) {
        // Added missing commas in SQL SET clause
        String sql = "UPDATE nurse SET dept_Id = ?, FName = ?, LName = ?, Gender = ?, contact_no = ? WHERE nurse_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement p = connection.prepareStatement(sql)) {
            p.setInt(1, nurse.getDeptId());
            p.setString(2, nurse.getFname());
            p.setString(3, nurse.getLname());
            p.setString(4, nurse.getGender() != null ? nurse.getGender().name() : null);
            p.setString(5, nurse.getContact());
            p.setInt(6, nurse.getId());
            return p.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int nurseId) {
        String sql = "DELETE FROM nurse WHERE nurse_Id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement p = connection.prepareStatement(sql)) {
            p.setInt(1, nurseId);
            return p.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Nurse findById(int id) {
        String sql = "SELECT * FROM nurse WHERE nurse_Id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement p = connection.prepareStatement(sql)) {
            p.setInt(1, id);
            try (ResultSet resultSet = p.executeQuery()) {
                if (resultSet.next()) return mapRow(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Nurse> findAll() {
        String sql = "SELECT * FROM nurse";
        List<Nurse> list = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Nurse> findByDepartment(int deptId) {
        List<Nurse> list = new ArrayList<>();
        String sql = "SELECT * FROM Nurse WHERE dept_Id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Nurse mapRow(ResultSet rs) throws SQLException {
        return new Nurse(
                rs.getInt("nurse_Id"),
                rs.getInt("dept_Id"),
                rs.getString("FName"),
                rs.getString("LName"),
                Gender.fromMessage(rs.getString("Gender")),
                rs.getString("contact_No")
        );
    }
}