CREATE DATABASE CaLouselF;

USE CaLouselF;

CREATE TABLE Users (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    Username VARCHAR(100) NOT NULL UNIQUE,
    Password VARCHAR(100) NOT NULL,
    PhoneNumber VARCHAR(15) NOT NULL,
    Address TEXT NOT NULL,
    Role ENUM('Buyer', 'Seller') NOT NULL
);

CREATE TABLE Items (
    ItemID INT AUTO_INCREMENT PRIMARY KEY,
    ItemName VARCHAR(100) NOT NULL,
    ItemCategory VARCHAR(100) NOT NULL,
    ItemSize VARCHAR(30) NOT NULL,
    ItemPrice DECIMAL(10, 2) NOT NULL,
    SellerID INT NOT NULL,
    ItemStatus ENUM('Pending', 'Approved', 'Declined') DEFAULT 'Pending',
    ReasonForDecline TEXT,
    FOREIGN KEY (SellerID) REFERENCES Users(UserID)
);

CREATE TABLE Wishlist (
    WishlistID INT AUTO_INCREMENT PRIMARY KEY,
    BuyerID INT NOT NULL,
    ItemID INT NOT NULL,
    FOREIGN KEY (BuyerID) REFERENCES Users(UserID),
    FOREIGN KEY (ItemID) REFERENCES Items(ItemID)
);

CREATE TABLE Transactions (
    TransactionID INT AUTO_INCREMENT PRIMARY KEY,
    BuyerID INT NOT NULL,
    ItemID INT NOT NULL,
    TotalPrice DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (BuyerID) REFERENCES Users(UserID),
    FOREIGN KEY (ItemID) REFERENCES Items(ItemID)
);

CREATE TABLE Offers (
    OfferID INT AUTO_INCREMENT PRIMARY KEY,
    ItemID INT NOT NULL,
    BuyerID INT NOT NULL,
    OfferItemPrice DECIMAL(10, 2) NOT NULL,
    Status ENUM('Pending', 'Accepted', 'Declined') DEFAULT 'Pending',
    ReasonForDecline TEXT,
    FOREIGN KEY (ItemID) REFERENCES Items(ItemID),
    FOREIGN KEY (BuyerID) REFERENCES Users(UserID)
);
