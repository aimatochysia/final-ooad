package controller;

import database.DatabaseConnector;
import model.Item;
import model.Transaction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionController {

    private static TransactionController singletonInstance;
    private final DatabaseConnector databaseConnector;

    private TransactionController() {
        this.databaseConnector = DatabaseConnector.getInstance();
    }

    public static TransactionController getInstance() {
        if (singletonInstance == null) {
            synchronized (TransactionController.class) {
                if (singletonInstance == null) {
                    singletonInstance = new TransactionController();
                }
            }
        }
        return singletonInstance;
    }

    public void createTransaction(Transaction transaction) {
        try {
            insertTransaction(transaction);
            removeItemFromWishlist(transaction.getBuyerID(), transaction.getItemID());
            System.out.println("Transaction created and item removed from wishlist.");
        } catch (Exception e) {
            System.err.println("Error creating transaction: " + e.getMessage());
        }
    }

    public List<Item> viewTransactionHistory(int buyerID) {
        String sql = String.format(
                "SELECT i.ItemID, i.ItemName, i.ItemCategory, i.ItemSize, t.TotalPrice " +
                "FROM Transactions t " +
                "JOIN Items i ON t.ItemID = i.ItemID " +
                "WHERE t.BuyerID = %d",
                buyerID
        );

        List<Item> transactionHistory = new ArrayList<>();
        try (ResultSet resultSet = databaseConnector.execQuery(sql)) {
            while (resultSet.next()) {
                transactionHistory.add(mapResultSetToItem(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching transaction history: " + e.getMessage());
        }

        return transactionHistory;
    }

    private void insertTransaction(Transaction transaction) throws SQLException {
        String sql = String.format(
                "INSERT INTO Transactions (BuyerID, ItemID, TotalPrice) VALUES (%d, %d, %.2f)",
                transaction.getBuyerID(), transaction.getItemID(), transaction.getTotalItemPrice()
        );
        databaseConnector.execute(sql);
    }

    private void removeItemFromWishlist(int buyerID, int itemID) throws SQLException {
        String sql = String.format(
                "DELETE FROM Wishlist WHERE BuyerID = %d AND ItemID = %d",
                buyerID, itemID
        );
        databaseConnector.execute(sql);
    }

    private Item mapResultSetToItem(ResultSet resultSet) throws SQLException {
        return new Item(
                resultSet.getInt("ItemID"),
                resultSet.getString("ItemName"),
                resultSet.getString("ItemCategory"),
                resultSet.getString("ItemSize"),
                resultSet.getDouble("TotalPrice"),
                0, 
                "Purchased",
                null 
        );
        
    }
}
