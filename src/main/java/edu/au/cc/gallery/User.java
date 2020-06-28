package edu.au.cc.gallery;

public class User {
	private String username;
	private String password;
	private String fullName;

	public User(String username, String password, String fullName) {
		this.username = username;
		this.password = password;
		this.fullName = fullName;
	}

	public String getUsername() { return username; }
	public void setUsername(String u) { username = u; }
	public String getPassword() { return password; }
	public void setPassword(String p) { password = p; }
	public String getFullName() { return fullName; }
	public void setFullName(String fn) { fullName = fn; }

	@Override
	public String toString() {
		return "Username: " + username + " password: " + password +" fullname: " + fullName;
	}
}
