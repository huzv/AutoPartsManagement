package models;

public class Category {
    private int categoryId;
    private String ctname;
    private String ctdescription;

    public Category() {

    }

    public Category(int categoryId, String ctname, String ctdescription) {
        this.categoryId = categoryId;
        this.ctname = ctname;
        this.ctdescription = ctdescription;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return ctname;
    }

    public void setName(String ctname) {
        this.ctname = ctname;
    }

    public String getDescription() {
        return ctdescription;
    }

    public void setDescription(String ctdescription) {
        this.ctdescription = ctdescription;
    }

    
    public String toString() {
        return "Category info Category#: " + categoryId +", " +  ctname  + ctdescription;
    }
}