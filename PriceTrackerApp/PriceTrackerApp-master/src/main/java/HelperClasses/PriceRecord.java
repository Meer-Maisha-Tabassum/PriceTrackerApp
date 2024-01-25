package HelperClasses;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class PriceRecord implements Comparable<PriceRecord> {
    private String date;
    private int premiseCode;
    private int itemCode;
    private double price;

    public PriceRecord(String date, int premiseCode, int itemCode, double price) {
        this.date = date;
        this.premiseCode = premiseCode;
        this.itemCode = itemCode;
        this.price = price;
    }

    // Getters and setters

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPremiseCode() {
        return premiseCode;
    }

    public void setPremiseCode(int premiseCode) {
        this.premiseCode = premiseCode;
    }

    public int getItemCode() {
        return itemCode;
    }

    public void setItemCode(int itemCode) {
        this.itemCode = itemCode;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public int compareTo(PriceRecord other) {
        // Compare based on the price field
        return Double.compare(this.price, other.price);
    }

    // Comparator for sorting by date
    public static Comparator<PriceRecord> getDateComparator() {
        return Comparator.comparing(PriceRecord::getDate);
    }

    // Helper method to parse the date string into a Date object
    private Date parseDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(this.date);
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing date", e);
        }
    }
}

