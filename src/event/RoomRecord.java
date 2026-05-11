package event;

import enums.PaymentType;

import java.time.LocalDate;

public class RoomRecord extends AdmissionRecord{

    public RoomRecord(int id, int patientId, LocalDate date,
                      int roomNo, int nurseId, LocalDate endingDate, int amount, PaymentType paymentType) {
        super(id, patientId, date,roomNo,nurseId,endingDate,amount,paymentType);
    }
}
