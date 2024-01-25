package HelperClasses;


public class CartItem {
    private String itemName;
    private int itemCode;
    private String unit;
    private int quantity;

    public CartItem(String itemName, int itemCode, String unit, int quantity) {
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.unit = unit;
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemCode() {
        return itemCode;
    }

    public void setItemCode(int itemCode) {
        this.itemCode = itemCode;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

