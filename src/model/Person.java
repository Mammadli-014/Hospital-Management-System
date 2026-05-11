package model;

public class Person {
    private int id;
    private String fname;
    private String lname;
    private Gender gender;

    private String contactNo;

    public Person(int id, String fname, String lname, Gender gender, String contactNo) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.gender = gender;
        this.contactNo = contactNo;
    }
    public Person(int id, String fname, String lname) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
    }

    public int getId()           { return id; }
    public String getFname()     { return fname; }
    public String getLname()     { return lname; }
    public Gender getGender()    { return gender; }
    public String getContact()   { return contactNo; }


    public void setId(int v)         { id = v; }
    public void setFname(String v)   { fname = v; }
    public void setLname(String v)   { lname = v; }
    public void setGender(Gender v)  { gender = v; }
    public void setContact(String v) { contactNo = v; }

}