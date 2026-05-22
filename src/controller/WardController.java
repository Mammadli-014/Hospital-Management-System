package controller;

import dao.WardDao;
import model.Ward;

import java.util.List;

public class WardController {

    private static WardController ward;
    private final WardDao wardDao;

    private WardController() {
        wardDao = new WardDao();
    }

    public static WardController getInstance() {
        if (ward == null) ward = new WardController();
        return ward;
    }

    public String addWard(String wardName, int deptId) {
        if (wardName == null || wardName.isBlank())
            return "ERROR: Ward name cannot be empty!";
        if (deptId <= 0)
            return "ERROR: Please select a valid department!";

        Ward w = new Ward(0, wardName, deptId);
        return wardDao.add(w)
                ? "SUCCESS: Ward has been added."
                : "ERROR: Ward could not be added.";
    }

    public String deleteWard(int wardNo) {
        if (wardNo <= 0)       return "ERROR: Invalid ward ID!";
        if (wardDao.findById(wardNo) == null) return "ERROR: Ward not found!";
        return wardDao.delete(wardNo)
                ? "SUCCESS: Ward deleted."
                : "ERROR: Could not delete ward.";
    }

    public List<Ward> findAll() {
        return wardDao.findAll();
    }

    public List<Ward> findByDepartment(int deptId) {
        return wardDao.findByDepartment(deptId);
    }

    public int getWardCount(int deptId) {
        return wardDao.countByDepartment(deptId);
    }
}