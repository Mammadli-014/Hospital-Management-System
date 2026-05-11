package model;

public class Nurse extends Person{
    private int deptId;

    public Nurse(int id,int deptId, String fname, String lname,Gender gender,String contactNo) {
        super(id, fname, lname,gender,contactNo);
        this.deptId=deptId;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }
}
