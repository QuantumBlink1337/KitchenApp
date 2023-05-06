package com.quantumblink;

public class ListSoughtItem {
    // TODO
    // convert to record
    private final int ItemID;
    private final int ListID;


    public ListSoughtItem(int itemID, int listID) {
        ItemID = itemID;
        ListID = listID;
    }

    public int getListID() {
        return ListID;
    }

    public int getItemID() {
        return ItemID;
    }
}
