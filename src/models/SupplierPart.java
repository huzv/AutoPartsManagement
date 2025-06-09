package models;

import java.sql.Date;

public class SupplierPart {

    private int supplierPartId;
    private int supplierId;
    private int partId;
    private String supplierStock;
    private double costPrice;
    private Date createdDate;
    private Supplier supplier;
    private Part part;

    public SupplierPart() {

    }

    public SupplierPart(int supplierPartId, int supplierId, int partId, String supplierStock, 
                       double costPrice, Date createdDate) {

        this.supplierPartId = supplierPartId;
        this.supplierId = supplierId;
        this.partId = partId;
        this.supplierStock = supplierStock;
        this.costPrice = costPrice;
        this.createdDate = createdDate;

    }

    public int getSupplierPartId() {
        return supplierPartId;
    }

    public void setSupplierPartId(int supplierPartId) {
        this.supplierPartId = supplierPartId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    public String getSupplierStock() {
        return supplierStock;
    }

    public void setSupplierSku(String supplierStock) {
        this.supplierStock = supplierStock;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }


    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    
    public String toString() {
        
        return "SupplierParts info :supplierpart #" + supplierPartId + ",supplier# " + supplierId + ",part# " + partId +
         ", " + supplierStock + ", " + costPrice + ", " + createdDate;
    }
}
