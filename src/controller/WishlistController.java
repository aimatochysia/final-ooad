package controller;

import database.DatabaseConnector;
import model.Wishlist;
import model.Item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WishlistController {

    private static WishlistController singletonInstance;
    private final DatabaseConnector databaseConnector;

    private WishlistController() {
        this.databaseConnector = DatabaseConnector.getInstance();
    }

    public static WishlistController getInstance() {
        if (singletonInstance == null) {
            synchronized (WishlistController.class) {
                if (singletonInstance == null) {
                    singletonInstance = new WishlistController();
                }
            }
        }
        return singletonInstance;
    }

    public void addItemToWishlist(Wishlist wishlist) {
        String sql = String.format(
                "INSERT INTO Wishlist (BuyerID, ItemID) VALUES (%d, %d)",
                wishlist.getBuyerID(), wishlist.getItemID());

        try {
            databaseConnector.execute(sql);
            System.out.println("Item successfully added to Wishlist.");
        } catch (Exception e) {
            System.err.println("Error adding item to Wishlist: " + e.getMessage());
        }
    }

    public List<Item> viewWishlist(int buyerID) {
        String sql = String.format(
                "SELECT i.* FROM Wishlist w JOIN Items i ON w.ItemID = i.ItemID WHERE w.BuyerID = %d",
                buyerID);
        List<Item> wishlistItems = new ArrayList<>();

        try (ResultSet resultSet = databaseConnector.execQuery(sql)) {
            while (resultSet.next()) {
                wishlistItems.add(mapResultSetToItem(resultSet, buyerID));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching Wishlist: " + e.getMessage());
        }

        return wishlistItems;
    }

    public void removeItemFromWishlist(Wishlist wishlist) {
        String sql = String.format(
                "DELETE FROM Wishlist WHERE WishlistID = %d AND BuyerID = %d",
                wishlist.getWishlistID(), wishlist.getBuyerID());

        try {
            databaseConnector.execute(sql);
            System.out.println("Item successfully removed from Wishlist.");
        } catch (Exception e) {
            System.err.println("Error removing item from Wishlist: " + e.getMessage());
        }
    }

    private Item mapResultSetToItem(ResultSet resultSet, int buyerID) throws SQLException {
        return new Item(
                resultSet.getInt("ItemID"),
                resultSet.getString("ItemName"),
                resultSet.getString("ItemCategory"),
                resultSet.getString("ItemSize"),
                resultSet.getDouble("ItemPrice"),
                buyerID,
                resultSet.getString("ItemStatus"),
                resultSet.getString("ReasonForDecline")
        );
    }
}
