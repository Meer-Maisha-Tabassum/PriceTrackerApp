package HelperClasses;

import HelperClasses.Item;
import HelperClasses.Premise;
import HelperClasses.PriceRecord;

// Class to represent the relationship between Item, Premise, and PriceRecord
public class DataRelationship {
    private Item item;
    private Premise premise;
    private PriceRecord priceRecord;

    public DataRelationship(Item item, Premise premise, PriceRecord priceRecord) {
        this.item = item;
        this.premise = premise;
        this.priceRecord = priceRecord;
    }

    // Getters and setters

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Premise getPremise() {
        return premise;
    }

    public void setPremise(Premise premise) {
        this.premise = premise;
    }

    public PriceRecord getPriceRecord() {
        return priceRecord;
    }

    public void setPriceRecord(PriceRecord priceRecord) {
        this.priceRecord = priceRecord;
    }
}

