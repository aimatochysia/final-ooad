package controller;

import database.DatabaseConnector;
import model.Item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemController {
	private static ItemController instance;
	private final DatabaseConnector db;

	private ItemController() {
		db = DatabaseConnector.getInstance();
	}

	public static ItemController getInstance() {
		if (instance == null) {
			instance = new ItemController();
		}
		return instance;
	}

	public void uploadItem(Item item) {
		if (validateItemDetails(item)) {
			String query = String.format(
					"INSERT INTO Items (ItemName, itemCategory, itemSize, itemPrice, SellerID, itemStatus) VALUES ('%s', '%s', '%s', %.2f, %d, 'Pending')",
					item.getItemName(), item.getItemCategory(), item.getItemSize(), item.getItemPrice(), item.getSellerID());
			db.execute(query);
			System.out.println("Item has been uploaded. Waiting for admin approval!");
		}
	}

	public void editItem(Item item) {
		if (validateItemDetails(item)) {
			String query = String.format(
					"UPDATE Items SET ItemName = '%s', itemCategory = '%s', itemSize = '%s', itemPrice = %.2f WHERE ItemID = %d AND itemStatus = 'Approved'",
					item.getItemName(), item.getItemCategory(), item.getItemSize(), item.getItemPrice(), item.getItemID());
			db.execute(query);
			System.out.println("Item details has been updated.");
		}
	}

	public void deleteItem(int itemID, int sellerID) {
		String query = String.format("DELETE FROM Items WHERE ItemID = %d AND SellerID = %d AND itemStatus = 'Approved'",
				itemID, sellerID);
		db.execute(query);
		System.out.println("Item has been deleted.");
	}
	
	public List<Item> viewPendingItems() {
	    String query = "SELECT * FROM Items WHERE itemStatus = 'Pending'";
	    ResultSet rs = db.execQuery(query);
	    List<Item> items = new ArrayList<>();
	    try {
	        while (rs.next()) {
	            items.add(new Item(
	                rs.getInt("ItemID"),
	                rs.getString("ItemName"),
	                rs.getString("itemCategory"),
	                rs.getString("itemSize"),
	                rs.getDouble("itemPrice"),
	                rs.getInt("SellerID"),
	                rs.getString("itemStatus"),
	                rs.getString("reasonForDecline")
	            ));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return items;
	}
	
	public List<Item> viewItems(int sellerID) {
	    String query = String.format("SELECT * FROM Items WHERE itemStatus = 'Approved' AND SellerID = %d", sellerID);
	    ResultSet rs = db.execQuery(query);
	    List<Item> items = new ArrayList<>();
	    try {
	        while (rs.next()) {
	            items.add(new Item(rs.getInt("ItemID"), rs.getString("ItemName"), rs.getString("itemCategory"), rs.getString("itemSize"), 
	            		rs.getDouble("itemPrice"), rs.getInt("SellerID"), rs.getString("itemStatus"), rs.getString("reasonForDecline")));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return items;
	}

	private boolean validateItemDetails(Item item) {
		if (item.getItemName() == null || item.getItemName().trim().length() < 3) {
			System.out.println("Items name must at least be 3 character long!");
			return false;
		}
		if (item.getItemCategory() == null || item.getItemCategory().trim().length() < 3) {
			System.out.println("Items category must at least be 3 character long!");
			return false;
		}
		if (item.getItemSize() == null || item.getItemSize().trim().isEmpty()) {
			System.out.println("Item size cannot be left empty!");
			return false;
		}
		if (item.getItemPrice() <= 0) {
			System.out.println("Item price cannot be 0 or empty!");
			return false;
		}
		return true;
	}
	
	// view items in buyer views
	public List<Item> viewItemsToBuy() {
	    String query = String.format("SELECT * FROM Items WHERE itemStatus = 'Approved'");
	    ResultSet rs = db.execQuery(query);
	    List<Item> items = new ArrayList<>();
	    try {
	    	while (rs.next()) {
	            items.add(new Item(rs.getInt("ItemID"), rs.getString("ItemName"), rs.getString("itemCategory"), rs.getString("itemSize"), 
	            		rs.getDouble("itemPrice"), rs.getInt("SellerID"), rs.getString("itemStatus"), rs.getString("reasonForDecline")));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return items;
	}
	
	// view items in buyer wishlist
	public List<Item> viewItemsWishlist(int buyerID) {
	    String query = String.format(
	            "SELECT i.ItemID, i.ItemName, i.itemCategory, i.itemSize, i.itemPrice " +
	            "FROM wishlist w " +
	            "JOIN items i ON w.ItemID = i.ItemID " +
	            "WHERE w.BuyerID = %d", 
	            buyerID
	    );
	    
	    ResultSet rs = db.execQuery(query);
	    List<Item> items = new ArrayList<>();
	    try {
	        while (rs.next()) {
	            items.add(new Item(
	                rs.getInt("ItemID"),
	                0, 
	                rs.getString("ItemName"),
	                rs.getString("itemCategory"),
	                rs.getString("itemSize"),
	                rs.getDouble("itemPrice"),
	                null, 
	                null  
	            ));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return items;
	}
}
