package HelperClasses;

// SharedDataModel.java
public class SharedDataModel {
    private static SharedDataModel instance;
    private String loggedInUsername;

    private SharedDataModel() {
        // private constructor to enforce singleton pattern
    }

    public static SharedDataModel getInstance() {
        if (instance == null) {
            instance = new SharedDataModel();
        }
        return instance;
    }

    public String getLoggedInUsername() {
        return loggedInUsername;
    }

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }
}

