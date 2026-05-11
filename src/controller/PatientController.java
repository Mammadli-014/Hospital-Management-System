package controller;

import dao.PatientDAO;
import model.Patient;

import java.util.List;

public class PatientController {
    private static PatientController patientController;
    private final PatientDAO patientDAO;
    private PatientController() {
        patientDAO=new PatientDAO();
    }
    public PatientController getInstance(){
        if(patientController == null) patientController=new PatientController();
        return patientController;
    }

    public String addPatient(Patient p) {
        if (p.getFname() == null || p.getFname().isBlank())
            return "Error: First name required!";
        if (p.getLname() == null || p.getLname().isBlank())
            return "Error: Las name required!";
        if (p.getContact() != null && !p.getContact().isBlank()
                && !p.getContact().matches("\\d{10,11}"))
            return "Error: Phone num must contain 11 nums!";

        boolean success = patientDAO.addPatient(p);
        return success ? "Patient has been added succesfully." : "Error: Patient can not been added.";
    }

    public String updatePatient(Patient patient){
        if(patient.getId()<0) return "Invalid ID";
        if(patient.getFname() == null || patient.getFname().isBlank())
            return "Error: Invalid first name!";
        if (patient.getLname() == null || patient.getLname().isBlank())
            return "Error: Invalid last name!";
        if (patient.getContact() != null && !patient.getContact().isBlank()
                && !patient.getContact().matches("\\d{10,11}"))
            return "Error: phone num must contain 11 nums!";
        boolean isExecute = patientDAO.updatePatient(patient);
        return isExecute? "Patient has been updated succesfully" : "Error: Patient can not been updated";
    }

    public String deletePatient(int patientId) {
        if (patientId <= 0)
            return "Invalid ID!";
        if (patientDAO.findById(patientId) == null)
            return "Error: There is no patient with this ID!";

        boolean success = patientDAO.deletePatient(patientId);
        return success ? "Patient has been deleted successfully." : "Error: Patient can not been deleted.";
    }

    public  Patient findByID(int patientId){
        return patientDAO.findById(patientId);
    }

    public List<Patient> listAllPatients(){
        return patientDAO.findAll();
    }

    public List<Patient> search(String keyword){
        if(keyword == null || keyword.isBlank())
            return listAllPatients();
        return patientDAO.search(keyword);
    }
}
