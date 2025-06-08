package models;

public class CarModel {
    private int modelId;
    private String brand;
    private String modelName;
    private int year;

    public CarModel() {

    }

    public CarModel(int modelId, String brand, String modelName, int year) {
        this.modelId = modelId;
        this.brand = brand;
        this.modelName = modelName;
        this.year = year;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

   
    public String toString() {
       
        return "CarModel info CarModel#: " + modelId +", " +  modelName  + brand + ", " + year;
    }
}