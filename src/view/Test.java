package view;

import dao.AppointmentDAO;
import db.DBConnection;
import event.AppointmentRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

public class Test {
    public static void main(String[] args) {

        AppointmentDAO appOBJ = new AppointmentDAO();
        int[] hours = {8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
        int[] minute = {0, 15, 30, 45};
        List<AppointmentRecord> apps = appOBJ.findAll();
        String sql = "UPDATE appointment set time = ?" + " WHERE appointment_Id = ? ";

        for (AppointmentRecord app : apps){
            Random r = new Random();
            int hour = r.nextInt(hours.length);
            int min = r.nextInt(minute.length);
            LocalTime localTime = LocalTime.of(hours[hour],minute[min]);
            try(Connection con = DBConnection.getConnection();
                PreparedStatement p =con.prepareStatement(sql)){
                p.setTime(1, Time.valueOf(localTime));
                p.setInt(2,app.getId());
                p.executeUpdate();
            }catch (SQLException e){e.printStackTrace();}
        }

    }
}