package controller;

import dao.*;
import event.BedRecord;
import event.RoomRecord;
import enums.PaymentType;

import java.time.LocalDate;
import java.util.List;

public class AdmissionController {

    private static AdmissionController instance;
    private final BedRecordDAO bedRecordDAO;
    private final RoomRecordDAO roomRecordDAO;
    private final PatientDAO patientDAO;
    private final NurseDAO nurseDAO;

    private AdmissionController() {
        this.bedRecordDAO = new BedRecordDAO();
        this.roomRecordDAO = new RoomRecordDAO();
        this.patientDAO = new PatientDAO();
        this.nurseDAO = new NurseDAO();
    }

    public static AdmissionController getInstance() {
        if (instance == null) instance = new AdmissionController();
        return instance;
    }

    public String admitToBed(int patientId, int nurseId, int bedNo, LocalDate admissionDate,
                             int initialAmount, PaymentType pType) {

        if (patientDAO.findById(patientId) == null)
            return "ERROR: Patient not found! (ID: " + patientId + ")";

        if (nurseDAO.findById(nurseId) == null)
            return "ERROR: Nurse not found! (ID: " + nurseId + ")";

        if (bedRecordDAO.isPatientAdmitted(patientId) || roomRecordDAO.isPatientAdmitted(patientId))
            return "ERROR: This patient is already admitted!";

        if (!bedRecordDAO.isBedAvailable(bedNo))
            return "ERROR: Bed is occupied! (Bed No: " + bedNo + ")";

        if (admissionDate.isAfter(LocalDate.now()))
            return "ERROR: Admission date cannot be in the future!";

        BedRecord record = new BedRecord(0, patientId, admissionDate, bedNo, nurseId, null, initialAmount, pType);

        boolean success = bedRecordDAO.add(record);
        return success ? "SUCCESS: Patient admitted to bed." : "ERROR: Admission failed.";
    }

    public String admitToRoom(int patientId, int nurseId, int roomNo, LocalDate admissionDate,
                              int initialAmount, PaymentType pType) {

        if (patientDAO.findById(patientId) == null)
            return "ERROR: Patient not found!";

        if (nurseDAO.findById(nurseId) == null)
            return "ERROR: Nurse not found!";

        if (bedRecordDAO.isPatientAdmitted(patientId) || roomRecordDAO.isPatientAdmitted(patientId))
            return "ERROR: Patient already admitted!";

        if (!roomRecordDAO.isRoomAvailable(roomNo))
            return "ERROR: Room is occupied! (Room No: " + roomNo + ")";

        RoomRecord record = new RoomRecord(0, patientId, admissionDate, roomNo, nurseId, null, initialAmount, pType);

        boolean success = roomRecordDAO.add(record);
        return success ? "SUCCESS: Patient admitted to room." : "ERROR: Admission failed.";
    }

    public String dischargeFromBed(int admissionId, LocalDate dischargeDate, int finalAmount) {
        BedRecord record = bedRecordDAO.findById(admissionId);
        if (record == null)
            return "ERROR: Admission record not found!";

        if (dischargeDate.isBefore(record.getDate()))
            return "ERROR: Discharge date cannot be before admission date!";

        if (record.getEndingDate() != null)
            return "ERROR: Patient already discharged!";

        boolean success = bedRecordDAO.discharge(admissionId, dischargeDate, finalAmount);
        return success ? "SUCCESS: Patient discharged from bed." : "ERROR: Discharge failed.";
    }

    public String dischargeFromRoom(int admissionId, LocalDate dischargeDate, int finalAmount) {
        RoomRecord record = roomRecordDAO.findById(admissionId);
        if (record == null)
            return "ERROR: Room admission record not found!";

        if (dischargeDate.isBefore(record.getDate()))
            return "ERROR: Discharge date cannot be before admission date!";

        if (record.getEndingDate() != null)
            return "ERROR: Patient already discharged!";

        boolean success = roomRecordDAO.discharge(admissionId, dischargeDate, finalAmount);
        return success ? "SUCCESS: Patient discharged from room." : "ERROR: Discharge failed.";
    }

    public List<BedRecord> getAllBedRecords() { return bedRecordDAO.findAll(); }
    public List<RoomRecord> getAllRoomRecords() { return roomRecordDAO.findAll(); }
    public List<Integer> getAvailableBeds() { return bedRecordDAO.getAvailableBeds(); }
    public List<Integer> getAvailableRooms() { return roomRecordDAO.getAvailableRooms(); }
}