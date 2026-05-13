package controller;

import dao.NurseDAO;
import model.Nurse;
import java.util.List;

public class NurseController {

    private static NurseController instance;
    private final NurseDAO nurseDAO;

    private NurseController() {
        this.nurseDAO = new NurseDAO();
    }

    public static NurseController getInstance() {
        if (instance == null) instance = new NurseController();
        return instance;
    }

    public String addNurse(Nurse n) {
        if (n.getFname() == null || n.getFname().isBlank())
            return "ERROR: First name is required!";
        if (n.getLname() == null || n.getLname().isBlank())
            return "ERROR: Last name is required!";
        if (n.getDeptId() <= 0)
            return "ERROR: A valid department must be selected!";

        if (n.getContact() != null && !n.getContact().isBlank()
                && !n.getContact().matches("\\d{10,11}"))
            return "ERROR: Phone number must be 10 or 11 digits!";

        boolean success = nurseDAO.add(n);
        return success ? "SUCCESS: Nurse added successfully." : "ERROR: Failed to add nurse.";
    }

    public String updateNurse(Nurse n) {
        if (n.getId() <= 0)
            return "ERROR: Invalid nurse ID!";
        if (n.getFname() == null || n.getFname().isBlank())
            return "ERROR: First name is required!";
        if (n.getLname() == null || n.getLname().isBlank())
            return "ERROR: Last name is required!";

        boolean success = nurseDAO.update(n);
        return success ? "SUCCESS: Nurse updated successfully." : "ERROR: Update failed.";
    }

    public String deleteNurse(int nurseId) {
        if (nurseId <= 0)
            return "ERROR: Invalid nurse ID!";
        if (nurseDAO.findById(nurseId) == null)
            return "ERROR: No nurse found with this ID!";

        boolean success = nurseDAO.delete(nurseId);
        return success ? "SUCCESS: Nurse deleted successfully." : "ERROR: Deletion failed.";
    }

    public Nurse findById(int nurseId) {
        return nurseDAO.findById(nurseId);
    }

    public List<Nurse> findAll() {
        return nurseDAO.findAll();
    }

    public List<Nurse> findByDepartment(int deptId) {
        if (deptId <= 0) return nurseDAO.findAll();
        return nurseDAO.findByDepartment(deptId);
    }
}