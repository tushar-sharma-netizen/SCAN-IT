package androidmads.example;

public class Details {
    String name;
    String address;
    String phone;
    String suffering;
    String type;

    public Details(){

    }

    public Details(String name, String address, String phone, String suffering, String type) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.suffering = suffering;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getSuffering() {
        return suffering;
    }

    public String getType() {
        return type;
    }
}
