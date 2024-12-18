package views;

import controller.ItemController;
import controller.OfferController;
import controller.UserController;
import database.DatabaseConnector;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Item;
import model.Offer;
import views.LoginView;

public class BuyerView {
	
	private Stage stage;
	private Scene scene;
	private VBox mainContainer;
	private TableView<Item> itemTable;
	private ItemController itemController;
	private int buyerId;

	public BuyerView(Stage stage, int buyerId) {
		this.stage = stage;
		this.buyerId = buyerId;
		this.itemController = ItemController.getInstance();
		initialize();
	}
	
	private void initialize() {
		mainContainer = new VBox(24);
		mainContainer.setPadding(new Insets(15));
		mainContainer.setAlignment(Pos.CENTER);

		Label headerLabel = new Label("Buyer Dashboard");
		headerLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

		setupItemTable();

		HBox buttonContainer = new HBox(12);
		HBox logoutContainer = new HBox(12);
		buttonContainer.setAlignment(Pos.CENTER);
		logoutContainer.setAlignment(Pos.CENTER);

		Button buyButton = new Button("Buy Item");
		Button wishlistButton = new Button("Add to Wishlist");
		Button makeOfferButton = new Button("Offer Price");
		Button viewWishlistButton = new Button("View Wishlist");
		Button viewTransactionsHitoryButton = new Button("View Transactions History");
		Button logoutButton = new Button("Logout");

		buttonContainer.getChildren().addAll(buyButton, wishlistButton, makeOfferButton, viewWishlistButton, viewTransactionsHitoryButton);
		logoutContainer.getChildren().addAll(logoutButton);
		mainContainer.getChildren().addAll(headerLabel, itemTable, buttonContainer, logoutContainer);

		buyButton.setOnAction(e -> handleBuyItem());
		wishlistButton.setOnAction(e -> handleWishlistItem());
		makeOfferButton.setOnAction(e -> handleMakeOfferItem());
		viewWishlistButton.setOnAction(e -> handleViewWishlist());
		viewTransactionsHitoryButton.setOnAction(e -> handleViewTrxHistory());
		logoutButton.setOnAction(e -> handleLogout());

		scene = new Scene(mainContainer, 800, 600);
		stage.setTitle("CaLouselF - Buyer Dashboard");
	}
	
	private void setupItemTable() {
		itemTable = new TableView<>();

		TableColumn<Item, Integer> idColumn = new TableColumn<>("Item ID");
		idColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getItemID()));

		TableColumn<Item, String> nameColumn = new TableColumn<>("Item Name");
		nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getItemName()));

		TableColumn<Item, String> categoryColumn = new TableColumn<>("Item Category");
		categoryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getItemCategory()));

		TableColumn<Item, String> sizeColumn = new TableColumn<>("Item Size");
		sizeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getItemSize()));

		TableColumn<Item, Double> priceColumn = new TableColumn<>("Item Price");
		priceColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getItemPrice()));

		itemTable.getColumns().addAll(idColumn, nameColumn, categoryColumn, sizeColumn, priceColumn);

		VBox.setVgrow(itemTable, Priority.ALWAYS);
		itemTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		refreshItemList();
	}
	
	private void refreshItemList() {
		itemTable.getItems().clear();
		itemTable.getItems().addAll(itemController.viewItemsToBuy());
	}

	private void handleBuyItem() {
		Item selectedItem = itemTable.getSelectionModel().getSelectedItem();
		if (selectedItem == null) {
			showAlert("Error", "Select an item you want to buy!");
			return;
		}
	// confirmation
	    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
	    confirmationAlert.setTitle("Confirmation");
	    confirmationAlert.setHeaderText("Warning !");
	    confirmationAlert.setContentText(
	        String.format("Are you sure you want to buy the item: %s for $%.2f?", 
	        selectedItem.getItemName(),  
	        selectedItem.getItemPrice()));
	    confirmationAlert.showAndWait().ifPresent(response -> {
	        if (response.getText().equalsIgnoreCase("OK")) {
	            try {
	                String insertTransactionQuery = String.format(
	                    "INSERT INTO Transactions (BuyerID, ItemID, TotalPrice) VALUES (%d, %d, %.2f)",
	                    this.buyerId,
	                    selectedItem.getItemID(),
	                    selectedItem.getItemPrice()
	                );
	                DatabaseConnector.getInstance().execute(insertTransactionQuery);

	                String deleteWishlistQuery = String.format(
	                    "DELETE FROM Wishlist WHERE BuyerID = %d AND ItemID = %d",
	                    this.buyerId,
	                    selectedItem.getItemID()
	                );
	                DatabaseConnector.getInstance().execute(deleteWishlistQuery);

	                showAlert("Success", "Item has been purchased.");
	                refreshItemList();
	            } catch (Exception e) {
	                e.printStackTrace();
	                showAlert("Error", "Theres ann error occurred while processing your purchase.");
	            }
	        } else {
	            showAlert("Cancelled", "The item purchase is cancelled!");
	        }
	    });
	}

	private void handleWishlistItem() {
		Item selectedItem = itemTable.getSelectionModel().getSelectedItem();
		if (selectedItem == null) {
			showAlert("Error", "Select an item to add to the wishlist.");
			return;
		}
		
		String queryWishlist = String.format("INSERT INTO wishlist (BuyerID, ItemID) VALUES(%d, %d);",
				this.buyerId, selectedItem.getItemID());
		DatabaseConnector.getInstance().execute(queryWishlist);
		
		showAlert("Success", "Item has been added to wishlist.");
		refreshItemList();
	}
	
	private void handleMakeOfferItem() {
	    Item selectedItem = itemTable.getSelectionModel().getSelectedItem();
	    if (selectedItem == null) {
	        showAlert("Error", "Select an item to make your offer!");
	        return;
	    }

	    // input harga
	    TextInputDialog offerDialog = new TextInputDialog();
	    offerDialog.setTitle("Make an Offer");
	    offerDialog.setHeaderText(String.format("Make an offer for item: %s (ID: %d)", 
	        selectedItem.getItemName(), 
	        selectedItem.getItemID()));
	    offerDialog.setContentText("Pleas enter your offer price:");

	    // input user handle
	    offerDialog.showAndWait().ifPresent(offerPriceStr -> {
	        if (offerPriceStr.trim().isEmpty()) {
	            showAlert("Error", "Offer price must be filled!");
	            return;
	        }

	        try {
	            double offerPrice = Double.parseDouble(offerPriceStr);
	            if (offerPrice <= 0) {
	                showAlert("Error", "Offer must be greater than zero!");
	                return;
	            }

	            Offer newOffer = new Offer(0, selectedItem.getItemID(), this.buyerId, offerPrice, "Pending", "N/A");
	            OfferController offerController = OfferController.getInstance();

	            // valid = insert to database
	            offerController.makeOffer(newOffer);
	            showAlert("Success", "Offer has been submitted! Status: Pending.");

	        } catch (NumberFormatException e) {
	            showAlert("Error", "Please enter a valid offer (numbers)!");
	        } catch (Exception e) {
	            e.printStackTrace();
	            showAlert("Error", "Theres an error occurred while submitting your offer.");
	        }
	    });
	}

	
	private void handleViewWishlist() {
		WishlistView wishlistView = new WishlistView(stage, this.buyerId);
		stage.setScene(wishlistView.getScene());
		stage.show();
	}
	
	private void handleViewTrxHistory() {
		PurchaseHistoryView purchaseHistoryView = new PurchaseHistoryView(stage, this.buyerId);
		stage.setScene(purchaseHistoryView.getScene());
		stage.show();
	}

	private void handleLogout() {
		try {
			new LoginView().start(stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setContentText(content);
		alert.showAndWait();
	}

	public Scene getScene() {
		return scene;
	}

}
