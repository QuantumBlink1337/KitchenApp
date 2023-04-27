package org.example;

import javax.xml.transform.Result;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Main {
    public static void main(String[] args) throws SQLException {
        System.out.println("Welcome to the Kitchen Management System! Establishing connection to database...");
        String jdbcUrl = "jdbc:mysql://localhost:3306/kitchen";
        Connection con = DriverManager.getConnection(jdbcUrl, "root", "010618");
        if (con != null) {
            System.out.println("Connection successful!");
        }
        else {
            System.out.println("Connection not successful. Shutting down!");
            System.exit(0);
        }
       generateShoppingListFromRecipe("Chicken Pesto Pasta", con);



//        String itemtypeQuery = "insert into ItemType values( 'F', '00000')";
//        String query = "insert into FoodItem values('00000', 'F', 'test_food', 100.0, 200.0, " +
//                "'2023-4-25 22:27:00', '2023-4-25 22:30:00', 0, 0, 100, 200, 300, 400)";
//        Statement st = con.createStatement();
//
//
//
//        st.executeUpdate(itemtypeQuery);
//        st.executeUpdate(query);
        con.close();
    }
    public static int queryHighestIDByType(char type, Connection con) throws SQLException {
        ArrayList<Integer> list = new ArrayList<>();
        Statement st = con.createStatement();
        ResultSet set = st.executeQuery("select I.ID_Value from ItemType as I where I.ID_Type = \""+type+"\"");
        if (!set.isBeforeFirst()) {
            return 0;
        }
        while (set.next()) {
            list.add(set.getInt("ID_Value"));
        }
        return Collections.max(list);
    }
    public static boolean isEmptySet(ResultSet set) throws SQLException {
        return !set.isBeforeFirst();
    }
    public static void generateShoppingListFromRecipe(String recipeName, Connection con) throws SQLException {
        // maybe move to function that checks if associated table is empty
        String currentDate = String.valueOf(LocalDate.now());
        int recipeID;
        Statement st = con.createStatement();
        String query = "select R.Recipe_ID from Recipe as R where R.Recipe_name = \"" + recipeName.toLowerCase() + "\"" ;
        ResultSet set = st.executeQuery(query);
        
        if (isEmptySet(set)) {
            System.out.println("Invalid recipe name");
            return;
        }
        set.next();
        recipeID = set.getInt("Recipe_ID");

        set = st.executeQuery("select L.List_ID from ShoppingList as L where L.ListDate = \"" + currentDate + "\"");
        if (isEmptySet(set)) {
            st.executeUpdate("insert into ShoppingList values(\""+currentDate+"\", 0, "+ queryHighestIDByType('l', con) + 1 +", 'L')");
        }


//        query = "select U.Food_ID from Used_ingredients as U where U.Recipe_ID == " + recipeID;
//        set = st.executeQuery(query);
//        ArrayList<FoodItem> used_ingredients = new ArrayList<>();
//        while (set.next()) {
//            ResultSet foodSet = st.executeQuery("select F.Food_mass, F.Food_cost, F.Food_name, ")
//        }



//        try (Statement statement = con.createStatement()) {
//            String recipeSearchQuery = "select COUNT(*) from ShoppingList as SL where SL.ListDate == " + currentDate;
//            ResultSet set = statement.executeQuery(recipeSearchQuery);
//        }
//        catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
    }

}