package com.example.csc311hw1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Main controller class
 */
public class HelloController {

    //Elements for gui
    @FXML
    private Button addData;

    @FXML
    private ListView <String> listview;

    @FXML
    private TextField nameField;

    @FXML
    private TextField categoryField;

    @FXML
    private TextField scoreField;

    static Connection conn; // used in a couple methods


//////////////////////////////////////////////////////////


    /**
     *  Exit method for file exit
     */
    public void exit() {
        System.exit(0);
    }
/////////////////////////////////////////////// simple exit for file/button ^

    /**
     *  initialization for DB checks it and then creates it
     */
    public void initialize() {

        String dbFilePath = ".//scores.accdb";
        String databaseURL = "jdbc:ucanaccess://" + dbFilePath;
        File dbFile = new File(dbFilePath);
        if (!dbFile.exists()) {
            try (Database db = DatabaseBuilder.create(Database.FileFormat.V2010, new File(dbFilePath))) {
                System.out.println("The database file has been created.");
            } catch (IOException ioe) {
                ioe.printStackTrace(System.err);
            }
        }
        try {
            databaseURL = "jdbc:ucanaccess://.//scores.accdb";
            conn = DriverManager.getConnection(databaseURL);
        } catch (SQLException ex) {
            Logger.getLogger(Grade.class.getName()).log(Level.SEVERE, null, ex);
        }
        // calling check method
        //if (!dbExists()) {createDBTable();}
    }
    /*
    //////////////////////////////////////////////// This checks if there is a DB
    public static boolean dbExists() {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "SCORES", null);
            return tables.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    */
     /* ////////////////////////////////////////// This creates a DB if not there
    public static void createDBTable() {
        String dbFilePath = ".//scores.accdb";
        String databaseURL = "jdbc:ucanaccess://" + dbFilePath;
        File dbFile = new File(dbFilePath);
        if (!dbFile.exists()) {
            try (Database db = DatabaseBuilder.create(Database.FileFormat.V2010, new File(dbFilePath))) {
                System.out.println("The database file has been created.");
            } catch (IOException ioe) {
                ioe.printStackTrace(System.err);}
        }
        try {
            Connection conn = DriverManager.getConnection(databaseURL);
            String sql;
            sql = "CREATE TABLE scores (Name nvarchar(255), Category nvarchar(255), Score int)";
            Statement createTableStatement = conn.createStatement();
            createTableStatement.execute(sql);
            conn.commit();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();}
    }   //////////////////////////////////// Only need it once to check and create (had trouble with DB)
    */

    /**
     * Supposed to be jsontoDB
     * takes data from scores.json to the DB
     */
    public void DBtoJSON() {
        deleteData();
        try {
            FileReader fr = new FileReader("scores.json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        FileReader fr = null;
        try {
            fr = new FileReader("scores.json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e); }
        Grade[] ea = gson.fromJson(fr, Grade[].class);
        for (int i = 0; i < ea.length; i++) {
            insertData(ea[i]);}
    }
/////////////////////////////////////////////////////////// Loads the DB ^

    /**
     * Deletes scores data otherwise repeats every time it runs
     * gets called when loading data (DBtoJSON) so old data doesn't appear
     */
    private void deleteData() {
        String sql = "DELETE FROM scores";
        PreparedStatement preparedStatement = null;
        try {
        preparedStatement = conn.prepareStatement(sql);
        int rowsDeleted = preparedStatement.executeUpdate();
        } catch (SQLException e) {
        throw new RuntimeException(e);}
    }
////////////////////////////////////////////////// clears extra repeating data ^


    /**
     *  Puts data into DB
     */
    public static void insertData(Grade ab) {
        String sql = "INSERT INTO scores (Name,Category,Score) VALUES (?, ?, ?)";
        PreparedStatement data = null;
        try {
            data = conn.prepareStatement(sql);

            data.setString(1, ab.getName());

            data.setString(2, ab.getCategory());

            data.setInt(3, ab.getScore());

            data.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
//////////////////////////////////////////////////// gets data ^


    /**
     *  Displays name,category n score data in the listview
     */
    public void DisplayDB() {
        try {
            String tableName = "scores";
            Statement st = conn.createStatement();
            ResultSet result = st.executeQuery("SELECT * FROM " + tableName);

            ObservableList<String> gradeItems = FXCollections.observableArrayList();

            while (result.next()) {
                String Name = result.getString("Name");
                String category = result.getString("Category");
                int score = result.getInt("Score");
                gradeItems.add(Name + " - " + category + " - " + score);
            }
            listview.setItems(gradeItems);
        } catch (SQLException except) {
            except.printStackTrace();
        }
    }
//////////////////////////////////////////////////////////   Displays the DB ^


    /**
     * Adds data that is entered into the textfield of the gui
     * makes sure none are empty and score is an int
     * @param event
     */
    public void addData (ActionEvent event){
        String name = nameField.getText().trim();
        String category = categoryField.getText().trim();
        String scoreStr = scoreField.getText().trim();

        if (name.isEmpty() || category.isEmpty() || scoreStr.isEmpty()) {
            showAlert("Not enough inputs.");
            return;
        }
        try {
            int score = Integer.parseInt(scoreStr);
            Grade newGrade = new Grade(name, category, score);
            insertData(newGrade);
            DisplayDB();
        } catch (NumberFormatException e) {
            showAlert("Invalid input");
        }
    }
    ////////////////////////////////////////////////////// Adding data  ^

    /**
     * Method for showing an error message
     * comes if textfield is empty or score is not an int
     * @param e
     */
    private void showAlert (String e){
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Warning");
        error.setHeaderText(null);
        error.setContentText(e);
        error.showAndWait();
    }
    ////////////////////////////////////////////////////  Error method ^

}






