package controller;

import database.DatabaseConnector;
import model.Offer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OfferController {

    private static OfferController singletonInstance;
    private final DatabaseConnector databaseConnector;

    private OfferController() {
        this.databaseConnector = DatabaseConnector.getInstance();
    }

    public static OfferController getInstance() {
        if (singletonInstance == null) {
            synchronized (OfferController.class) {
                if (singletonInstance == null) {
                    singletonInstance = new OfferController();
                }
            }
        }
        return singletonInstance;
    }

    public void makeOffer(Offer offer) {
        double highestOffer = fetchHighestOffer(offer.getItemID());
        if (offer.getOfferItemPrice() <= highestOffer) {
            System.out.println("You must offer higher than the current highest offer!");
            return;
        }

        try {
            String sql = String.format(
                    "INSERT INTO Offers (ItemID, BuyerID, OfferItemPrice) VALUES (%d, %d, %.2f)",
                    offer.getItemID(), offer.getBuyerID(), offer.getOfferItemPrice()
            );
            databaseConnector.execute(sql);
            System.out.println("Offer submitted successfully.");
        } catch (Exception e) {
            System.err.println("Error submitting offer: " + e.getMessage());
        }
    }

    public List<Offer> viewOffers(int sellerID) {
        String sql = String.format(
                "SELECT o.* FROM Offers o " +
                "JOIN Items i ON o.ItemID = i.ItemID " +
                "WHERE i.SellerID = %d AND o.ItemStatus = 'Pending'",
                sellerID
        );

        List<Offer> offers = new ArrayList<>();
        try (ResultSet resultSet = databaseConnector.execQuery(sql)) {
            while (resultSet.next()) {
                offers.add(mapResultSetToOffer(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching offers: " + e.getMessage());
        }

        return offers;
    }

    public void acceptOffer(int offerID) {
        try {
            updateOfferStatus(offerID, "Accepted", null);
            removeAcceptedItem(offerID);
            System.out.println("Offer accepted successfully.");
        } catch (Exception e) {
            System.err.println("Error accepting offer: " + e.getMessage());
        }
    }

    public void declineOffer(int offerID, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            System.out.println("A valid reason must be provided to decline the offer.");
            return;
        }

        try {
            updateOfferStatus(offerID, "Declined", reason);
            System.out.println("Offer declined with reason: " + reason);
        } catch (Exception e) {
            System.err.println("Error declining offer: " + e.getMessage());
        }
    }

    public double fetchHighestOffer(int itemID) {
        String sql = String.format(
                "SELECT MAX(OfferItemPrice) AS HighestOffer FROM Offers WHERE ItemID = %d",
                itemID
        );

        try (ResultSet resultSet = databaseConnector.execQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getDouble("HighestOffer");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching highest offer: " + e.getMessage());
        }

        return 0;
    }

    private void updateOfferStatus(int offerID, String status, String reason) throws SQLException {
        String sql = (reason == null) ? 
                String.format("UPDATE Offers SET ItemStatus = '%s' WHERE OfferID = %d", status, offerID) :
                String.format("UPDATE Offers SET ItemStatus = '%s', ReasonForDecline = '%s' WHERE OfferID = %d", 
                              status, reason, offerID);

        databaseConnector.execute(sql);
    }

    private void removeAcceptedItem(int offerID) throws SQLException {
        String sql = String.format(
                "DELETE FROM Items WHERE ItemID = (SELECT ItemID FROM Offers WHERE OfferID = %d)",
                offerID
        );
        databaseConnector.execute(sql);
    }

    private Offer mapResultSetToOffer(ResultSet resultSet) throws SQLException {
        return new Offer(
                resultSet.getInt("OfferID"),
                resultSet.getInt("ItemID"),
                resultSet.getInt("BuyerID"),
                resultSet.getDouble("OfferItemPrice"),
                resultSet.getString("ItemStatus"),
                resultSet.getString("ReasonForDecline")
        );
    }
}
