package model;

public class Offer {
	private int offerID;
	private int itemID;
	private int buyerID;
	private double offerItemPrice;
	private String itemStatus;
	private String reasonForDecline;

	public Offer(int offerID, int itemID, int buyerID, double offerItemPrice, String itemStatus, String reasonForDecline) {
		super();
		this.offerID = offerID;
		this.itemID = itemID;
		this.buyerID = buyerID;
		this.offerItemPrice = offerItemPrice;
		this.itemStatus = itemStatus;
		this.reasonForDecline = reasonForDecline;
	}

	public int getOfferID() {
		return offerID;
	}

	public void setOfferID(int offerID) {
		this.offerID = offerID;
	}

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	public int getBuyerID() {
		return buyerID;
	}

	public void setBuyerID(int buyerID) {
		this.buyerID = buyerID;
	}

	public double getOfferItemPrice() {
		return offerItemPrice;
	}

	public void setOfferItemPrice(double offerItemPrice) {
		this.offerItemPrice = offerItemPrice;
	}

	public String getItemStatus() {
		return itemStatus;
	}

	public void setItemStatus(String itemStatus) {
		this.itemStatus = itemStatus;
	}

	public String getReasonForDecline() {
		return reasonForDecline;
	}

	public void setReasonForDecline(String reasonForDecline) {
		this.reasonForDecline = reasonForDecline;
	}
}
