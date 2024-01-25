package HelperClasses;

public class Premise {
    private int premiseCode;
    private String premiseName;
    private String address;
    private String premiseType;
    private String state;
    private String district;

    public Premise(int premiseCode, String premiseName, String address, String premiseType, String state, String district) {
        this.premiseCode = premiseCode;
        this.premiseName = premiseName;
        this.address = address;
        this.premiseType = premiseType;
        this.state = state;
        this.district = district;
    }

    // Getters and setters

    public int getPremiseCode() {
        return premiseCode;
    }

    public void setPremiseCode(int premiseCode) {
        this.premiseCode = premiseCode;
    }

    public String getPremiseName() {
        return premiseName;
    }

    public void setPremiseName(String premiseName) {
        this.premiseName = premiseName;
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
}
