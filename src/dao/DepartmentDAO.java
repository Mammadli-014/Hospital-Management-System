package dao;

import db.DBConnection;
import model.Department;
import model.Nurse;
import model.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {


    public List<Department> findAll(){
        List<Department> departments =new ArrayList<>();
        String sql = "SELECT * FROM department";
        try(Connection con = DBConnection.getConnection();
            PreparedStatement p = con.prepareStatement(sql)){
            ResultSet rs = p.executeQuery();
            while(rs.next()) departments.add(mapRow(rs));

        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
        return departments;
    }

    public Department findById(int id) {
        String sql = "SELECT * FROM department WHERE dept_id = ?";
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

    public int getDoctorCount(int deptId) {
        String sql = "SELECT COUNT(*) FROM doctor WHERE dept_Id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getNurseCount(int deptId) {
        String sql = "SELECT COUNT(*) FROM nurse WHERE dept_Id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }


    private Department mapRow(ResultSet rs)throws SQLException {
        return new Department(
        rs.getInt("dept_Id"),
        rs.getString("dept_Name")
        );
    }
}
