module com.example.wix1002 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens FXMLControllerClasses to javafx.fxml;
    exports FXMLControllerClasses;
    exports HelperClasses;
    opens HelperClasses to javafx.fxml;
}