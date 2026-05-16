package controller;

import dao.DoctorDAO;
import dao.PatientDAO;
import dao.SurgeryDAO;
import event.SurgeryRecord;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class SurgeryController {

    private static controller.SurgeryController instance;
    private final SurgeryDAO surgeryDAO;
    private final PatientDAO patientDAO;
    private final DoctorDAO doctorDAO;

    private SurgeryController() {
        this.surgeryDAO  = new SurgeryDAO();
        this.patientDAO  = new PatientDAO();
        this.doctorDAO   = new DoctorDAO();
    }

    public static controller.SurgeryController getInstance() {
        if (instance == null) instance = new controller.SurgeryController();
        return instance;
    }

    public String addSurgery(int patientId, int surgeonId, int nurseId, LocalDate date,
                             LocalTime start, LocalTime end, int roomNo, String surgeryType, String notes) {

        if (patientDAO.findById(patientId) == null)
            return "HATA: Hasta bulunamadı!";

        if (doctorDAO.findById(surgeonId) == null)
            return "HATA: Cerrah bulunamadı!";

        if (surgeryType == null || surgeryType.isBlank())
            return "HATA: Ameliyat türü boş olamaz!";

        if (surgeryDAO.patientHasSurgeryOnDay(patientId, date))
            return "HATA: Bu hasta bu tarihte zaten ameliyat geçiriyor!";

        if (surgeryDAO.countDoctorSurgeriesOnDay(surgeonId, date) >= 5)
            return "HATA: Doktor bu gün için kapasiteye ulaştı!";

        SurgeryRecord record = new SurgeryRecord(0, patientId, date, surgeonId, nurseId, surgeryType, start, end, roomNo, notes);

        boolean success = surgeryDAO.add(record);
        return success ? "OK: Ameliyat kaydı eklendi." : "HATA: Kayıt eklenemedi.";
    }

    public List<SurgeryRecord> findAll() {
        return surgeryDAO.findAll();
    }

    public SurgeryRecord findById(int id){
        return surgeryDAO.findById(id);
    }
    public String delete(int id){
        boolean a = false;
        if(findById(id) != null){
            a =surgeryDAO.delete(id);
        }
        if(a) return "Success: Surgery Record has been deleted.";
        else return "Record can not been deleted.";
    }

    public List<SurgeryRecord> findByPatient(int patientId) {
        return surgeryDAO.findByPatient(patientId);
    }

    public List<SurgeryRecord> findByDoctor(int doctorId) {
        return surgeryDAO.findByDoctor(doctorId);
    }

    public List<SurgeryRecord> findByDate(LocalDate date) {
        return surgeryDAO.findByDate(date);
    }
}