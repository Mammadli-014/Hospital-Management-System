package controller;

import dao.DoctorDAO;
import model.Doctor;
import java.util.List;

public class DoctorController {

    private static DoctorController instance;
    private final DoctorDAO doctorDAO;

    private DoctorController() {
        this.doctorDAO = new DoctorDAO();
    }

    public static DoctorController getInstance() {
        if (instance == null) instance = new DoctorController();
        return instance;
    }

    public String addDoctor(Doctor d) {
        if (d.getFname() == null || d.getFname().isBlank())
            return "ERROR: First name is required!";
        if (d.getLname() == null || d.getLname().isBlank())
            return "ERROR: Last name is required!";
        if (d.getDeptId() <= 0)
            return "ERROR: A valid department must be selected!";

        if (d.getContact() != null && !d.getContact().isBlank()
                && !d.getContact().matches("\\d{10,11}"))
            return "ERROR: Contact number must be 10 or 11 digits!";

        boolean success = doctorDAO.add(d);
        return success ? "SUCCESS: Doctor added successfully." : "ERROR: Failed to add doctor.";
    }

    public String updateDoctor(Doctor d) {
        if (d.getId() <= 0)
            return "ERROR: Invalid doctor ID!";
        if (d.getFname() == null || d.getFname().isBlank())
            return "ERROR: First name is required!";
        if (d.getLname() == null || d.getLname().isBlank())
            return "ERROR: Last name is required!";

        boolean success = doctorDAO.update(d);
        return success ? "SUCCESS: Doctor updated successfully." : "ERROR: Update failed.";
    }

    public String deleteDoctor(int doctorId) {
        if (doctorId <= 0)
            return "ERROR: Invalid doctor ID!";
        if (doctorDAO.findById(doctorId) == null)
            return "ERROR: No doctor found with this ID!";

        boolean success = doctorDAO.delete(doctorId);
        return success ? "SUCCESS: Doctor deleted." : "ERROR: Deletion failed.";
    }

    public Doctor findById(int doctorId) {
        return doctorDAO.findById(doctorId);
    }

    public List<Doctor> findAll() {
        return doctorDAO.findAll();
    }

    public List<Doctor> findByDepartment(int deptId) {
        if (deptId <= 0) return doctorDAO.findAll();
        return doctorDAO.findByDepartment(deptId);
    }

    public List<Doctor> findBySurgeonType(String type) {
        if (type == null || type.isBlank()) return doctorDAO.findAll();
        return doctorDAO.findBySurgeonType(type);
    }
}