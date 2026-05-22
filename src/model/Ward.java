package model;

public class Ward {
    private int wardNo;
    private String wardName;
    private int deptId;

    public Ward(int wardNo, String wardName, int deptId) {
        this.wardNo = wardNo;
        this.wardName = wardName;
        this.deptId = deptId;
    }

    public int getWardNo() {
        return wardNo;
    }

    public String getWardName() {
        return wardName;
    }

    public int getDeptId() {
        return deptId;
    }
}
