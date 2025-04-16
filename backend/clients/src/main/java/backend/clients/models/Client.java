package backend.clients.models;

public class Client {
    private int ID;
    private String Name;
    private String Email;
    private String Phone;
    private String Password;
    private Double Miles;
    private Address Address;

    public Client() {
    }

    public Client(int iD, String name, String email, String phone, String password, Double miles,
            backend.clients.models.Address address) {
        ID = iD;
        Name = name;
        Email = email;
        Phone = phone;
        Password = password;
        Miles = miles;
        Address = address;
    }

    public int getID() {
        return ID;
    }
    public void setID(int iD) {
        ID = iD;
    }
    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }
    public String getEmail() {
        return Email;
    }
    public void setEmail(String email) {
        Email = email;
    }
    public String getPhone() {
        return Phone;
    }
    public void setPhone(String phone) {
        Phone = phone;
    }
    public String getPassword() {
        return Password;
    }
    public void setPassword(String password) {
        Password = password;
    }
    public Double getMiles() {
        return Miles;
    }
    public void setMiles(Double miles) {
        Miles = miles;
    }
    public Address getAddress() {
        return Address;
    }
    public void setAddress(Address address) {
        Address = address;
    }
}
