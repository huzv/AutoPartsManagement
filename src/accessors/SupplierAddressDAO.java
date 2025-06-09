package accessors;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.SupplierAddress;
import utils.DBChelper;

public class SupplierAddressDAO {

    // Custom exceptions
    public static class AddressNotFoundException extends Exception {
        public AddressNotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicateAddressException extends Exception {
        public DuplicateAddressException(String message) {
            super(message);
        }
    }

    public static class InvalidAddressException extends Exception {
        public InvalidAddressException(String message) {
            super(message);
        }
    }
// query to get all info about all supliers addressess
    public List<SupplierAddress> getAllAddresses() throws SQLException, Exception { //maybe change the name
        List<SupplierAddress> addresses = new ArrayList<>();
        String sql = "SELECT * FROM supplier_addresses";

        try (Connection conn = DBChelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SupplierAddress address = new SupplierAddress(
                    rs.getInt("saddress_id"),
                   rs.getString("address"),
                    rs.getString("city")
                );
                addresses.add(address);
            }
        }
        return addresses;
    }
// query to search for the supplier address by its id
    public SupplierAddress getAddressById(int saddressId) throws SQLException, Exception, AddressNotFoundException {
        String sql = "SELECT * FROM supplier_addresses WHERE saddress_id = ?";

        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, saddressId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new SupplierAddress(
                        rs.getInt("saddress_id"),
                        rs.getString("address"),
                        rs.getString("city")
                    );
                } else {
                    throw new AddressNotFoundException("Address with ID " + saddressId + " is not found");
                }
            }
        }
    }
//query to insert new supplier address
    public boolean addAddress(SupplierAddress address) throws SQLException, Exception, DuplicateAddressException, InvalidAddressException {
        // Validation for the information
        if (address.getSAddressId() == 0) {
            throw new InvalidAddressException("Address ID must be provided and cannot be 0");
        }
        if (address.getAddress() == null || address.getAddress().trim().isEmpty()) {
            throw new InvalidAddressException("Address cannot be empty");
        }
        if (address.getCity() == null || address.getCity().trim().isEmpty()) {
            throw new InvalidAddressException("City cannot be empty");
        }

        // Check for the duplicate supplaier address_id
        String checkSql = "SELECT 1 FROM supplier_addresses WHERE saddress_id = ?";
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, address.getSAddressId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    throw new DuplicateAddressException("Supplier Address ID " + address.getSAddressId() + " already exists");
                }
            }
        }

        
        String sql = "INSERT INTO supplier_addresses (saddress_id, address, city) VALUES (?, ?, ?)";

        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, address.getSAddressId());
            pstmt.setString(2, address.getAddress());
            pstmt.setString(3, address.getCity());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
// query to update the supplier address // maybe change the name
    public boolean updateAddress(SupplierAddress address) throws SQLException, Exception, AddressNotFoundException, InvalidAddressException {
        // check if the supplier address exists
        getAddressById(address.getSAddressId());

        // Validation for the supplieraddrees info
        if (address.getAddress() == null || address.getAddress().trim().isEmpty()) {
            throw new InvalidAddressException("Address cannot be empty");
        }
        if (address.getCity() == null || address.getCity().trim().isEmpty()) {
            throw new InvalidAddressException("City cannot be empty");
        }

        String sql = "UPDATE supplier_addresses SET address = ?, city = ? WHERE saddress_id = ?";

        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, address.getAddress());
            pstmt.setString(2, address.getCity());
            pstmt.setInt(3, address.getSAddressId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new AddressNotFoundException("Address with ID " + address.getSAddressId() + " not found for update");
            }
            return true;
        }
    }
// query to delete the supplier address
    public boolean deleteAddress(int saddressId) throws SQLException, Exception, AddressNotFoundException {
        // check if the address exists
        getAddressById(saddressId);

        String sql = "DELETE FROM supplier_addresses WHERE saddress_id = ?";

        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, saddressId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new AddressNotFoundException("Address with ID " + saddressId + " not found for deletion");
            }
            return true;
        }
    }
}