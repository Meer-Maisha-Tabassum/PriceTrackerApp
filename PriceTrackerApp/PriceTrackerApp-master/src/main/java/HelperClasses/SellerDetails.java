package HelperClasses;

import java.util.Objects;

public class SellerDetails implements Comparable<SellerDetails> {
    String premise, address, premiseType, state, district;
    ItemDetails itemDetails;

    public SellerDetails(String premise, String address, String premiseType, String state, String district, ItemDetails itemDetails) {
        this.premise = premise;
        this.address = address;
        this.premiseType = premiseType;
        this.state = state;
        this.district = district;
        this.itemDetails = itemDetails;
    }

    public String getPremise() {
        return premise;
    }

    public void setPremise(String premise) {
        this.premise = premise;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPremiseType() {
        return premiseType;
    }

    public void setPremiseType(String premiseType) {
        this.premiseType = premiseType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public ItemDetails getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(ItemDetails itemDetails) {
        this.itemDetails = itemDetails;
    }

    public String display() {
        return ("Item Name: " + itemDetails.getItemName() + "\n") +
                ("Item Price: RM " + itemDetails.getPrice() + "\n") +
                ("Item Unit: " + itemDetails.getItemUnit() + "\n") +
                ("Item Code: : " + itemDetails.getItemCode() + "\n") +
                ("Premise: " + premise + "\n") +
                ("Premise Type: " + premiseType + "\n") +
                ("Premise Code: " + itemDetails.getPremiseCode() + "\n") +
                ("Address: " + address + "\n") +
                ("State: " + state + "\n") +
                ("District: " + district + "\n");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SellerDetails that = (SellerDetails) o;
        return Objects.equals(premise, that.premise) &&
                Objects.equals(address, that.address) &&
                Objects.equals(premiseType, that.premiseType) &&
                Objects.equals(state, that.state) &&
                Objects.equals(district, that.district) &&
                Objects.equals(itemDetails, that.itemDetails);
    }

    @Override
    public int compareTo(SellerDetails other) {
        // Compare based on the price field
        return Double.compare(this.itemDetails.getPrice(), other.itemDetails.getPrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(premise, address, premiseType, state, district, itemDetails);
    }
}