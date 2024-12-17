package controller;

import database.DatabaseConnector;
import model.Offer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OfferController {
	private static OfferController instance;
	private final DatabaseConnector db;

	private OfferController() {
		db = DatabaseConnector.getInstance();
	}

	public static OfferController getInstance() {
		if (instance == null) {
			instance = new OfferController();
		}
		return instance;
	}

	public void makeOffer(Offer offer) {
		double highestOffer = getHighestOffer(offer.getItemID());
		if (offer.getOfferPrice() <= highestOffer) {
			System.out.println("You must offer higher than the previous offers!");
			return;
		}
		String query = String.format("INSERT INTO Offers (ItemID, BuyerID, OfferItemPrice) VALUES (%d, %d, %.2f)",
				offer.getItemID(), offer.getBuyerID(), offer.getOfferItemPrice());
		db.execute(query);
		System.out.println("Offer has been submitted.");
	}

	public List<Offer> viewOffers(int sellerID) {
		String query = String.format(
				"SELECT o.* FROM Offers o JOIN Items i ON o.ItemID = i.ItemID WHERE i.SellerID = %d AND o.ItemStatus = 'Pending'",
				sellerID);
		ResultSet rs = db.execQuery(query);
		List<Offer> offers = new ArrayList<>();
		try {
			while (rs.next()) {
				offers.add(new Offer(rs.getInt("OfferID"), rs.getInt("ItemID"), rs.getInt("BuyerID"),
						rs.getDouble("OfferItemPrice"), rs.getString("ItemStatus"), rs.getString("ReasonForDecline")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return offers;
	}

	public void acceptOffer(int offerID) {
		String query = String.format("UPDATE Offers SET ItemStatus = 'Accepted' WHERE OfferID = %d", offerID);
		db.execute(query);
		removeItemAfterAcceptance(offerID);
		System.out.println("Offer has been accepted.");
	}

	public void declineOffer(int offerID, String reason) {
		if (reason == null || reason.trim().isEmpty()) {
			System.out.println("Must provide reason for declining!");
			return;
		}
		String query = String.format("UPDATE Offers SET Status = 'Declined', ReasonForDecline = '%s' WHERE OfferID = %d",
				reason, offerID);
		db.execute(query);
		System.out.println("Offer has been declined.");
	}

	public double getHighestOffer(int itemID) {
		String query = String.format("SELECT MAX(OfferPrice) AS HighestOffer FROM Offers WHERE ItemID = %d", itemID);
		ResultSet rs = db.execQuery(query);
		try {
			if (rs.next()) {
				return rs.getDouble("HighestOffer");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private void removeItemAfterAcceptance(int offerID) {
		String query = String.format("DELETE FROM Items WHERE ItemID = (SELECT ItemID FROM Offers WHERE OfferID = %d)",
				offerID);
		db.execute(query);
	}
}
