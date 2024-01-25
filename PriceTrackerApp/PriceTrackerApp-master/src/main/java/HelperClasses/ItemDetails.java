package HelperClasses;

import java.util.Objects;

public class ItemDetails implements Comparable<ItemDetails> {
    String itemName;
    String itemUnit;
    int itemCode;
    int premiseCode;
    double price;

    public ItemDetails(String itemName, String itemUnit, int itemCode, int premiseCode, double price) {
        this.itemName = itemName;
        this.itemUnit = itemUnit;
        this.itemCode = itemCode;
        this.premiseCode = premiseCode;
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemUnit() {
        return itemUnit;
    }

    public void setItemUnit(String itemUnit) {
        this.itemUnit = itemUnit;
    }

    public int getItemCode() {
        return itemCode;
    }

    public void setItemCode(int itemCode) {
        this.itemCode = itemCode;
    }

    public int getPremiseCode() {
        return premiseCode;
    }

    public void setPremiseCode(int premiseCode) {
        this.premiseCode = premiseCode;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public int compareTo(ItemDetails other) {
        // Compare based on the price field
        return Double.compare(this.price, other.price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDetails that = (ItemDetails) o;
        return itemCode == that.itemCode &&
                premiseCode == that.premiseCode &&
                Double.compare(that.price, price) == 0 &&
                Objects.equals(itemName, that.itemName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemName, itemCode, premiseCode, price);
    }
}
