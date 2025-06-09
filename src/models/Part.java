package models;

public class Part {
    private int partId;
    private String pname;
    private String pdescription;
    private double price;
    private int quantity;
    private int categoryId;
    private Category category;

    public Part() {}

    public Part(int partId, String pname, String pdescription, double price, int quantity, int categoryId) {
        this.partId = partId;
        this.pname = pname;
        this.pdescription = pdescription;
        this.price = price;
        this.quantity = quantity;
        this.categoryId = categoryId;
    }

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    public String getName() {
        return pname;
    }

    public void setName(String name) {
        this.pname = name;
    }

    public String getDescription() {
        return pdescription;
    }

    public void setDescription(String pdescription) {
        this.pdescription = pdescription;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

   
    public String toString() {
        
        return "Part info : Part#" + partId +", " +  pname +", " + pdescription + ", " + " (" + price + ")" + ", " + quantity + ", " + categoryId;
    }
}
