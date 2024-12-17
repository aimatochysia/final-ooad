package model;

public class Wishlist {
	private int buyerID;
	private int wishlistID;
	private int itemID;

	public Wishlist(int buyerID, int wishlistID, int itemID) {
		super();
		this.buyerID = buyerID;
		this.wishlistID = wishlistID;
		this.itemID = itemID;
	}

	public int getBuyerID() {
		return buyerID;
	}

	public void setBuyerID(int buyerID) {
		this.buyerID = buyerID;
	}

	public int getWishlistID() {
		return wishlistID;
	}

	public void setWishlistID(int wishlistID) {
		this.wishlistID = wishlistID;
	}

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}
}
