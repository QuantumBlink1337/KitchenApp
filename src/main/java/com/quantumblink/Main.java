package com.quantumblink;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

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
        ArrayList<FoodQuantity> spoiledFoodsNames;
        if ((spoiledFoodsNames = querySpoiledFoods(con)) != null) {
            System.out.println("Spoiled foods were detected!");
            for (FoodQuantity item : spoiledFoodsNames) {
                System.out.printf("Food name: %s | Days over expiration date: %d", item.getItem_name(), dateDayDifference(item.getFBestBy_Date(), String.valueOf(LocalDate.now())));
            }
            System.out.println("You can add them to a shopping list later.");
        }
        Scanner sc = new Scanner(System.in);
        int input = -1;
        while (!validInputs(input)) {
            System.out.println("(0): Exit Program");
            System.out.println("(1): Generate Shopping List From Recipe");
            System.out.println("(2): Add Item to Shopping List");
            System.out.println("(3): Add Items from Shopping List to Kitchen");
            System.out.println("(4): Add Spoiled Items to Shopping List");
            try {
                input = sc.nextInt();
                sc.nextLine();
            }
            catch (InputMismatchException e) {
                // this does not work
                System.out.println("Sorry, please try again.");
                input = -1;
            }
            if (input == 0) {
                System.out.println("Thanks for using Kitchen Management System!");
                System.exit(0);
            }
            if (input == 1) {
                System.out.println("What is the name of the recipe?");
                String recipeName = sc.nextLine();
                generateShoppingListFromRecipe(recipeName, con);
                input = -1;
            }
            else if (input == 2) {
                System.out.println("What is the name of the item?");
                String itemName = sc.nextLine();
                addItemToShoppingList(itemName, con, false);
                input = -1;
            }
            else if (input == 3) {
                addItemsToKitchen(LocalDate.now(), con);
                input = -1;
            }
            else if (input == 4 && spoiledFoodsNames != null) {
                for (FoodQuantity item : spoiledFoodsNames) {
                    addItemToShoppingList(item.getItem_name(), con, true);
                }
                input = -1;
            }
        }




        con.close();
    }
    public static boolean validInputs(int input) {
        int[] validInputs = new int[]{0, 1, 2, 3, 4};
        for (int x : validInputs) {
            if (x == input) {
                return true;
            }
        }
        return false;
    }
    public static int queryHighestIDByType(char type, Connection con) throws SQLException {
        Statement st = con.createStatement();
        int rowsReturned = 0;
        if (type == 'L') {
            rowsReturned = st.executeUpdate("call maxListID(@MAX)");
        }
        else if (type == 'Q') {
            rowsReturned = st.executeUpdate("call maxQuantityID(@MAX)");
        }
        else if (type == 'F') {
            rowsReturned = st.executeUpdate("call maxFoodTypeID(@MAX)");
        }

        ResultSet set = st.executeQuery("select @MAX");
        set.next();
        return  rowsReturned != 0 ? set.getInt("@MAX") : -1;
    }
    public static boolean isEmptySet(ResultSet set) throws SQLException {
        return !set.isBeforeFirst();
    }
    public static int dateDayDifference(String date_a, String date_b) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date A = sdf.parse(date_a);
            Date B = sdf.parse(date_b);

            long diffInMS = Math.abs(B.getTime() - A.getTime());
            return (int) TimeUnit.DAYS.convert(diffInMS, TimeUnit.MILLISECONDS);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


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
        displayShoppingList(shoppingListID, con);

    }
    public static int searchForItemByName(String name, Connection con, boolean createIfNotFound) throws SQLException {
        Statement statement = con.createStatement();
        String query = "call searchForItemByName(\"" + name + "\", @ID)";
        statement.executeUpdate(query);
        ResultSet set = statement.executeQuery("select @ID");
        set.next();
        int ID = set.getInt("ID");
        if (!set.wasNull()) {
            return ID;
        }
        else if (createIfNotFound) {
            createItemType(name, queryHighestIDByType('F', con), con);
        }
        return -1;
    }
    public static void addItemToShoppingList(String itemName, Connection con, boolean omitDisplay) throws SQLException {
        String currentDate = String.valueOf(LocalDate.now());
        int shoppingListID = searchForShoppingListByDate(currentDate, con);
        if (shoppingListID == -1) {
            createShoppingList(currentDate, con);
        }
        int itemID = searchForItemByName(itemName, con, false);
        createSoughtItem(shoppingListID, itemID, con);
        if (!omitDisplay) {
            displayShoppingList(shoppingListID, con);
        }
    }
    public static FoodType userDefineFoodType(Connection con) throws SQLException {
        String foodName;
        Scanner sc = new Scanner(System.in);
        String[] types = {"calories", "fat", "protein", "carbs"};
        int[] values = {0, 0, 0, 0};
        System.out.println("What is the name of the new food description?");
        foodName = sc.nextLine();
        for (int i = 0; i < types.length; i++) {
            while (values[i] == 0) {
                try {
                    System.out.println("How many " + types[i] + " are in the food?");
                    values[i] = sc.nextInt();
                }
                catch (InputMismatchException e) {
                    System.out.println("Sorry, that wasn't a number. Try again.");
                }
            }
        }
        int Food_ID = queryHighestIDByType('F', con) + 1;
        return new FoodType(Food_ID, foodName, values[0], values[1], values[2], values[3]);
    }
    public static void createFoodType(FoodType foodType, Connection con) throws SQLException {
        Statement st = con.createStatement();
        String query = "call createFoodType("+ foodType.getFood_ID() + ", \"" + foodType.getFood_name() + "\", " + foodType.getCalories() + ", " + foodType.getFat() + ", " + foodType.getProtein() + ", " + foodType.getCarbs() + ")";
        st.executeUpdate(query);
    }
    public static void createItemType(String name, int ID, Connection con) throws SQLException {
        Statement st = con.createStatement();
        String query = "call createItemType("+ID+", \""+name+"\", null, null, null)";
        st.executeUpdate(query);
    }
    public static ArrayList<FoodQuantity> querySpoiledFoods(Connection con) throws SQLException {
        Statement st = con.createStatement();
        String query = "call retrieveSpoiledFoods(\""+LocalDate.now()+"\")";
        ResultSet spoiledFoodsSet = st.executeQuery(query);
        if (isEmptySet(spoiledFoodsSet)) {
            return null;
        }
        ArrayList<FoodQuantity> spoiledFood = new ArrayList<>();
        while (spoiledFoodsSet.next()) {
            spoiledFood.add(new FoodQuantity(spoiledFoodsSet.getInt("Quantity_ID"), spoiledFoodsSet.getInt("Food_ID"), spoiledFoodsSet.getString("Item_name"),
                    spoiledFoodsSet.getString("FBestBy_Date")));
        }
        return spoiledFood;
    }

    public static void addItemsToKitchen(LocalDate date, Connection con) throws SQLException {
        String currentDate = date.toString();
        int listID = searchForShoppingListByDate(currentDate, con);
        if (listID == -1) {
            return;
        }
        ArrayList<ListSoughtItem> soughtItems = new ArrayList<>();
        Statement statement = con.createStatement();
        ResultSet soughtSet = statement.executeQuery("call returnAllSoughtItems("+listID+")");
        while (soughtSet.next()) {
            soughtItems.add(new ListSoughtItem(soughtSet.getInt("Item_ID"),soughtSet.getInt("List_ID")));
        }
        for (ListSoughtItem item : soughtItems) {
            boolean isFoodItem = false, isCleaningItem = true;
            statement.executeUpdate("call searchForFoodItem("+item.getItemID()+", @Name)");
            ResultSet foodTypeExists = statement.executeQuery("select @Name");
            foodTypeExists.next();
            foodTypeExists.getString("@Name");
            if (foodTypeExists.getString("@Name") == null) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("No food type was detected for this item. Would you like to create a definition? Note that by selecting no, it is assumed to be cleaning item.");
                String input = "";
                while (!input.equalsIgnoreCase("Y") || !input.equalsIgnoreCase("N")) {
                    input = scanner.nextLine();
                    if (input.equalsIgnoreCase("Y")) {
                        createFoodType(userDefineFoodType(con), con);
                        isFoodItem = true;
                        isCleaningItem = false;
                    }
                }
            }
            else {
                isFoodItem = true;
                isCleaningItem = false;
            }
            if (isFoodItem) {
                foodTypeExists.next();
                try {
                    statement.executeUpdate("call createFoodQuantity("+item.getItemID() +", "+item.getItemID() + ", \"" + date.plusWeeks(1) + "\")");
                }
                catch (SQLIntegrityConstraintViolationException e) {
                    System.out.println("Violation detected, probably duplicate. ");
                    e.printStackTrace();
                }
            }
            else if (isCleaningItem) {

            }
            deleteSoughtItem(item.getItemID(), con);
        }


    }
}