package HelperClasses;

public class Item {
    private String itemCode;
    private String itemName;
    private String unit;
    private String itemGroup;
    private String itemCategory;

    public Item(String itemCode, String itemName, String unit, String itemGroup, String itemCategory) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.unit = unit;
        this.itemGroup = itemGroup;
        this.itemCategory = itemCategory;
    }

    // Getters and setters

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(String itemGroup) {
        this.itemGroup = itemGroup;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }
}
