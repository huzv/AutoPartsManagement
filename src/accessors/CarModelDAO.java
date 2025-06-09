package accessors;

import utils.DBChelper;
import models.CarModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarModelDAO {

    // Custom exceptions
    public static class CarModelNotFoundException extends Exception {
        public CarModelNotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicateCarModelException extends Exception {
        public DuplicateCarModelException(String message) {
            super(message);
        }
    }

    public static class InvalidCarModelException extends Exception {
        public InvalidCarModelException(String message) {
            super(message);
        }
    }
// query to get all the data about all the car models
    public List<CarModel> getAllModels() throws Exception ,SQLException {
        List<CarModel> models = new ArrayList<>();
        String sql = "SELECT * FROM carmodels";
        
        try (Connection conn = DBChelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                CarModel model = new CarModel(
                    rs.getInt("model_id"),
                    rs.getString("brand"),
                    rs.getString("model_name"),
                    rs.getInt("cmyear")
                );
                models.add(model);
            }
        }
        return models;
    }
//query to search for car model by id
    public CarModel getModelById(int modelId) throws Exception ,SQLException, CarModelNotFoundException {
        String sql = "SELECT * FROM carmodels WHERE model_id = ?";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, modelId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new CarModel(
                        rs.getInt("model_id"),
                        rs.getString("brand"),
                        rs.getString("model_name"),
                        rs.getInt("cmyear")
                    );
                } else {
                    throw new CarModelNotFoundException("Car model with ID " + modelId + " is not found");
                }
            }
        }
    }

// query to add model
    public boolean addModel(CarModel model) throws SQLException, Exception, DuplicateCarModelException, InvalidCarModelException {
        // Validate modelId
        if (model.getModelId() == 0) {
            throw new InvalidCarModelException("Model ID must be provided and cannot be 0");
        }

        // Check if there is  duplicate model_id
        String checkSql = "SELECT 1 FROM carmodels WHERE model_id = ?";
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, model.getModelId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    throw new DuplicateCarModelException("Car model ID " + model.getModelId() + " already exists");
                }
            }
        }

        // Validation for model data
        if (model.getBrand() == null || model.getBrand().trim().isEmpty()) {
            throw new InvalidCarModelException("Brand cannot be empty");
        }
        if (model.getModelName() == null || model.getModelName().trim().isEmpty()) {
            throw new InvalidCarModelException("Model name cannot be empty");
        }
        if (model.getYear() < 1950 || model.getYear() > java.time.Year.now().getValue() + 1) {
            throw new InvalidCarModelException("Wrong year");
        }

        
        String sql = "INSERT INTO carmodels (model_id, brand, model_name, cmyear) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, model.getModelId());
            pstmt.setString(2, model.getBrand());
            pstmt.setString(3, model.getModelName());
            pstmt.setInt(4, model.getYear());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

// query to update carmodel
    public boolean updateModel(CarModel model) throws Exception ,SQLException, CarModelNotFoundException, InvalidCarModelException {
        //check if  the model exists
        getModelById(model.getModelId());

       
        if (model.getBrand() == null || model.getBrand().trim().isEmpty()) {
            throw new InvalidCarModelException("Brand cannot be empty");
        }
        if (model.getModelName() == null || model.getModelName().trim().isEmpty()) {
            throw new InvalidCarModelException("Model name cannot be empty");
        }
        if (model.getYear() < 1900 || model.getYear() > java.time.Year.now().getValue() + 1) {
            throw new InvalidCarModelException("Invalid year");
        }

        String sql = "UPDATE carmodels SET brand = ?, model_name = ?, cmyear = ? WHERE model_id = ?";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, model.getBrand());
            pstmt.setString(2, model.getModelName());
            pstmt.setInt(3, model.getYear());
            pstmt.setInt(4, model.getModelId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new CarModelNotFoundException("Car model with ID " + model.getModelId() + " not found for update");
            }
            return true;
        }
    }
// query to delete car model
    public boolean deleteModel(int modelId) throws Exception ,SQLException, CarModelNotFoundException {
        // check if  the model exists
        getModelById(modelId);

        String sql = "DELETE FROM carmodels WHERE model_id = ?";
        
        try (Connection conn = DBChelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, modelId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new CarModelNotFoundException("Car model with ID " + modelId + " not found for deletion");
            }
            return true;
        }
    }
}