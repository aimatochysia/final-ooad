package model;

public class User {
	private int userID;
	private String role;
	private String username;
	private String password;
	private String phoneNumber;
	private String address;

	public User(int userID, String role, String username, String password, String phoneNumber, String address) {
		super();
		this.userID = userID;
		this.role = role;
		this.username = username;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.address = address;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
