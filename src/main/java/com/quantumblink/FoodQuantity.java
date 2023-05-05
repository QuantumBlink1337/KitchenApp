package com.quantumblink;

public class FoodQuantity {
    private final String FBestBy_Date;
    private final int Food_ID;

    public FoodQuantity(int quantityId, int foodId, String itemName, String fBestByDate) {
        FBestBy_Date = fBestByDate;
        Food_ID = foodId;
        Quantity_ID = quantityId;
        Item_name = itemName;
    }

    public String getFBestBy_Date() {
        return FBestBy_Date;
    }

    public int getFood_ID() {
        return Food_ID;
    }

    public int getQuantity_ID() {
        return Quantity_ID;
    }

    private final int Quantity_ID;
    // not an explicit field on the DB schema but convenient for now
    private String Item_name;


    public FoodQuantity(String fBestByDate, int foodId, int quantityId, String itemName) {
        FBestBy_Date = fBestByDate;
        Food_ID = foodId;
        Quantity_ID = quantityId;
        Item_name = itemName;
    }

    public FoodQuantity(String FBestBy_Date, int food_ID, int quantity_ID) {
        this.FBestBy_Date = FBestBy_Date;
        Food_ID = food_ID;
        Quantity_ID = quantity_ID;
        Item_name = null;
    }

    public String getItem_name() {
        return Item_name;
    }

    public void setItem_name(String item_name) {
        Item_name = item_name;
    }
}
