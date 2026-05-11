import java.io.Serializable;

/**
 * Abstract base class representing a person.
 * This class demonstrates abstraction and encapsulation.
 * 
 * @author JIBON
 * @version 1.0
 */
public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String phoneNumber;
    private String address;
    
    /**
     * Default constructor.
     */
    public Person() {}
    
    /**
     * Parameterized constructor.
     * @param name Person's name
     * @param phoneNumber Phone number
     * @param address Address
     */
    public Person(String name, String phoneNumber, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
    
    /**
     * Gets the name.
     * @return Name
     */
    public String getName() { return name == null ? "" : name; }
    
    /**
     * Sets the name.
     * @param name Name to set
     */
    public void setName(String name) { this.name = name; }
    
    /**
     * Gets the phone number.
     * @return Phone number
     */
    public String getPhoneNumber() { return phoneNumber == null ? "" : phoneNumber; }
    
    /**
     * Sets the phone number.
     * @param phoneNumber Phone number to set
     */
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    /**
     * Gets the address.
     * @return Address
     */
    public String getAddress() { return address == null ? "" : address; }
    
    /**
     * Sets the address.
     * @param address Address to set
     */
    public void setAddress(String address) { this.address = address; }
    
    /**
     * Abstract method for displaying info.
     * Demonstrates polymorphism.
     * @return String representation
     */
    @Override
    public abstract String toString();
    
    /**
     * Gets details for file storage.
     * @return Formatted string
     */
    public String getDetailsForFile() {
        return name + "," + phoneNumber + "," + address;
    }
}
