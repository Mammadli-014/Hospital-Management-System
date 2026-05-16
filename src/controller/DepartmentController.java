package controller;

import dao.DepartmentDAO;
import model.Department;

import java.util.List;

public class DepartmentController {
    private static DepartmentController departmentController;
    private DepartmentDAO departmentDAO;
    private DepartmentController(){
        departmentDAO=new DepartmentDAO();
    }
    public static DepartmentController getInstance(){
        if(departmentController == null) departmentController = new DepartmentController();
        return departmentController;
    }

    public List<Department> findAll(){
        return departmentDAO.findAll();
    }

    public int getDoctorCount(int deptId) {
        return departmentDAO.getDoctorCount(deptId);
    }

    public int getNurseCount(int deptId){
        return departmentDAO.getNurseCount(deptId);
    }
}
