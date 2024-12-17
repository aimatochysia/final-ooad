package model;

public class Offer {
	private int offerID;
	private int itemID;
	private int buyerID;
	private double offerPrice;
	private String itemStatus;
	private String reasonForDecline;

	public Offer(int offerID, int itemID, int buyerID, double offerPrice, String itemStatus, String reasonForDecline) 
	{
		super();
		this.offerID = offerID;
		this.itemID = itemID;
		this.buyerID = buyerID;
		this.offerPrice = offerPrice;
		this.itemStatus = status;
		this.reasonForDecline = reasonForDecline;
	}

	public int getOfferID() 
	{
		return offerID;
	}

	public void setOfferID(int offerID) 
	{
		this.offerID = offerID;
	}

	public int getItemID() 
	{
		return itemID;
	}

	public void setItemID(int itemID) 
	{
		this.itemID = itemID;
	}

	public int getBuyerID() 
	{
		return buyerID;
	}

	public void setBuyerID(int buyerID) 
	{
		this.buyerID = buyerID;
	}

	public double getOfferPrice() 
	{
		return offerPrice;
	}

	public void setOfferPrice(double offerPrice) 
	{
		this.offerPrice = offerPrice;
	}

	public String getItemStatus() 
	{
		return itemStatus;
	}

	public void setItemStatus(String itemStatus) 
	{
		this.ItemStatus = itemStatus;
	}

	public String getReasonForDecline() 
	{
		return reasonForDecline;
	}

	public void setDeclineReason(String declineReason) 
	{
		this.declineReason = declineReason;
	}
}
