package views.homepage;

import controller.ItemController;
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
import views.auth.LoginView;

public class WishlistView {
		
		private Stage stage;
		private Scene scene;
		private VBox mainContainer;
		private TableView<Item> itemTable;
		private ItemController itemController;
		private int buyerId;

		public WishlistView(Stage stage, int buyerId) {
			this.stage = stage;
			this.buyerId = buyerId;
			this.itemController = ItemController.getInstance();
			initialize();
		}
		
		private void initialize() {
			mainContainer = new VBox(24);
			mainContainer.setPadding(new Insets(24));
			mainContainer.setAlignment(Pos.CENTER);

			Label headerLabel = new Label("Wishlist");
			headerLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

			setupItemTable();

			HBox buttonContainer = new HBox(12);
			buttonContainer.setAlignment(Pos.CENTER);

			Button removeItemButton = new Button("Remove Item");
			Button backButton = new Button("Back into Dashboard");

			buttonContainer.getChildren().addAll(removeItemButton, backButton);

			mainContainer.getChildren().addAll(headerLabel, itemTable, buttonContainer);

			removeItemButton.setOnAction(e -> handleRemoveItem());
			backButton.setOnAction(e -> handleBackToDashboard());

			scene = new Scene(mainContainer, 900, 650);
			stage.setTitle("Buyer Wishlist");
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
			itemTable.getItems().addAll(itemController.viewItemsWishlist(this.buyerId));
		}

		private void handleRemoveItem() {
			Item selectedItem = itemTable.getSelectionModel().getSelectedItem();
			if (selectedItem == null) {
				showAlert("Error", "Select a wishlist items to remove.");
				return;
			}
			
		    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
		    confirmationAlert.setTitle("Confirmation");
		    confirmationAlert.setHeaderText("Warning !");
		    confirmationAlert.setContentText(
		        String.format("Do you want to remove the item: %s ?", 
		        selectedItem.getItemName()));
		    
		    confirmationAlert.showAndWait().ifPresent(response -> {
		        if (response.getText().equalsIgnoreCase("OK")) {
		            try {
		                String deleteWishlistQuery = String.format(
		                    "DELETE FROM Wishlist WHERE BuyerID = %d AND ItemID = %d",
		                    this.buyerId,
		                    selectedItem.getItemID()
		                );
		                DatabaseConnector.getInstance().execute(deleteWishlistQuery);

		                showAlert("Success", "Item successfully removed.");
		                refreshItemList(); 
		            } catch (Exception e) {
		                e.printStackTrace();
		                showAlert("Error", "Theres an error occurred while processing your request.");
		            }
		        } else {
		            showAlert("Cancelled", "Item removal has been cancelled.");
		        }
		    });
			
		}

		private void handleBackToDashboard() {
			BuyerView buyerView = new BuyerView(stage, this.buyerId);
			stage.setScene(buyerView.getScene());
			stage.show();
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
