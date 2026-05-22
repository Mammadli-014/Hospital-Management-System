package dao;

import db.DBConnection;
import model.Ward;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WardDao {

    public boolean add(Ward w) {
        String sql = "INSERT INTO ward (ward_Name, dept_Id) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, w.getWardName());
            ps.setInt(2,    w.getDeptId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int wardNo) {
        String sql = "DELETE FROM ward WHERE ward_No = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, wardNo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public Ward findById(int wardNo) {
        String sql = "SELECT * FROM ward WHERE ward_No = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, wardNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Ward> findAll() {
        List<Ward> list = new ArrayList<>();
        String sql = "SELECT * FROM ward ORDER BY ward_No";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Ward> findByDepartment(int deptId) {
        List<Ward> list = new ArrayList<>();
        String sql = "SELECT * FROM ward WHERE dept_Id = ? ORDER BY ward_Name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public int countByDepartment(int deptId) {
        String sql = "SELECT COUNT(*) FROM ward WHERE dept_Id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Ward mapRow(ResultSet rs) throws SQLException {
        return new Ward(
                rs.getInt("ward_No"),
                rs.getString("ward_Name"),
                rs.getInt("dept_Id")
        );
    }
}