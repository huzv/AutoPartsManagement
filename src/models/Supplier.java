package models;

import java.sql.Date;

public class Supplier {
    private int supplierId;
    private String sname;
    private String semail;
    private String sphone;
    private int addressId;
    private Date createdDate;
    private boolean isActive;
    private SupplierAddress saddress;

    public Supplier() {

    }

    public Supplier(int supplierId, String sname, String semail, String sphone, 
                   int addressId, Date createdDate, boolean isActive) {
                    
        this.supplierId = supplierId;
        this.sname = sname;
        this.semail = semail;
        this.sphone = sphone;
        this.addressId = addressId;
        this.createdDate = createdDate;
        this.isActive = isActive;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSName() {
        return sname;
    }

    public void setSName(String sname) {
        this.sname = sname;
    }


    public String getSEmail() {
        return semail;
    }

    public void setSEmail(String semail) {
        this.semail = semail;
    }

    public String getSPhone() {
        return sphone;
    }

    public void setSPhone(String sphone) {
        this.sphone = sphone;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public SupplierAddress getSAddress() {
        return saddress;
    }

    public void setSAddress(SupplierAddress saddress) {
        this.saddress = saddress;
    }

   
    public String toString() {
        return "Supplier info :supplier #" + supplierId + ", " + sname + ", " + semail + ", " + sphone + 
        ", " + addressId + ", " + createdDate + ", " + isActive;
    }
}