package views.homepage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import controller.ItemController;
import controller.UserController;
import database.DatabaseConnector;
import model.Item;
import views.auth.LoginView;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class AdminView {
	private Stage stage;
	private Scene scene;
	private VBox mainContainer;
	private TableView<Item> itemTable;
	private ItemController itemController;
	private UserController userController;
	

	public AdminView(Stage stage) {
		this.stage = stage;
		this.itemController = ItemController.getInstance();
		initialize();
	}

	private void initialize() {
		mainContainer = new VBox(20);
		mainContainer.setPadding(new Insets(20));
		mainContainer.setAlignment(Pos.CENTER);

		Label headerLabel = new Label("Admin Dashboard");
		headerLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

		setupItemTable();

		HBox buttonContainer = new HBox(12);
		buttonContainer.setAlignment(Pos.CENTER);

		Button approveButton = new Button("Approve Item");
		Button declineButton = new Button("Decline Item");
		Button logoutButton = new Button("Logout");

		buttonContainer.getChildren().addAll(approveButton, declineButton, logoutButton);

		mainContainer.getChildren().addAll(headerLabel, itemTable, buttonContainer);

		approveButton.setOnAction(e -> handleApproveItem());
		declineButton.setOnAction(e -> handleDeclineItem());
		logoutButton.setOnAction(e -> handleLogout());

		scene = new Scene(mainContainer, 800, 600);
		stage.setTitle("CaLouselF - Admin Dashboard");
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

		TableColumn<Item, String> statusColumn = new TableColumn<>("Item Status");
		statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getItemStatus()));

		itemTable.getColumns().addAll(idColumn, nameColumn, categoryColumn, sizeColumn, priceColumn, statusColumn);

		VBox.setVgrow(itemTable, Priority.ALWAYS);
		itemTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		refreshItemList();
	}

	private void refreshItemList() {
		itemTable.getItems().clear();
		itemTable.getItems().addAll(itemController.viewPendingItems());
	}

	private void handleApproveItem() {
		Item selectedItem = itemTable.getSelectionModel().getSelectedItem();
		if (selectedItem == null) {
			showAlert("Error", "Select an item to be approved!");
			return;
		}

		String query = String.format("UPDATE Items SET Status = 'Approved' WHERE ItemID = %d",
				selectedItem.getItemID());
		DatabaseConnector.getInstance().execute(query);

		showAlert("Success", "Item has been approved!");
		refreshItemList();
	}

	private void handleDeclineItem() {
		Item selectedItem = itemTable.getSelectionModel().getSelectedItem();
		if (selectedItem == null) {
			showAlert("Error", "Select an item to be declined!");
			return;
		}

		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Decline Item?");
		dialog.setHeaderText("Enter reason for declining the items:");
		dialog.setContentText("Reason:");

		dialog.showAndWait().ifPresent(reason -> {
			if (reason.trim().isEmpty()) {
				showAlert("Error", "Reason must be filled!");
				return;
			}

			String query = String.format("UPDATE Items SET Status = 'Declined', ReasonForDecline = '%s' WHERE ItemID = %d",
					reason, selectedItem.getItemID());
			DatabaseConnector.getInstance().execute(query);

			showAlert("Success", "Item has been declined.");
			refreshItemList();
		});
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
