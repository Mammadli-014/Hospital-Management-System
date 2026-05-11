package controller;

import java.time.LocalDateTime;

public class DoctorController {
    private static DoctorController doctorController;

    private DoctorController() {
    }

    public DoctorController getInstance(){
        if(doctorController == null) doctorController=new DoctorController();
        return doctorController;
    }


}
