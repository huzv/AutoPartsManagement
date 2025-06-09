package accessors;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Supplier;
import models.SupplierAddress;
import utils.DBChelper;

public class SupplierDAO {
    // Custom exceptions
    public static class SupplierNotFoundException extends Exception {
        public SupplierNotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicateSupplierException extends Exception {
        public DuplicateSupplierException(String message) {
            super(message);
        }
    }

    public static class InvalidSupplierException extends Exception {
        public InvalidSupplierException(String message) {
            super(message);
        }
    }
// query to get all the info about all suppliers
    public List<Supplier> getAllSuppliers() throws SQLException, Exception {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers";

        try (Connection conn = DBChelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Supplier supplier = new Supplier(
                    rs.getInt("supplier_id"),
                    rs.getString("sname"),
                    rs.getString("semail"),
                    rs.getString("sphone"),
                    rs.getInt("address_id"),
                    rs.getDate("created_date"),
                    rs.getBoolean("is_active")
                );
                suppliers.add(supplier);
            }
        }
        return suppliers;
    }
// query to search for suppliers by  thier id
    public Supplier getSupplierById(int supplierId) throws SQLException, Exception, SupplierNotFoundException {
        String sql = "SELECT * FROM suppliers WHERE supplier_id = ?";

        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("sname"),
                        rs.getString("semail"),
                        rs.getString("sphone"),
                        rs.getInt("address_id"),
                        rs.getDate("created_date"),
                        rs.getBoolean("is_active")
                    );
                } else {
                    throw new SupplierNotFoundException("Supplier with ID " + supplierId + " not found");
                }
            }
        }
    }
// query to insert new supplier 
    public boolean addSupplier(Supplier supplier) throws SQLException, Exception, DuplicateSupplierException, InvalidSupplierException {
        // Validation for supplier information
        if (supplier.getSupplierId() == 0) {
            throw new InvalidSupplierException("Supplier ID must be provided and cannot be 0");
        }
        if (supplier.getSName() == null || supplier.getSName().trim().isEmpty()) {
            throw new InvalidSupplierException("Supplier name can not be empty");
        }
        if (supplier.getSEmail() == null || supplier.getSEmail().trim().isEmpty()) {
            throw new InvalidSupplierException("Email can not be empty");
        }
        if (supplier.getSPhone() == null || supplier.getSPhone().trim().isEmpty()) {
            throw new InvalidSupplierException("Phone can not be empty");
        }
        if (supplier.getAddressId() <= 0) {
            throw new InvalidSupplierException("Address ID should be positive");
        }

        // check if the supplier address_id exists
        SupplierAddressDAO addressDAO = new SupplierAddressDAO();
        try {
            addressDAO.getAddressById(supplier.getAddressId());
        } catch (SupplierAddressDAO.AddressNotFoundException e) {
            throw new InvalidSupplierException("Address with ID " + supplier.getAddressId() + " does not exist");
        }

        // Check for duplicate supplier_id for the supplier 
        String checkSql = "SELECT 1 FROM suppliers WHERE supplier_id = ?";
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, supplier.getSupplierId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    throw new DuplicateSupplierException("Supplier ID " + supplier.getSupplierId() + " already exists");
                }
            }
        }

        
        String sql = "INSERT INTO suppliers (supplier_id, sname, semail, sphone, address_id, created_date, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplier.getSupplierId());
            pstmt.setString(2, supplier.getSName());
            pstmt.setString(3, supplier.getSEmail());
            pstmt.setString(4, supplier.getSPhone());
            pstmt.setInt(5, supplier.getAddressId());
            pstmt.setDate(6, supplier.getCreatedDate());
            pstmt.setBoolean(7, supplier.isActive());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
// query to update the supplier info    
    public boolean updateSupplier(Supplier supplier) throws SQLException, Exception, SupplierNotFoundException, InvalidSupplierException {
        // check if supplier exists by id
        getSupplierById(supplier.getSupplierId());

        // Validation for the supplier info
        if (supplier.getSName() == null || supplier.getSName().trim().isEmpty()) {
            throw new InvalidSupplierException("Supplier name cannot be empty");
        }
        if (supplier.getSEmail() == null || supplier.getSEmail().trim().isEmpty()) {
            throw new InvalidSupplierException("Email cannot be empty");
        }
        if (supplier.getSPhone() == null || supplier.getSPhone().trim().isEmpty()) {
            throw new InvalidSupplierException("Phone cannot be empty");
        }
        if (supplier.getAddressId() <= 0) {
            throw new InvalidSupplierException("Address ID must be positive");
        }

        // check if the supplier address_id exists
        SupplierAddressDAO addressDAO = new SupplierAddressDAO();
        try {
            addressDAO.getAddressById(supplier.getAddressId());
        } catch (SupplierAddressDAO.AddressNotFoundException e) {
            throw new InvalidSupplierException("Address with ID " + supplier.getAddressId() + " does not exist");
        }

        String sql = "UPDATE suppliers SET sname = ?, semail = ?, sphone = ?, address_id = ?, created_date = ?, is_active = ? WHERE supplier_id = ?";
// the query
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, supplier.getSName());
            pstmt.setString(2, supplier.getSEmail());
            pstmt.setString(3, supplier.getSPhone());
            pstmt.setInt(4, supplier.getAddressId());
            pstmt.setDate(5, supplier.getCreatedDate());
            pstmt.setBoolean(6, supplier.isActive());
            pstmt.setInt(7, supplier.getSupplierId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SupplierNotFoundException("Supplier with ID " + supplier.getSupplierId() + " not found for update");
            }
            return true;
        }
    }
//query to delete the supplier by id 
    public boolean deleteSupplier(int supplierId) throws SQLException, Exception, SupplierNotFoundException {
        //check if supplier exists by id
        getSupplierById(supplierId);

        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";

        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SupplierNotFoundException("Supplier with ID " + supplierId + " not found for deletion");
            }
            return true;
        }
    }
// method to get supplier with supplier address
    public Supplier getSupplierWithAddress(int supplierId) throws SQLException, Exception, SupplierNotFoundException {
        Supplier supplier = getSupplierById(supplierId);
        if (supplier != null) {
            SupplierAddressDAO addressDAO = new SupplierAddressDAO();
            try {
                SupplierAddress address = addressDAO.getAddressById(supplier.getAddressId());
                supplier.setSAddress(address);
            } catch (SupplierAddressDAO.AddressNotFoundException e) {
                supplier.setSAddress(null); //check if case supplier address doesn't exist and set its to null
            }
        }
        return supplier;
    }
}