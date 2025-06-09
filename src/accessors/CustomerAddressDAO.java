package accessors;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.CustomerAddress;
import utils.DBChelper;

public class CustomerAddressDAO {

    // Custom exceptions
    public static class AddressNotFoundException extends Exception {
        public AddressNotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicateAddressIdException extends Exception {
        public DuplicateAddressIdException(String message) {
            super(message);
        }
    }
// query to get all the info about all cusotmer address
    public List<CustomerAddress> getAllAddresses() throws Exception {
        List<CustomerAddress> addresses = new ArrayList<>();
        String sql = "SELECT * FROM customers_addresses";

        try (Connection conn = DBChelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                CustomerAddress address = new CustomerAddress(
                    rs.getInt("address_id"),
                    rs.getString("address"),
                    rs.getString("city")
                );
                addresses.add(address);
            }
        }
        return addresses;
    }
// query to seacrh for the customer address by id
    public CustomerAddress getAddressById(int addressId) throws Exception, AddressNotFoundException {
        String sql = "SELECT * FROM customers_addresses WHERE address_id = ?";
        CustomerAddress address = null;

        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, addressId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    address = new CustomerAddress(
                        rs.getInt("address_id"),
                        rs.getString("address"),
                        rs.getString("city")
                    );
                } else {
                    throw new AddressNotFoundException("Address with the ID " + addressId + " doesnt exist");
                }
            }
        }
        return address;
    }
// to add customer addresss
    public boolean addAddress(CustomerAddress address) throws Exception, DuplicateAddressIdException {
        // Check if address_id is provided
        if (address.getAddressId() == 0) {
            throw new IllegalArgumentException("Address ID must be provided and cannot be 0");
        }

        // Check if duplicate address_id
        String checkSql = "SELECT 1 FROM customers_addresses WHERE address_id = ?";
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, address.getAddressId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    throw new DuplicateAddressIdException("Address ID " + address.getAddressId() + " already exists");
                }
            }
        }

        
        String sql = "INSERT INTO customers_addresses (address_id, address, city) VALUES (?, ?, ?)";

        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, address.getAddressId());
            pstmt.setString(2, address.getAddress());
            pstmt.setString(3, address.getCity());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
// query to update the customer address
    public boolean updateAddress(CustomerAddress address) throws Exception, AddressNotFoundException {
        getAddressById(address.getAddressId());

        String sql = "UPDATE customers_addresses SET address = ?, city = ? WHERE address_id = ?";

        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, address.getAddress());
            pstmt.setString(2, address.getCity());
            pstmt.setInt(3, address.getAddressId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new AddressNotFoundException("Address with ID " + address.getAddressId() + " not found for update");
            }
            return true;
        }
    }
// query to delete the customer address
    public boolean deleteAddress(int addressId) throws Exception, AddressNotFoundException {
        getAddressById(addressId);

        String sql = "DELETE FROM customers_addresses WHERE address_id = ?";

        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, addressId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new AddressNotFoundException("Address with ID " + addressId + " not found for deletion");
            }
            return true;
        }
    }
}