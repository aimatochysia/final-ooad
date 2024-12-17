package model;

public class Transaction {
	private int transactionID;
	private int buyerID;
	private int itemID;
	private double totalItemPrice;

	public Transaction(int transactionID, int buyerID, int itemID, double totalItemPrice) {
		super();
		this.transactionID = transactionID;
		this.buyerID = buyerID;
		this.itemID = itemID;
		this.totalItemPrice = totalItemPrice;
	}

	public int getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(int transactionID) {
		this.transactionID = transactionID;
	}

	public int getBuyerID() {
		return buyerID;
	}

	public void setBuyerID(int buyerID) {
		this.buyerID = buyerID;
	}

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	public double getTotalItemPrice() {
		return totalItemPrice;
	}

	public void setTotalItemPrice(double totalItemPrice) {
		this.totalItemPrice = totalItemPrice;
	}
}
