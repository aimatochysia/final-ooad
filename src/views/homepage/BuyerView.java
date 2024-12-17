package views.homepage;

import java.util.List;
import model.Item;
import controller.WishlistController;
import controller.PurchaseController;
import controller.OfferController;
import controller.TransactionController;
import model.Wishlist;
import model.Transaction;

public class BuyerView {

    private WishlistController wishlistController;
    private PurchaseController purchaseController;
    private OfferController offerController;
    private TransactionController transactionController;

    public BuyerView() {
        wishlistController = WishlistController.getInstance();
        purchaseController = PurchaseController.getInstance();
        offerController = OfferController.getInstance();
        transactionController = TransactionController.getInstance();
    }

    // Display all items in the buyer's wishlist
    public void viewWishlist(int buyerID) {
        List<Item> wishlistItems = wishlistController.viewWishlist(buyerID);
        System.out.println("Wishlist Items:");
        for (Item item : wishlistItems) {
            System.out.println(item.getItemName() + " | " + item.getItemCategory() + " | " + item.getItemPrice());
        }
    }

    // Add an item to the buyer's wishlist
    public void addItemToWishlist(int buyerID, Item item) {
        Wishlist wishlist = new Wishlist(buyerID, item.getItemID());
        wishlistController.addItemToWishlist(wishlist);
        System.out.println("Item added to wishlist successfully.");
    }

    // Remove an item from the wishlist
    public void removeItemFromWishlist(int buyerID, Item item) {
        Wishlist wishlist = new Wishlist(buyerID, item.getItemID());
        wishlistController.removeItemFromWishlist(wishlist);
        System.out.println("Item removed from wishlist successfully.");
    }

    // Confirm purchase of an item
    public void purchaseItem(int buyerID, Item item) {
        // Generate a new transaction ID and add the purchase
        Transaction transaction = new Transaction(buyerID, item.getItemID());
        transactionController.createTransaction(transaction);
        purchaseController.deleteItemFromWishlist(buyerID, item.getItemID()); // Remove from wishlist
        System.out.println("Item purchased successfully!");
    }

    // Make an offer on an item
    public void makeOffer(int buyerID, Item item, double offerPrice) {
        if (offerPrice <= 0) {
            System.out.println("Offer price must be greater than zero.");
            return;
        }

        if (offerController.canMakeOffer(item.getItemID(), offerPrice)) {
            offerController.placeOffer(buyerID, item.getItemID(), offerPrice);
            System.out.println("Offer placed successfully.");
        } else {
            System.out.println("Offer must be higher than the current offer.");
        }
    }

    // View purchase history of the buyer
    public void viewPurchaseHistory(int buyerID) {
        List<Transaction> transactions = transactionController.getPurchaseHistory(buyerID);
        System.out.println("Purchase History:");
        for (Transaction transaction : transactions) {
            System.out.println("Transaction ID: " + transaction.getTransactionID());
            Item item = purchaseController.getItemByTransactionID(transaction.getItemID());
            System.out.println(item.getItemName() + " | " + item.getItemCategory() + " | " + item.getItemPrice());
        }
    }

    // View offered items as a seller
    public void viewOfferedItems(int sellerID) {
        List<Item> offeredItems = offerController.getOfferedItems(sellerID);
        System.out.println("Offered Items:");
        for (Item item : offeredItems) {
            System.out.println(item.getItemName() + " | " + item.getItemCategory() + " | Initial Price: "
                    + item.getItemPrice() + " | Offered Price: " + offerController.getHighestOffer(item.getItemID()));
        }
    }

    // View items pending review (Admin function)
    public void viewRequestedItems() {
        List<Item> requestedItems = purchaseController.getRequestedItems();
        System.out.println("Requested Items for Review:");
        for (Item item : requestedItems) {
            System.out.println(item.getItemName() + " | " + item.getItemCategory() + " | " + item.getItemPrice());
        }
    }

    // Approve an item (Admin function)
    public void approveItem(Item item) {
        purchaseController.approveItem(item);
        System.out.println("Item approved for sale.");
    }

    // Decline an item (Admin function)
    public void declineItem(Item item, String reason) {
        if (reason.isEmpty()) {
            System.out.println("Reason for decline cannot be empty.");
            return;
        }
        purchaseController.declineItem(item, reason);
        System.out.println("Item declined: " + reason);
    }
}
