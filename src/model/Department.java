package model;

public class Department {
    private int deptId;
    private String dept_Name;

    public Department(int deptId, String dept_Name) {
        this.deptId = deptId;
        this.dept_Name = dept_Name;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public String getDept_Name() {
        return dept_Name;
    }

    public void setDept_Name(String dept_Name) {
        this.dept_Name = dept_Name;
    }

}
