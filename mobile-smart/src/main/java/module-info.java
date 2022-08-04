module com.test.mobilesmart {
    requires javafx.controls;
    requires javafx.fxml;
    requires gson;
    requires java.sql;
    requires org.json;


    opens com.test.mobilesmart.Client to javafx.fxml;
    exports com.test.mobilesmart.Client;
    exports com.test.mobilesmart.Server;
    opens com.test.mobilesmart.Server to javafx.fxml;
}