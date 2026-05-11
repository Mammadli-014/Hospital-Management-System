package event;

import java.time.LocalDate;

public class BedRecord extends AdmissionRecord{

    public BedRecord(int id, int patientId, LocalDate date,
                      int roomNo, int nurseId, LocalDate endingDate, int amount, PaymentType paymentType) {
        super(id, patientId, date,roomNo,nurseId,endingDate,amount,paymentType);
    }
}
