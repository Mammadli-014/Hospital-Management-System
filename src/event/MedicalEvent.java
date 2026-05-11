package event;

import java.time.LocalDate;

public abstract class MedicalEvent {
    private int id;
    private int patientId;
    private LocalDate date;

    public MedicalEvent(int id, int patientId, LocalDate date) {
        this.id=id;
        this.patientId=patientId;
        this.date=date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

}


abstract class AdmissionRecord extends MedicalEvent{
    private int no;
    private int nurseNo;
    private LocalDate endingDate;
    private int amount;
    private PaymentType paymentType;

    public AdmissionRecord(int id, int patientId, LocalDate date,
                           int no, int nurseNo, LocalDate endingDate, int amount, PaymentType paymentType) {
        super(id, patientId, date);
        this.no = no;
        this.nurseNo = nurseNo;
        this.endingDate = endingDate;
        this.amount = amount;
        this.paymentType = paymentType;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getNurseNo() {
        return nurseNo;
    }

    public void setNurseNo(int nurseNo) {
        this.nurseNo = nurseNo;
    }

    public LocalDate getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(LocalDate endingDate) {
        this.endingDate = endingDate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }
}

