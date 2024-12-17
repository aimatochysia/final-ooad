package controller;

import database.DatabaseConnector;
import model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserController {
	private static UserController instance;
	private final DatabaseConnector db;

	private UserController() {
		db = DatabaseConnector.getInstance();
	}

	public static UserController getInstance() {
		if (instance == null) {
			instance = new UserController();
		}
		return instance;
	}

	public Map<String, String> registerUser(User user, String password) {
		Map<String, String> validationErrors = validateRegistration(user, password);

		if (!validationErrors.isEmpty()) {
			return validationErrors;
		}

		try {
			String query = String.format(
					"INSERT INTO Users (Role, Username, Password, PhoneNumber, Address) "
							+ "VALUES ('%s', '%s', '%s', '%s', '%s')",
				user.getRole(), user.getUsername(), password, user.getPhoneNumber(), user.getAddress());
			db.execute(query);
			System.out.println("User succesfully registered.");
		} catch (Exception e) {
			validationErrors.put("database", "There is an error occurred during registration.");
			e.printStackTrace();
		}

		return validationErrors;
	}

	public User loginUser(String username, String password) {
		if (username.equals("admin") && password.equals("admin")) {
			return new User(0, "admin", "", "", "", "Admin");
		}
		
		try {
			String query = String.format("SELECT * FROM Users WHERE Username = '%s'", username);
			ResultSet rs = db.execQuery(query);

			if (rs.next()) {
				String storedPassword = rs.getString("Password");
				if (storedPassword.equals(password)) {
					
					return new User(rs.getString("Role"), rs.getInt("UserID"), rs.getString("Username"), storedPassword, rs.getString("PhoneNumber"), rs.getString("Address"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	private Map<String, String> validateRegistration(User user, String password) {
		Map<String, String> errors = new HashMap<>();

		if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
			errors.put("username", "Username must be filled cannot be empty!");
		} else if (user.getUsername().length() < 3) {
			errors.put("username", "Username must contain at least 3 characters!");
		} else if (!isUsernameUnique(user.getUsername())) {
			errors.put("username", "The username has already been taken!");
		}

		if (password == null || password.trim().isEmpty()) {
			errors.put("password", "Password must be filled!");
		} else if (password.length() < 8 || !containsSpecialCharacter(password)) {
			errors.put("password", "Password must at least contains 8 characters and include special characters!");
		}

		if (user.getPhoneNumber() == null || !user.getPhoneNumber().startsWith("+62")
				|| user.getPhoneNumber().length() != 13) {
			errors.put("phoneNumber", "Phone number have to start with +62 and should be at least 10 digits long!");
		}

		if (user.getAddress() == null || user.getAddress().trim().isEmpty()) {
			errors.put("address", "Address must be filled!");
		}

		if (!user.getRole().equalsIgnoreCase("Seller") && !user.getRole().equalsIgnoreCase("Buyer")) {
			errors.put("role", "Role have to be 'Buyer' or 'Seller'.");
		}

		return errors;
	}

	private boolean isUsernameUnique(String username) {
		try {
			String query = String.format("SELECT * FROM Users WHERE Username = '%s'", username);
			ResultSet rs = db.execQuery(query);
			return !rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean containsSpecialCharacter(String password) {
		String specialChars = "!@#$%^&*";
		for (char c : password.toCharArray()) {
			if (specialChars.indexOf(c) != -1) {
				return true;
			}
		}
		return false;
	}
}
