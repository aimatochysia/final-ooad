package model;

public class Item {
    private int itemID;
    private String itemName;
    private String itemCategory;
    private String itemSize;
    private double itemPrice;
    private int sellerID;
    private String itemStatus;
    private String reasonForDecline;

    public Item(int itemID, String itemName, String itemCategory, String itemSize, double itemPrice, int sellerID, String itemStatus, String reasonForDecline) 
    {
        this.itemID = itemID;
        this.itemName = itemName;
        this.itemCategory = itemCategory;
        this.itemSize = itemSize;
        this.itemPrice = itemPrice;
        this.sellerID = sellerID;
        this.itemStatus = itemStatus;
        this.reasonForDecline = reasonForDecline;
    }

    public int getItemID() 
    {
        return itemID;
    }

    public void setItemID(int itemID) 
    {
        this.itemID = itemID;
    }

    public void setSellerID(int sellerID) 
    {
        this.sellerID = sellerID;
    }

    public String getItemName() 
    {
        return itemName;
    }

    public void setItemName(String itemName) 
    {
        this.itemName = itemName;
    }

    public String getItemCategory() 
    {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) 
    {
        this.itemCategory = itemCategory;
    }

    public String getItemSize() 
    {
        return itemSize;
    }

    public void setItemSize(String itemSize) 
    {
        this.itemSize = itemSize;
    }

    public double getItemPrice() 
    {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) 
    {
        this.itemPrice = itemPrice;
    }

    public int getSellerID() 
    {
        return sellerID;
    }

    public String getItemStatus() 
    {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) 
    {
        this.itemStatus = itemStatus;
    }

    public String getReasonForDecline() 
    {
        return reasonForDecline;
    }

    public void setReasonForDecline(String reasonForDecline) 
    {
        this.reasonForDecline = reasonForDecline;
    }
}
