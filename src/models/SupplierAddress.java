package models;

public class SupplierAddress {
    private int saddressId;
    private String address;
    private String city;

    public SupplierAddress() {

    }

    public SupplierAddress(int saddressId, String address, String city) {
        this.saddressId = saddressId;
        this.address = address;
        this.city = city;
       
    }

    public int getSAddressId() {
        return saddressId;
    }

    public void setSAddressId(int saddressId) {
        this.saddressId = saddressId;
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
       
        return "Suplieraddress info :address #" + saddressId + ", " + address + ", " + city;
    }
}
