package controller;


import dao.*;
import event.MedicalRecord;
import event.SurgeryRecord;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class MedicalRecordController {

    private static MedicalRecordController instance;
    private final MedicalRecordDAO medicalRecordDAO;
    private final PatientDAO       patientDAO;
    private final DoctorDAO        doctorDAO;

    private MedicalRecordController() {
        this.medicalRecordDAO = new MedicalRecordDAO();
        this.patientDAO       = new PatientDAO();
        this.doctorDAO        = new DoctorDAO();
    }

    public static MedicalRecordController getInstance() {
        if (instance == null) instance = new MedicalRecordController();
        return instance;
    }

    public String addRecord(int patientId, int doctorId, String diagnosis, String treatment,
                            int weight, int height, String bp, int temp, LocalDate date, LocalDate nextVisit) {

        if (patientDAO.findById(patientId) == null)
            return "HATA: Hasta bulunamadı!";

        if (doctorDAO.findById(doctorId) == null)
            return "HATA: Doktor bulunamadı!";

        if (diagnosis == null || diagnosis.isBlank())
            return "HATA: Tanı alanı boş olamaz!";

        MedicalRecord record = new MedicalRecord(0, patientId, date, doctorId, weight, height,
                bp, temp, diagnosis, treatment, nextVisit);

        boolean success = medicalRecordDAO.add(record);
        return success ? "OK: Tıbbi kayıt eklendi." : "HATA: Kayıt eklenemedi.";
    }

    public List<MedicalRecord> findAll() {
        return medicalRecordDAO.findAll();
    }

    public List<MedicalRecord> findByPatient(int patientId) {
        return medicalRecordDAO.findByPatient(patientId);
    }

    public List<MedicalRecord> findByDoctor(int doctorId) {
        return medicalRecordDAO.findByDoctor(doctorId);
    }
}