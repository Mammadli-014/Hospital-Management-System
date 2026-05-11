package dao;

import db.DBConnection;
import model.Gender;
import model.Nurse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NurseDAO {

    public boolean addNurse(Nurse nurse){
        String sql="INSERT INTO nurse(dept_Id,FName,LName,Gender,contact_No)" + "VALUES(?,?,?,?,?)";
        try(Connection connection = DBConnection.getConnection();
            PreparedStatement p = connection.prepareStatement(sql)
        ) {
            p.setInt(1,nurse.getDeptId());
            p.setString(2,nurse.getFname());
            p.setString(3, nurse.getLname());
            p.setString(4,String.valueOf(nurse.getGender()));
            p.setString(5, nurse.getContact());
            return p.executeUpdate()>0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateNurse(Nurse nurse){
        String sql = "Update nurse set dept_Id=? FName = ?,LName = ?,Gender = ?,contact_no = ?" + "Where nurse_id = ?";
        try(Connection connection = DBConnection.getConnection();
            PreparedStatement p = connection.prepareStatement(sql)
        ){
            p.setInt(1,nurse.getDeptId());
            p.setString(2,nurse.getFname());
            p.setString(3,nurse.getLname());
            p.setString(4,String.valueOf(nurse.getGender()));
            p.setString(5,nurse.getContact());
            p.setInt(6,nurse.getId());
            return p.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteNurse(Nurse nurse){
        String sql = "Delete from nurse"+ "where nurse_Id = ?";
        try(Connection connection = DBConnection.getConnection();
            PreparedStatement p =connection.prepareStatement(sql);
        ){
            p.setInt(1,nurse.getId());
            return p.executeUpdate() > 0;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }

    }

    public Nurse findById(int id){
        String sql = "Select * from nurse" + "where nurse_Id =?";
        try(Connection connection = DBConnection.getConnection();
            PreparedStatement p = connection.prepareStatement(sql);
        ){
            p.setInt(1,id);
            ResultSet resultSet=p.executeQuery();
            if(resultSet.next()) return mapRow(resultSet);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public List<Nurse> findAll(){
        String sql = "Select * from nurse" + "ORDER by FName,LName";
        List<Nurse> nurseList = new ArrayList<>();
        try(Connection connection = DBConnection.getConnection();
            PreparedStatement p = connection.prepareStatement(sql);
        ){
            ResultSet resultSet=p.executeQuery();
            while(resultSet.next()) {nurseList.add(mapRow(resultSet));}
            return nurseList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public List<Nurse> search(String keyword){
        List<Nurse> list = new ArrayList<>();
        String sql = "SELECT * FROM Nurse WHERE Fname LIKE ? OR Lname LIKE ? OR Nurse_Id LIKE ?";
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

    public int countNurse(){
        String sql = "select count(*) from nurse";
        try (Connection con = DBConnection.getConnection();
             Statement statement = con.createStatement();
             ResultSet resultSet = statement.executeQuery(sql);
        ) {
            if (resultSet.next()) return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Nurse mapRow(ResultSet resultSet) throws SQLException{
        return new Nurse(
                resultSet.getInt("nurse_Id"),
                resultSet.getInt("dept_Id"),
                resultSet.getString("FName"),
                resultSet.getString("LName"),
                Gender.fromMessage(resultSet.getString("Gender")),
                resultSet.getString("contact_No")
        );
    }
}
