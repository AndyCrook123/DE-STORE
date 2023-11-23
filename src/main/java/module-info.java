module com.andrewcrook.destore {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.dlsc.formsfx;
    requires javax.mail.api;

    opens com.andrewcrook.destore to javafx.fxml;
    opens com.andrewcrook.destore.presentationlayer to javafx.fxml;
    opens com.andrewcrook.destore.dataaccesslayer to javafx.fxml;
    opens com.andrewcrook.destore.businesslogiclayer to javafx.fxml;
    opens com.andrewcrook.destore.businesslogiclayer.util to javafx.fxml;

    exports com.andrewcrook.destore;
    exports com.andrewcrook.destore.presentationlayer;
    exports com.andrewcrook.destore.dataaccesslayer;
    exports com.andrewcrook.destore.businesslogiclayer;

}