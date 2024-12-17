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

import java.util.Map;

public class RegisterView extends Application {

    private UserController userController;

    @Override
    public void start(Stage primaryStage) {

        userController = UserController.getInstance();

        Label title = new Label("CaLouselF");
        title.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 50px; -fx-font-weight: bold;");

        Label subtitle = new Label("Create your account");
        subtitle.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 22px; -fx-font-weight: semi-bold;");

        Label usernameLbl = new Label("Username:");
        TextField usernameTf = new TextField();
        usernameTf.setPromptText("Enter username");
        usernameTf.setPrefWidth(300);

        Label passwordLbl = new Label("Password:");
        PasswordField passwordPf = new PasswordField();
        passwordPf.setPromptText("Enter password");
        passwordPf.setPrefWidth(300);

        Label phoneLbl = new Label("Phone Number (+62):");
        TextField phoneTf = new TextField();
        phoneTf.setPromptText("+62xxxxxxxxxxx");
        phoneTf.setPrefWidth(300);

        Label phoneHelper = new Label("Format: +62 followed by 10 digits");
        phoneHelper.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");

        Label addressLbl = new Label("Address:");
        TextField addressTf = new TextField();
        addressTf.setPromptText("Enter address");
        addressTf.setPrefWidth(300);

        Label roleLbl = new Label("Role:");
        ComboBox<String> roleCb = new ComboBox<>();
        roleCb.getItems().addAll("Buyer", "Seller");
        roleCb.setPromptText("Select Role");
        roleCb.setPrefWidth(300);

        Button registerBtn = new Button("Register");
        registerBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10px 20px;");
        Label errorLbl = new Label();
        errorLbl.setStyle("-fx-text-fill: red;");

        Hyperlink loginLink = new Hyperlink("Already have an account? Login here");
        loginLink.setStyle("-fx-text-fill: #2196F3;");

        VBox layout = new VBox(16, title, subtitle, usernameLbl, usernameTf, passwordLbl, passwordPf, phoneLbl, phoneTf,
                phoneHelper, addressLbl, addressTf, roleLbl, roleCb, registerBtn, errorLbl, loginLink);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 40;");
        layout.setMaxWidth(400);

        registerBtn.setOnAction(e -> {
            errorLbl.setStyle("-fx-text-fill: red;");
            errorLbl.setText("");

            String username = usernameTf.getText().trim();
            String password = passwordPf.getText().trim();
            String phoneNumber = phoneTf.getText().trim();
            String address = addressTf.getText().trim();
            String role = roleCb.getValue();

            if (username.isEmpty() || password.isEmpty() || phoneNumber.isEmpty() || address.isEmpty()) {
                errorLbl.setText("All fields must be filled.");
                return;
            }

            if (role == null) {
                errorLbl.setText("Role must be selected.");
                return;
            }

            User newUser = new User(0, username, null, phoneNumber, address, role);
            Map<String, String> errors = userController.registerUser(newUser, password);

            if (errors.isEmpty()) {
                errorLbl.setStyle("-fx-text-fill: green;");
                errorLbl.setText("Registration Successful!");

                usernameTf.clear();
                passwordPf.clear();
                phoneTf.clear();
                addressTf.clear();
                roleCb.setValue(null);
            } else {
                errorLbl.setStyle("-fx-text-fill: red;");
                StringBuilder errorMsg = new StringBuilder();
                errors.forEach((key, value) -> errorMsg.append(value).append("\n"));
                errorLbl.setText(errorMsg.toString());
            }
        });

        loginLink.setOnAction(e -> {
            try {
                new LoginView().start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
                errorLbl.setText("Failed to navigate to login page.");
            }
        });

        StackPane root = new StackPane(layout);
        Scene scene = new Scene(root, 600, 500);
        scene.setFill(javafx.scene.paint.Color.LIGHTSKYBLUE);

        primaryStage.setTitle("Register Page");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
