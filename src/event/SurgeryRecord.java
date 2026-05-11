package event;

import java.time.LocalDate;
import java.time.LocalTime;

public class SurgeryRecord extends MedicalEvent{
    private int surgeonId;
    private int nurseId;
    private String surgeryType;
    private LocalTime startTime;
    private LocalTime endTime;
    private int roomNo;
    private String notes;

    public SurgeryRecord(int id, int patientId, LocalDate date, int surgeonId, int nurseId,
        String surgeryType, LocalTime startTime, LocalTime endTime, int roomNo, String notes) {
        super(id, patientId, date);
        this.surgeonId = surgeonId;
        this.nurseId = nurseId;
        this.surgeryType = surgeryType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomNo = roomNo;
        this.notes = notes;
    }

    public int getSurgeonId() {
        return surgeonId;
    }

    public void setSurgeonId(int surgeonId) {
        this.surgeonId = surgeonId;
    }

    public int getNurseId() {
        return nurseId;
    }

    public void setNurseId(int nurseId) {
        this.nurseId = nurseId;
    }

    public String getSurgeryType() {
        return surgeryType;
    }

    public void setSurgeryType(String surgeryType) {
        this.surgeryType = surgeryType;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public int getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(int roomNo) {
        this.roomNo = roomNo;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
