package accessors;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Supplier;
import utils.DBChelper;
import models.Part;
import models.SupplierPart;

public class SupplierPartDAO {
    // Custom exceptions
    public static class SupplierPartNotFoundException extends Exception {
        public SupplierPartNotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicateSupplierPartException extends Exception {
        public DuplicateSupplierPartException(String message) {
            super(message);
        }
    }

    public static class InvalidSupplierPartException extends Exception {
        public InvalidSupplierPartException(String message) {
            super(message);
        }
    }
// query to get all info about supplier parts for a given supplier_id
// the parts a certain supplier have 
    public List<SupplierPart> getPartsBySupplierId(int supplierId) throws SQLException, Exception {
        List<SupplierPart> supplierParts = new ArrayList<>();
        String sql = "SELECT * FROM supplier_parts WHERE supplier_id = ?";

        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SupplierPart supplierPart = new SupplierPart(
                        rs.getInt("supplier_part_id"),
                        rs.getInt("supplier_id"),
                        rs.getInt("part_id"),
                        rs.getString("supplier_stock"),
                        rs.getDouble("cost_price"),
                        rs.getDate("created_date")
                    );
                    supplierParts.add(supplierPart);
                }
            }
        }
        return supplierParts;
    }
// query to get all info about supplier parts for a given part_id 
// differnt from the past one because it's for a given part_id not supplier_id
    public SupplierPart getSupplierPartById(int supplierPartId) throws SQLException, Exception, SupplierPartNotFoundException {
        String sql = "SELECT * FROM supplier_parts WHERE supplier_part_id = ?";

        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierPartId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new SupplierPart(
                        rs.getInt("supplier_part_id"),
                        rs.getInt("supplier_id"),
                        rs.getInt("part_id"),
                        rs.getString("supplier_stock"),
                        rs.getDouble("cost_price"),
                        rs.getDate("created_date")
                    );
                } else {
                    throw new SupplierPartNotFoundException("Supplier part with ID " + supplierPartId + " not found");
                }
            }
        }
    }
// query to insert a new supplier(part)
    public boolean addSupplierPart(SupplierPart supplierPart) throws SQLException, Exception, DuplicateSupplierPartException, InvalidSupplierPartException {
        // Validatino for the data 
        if (supplierPart.getSupplierPartId() == 0) {
            throw new InvalidSupplierPartException("Supplier part ID must be provided and cannot be 0");
        }
        if (supplierPart.getSupplierId() <= 0) {
            throw new InvalidSupplierPartException("Supplier ID must be positive");
        }
        if (supplierPart.getPartId() <= 0) {
            throw new InvalidSupplierPartException("Part ID must be positive");
        }
        if (supplierPart.getSupplierStock() == null || supplierPart.getSupplierStock().trim().isEmpty()) {
            throw new InvalidSupplierPartException("Supplier stock cannot be empty");
        }
        if (supplierPart.getCostPrice() <= 0) {
            throw new InvalidSupplierPartException("Cost price must be positive");
        }

        // check if the  supplier_id exists
        SupplierDAO supplierDAO = new SupplierDAO();
        try {
            supplierDAO.getSupplierById(supplierPart.getSupplierId());
        } catch (SupplierDAO.SupplierNotFoundException e) {
            throw new InvalidSupplierPartException("Supplier with ID " + supplierPart.getSupplierId() + " does not exist");
        }

        // check if part_id exists
        PartDAO partDAO = new PartDAO();
        try {
            partDAO.getPartById(supplierPart.getPartId());
        } catch (PartDAO.PartNotFoundException e) {
            throw new InvalidSupplierPartException("Part with ID " + supplierPart.getPartId() + " does not exist");
        }

        // Check  supplier_part_id exists so it can cause a duplication
        String checkSql = "SELECT 1 FROM supplier_parts WHERE supplier_part_id = ?";
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, supplierPart.getSupplierPartId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    throw new DuplicateSupplierPartException("Supplier part ID " + supplierPart.getSupplierPartId() + " already exists");
                }
            }
        }

        
        String sql = "INSERT INTO supplier_parts (supplier_part_id, supplier_id, part_id, supplier_stock, cost_price, created_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierPart.getSupplierPartId());
            pstmt.setInt(2, supplierPart.getSupplierId());
            pstmt.setInt(3, supplierPart.getPartId());
            pstmt.setString(4, supplierPart.getSupplierStock());
            pstmt.setDouble(5, supplierPart.getCostPrice());
            pstmt.setDate(6, supplierPart.getCreatedDate());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean updateSupplierPart(SupplierPart supplierPart) throws SQLException, Exception, SupplierPartNotFoundException, InvalidSupplierPartException {
        // check if the supplier part exists
        getSupplierPartById(supplierPart.getSupplierPartId());

        // Validation for the data
        if (supplierPart.getSupplierId() <= 0) {
            throw new InvalidSupplierPartException("Supplier ID must be positive");
        }
        if (supplierPart.getPartId() <= 0) {
            throw new InvalidSupplierPartException("Part ID must be positive");
        }
        if (supplierPart.getSupplierStock() == null || supplierPart.getSupplierStock().trim().isEmpty()) {
            throw new InvalidSupplierPartException("Supplier stock cannot be empty");
        }
        if (supplierPart.getCostPrice() <= 0) {
            throw new InvalidSupplierPartException("Cost price must be positive");
        }

        // check if the  supplier_id exists
        SupplierDAO supplierDAO = new SupplierDAO();
        try {
            supplierDAO.getSupplierById(supplierPart.getSupplierId());
        } catch (SupplierDAO.SupplierNotFoundException e) {
            throw new InvalidSupplierPartException("Supplier with ID " + supplierPart.getSupplierId() + " does not exist");
        }

        // check if part_id exists
        PartDAO partDAO = new PartDAO();
        try {
            partDAO.getPartById(supplierPart.getPartId());
        } catch (PartDAO.PartNotFoundException e) {
            throw new InvalidSupplierPartException("Part with ID " + supplierPart.getPartId() + " does not exist");
        }

        String sql = "UPDATE supplier_parts SET supplier_id = ?, part_id = ?, supplier_stock = ?, cost_price = ?, created_date = ? WHERE supplier_part_id = ?";

        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierPart.getSupplierId());
            pstmt.setInt(2, supplierPart.getPartId());
            pstmt.setString(3, supplierPart.getSupplierStock());
            pstmt.setDouble(4, supplierPart.getCostPrice());
            pstmt.setDate(5, supplierPart.getCreatedDate());
            pstmt.setInt(6, supplierPart.getSupplierPartId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SupplierPartNotFoundException("Supplier part with ID " + supplierPart.getSupplierPartId() + " not found for update");
            }
            return true;
        }
    }
// query to delete the supplier(part) by supplierpart_id
    public boolean deleteSupplierPart(int supplierPartId) throws SQLException, Exception, SupplierPartNotFoundException {
        // check if the supplier part exists
        getSupplierPartById(supplierPartId);

        String sql = "DELETE FROM supplier_parts WHERE supplier_part_id = ?";

        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierPartId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SupplierPartNotFoundException("Supplier part with ID " + supplierPartId + " not found for deletion");
            }
            return true;
        }
    }
// method to get the supplier(part) with details(supplier and part)
    public SupplierPart getSupplierPartWithDetails(int supplierPartId) throws SQLException, Exception, SupplierPartNotFoundException {
        SupplierPart supplierPart = getSupplierPartById(supplierPartId);
        if (supplierPart != null) {
            SupplierDAO supplierDAO = new SupplierDAO();
            try {
                Supplier supplier = supplierDAO.getSupplierById(supplierPart.getSupplierId());
                supplierPart.setSupplier(supplier);
            } catch (SupplierDAO.SupplierNotFoundException e) {
                supplierPart.setSupplier(null); // check if the supplier doesn't exist
            }

            PartDAO partDAO = new PartDAO();
            try {
                Part part = partDAO.getPartById(supplierPart.getPartId());
                supplierPart.setPart(part);
            } catch (PartDAO.PartNotFoundException e) {
                supplierPart.setPart(null); // check if the  part doesn't exist
            }
        }
        return supplierPart;
    }
}