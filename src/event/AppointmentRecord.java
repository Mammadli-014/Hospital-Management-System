package event;

import java.time.LocalDate;

public class AppointmentRecord extends MedicalEvent{
    private int doctorId;
    private String reason;
    private int paymentAmount;
    private AppStatus status;
    private PaymentType paymentType;
    private AppointmentType appointmentType;

    public AppointmentRecord(int id, int patientId, LocalDate date,int doctorId, String reason, int paymentAmount, AppStatus status,
                             PaymentType paymentType, AppointmentType appointmentType) {
        super(id,patientId,date);
        this.doctorId = doctorId;
        this.reason = reason;
        this.paymentAmount = paymentAmount;
        this.status = status;
        this.paymentType = paymentType;
        this.appointmentType = appointmentType;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(int paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public AppStatus getStatus() {
        return status;
    }

    public void setStatus(AppStatus status) {
        this.status = status;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public AppointmentType getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(AppointmentType appointmentType) {
        this.appointmentType = appointmentType;
    }
}




enum AppStatus {
    COMPLETED, CANCELLED, SCHEDULED, NO_SHOW;
}

enum PaymentType{
    INSURANCE,CARD,CASH,DIGITAL_WALLET;
}

enum AppointmentType{
    CALL,IN_PERSON,ONLINE;
}