package controller;

import database.DatabaseConnector;
import model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserController {

    private static UserController singletonInstance;
    private final DatabaseConnector databaseConnector;

    private UserController() {
        this.databaseConnector = DatabaseConnector.getInstance();
    }

    public static UserController getInstance() {
        if (singletonInstance == null) {
            synchronized (UserController.class) {
                if (singletonInstance == null) {
                    singletonInstance = new UserController();
                }
            }
        }
        return singletonInstance;
    }

    public Map<String, String> registerUser(User user, String password) {
        Map<String, String> errors = performValidation(user, password);

        if (!errors.isEmpty()) {
            return errors;
        }

        try {
            String sql = String.format(
                    "INSERT INTO Users (Role, Username, Password, PhoneNumber, Address) VALUES ('%s', '%s', '%s', '%s', '%s')",
                    user.getRole(), user.getUsername(), password, user.getPhoneNumber(), user.getAddress());
            databaseConnector.execute(sql);
            System.out.println("User successfully added to the database.");
        } catch (Exception ex) {
            errors.put("database", "An error occurred while registering the user.");
            ex.printStackTrace();
        }

        return errors;
    }

    public User loginUser(String username, String password) {
        if ("admin".equals(username) && "admin".equals(password)) {
            return new User(0, "admin", "", "", "", "Admin");
        }

        try {
            String sql = String.format("SELECT * FROM Users WHERE Username = '%s'", username);
            ResultSet resultSet = databaseConnector.execQuery(sql);

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("Password");
                if (storedPassword.equals(password)) {
                    return new User(
                            resultSet.getInt("UserID"),
                            resultSet.getString("Role"),
                            resultSet.getString("Username"),
                            storedPassword,
                            resultSet.getString("PhoneNumber"),
                            resultSet.getString("Address"));
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return null;
    }

    private Map<String, String> performValidation(User user, String password) {
        Map<String, String> validationErrors = new HashMap<>();

        validateUsername(user.getUsername(), validationErrors);
        validatePassword(password, validationErrors);
        validatePhoneNumber(user.getPhoneNumber(), validationErrors);
        validateAddress(user.getAddress(), validationErrors);
        validateRole(user.getRole(), validationErrors);

        return validationErrors;
    }

    private void validateUsername(String username, Map<String, String> errors) {
        if (username == null || username.trim().isEmpty()) {
            errors.put("username", "Username cannot be empty.");
        } else if (username.length() < 3) {
            errors.put("username", "Username must be at least 3 characters long.");
        } else if (!isUniqueUsername(username)) {
            errors.put("username", "This username is already taken.");
        }
    }

    private void validatePassword(String password, Map<String, String> errors) {
        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "Password cannot be empty.");
        } else if (password.length() < 8 || !hasSpecialCharacter(password)) {
            errors.put("password", "Password must be at least 8 characters long and contain at least one special character.");
        }
    }

    private void validatePhoneNumber(String phoneNumber, Map<String, String> errors) {
        if (phoneNumber == null || !phoneNumber.startsWith("+62") || phoneNumber.length() != 13) {
            errors.put("phoneNumber", "Phone number must start with +62 and be 13 digits long.");
        }
    }

    private void validateAddress(String address, Map<String, String> errors) {
        if (address == null || address.trim().isEmpty()) {
            errors.put("address", "Address cannot be empty.");
        }
    }

    private void validateRole(String role, Map<String, String> errors) {
        if (!"Seller".equalsIgnoreCase(role) && !"Buyer".equalsIgnoreCase(role)) {
            errors.put("role", "Role must be either 'Seller' or 'Buyer'.");
        }
    }

    private boolean isUniqueUsername(String username) {
        try {
            String sql = String.format("SELECT * FROM Users WHERE Username = '%s'", username);
            ResultSet resultSet = databaseConnector.execQuery(sql);
            return !resultSet.next();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    private boolean hasSpecialCharacter(String password) {
        String specialCharacters = "!@#$%^&*";
        for (char character : password.toCharArray()) {
            if (specialCharacters.indexOf(character) >= 0) {
                return true;
            }
        }
        return false;
    }
}
