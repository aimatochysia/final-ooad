package views.auth;

import controller.UserController;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.User;
import views.homepage.AdminView;
import views.homepage.BuyerView;
import views.homepage.SellerView;

public class LoginView extends Application {

    @Override
    public void start(Stage primaryStage) {

        UserController userController = UserController.getInstance();

        // Create title and subtitle
        Label title = new Label("CaLouselF");
        title.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #3E4C59;");

        Label subtitle = new Label("Welcome Back!");
        subtitle.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 18px; -fx-font-weight: semi-bold; -fx-text-fill: #8F8F8F;");

        // Create labels and fields for username and password
        Label usernameLbl = new Label("Username:");
        TextField usernameTf = new TextField();
        usernameTf.setPromptText("Enter your username");

        Label passwordLbl = new Label("Password:");
        PasswordField passwordPf = new PasswordField();
        passwordPf.setPromptText("Enter your password");

        // Create error label
        Label errorLbl = new Label();
        errorLbl.setStyle("-fx-text-fill: #FF0000;");

        // Login Button
        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px;");
        
        // Register link
        Hyperlink registerLink = new Hyperlink("Don't have an account? Register here");
        registerLink.setStyle("-fx-text-fill: #2196F3;");

        // Set up VBox layout
        VBox layout = new VBox(20, title, subtitle, usernameLbl, usernameTf, passwordLbl, passwordPf, loginBtn, errorLbl, registerLink);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 50;");
        layout.setMaxWidth(400);

        // Handle login action
        loginBtn.setOnAction(e -> {
            String username = usernameTf.getText();
            String password = passwordPf.getText();

            if (username.isEmpty() || password.isEmpty()) {
                errorLbl.setText("Both fields are required!");
                return;
            }

            User user = userController.loginUser(username, password);
            if (user == null) {
                errorLbl.setText("Incorrect username or password.");
            } else {
                System.out.println("Logged in as: " + user.getUsername() + " (" + user.getRole() + ")");

                if ("Admin".equalsIgnoreCase(user.getRole())) {
                    AdminView adminView = new AdminView(primaryStage);
                    primaryStage.setScene(adminView.getScene());
                    primaryStage.show();
                } else if ("Seller".equalsIgnoreCase(user.getRole())) {
                    SellerView sellerView = new SellerView(primaryStage, user.getUserID());
                    primaryStage.setScene(sellerView.getScene());
                    primaryStage.show();
                } else {
                    BuyerView buyerView = new BuyerView(primaryStage, user.getUserID());
                    primaryStage.setScene(buyerView.getScene());
                    primaryStage.show();
                }
            }
        });

        // Handle registration action
        registerLink.setOnAction(e -> {
            try {
                new RegisterView().start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Set up scene and stage
        StackPane root = new StackPane(layout);
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Login Page");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
