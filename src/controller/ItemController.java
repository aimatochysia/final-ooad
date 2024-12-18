package controller;

import database.DatabaseConnector;
import model.Item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemController {

    private static ItemController singletonInstance;
    private final DatabaseConnector databaseConnector;

    private ItemController() {
        this.databaseConnector = DatabaseConnector.getInstance();
    }

    public static ItemController getInstance() {
        if (singletonInstance == null) {
            synchronized (ItemController.class) {
                if (singletonInstance == null) {
                    singletonInstance = new ItemController();
                }
            }
        }
        return singletonInstance;
    }

    public void uploadItem(Item item) {
        if (!validateItemDetails(item)) return;

        String sql = String.format(
                "INSERT INTO Items (ItemName, itemCategory, itemSize, itemPrice, SellerID, itemStatus) " +
                        "VALUES ('%s', '%s', '%s', %.2f, %d, 'Pending')",
                item.getItemName(), item.getItemCategory(), item.getItemSize(), item.getItemPrice(), item.getSellerID()
        );

        try {
            databaseConnector.execute(sql);
            System.out.println("Item uploaded successfully. Awaiting admin approval.");
        } catch (Exception e) {
            System.err.println("Error uploading item: " + e.getMessage());
        }
    }

    public void editItem(Item item) {
        if (!validateItemDetails(item)) return;

        String sql = String.format(
                "UPDATE Items SET ItemName = '%s', itemCategory = '%s', itemSize = '%s', itemPrice = %.2f " +
                        "WHERE ItemID = %d AND itemStatus = 'Approved'",
                item.getItemName(), item.getItemCategory(), item.getItemSize(), item.getItemPrice(), item.getItemID()
        );

        try {
            databaseConnector.execute(sql);
            System.out.println("Item details updated successfully.");
        } catch (Exception e) {
            System.err.println("Error updating item: " + e.getMessage());
        }
    }

    public void deleteItem(int itemID, int sellerID) {
        String sql = String.format(
                "DELETE FROM Items WHERE ItemID = %d AND SellerID = %d AND itemStatus = 'Approved'",
                itemID, sellerID
        );

        try {
            databaseConnector.execute(sql);
            System.out.println("Item deleted successfully.");
        } catch (Exception e) {
            System.err.println("Error deleting item: " + e.getMessage());
        }
    }

    public List<Item> viewPendingItems() {
        return fetchItems("SELECT * FROM Items WHERE itemStatus = 'Pending'");
    }

    public List<Item> viewItems(int sellerID) {
        String sql = String.format("SELECT * FROM Items WHERE itemStatus = 'Approved' AND SellerID = %d", sellerID);
        return fetchItems(sql);
    }

    public List<Item> viewItemsToBuy() {
        return fetchItems("SELECT * FROM Items WHERE itemStatus = 'Approved'");
    }

    public List<Item> viewItemsWishlist(int buyerID) {
        String sql = String.format(
                "SELECT i.ItemID, i.ItemName, i.itemCategory, i.itemSize, i.itemPrice " +
                        "FROM wishlist w " +
                        "JOIN items i ON w.ItemID = i.ItemID " +
                        "WHERE w.BuyerID = %d",
                buyerID
        );

        List<Item> items = new ArrayList<>();
        try (ResultSet resultSet = databaseConnector.execQuery(sql)) {
            while (resultSet.next()) {
                items.add(mapResultSetToItem(resultSet, buyerID));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching wishlist items: " + e.getMessage());
        }
        return items;
    }

    private boolean validateItemDetails(Item item) {
        if (item.getItemName() == null || item.getItemName().trim().length() < 3) {
            System.out.println("Item name must be at least 3 characters long.");
            return false;
        }
        if (item.getItemCategory() == null || item.getItemCategory().trim().length() < 3) {
            System.out.println("Item category must be at least 3 characters long.");
            return false;
        }
        if (item.getItemSize() == null || item.getItemSize().trim().isEmpty()) {
            System.out.println("Item size cannot be empty.");
            return false;
        }
        if (item.getItemPrice() <= 0) {
            System.out.println("Item price must be greater than 0.");
            return false;
        }
        return true;
    }

    private List<Item> fetchItems(String sql) {
        List<Item> items = new ArrayList<>();
        try (ResultSet resultSet = databaseConnector.execQuery(sql)) {
            while (resultSet.next()) {
                items.add(mapResultSetToItem(resultSet, resultSet.getInt("SellerID")));
            }
        } catch (SQLException e) {
            System.err.println("Error get items: " + e.getMessage());
        }
        return items;
    }

    private Item mapResultSetToItem(ResultSet resultSet, int buyerID) throws SQLException {
        return new Item(
                resultSet.getInt("ItemID"),
                resultSet.getString("ItemName"),
                resultSet.getString("itemCategory"),
                resultSet.getString("itemSize"),
                resultSet.getDouble("itemPrice"),
                buyerID,
                resultSet.getString("itemStatus"),
                resultSet.getString("reasonForDecline")
        );
    }
}
