import dao.AppointmentDAO;
import db.DBConnection;
import event.AppointmentRecord;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class Test {
    public static void main(String[] args) {
        AppointmentDAO appOBJ = new AppointmentDAO();

        int[] month = {6, 7};

        List<AppointmentRecord> apps = appOBJ.findScheduled();
        String sql = "UPDATE appointment SET appointment_Date = ? WHERE appoIntment_Id = ?";
        int a=0;

        for (AppointmentRecord app : apps) {
            Random r = new Random();
            int dayIndex = r.nextInt(1, 30);
            int monthIndex = r.nextInt(month.length);
            LocalDate localDate = LocalDate.of(2026, month[monthIndex], dayIndex);
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement p = con.prepareStatement(sql)) {
                p.setDate(1, java.sql.Date.valueOf(localDate));
                p.setInt(2, app.getId());
                a +=p.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println(a);

    }
}