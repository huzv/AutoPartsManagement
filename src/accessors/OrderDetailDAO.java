package accessors;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.OrderDetail;
import models.Part; 
import utils.DBChelper;

public class OrderDetailDAO {
    // Custom exceptions
    public static class OrderDetailNotFoundException extends Exception {
        public OrderDetailNotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicateOrderDetailException extends Exception {
        public DuplicateOrderDetailException(String message) {
            super(message);
        }
    }

    public static class InvalidOrderDetailException extends Exception {
        public InvalidOrderDetailException(String message) {
            super(message);
        }
    }
// query to get order details by the order id
    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) throws Exception,SQLException {
        List<OrderDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM orderdetails WHERE order_id = ?";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderDetail detail = new OrderDetail(
                        rs.getInt("order_detail_id"),
                        rs.getInt("order_id"),
                        rs.getInt("part_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price")
                    );
                    details.add(detail);
                }
            }
        }
        return details;
    }
// query to get order details by the order details id
    public OrderDetail getOrderDetailById(int orderDetailId) throws Exception,SQLException, OrderDetailNotFoundException {
        String sql = "SELECT * FROM orderdetails WHERE order_detail_id = ?";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderDetailId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new OrderDetail(
                        rs.getInt("order_detail_id"),
                        rs.getInt("order_id"),
                        rs.getInt("part_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price")
                    );
                } else {
                    throw new OrderDetailNotFoundException("Order detail with ID " + orderDetailId + " not found");
                }
            }
        }
    }
// query to add order detail
public boolean addOrderDetail(OrderDetail detail) throws Exception, SQLException, DuplicateOrderDetailException, InvalidOrderDetailException {

    // Validation orderDetailId
    if (detail.getOrderDetailId() <= 0) {
        throw new InvalidOrderDetailException("Order Detail ID must be provided and positive");
    }

    // Check if order detail ID exists
    String checkSql = "SELECT 1 FROM orderdetails WHERE order_detail_id = ?";
    try (Connection conn = DBChelper.getConnection();
         PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
        
        checkStmt.setInt(1, detail.getOrderDetailId());
        try (ResultSet rs = checkStmt.executeQuery()) {
            if (rs.next()) {
                throw new DuplicateOrderDetailException("Order detail ID " + detail.getOrderDetailId() + " already exists");
            }
        }
    }

    // Validatiaion for the  order detail data
    if (detail.getOrderId() <= 0) {
        throw new InvalidOrderDetailException("Order ID must be positive");
    }
    if (detail.getPartId() <= 0) {
        throw new InvalidOrderDetailException("Part ID must be positive");
    }
    if (detail.getQuantity() <= 0) {
        throw new InvalidOrderDetailException("Quantity must be positive");
    }
    if (detail.getUnitPrice() <= 0) {
        throw new InvalidOrderDetailException("Unit price must be positive");
    }

  
    String sql = "INSERT INTO orderdetails (order_detail_id, order_id, part_id, quantity, unit_price) VALUES (?, ?, ?, ?, ?)";
    
    try (Connection conn = DBChelper.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, detail.getOrderDetailId());
        pstmt.setInt(2, detail.getOrderId());
        pstmt.setInt(3, detail.getPartId());
        pstmt.setInt(4, detail.getQuantity());
        pstmt.setDouble(5, detail.getUnitPrice());

        int affectedRows = pstmt.executeUpdate();
        return affectedRows > 0;
    }
}
// query to update the order detail
    public boolean updateOrderDetail(OrderDetail detail) throws Exception,SQLException, OrderDetailNotFoundException, InvalidOrderDetailException {
        // check if the order detail exists
        getOrderDetailById(detail.getOrderDetailId());

        // Validation for order detail data
        if (detail.getQuantity() <= 0) {
            throw new InvalidOrderDetailException("Quantity must be positive");
        }
        if (detail.getUnitPrice() <= 0) {
            throw new InvalidOrderDetailException("Unit price must be positive");
        }

        String sql = "UPDATE orderdetails SET quantity = ?, unit_price = ? WHERE order_detail_id = ?";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, detail.getQuantity());
            pstmt.setDouble(2, detail.getUnitPrice());
            pstmt.setInt(3, detail.getOrderDetailId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new OrderDetailNotFoundException("Order detail with ID " + detail.getOrderDetailId() + " not found for update");
            }
            return true;
        }
    }

    public boolean deleteOrderDetail(int orderDetailId) throws Exception,SQLException, OrderDetailNotFoundException {
        //  check if the order detail exists
        getOrderDetailById(orderDetailId);

        String sql = "DELETE FROM orderdetails WHERE order_detail_id = ?";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderDetailId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new OrderDetailNotFoundException("Order detail with ID " + orderDetailId + " not found for deletion");
            }
            return true;
        }
    }

    // query to get order detail with the parts with it
    public OrderDetail getOrderDetailWithPart(int orderDetailId) throws Exception, SQLException, OrderDetailNotFoundException {
        OrderDetail detail = getOrderDetailById(orderDetailId);
        try {
            PartDAO partDAO = new PartDAO();
            Part part = partDAO.getPartById(detail.getPartId());
            detail.setPart(part);
        } catch (PartDAO.PartNotFoundException e) {
            //if part doesn't exist set to null
            detail.setPart(null);
        }
        return detail;
    }
}