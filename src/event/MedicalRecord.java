package event;

import java.time.LocalDate;

public class MedicalRecord extends MedicalEvent{
    private int doctId;
    private int weight;
    private int height;
    private String bloodPresure;
    private int temp;
    private String diagnosis;
    private String treatment;

    private LocalDate next_visit;

    public MedicalRecord(int id, int patientId, LocalDate date, int doctId, int weight, int height,
     String bloodPresure, int temp, String diagnosis, String treatment,LocalDate next_visit) {
        super(id, patientId, date);
        this.doctId = doctId;
        this.weight = weight;
        this.height = height;
        this.bloodPresure = bloodPresure;
        this.temp = temp;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.next_visit= next_visit;
    }

    public int getDoctId() {
        return doctId;
    }

    public void setDoctId(int doctId) {
        this.doctId = doctId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getBloodPresure() {
        return bloodPresure;
    }

    public void setBloodPresure(String bloodPresure) {
        this.bloodPresure = bloodPresure;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public LocalDate getNext_visit() {
        return next_visit;
    }

    public void setNext_visit(LocalDate next_visit) {
        this.next_visit = next_visit;
    }


}
