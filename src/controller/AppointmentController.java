package controller;

import java.time.LocalDateTime;
import java.util.List;

public class AppointmentController {
    private static AppointmentController appointmentDAO;
    private List<AppointmentController> appointmentDAOList;

    private AppointmentController() {
    }

    public AppointmentController getInstance(){
        if(appointmentDAO == null) appointmentDAO=new AppointmentController();
        return appointmentDAO;
    }

    public String createAppointment(int patientId, int doctorId, LocalDateTime dt) {
        if (appointmentDAO.existsConflict(doctorId, dt)) {
            return "Bu saatte doktorun randevusu var!";
        }
        if (dt.isBefore(LocalDateTime.now())) {
            return "Geçmiş tarihe randevu oluşturulamaz!";
        }
        //appointmentDAOList.add();
        return "";
    }
    private boolean existsConflict(int doctorId, LocalDateTime dt){
        return false;
    }
}
