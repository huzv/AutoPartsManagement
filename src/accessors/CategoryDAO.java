package accessors;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Category;
import utils.DBChelper;

public class CategoryDAO {

    // Custom exceptions
    public static class CategoryNotFoundException extends Exception {
        public CategoryNotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicateCategoryException extends Exception {
        public DuplicateCategoryException(String message) {
            super(message);
        }
    }

    public static class InvalidCategoryException extends Exception {
        public InvalidCategoryException(String message) {
            super(message);
        }
    }

    public List<Category> getAllCategories() throws Exception,SQLException { // to get all info about all categories
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";
        
        try (Connection conn = DBChelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Category category = new Category(
                    rs.getInt("category_id"),
                    rs.getString("ctname"),
                    rs.getString("ctdescription")
                );
                categories.add(category);
            }
        }
        return categories;
    }

    public Category getCategoryById(int categoryId) throws Exception,SQLException, CategoryNotFoundException { //search
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Category(
                        rs.getInt("category_id"),
                        rs.getString("ctname"),
                        rs.getString("ctdescription")
                    );
                } else {
                    throw new CategoryNotFoundException("Category with ID " + categoryId + " not found");
                }
            }
        }
    }

// to add cateogory
    public boolean addCategory(Category category) throws SQLException, Exception, DuplicateCategoryException, InvalidCategoryException {
        // Validate categoryId
        if (category.getCategoryId() == 0) {
            throw new InvalidCategoryException("Category ID must be provided and cannot be 0");
        }

        // Check if duplicate category_id
        String checkSql = "SELECT 1 FROM categories WHERE category_id = ?";
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, category.getCategoryId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    throw new DuplicateCategoryException("Category ID " + category.getCategoryId() + " already exists");
                }
            }
        }

        // Validation for category data
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new InvalidCategoryException("Category name cannot be empty");
        }
        if (category.getDescription() == null) {
            throw new InvalidCategoryException("Category description cannot be null");
        }

        
        String sql = "INSERT INTO categories (category_id, ctname, ctdescription) VALUES (?, ?, ?)";

        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, category.getCategoryId());
            pstmt.setString(2, category.getName());
            pstmt.setString(3, category.getDescription());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

// query to update cat  
    public boolean updateCategory(Category category) throws Exception,SQLException, CategoryNotFoundException, InvalidCategoryException {
        // check if the category exists
        getCategoryById(category.getCategoryId());

        // Validataiton category data
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new InvalidCategoryException("Category name cannot be empty");
        }
        if (category.getDescription() == null) {
            throw new InvalidCategoryException("Category description cannot be null");
        }

        String sql = "UPDATE categories SET ctname = ?, ctdescription = ? WHERE category_id = ?";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getDescription());
            pstmt.setInt(3, category.getCategoryId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new CategoryNotFoundException("Category with ID " + category.getCategoryId() + " is not found to update");
            }
            return true;
        }
    }
// query to delete cat
    public boolean deleteCategory(int categoryId) throws Exception,SQLException, CategoryNotFoundException {
        // check if  the category exists
        getCategoryById(categoryId);

        String sql = "DELETE FROM categories WHERE category_id = ?";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new CategoryNotFoundException("Category with ID " + categoryId + " not found for deletion");
            }
            return true;
        }
    }
}