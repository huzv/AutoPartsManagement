package models;

public class Customer {
    private int customerId;
    private String cname;
    private String email;
    private String phone;
    private int addressId;
    private CustomerAddress caddress;

    public Customer() {

    }

    public Customer(int customerId, String cname, String email, String phone, int addressId) {
        this.customerId = customerId;
        this.cname = cname;
        this.email = email;
        this.phone = phone;
        this.addressId = addressId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return cname;
    }

    public void setName(String cname) {
        this.cname = cname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public CustomerAddress getCAddress() {
        return caddress;
    }

    public void setCAddress(CustomerAddress caddress) {
        this.caddress = caddress;
    }

  
    public String toString() {
        
        return "Customer info Custome#: " + customerId +", " +  cname + " (" + email + ")" + " (" + phone + ")" + ", " + caddress;
    }
}
