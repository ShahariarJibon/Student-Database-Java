import java.io.Serializable;

public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String phoneNumber;
    private String address;
    
    public Person() {}
    
    public Person(String name, String phoneNumber, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
    
    public String getName() { return name == null ? "" : name; }
    
    public void setName(String name) { this.name = name; }
    
    public String getPhoneNumber() { return phoneNumber == null ? "" : phoneNumber; }
    
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getAddress() { return address == null ? "" : address; }
    
    public void setAddress(String address) { this.address = address; }
    
    @Override
    public abstract String toString();
    
    public String getDetailsForFile() {
        return name + "," + phoneNumber + "," + address;
    }
}
