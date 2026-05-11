package model;

public class Doctor extends Person {
    private String surgeonType;
    private int deptId;
    private String officeNo;

    public Doctor(int id, String fname, String lname, Gender gender, String surgeonType, int deptId, String officeNo, String contactNo) {
        super(id, fname, lname, gender, contactNo);
        this.surgeonType = surgeonType;
        this.deptId = deptId;
        this.officeNo = officeNo;
    }

    public String getSurgeonType() {
        return surgeonType;
    }

    public int getDeptId() {
        return deptId;
    }

    public String getOfficeNo() {
        return officeNo;
    }

    public void setSurgeonType(String v) {
        surgeonType = v;
    }

    public void setDeptId(int v) {
        deptId = v;
    }

    public void setOfficeNo(String v) {
        officeNo = v;
    }


    @Override
    public String toString() {
        return getDeptId() + " - Dr. " + getFname() + " " + getLname();
    }
}
