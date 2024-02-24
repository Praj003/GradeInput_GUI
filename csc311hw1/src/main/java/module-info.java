module com.example.csc311hw1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // added
    requires com.healthmarketscience.jackcess;
    requires com.google.gson;


    opens com.example.csc311hw1 to javafx.fxml, com.google.gson; //added
    exports com.example.csc311hw1;

}