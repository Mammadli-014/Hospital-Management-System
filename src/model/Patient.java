package model;

import java.time.LocalDate;

public class Patient extends Person{
    private String address;
    private LocalDate data_birth;

    public Patient(int id, String fname, String lname, Gender gender,LocalDate data_birth,String contact,String address) {
        super(id,fname,lname,gender,contact);
        this.address=address;
        this.data_birth=data_birth;
    }

    public String getAddress()   { return address; }

    public void setAddress(String v) { address = v; }

    public LocalDate getData_birth() {
        return data_birth;
    }

    public void setData_birth(LocalDate data_birth) {
        this.data_birth = data_birth;
    }

    @Override
    public String toString() {
        return getId() + " - " + getFname() + " " + getAddress();
    }
}
