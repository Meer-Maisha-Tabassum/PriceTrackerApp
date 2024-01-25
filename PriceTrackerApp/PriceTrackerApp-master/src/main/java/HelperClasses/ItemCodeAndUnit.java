package HelperClasses;

public class ItemCodeAndUnit {
    private int itemCode;
    private String unit;

    public ItemCodeAndUnit(int itemCode, String unit) {
        this.itemCode = itemCode;
        this.unit = unit;
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
}
