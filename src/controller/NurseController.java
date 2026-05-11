package controller;

public class NurseController {
    private static NurseController nurseController;

    private NurseController() {
    }

    public NurseController getInstance(){
        if(nurseController == null) nurseController=new NurseController();
        return nurseController;
    }


}
