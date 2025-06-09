package models;

public class OrderDetail {
    private int orderDetailId;
    private int orderId;
    private int partId;
    private int quantity;
    private double unitPrice;
    private Part part;

    public OrderDetail() {

    }

    public OrderDetail(int orderDetailId, int orderId, int partId, int quantity, double unitPrice) {
        this.orderDetailId = orderDetailId;
        this.orderId = orderId;
        this.partId = partId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public double getSubtotal() {
        return quantity * unitPrice;
    }

    
    public String toString() {
       
        return "OrderDetail info :Orderdetail #" + orderDetailId + ", Customer# " + orderId + ", Part# " + partId + ", Quantity "  
       + quantity + ", Unit Price " + unitPrice;
    }
}
