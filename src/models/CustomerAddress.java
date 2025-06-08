package models;

public class CustomerAddress {
    private int addressId;
    private String address;
    private String city;

    public CustomerAddress() {

    }

    public CustomerAddress(int addressId, String address, String city) {
        this.addressId = addressId;
        this.address = address; //street
        this.city = city;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

   
    public String toString() {
        return "Customer address info Address#: " + addressId + ", " + address + ", " + city;
    }
}
