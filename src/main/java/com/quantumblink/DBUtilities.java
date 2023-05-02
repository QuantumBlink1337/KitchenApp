package com.quantumblink;

import java.sql.*;

public class DBUtilities {
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
        int ID = Main.queryHighestIDByType('L', con);
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

    public static int searchForItemByName(String name, Connection con) throws SQLException {
        Statement statement = con.createStatement();
        String query = "call searchForItemByName(\"" + name + "\", @ID)";
        statement.executeUpdate(query);
        ResultSet set = statement.executeQuery("select @ID");
        set.next();
        return set.getInt("@ID");
    }
}
