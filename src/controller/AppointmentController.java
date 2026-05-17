package controller;

import dao.*;
import enums.*;
import event.AppointmentRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AppointmentController {

    private static AppointmentController instance;
    private final AppointmentDAO appointmentDAO;
    private final PatientDAO     patientDAO;
    private final DoctorDAO      doctorDAO;

    private AppointmentController() {
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO     = new PatientDAO();
        this.doctorDAO      = new DoctorDAO();
    }

    public static AppointmentController getInstance() {
        if (instance == null) instance = new AppointmentController();
        return instance;
    }

    public String createAppointment(int patientId, int doctorId, LocalDateTime dt, String reason,
                                    int amount, PaymentType pType, AppointmentType aType, LocalTime time) {

        if (dt.isBefore(LocalDateTime.now()))
            return "ERROR: Cannot create appointment in the past!";

        if (patientDAO.findById(patientId) == null)
            return "ERROR: Patient not found! (ID: " + patientId + ")";

        if (doctorDAO.findById(doctorId) == null)
            return "ERROR: Doctor not found! (ID: " + doctorId + ")";

        if (appointmentDAO.existsConflict(doctorId, dt))
            return "ERROR: Doctor already has an appointment at this time!";

        if (appointmentDAO.patientHasAppointmentOnDay(patientId, doctorId, dt.toLocalDate()))
            return "ERROR: Patient already has an appointment with this doctor today!";

        AppointmentRecord record = new AppointmentRecord(
                0, patientId, dt.toLocalDate(), doctorId, reason, amount, AppStatus.SCHEDULED, pType, aType,time
        );

        boolean success = appointmentDAO.add(record);
        return success ? "SUCCESS: Appointment created." : "ERROR: Failed to save appointment.";
    }

    public String deleteAppointment(int appId){
        AppointmentRecord existing = appointmentDAO.findById(appId);
        if (existing == null)
            return "ERROR: Appointment not found!";

        if (existing.getDate().isBefore(LocalDate.now()))
            return "ERROR: Past appointments cannot be deleted!";

        boolean success = appointmentDAO.deleteAppointment(appId);
        return success ? "SUCCESS: Appointment deleted." : "ERROR: Deletion failed.";
    }
    public String cancelAppointment(int appointmentId) {
        AppointmentRecord existing = appointmentDAO.findById(appointmentId);
        if (existing == null)
            return "ERROR: Appointment not found!";

        if (existing.getDate().isBefore(LocalDate.now()))
            return "ERROR: Past appointments cannot be cancelled!";

        boolean success = appointmentDAO.cancelAppointment(appointmentId,AppStatus.CANCELLED);
        return success ? "SUCCESS: Appointment cancelled." : "ERROR: Cancellation failed.";
    }

    public List<AppointmentRecord> findAll() {
        return appointmentDAO.findAll();
    }

    public List<AppointmentRecord> findByPatient(int patientId) {
        return appointmentDAO.findByPatient(patientId);
    }

    public List<AppointmentRecord> findByDoctor(int doctorId) {
        return appointmentDAO.findByDoctor(doctorId);
    }

}