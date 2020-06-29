package androidmads.example;

import java.util.Date;

public class Reports {
    String id;
    String number1;
    String number2;
    String suffering;
    Date time;


    public Reports(){

    }

    public Reports(String id, String number1, String number2, String suffering, Date time) {
        this.id = id;
        this.number1 = number1;
        this.number2 = number2;
        this.suffering = suffering;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getNumber1() {
        return number1;
    }

    public String getNumber2() {
        return number2;
    }

    public String getSuffering() {
        return suffering;
    }

    public Date getTime() {
        return time;
    }
}

