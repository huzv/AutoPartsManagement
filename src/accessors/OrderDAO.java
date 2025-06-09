package accessors;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Customer;
import models.Order;
import models.OrderDetail;
import models.Part;
import utils.DBChelper;

public class OrderDAO {
    // Custom exceptions
    public static class OrderNotFoundException extends Exception {
        public OrderNotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicateOrderException extends Exception {
        public DuplicateOrderException(String message) {
            super(message);
        }
    }

    public static class InvalidOrderException extends Exception {
        public InvalidOrderException(String message) {
            super(message);
        }
    }
// query to get all the info for all orders
    public List<Order> getAllOrders() throws Exception,SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        
        try (Connection conn = DBChelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Order order = new Order(
                    rs.getInt("order_id"),
                    rs.getInt("customer_id"),
                    rs.getDate("order_date"),
                    rs.getString("ostatus"),
                    rs.getString("payment_method"),
                    rs.getDouble("total_amount")
                );
                orders.add(order);
            }
        }
        return orders;
    }
// query to seacrh for order by id
    public Order getOrderById(int orderId) throws Exception,SQLException, OrderNotFoundException {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Order(
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getDate("order_date"),
                        rs.getString("ostatus"),
                        rs.getString("payment_method"),
                        rs.getDouble("total_amount")
                    );
                } else {
                    throw new OrderNotFoundException("Order with ID " + orderId + " not found");
                }
            }
        }
    }
// query to add order(insert)
public boolean addOrder(Order order) throws SQLException, Exception, DuplicateOrderException, InvalidOrderException {
    // Validate orderId
    if (order.getOrderId() == 0) {
        throw new InvalidOrderException("Order ID must be provided and cannot be 0");
    }

    // Check for duplicate order_id
    String checkSql = "SELECT 1 FROM orders WHERE order_id = ?";
    try (Connection conn = DBChelper.getConnection();
         PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
        
        checkStmt.setInt(1, order.getOrderId());
        try (ResultSet rs = checkStmt.executeQuery()) {
            if (rs.next()) {
                throw new DuplicateOrderException("Order ID " + order.getOrderId() + " already exists");
            }
        }
    }

    // Validate order data
    if (order.getCustomerId() <= 0) {
        throw new InvalidOrderException("Customer ID must be positive");
    }
    if (order.getOrderDate() == null) {
        throw new InvalidOrderException("Order date cannot be null");
    }
    if (order.getStatus() == null || order.getStatus().trim().isEmpty()) {
        throw new InvalidOrderException("Order status cannot be empty");
    }
    if (order.getPaymentMethod() == null || order.getPaymentMethod().trim().isEmpty()) {
        throw new InvalidOrderException("Payment method cannot be empty");
    }
    if (order.getTotalAmount() <= 0) {
        throw new InvalidOrderException("Total amount must be positive");
    }

    
    String sql = "INSERT INTO orders (order_id, customer_id, order_date, ostatus, payment_method, total_amount) VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = DBChelper.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, order.getOrderId());
        pstmt.setInt(2, order.getCustomerId());
        pstmt.setDate(3, order.getOrderDate());
        pstmt.setString(4, order.getStatus());
        pstmt.setString(5, order.getPaymentMethod());
        pstmt.setDouble(6, order.getTotalAmount());

        int affectedRows = pstmt.executeUpdate();
        return affectedRows > 0;
    }
}


    public boolean updateOrder(Order order) throws Exception, SQLException, OrderNotFoundException, InvalidOrderException {
        // check if the order exists
        getOrderById(order.getOrderId());

        // Validation for order data
        if (order.getCustomerId() <= 0) {
            throw new InvalidOrderException("Customer ID must be positive");
        }
        if (order.getOrderDate() == null) {
            throw new InvalidOrderException("Order date cannot be null");
        }
        if (order.getStatus() == null || order.getStatus().trim().isEmpty()) {
            throw new InvalidOrderException("Order status cannot be empty");
        }
        if (order.getPaymentMethod() == null || order.getPaymentMethod().trim().isEmpty()) {
            throw new InvalidOrderException("Payment method cannot be empty");
        }
        if (order.getTotalAmount() <= 0) {
            throw new InvalidOrderException("Total amount must be positive");
        }

        String sql = "UPDATE orders SET customer_id = ?, order_date = ?, ostatus = ?, payment_method = ?, total_amount = ? WHERE order_id = ?";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, order.getCustomerId());
            pstmt.setDate(2, order.getOrderDate());
            pstmt.setString(3, order.getStatus());
            pstmt.setString(4, order.getPaymentMethod());
            pstmt.setDouble(5, order.getTotalAmount());
            pstmt.setInt(6, order.getOrderId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new OrderNotFoundException("Order with ID " + order.getOrderId() + " not found for update");
            }
            return true;
        }
    }
// query to delete order
    public boolean deleteOrder(int orderId) throws Exception,SQLException, OrderNotFoundException {
        // check if  the order exists
        getOrderById(orderId);

        String sql = "DELETE FROM orders WHERE order_id = ?";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new OrderNotFoundException("Order with ID " + orderId + " not found for deletion");
            }
            return true;
        }
    }

    public Order getOrderWithDetails(int orderId) throws Exception, SQLException, OrderNotFoundException {
        // get the order
        Order order = getOrderById(orderId);
    
        try {
            // get the customer details
            CustomerDAO customerDAO = new CustomerDAO();
            Customer customer = customerDAO.getCustomerById(order.getCustomerId());
            order.setCustomer(customer);
        } catch (CustomerDAO.CustomerNotFoundException e) {
            // If customer doesnt exist set to null // maybe changed later
            order.setCustomer(null);
        }
    
        try {
            // get the order details
            OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
            List<OrderDetail> orderDetails = orderDetailDAO.getOrderDetailsByOrderId(orderId);
            
            // get part details for each order detail
            for (OrderDetail detail : orderDetails) {
                try {
                    PartDAO partDAO = new PartDAO();
                    Part part = partDAO.getPartById(detail.getPartId());
                    detail.setPart(part);
                } catch (PartDAO.PartNotFoundException e) {
                    // If part doesnt exist set to null // maybe change later
                    detail.setPart(null);
                }
            }
            // Set the order details in the order object
            order.setOrderDetails(orderDetails);
            
        } catch (OrderDetailDAO.OrderDetailNotFoundException e) {
            // If  order details are not found put an empty list //maybe change later
            order.setOrderDetails(new ArrayList<>());
        }
    
        return order;
    }
}