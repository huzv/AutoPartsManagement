package models;

import java.sql.Date;

public class Order {
    private int orderId;
    private int customerId;
    private Date orderDate;
    private String status;
    private String paymentMethod;
    private double totalAmount;
    private Customer customer;

    public Order() {

    }

    public Order(int orderId, int customerId, Date orderDate, String status, String paymentMethod, double totalAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    
    public String toString() {
        return "Order info :Order #" + orderId + ", Customer# " + customerId + " , " + "(" + orderDate + ")" + 
                ", " + status + ", " + paymentMethod + ", " + totalAmount;
    }
}