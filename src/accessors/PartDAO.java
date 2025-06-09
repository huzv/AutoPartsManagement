package accessors;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Part;
import models.Category;
import utils.DBChelper;

public class PartDAO {
    // Custom exceptions
    public static class PartNotFoundException extends Exception {
        public PartNotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicatePartException extends Exception {
        public DuplicatePartException(String message) {
            super(message);
        }
    }

    public static class InvalidPartException extends Exception {
        public InvalidPartException(String message) {
            super(message);
        }
    }
// query to get all info about all parts
    public List<Part> getAllParts() throws Exception,SQLException {
        List<Part> parts = new ArrayList<>();
        String sql = "SELECT * FROM parts";
        
        try (Connection conn = DBChelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Part part = new Part(
                    rs.getInt("part_id"),
                    rs.getString("pname"),
                    rs.getString("pdescription"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getInt("category_id")
                );
                parts.add(part);
            }
        }
        return parts;
    }
// query to search for part by its id
    public Part getPartById(int partId) throws Exception,SQLException, PartNotFoundException {
        String sql = "SELECT * FROM parts WHERE part_id = ?";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, partId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Part(
                        rs.getInt("part_id"),
                        rs.getString("pname"),
                        rs.getString("pdescription"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getInt("category_id")
                    );
                } else {
                    throw new PartNotFoundException("Part with ID " + partId + " not found");
                }
            }
        }
    }
// query to insert new part
    public boolean addPart(Part part) throws Exception, SQLException, DuplicatePartException, InvalidPartException {
        // Validate the partId
        if (part.getPartId() <= 0) {
            throw new InvalidPartException("Part ID must be provided and positive");
        }

        // Check if part ID exists
        String checkSql = "SELECT 1 FROM parts WHERE part_id = ?";
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, part.getPartId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    throw new DuplicatePartException("Part ID " + part.getPartId() + " already exists");
                }
            }
        }

        // Validation for the part data
        if (part.getName() == null || part.getName().trim().isEmpty()) {
            throw new InvalidPartException("Part name cannot be empty");
        }
        if (part.getPrice() <= 0) {
            throw new InvalidPartException("Price must be positive");
        }
        if (part.getQuantity() < 0) {
            throw new InvalidPartException("Quantity cannot be negative");
        }
        if (part.getCategoryId() <= 0) {
            throw new InvalidPartException("Category ID must be positive");
        }

        
        String sql = "INSERT INTO parts (part_id, pname, pdescription, price, quantity, category_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, part.getPartId());
            pstmt.setString(2, part.getName());
            pstmt.setString(3, part.getDescription());
            pstmt.setDouble(4, part.getPrice());
            pstmt.setInt(5, part.getQuantity());
            pstmt.setInt(6, part.getCategoryId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
// query to update part information
    public boolean updatePart(Part part) throws Exception, SQLException, PartNotFoundException, InvalidPartException {
        // check if the part exists
        getPartById(part.getPartId());

        // Validation for the part data
        if (part.getName() == null || part.getName().trim().isEmpty()) {
            throw new InvalidPartException("Part name cannot be empty");
        }
        if (part.getPrice() <= 0) {
            throw new InvalidPartException("Price must be positive");
        }
        if (part.getQuantity() < 0) {
            throw new InvalidPartException("Quantity cannot be negative");
        }

        String sql = "UPDATE parts SET pname = ?, pdescription = ?, price = ?, quantity = ?, category_id = ? WHERE part_id = ?";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, part.getName());
            pstmt.setString(2, part.getDescription());
            pstmt.setDouble(3, part.getPrice());
            pstmt.setInt(4, part.getQuantity());
            pstmt.setInt(5, part.getCategoryId());
            pstmt.setInt(6, part.getPartId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new PartNotFoundException("Part with ID " + part.getPartId() + " not found for update");
            }
            return true;
        }
    }
// query to delete part by its id
    public boolean deletePart(int partId) throws Exception,SQLException, PartNotFoundException {
        // First verify the part exists
        getPartById(partId);

        String sql = "DELETE FROM parts WHERE part_id = ?";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, partId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new PartNotFoundException("Part with ID " + partId + " not found for deletion");
            }
            return true;
        }
    }
// method to get the part with its category
    public Part getPartWithCategory(int partId) throws Exception,SQLException, PartNotFoundException {
        Part part = getPartById(partId);
        if (part != null) {
            CategoryDAO categoryDAO = new CategoryDAO();
            try {
                Category category = categoryDAO.getCategoryById(part.getCategoryId());
                part.setCategory(category);
            } catch (CategoryDAO.CategoryNotFoundException e) {
                // if the category doesn't exist set its category to null
                part.setCategory(null);
            }
        }
        return part;
    }
// query to update the part quantity
    public boolean updatePartQuantity(int partId, int quantityUpdated) throws Exception,SQLException, PartNotFoundException {
        // check if the part exists
        getPartById(partId);

        String sql = "UPDATE parts SET quantity = quantity + ? WHERE part_id = ?";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quantityUpdated);
            pstmt.setInt(2, partId);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new PartNotFoundException("Part with ID " + partId + " is not found to update its quantity");
            }
            return true;
        }
    }
}