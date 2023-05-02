package org.example;

import javax.xml.transform.Result;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        int rowsReturned = 0;
        if (type == 'L') {
            rowsReturned = st.executeUpdate("call maxListID(@MAX)");
        }
        ResultSet set = st.executeQuery("select @MAX");
        set.next();
        return  rowsReturned != 0 ? set.getInt("@MAX") : -1;
    }
    public static boolean isEmptySet(ResultSet set) throws SQLException {
        return !set.isBeforeFirst();
    }
    public static int recipeExist(String recipeName, Connection con) throws SQLException {
        Statement st = con.createStatement();
        if ( st.executeUpdate("call searchForRecipeByName(\""+recipeName+"\", @ID)") == 0) {
            return -1;
        }
        ResultSet set = st.executeQuery("select @ID");
        set.next();
        return set.getInt("@ID");
    }
    public static int searchForShoppingListByDate(String currentDate, Connection con) throws SQLException {
        Statement st = con.createStatement();
        String query = "call searchForShoppingListByDate(\""+currentDate+"\", @ID)";
        if (st.executeUpdate(query) == 0) {
            return -1;
        }
        ResultSet set = st.executeQuery("select @ID");
        set.next();
        return set.getInt("@ID");
    }
    public static void createShoppingList(String currentDate, Connection con) throws SQLException {
        Statement st = con.createStatement();
        int ID = queryHighestIDByType('L', con);
        String query = "call createShoppingList("+(ID+1)+", \"" + currentDate + "\")";
        st.executeUpdate(query);
    }
    public static void createSoughtItem(int ListID, int ItemID, Connection con) throws SQLException {
        Statement st = con.createStatement();
        deleteSoughtItem(ItemID, con);
        String query = "call createListSoughtItem("+ ListID + ", " + ItemID + ")";
        try {
            st.executeUpdate(query);
        }
        catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        }
    }
    public static void deleteSoughtItem(int ItemID, Connection con) throws SQLException {
        Statement st = con.createStatement();
        String query = "call deleteSoughtItem("+ItemID+")";
        st.executeUpdate(query);
    }
    public static void displayShoppingList(int ListID, Connection con) throws SQLException {
        String query = "call displayShoppingList("+ListID+")";
        CallableStatement statement = con.prepareCall(query);
        ResultSet dateSet = statement.executeQuery();
        while (dateSet.next()) {
            System.out.println("Date: " + dateSet.getString("ListDate"));
        }
        statement.getMoreResults();
        ResultSet items = statement.getResultSet();
        while (items.next()) {
            System.out.println(items.getString("Item_name"));
        }
    }
    public static void generateShoppingListFromRecipe(String recipeName, Connection con) throws SQLException {
        // maybe move to function that checks if associated table is empty
        String currentDate = String.valueOf(LocalDate.now());
        int recipeID = recipeExist(recipeName, con);
        if (recipeID == -1) {
            System.out.println("Invalid recipe name.");
            return;
        }
        int shoppingListID = searchForShoppingListByDate(currentDate, con);
        if (shoppingListID == -1) {
            createShoppingList(currentDate, con);
        }
        Statement statement = con.createStatement();
        String query = "call retrieveIngredientsFromRecipe("+recipeID+")";
        ResultSet set = statement.executeQuery(query);
        if (isEmptySet(set)) {
            return;
        }
        while (set.next()) {
            createSoughtItem(shoppingListID, set.getInt("Food_ID"), con);
        }

    }

}