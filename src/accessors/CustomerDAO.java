package accessors;

import models.Customer;
import models.CustomerAddress;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.DBChelper;

public class CustomerDAO {

    // Custom exceptions
    public static class CustomerNotFoundException extends Exception {
        public CustomerNotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicateCustomerException extends Exception {
        public DuplicateCustomerException(String message) {
            super(message);
        }
    }

    public static class InvalidAddressException extends Exception {
        public InvalidAddressException(String message) {
            super(message);
        }
    }
    public static class InvalidCustomerException extends Exception {
        public InvalidCustomerException(String message) {
            super(message);
        }
    }
// query to get all info about all customers
    public List<Customer> getAllCustomers() throws Exception ,SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        
        try (Connection conn = DBChelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Customer customer = new Customer(
                    rs.getInt("customer_id"),
                    rs.getString("cname"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getInt("address_id")
                );
                customers.add(customer);
            }
        }
        return customers;
    }
// query to search fro customer by id 
    public Customer getCustomerById(int customerId) throws Exception ,SQLException, CustomerNotFoundException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("cname"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getInt("address_id")
                    );
                } else {
                    throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
                }
            }
        }
    }
// querey to add new customer
    public boolean addCustomer(Customer customer) throws SQLException, Exception, DuplicateCustomerException, InvalidCustomerException, InvalidAddressException {
        // Validate customerId
        if (customer.getCustomerId() == 0) {
            throw new InvalidCustomerException("Customer ID must be provided and cannot be 0");
        }

        // Check for duplicate customer_id
        String checkSql = "SELECT 1 FROM customers WHERE customer_id = ?";
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, customer.getCustomerId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    throw new DuplicateCustomerException("Customer ID " + customer.getCustomerId() + " already exists");
                }
            }
        }

        // Check if the address exists
        CustomerAddressDAO addressDAO = new CustomerAddressDAO();
        try {
            addressDAO.getAddressById(customer.getAddressId());
        } catch (CustomerAddressDAO.AddressNotFoundException e) {
            throw new InvalidAddressException("Address ID " + customer.getAddressId() + " doesn't exist");
        }

      
        String sql = "INSERT INTO customers (customer_id, cname, email, phone, address_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customer.getCustomerId());
            pstmt.setString(2, customer.getName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getPhone());
            pstmt.setInt(5, customer.getAddressId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean updateCustomer(Customer customer) throws Exception ,SQLException, CustomerNotFoundException, InvalidAddressException {
        // check if  the customer exists
        getCustomerById(customer.getCustomerId());

        // check if the new customeraddress exists
        CustomerAddressDAO addressDAO = new CustomerAddressDAO();
        try {
            addressDAO.getAddressById(customer.getAddressId());
        } catch (CustomerAddressDAO.AddressNotFoundException e) {
            throw new InvalidAddressException("Address ID " + customer.getAddressId() + " doesn't exist");
        }

        String sql = "UPDATE customers SET cname = ?, email = ?, phone = ?, address_id = ? WHERE customer_id = ?";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhone());
            pstmt.setInt(4, customer.getAddressId());
            pstmt.setInt(5, customer.getCustomerId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new CustomerNotFoundException("Customer with ID " + customer.getCustomerId() + " not found for update");
            }
            return true;
        }
    }
// query to delete customer
    public boolean deleteCustomer(int customerId) throws Exception ,SQLException, CustomerNotFoundException {
        // check if the customer exists
        getCustomerById(customerId);

        String sql = "DELETE FROM customers WHERE customer_id = ?";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new CustomerNotFoundException("Customer with ID " + customerId + " not found for deletion");
            }
            return true;
        }
    }
// method to get the address of the customer
    public Customer getCustomerWithAddress(int customerId) throws Exception , SQLException, CustomerNotFoundException, CustomerAddressDAO.AddressNotFoundException {
        Customer customer = getCustomerById(customerId);
        CustomerAddressDAO addressDAO = new CustomerAddressDAO();
        CustomerAddress address = addressDAO.getAddressById(customer.getAddressId());
        customer.setCAddress(address);
        return customer;
    }
}