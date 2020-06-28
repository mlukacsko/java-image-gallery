package edu.au.cc.gallery;

import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;

import java.sql.SQLException;


public class PostgresUserDAO implements UserDAO {
	private DB connection;

	public PostgresUserDAO() throws SQLException{
		connection = new DB();
		connection.connect();
	}

	public List<User> getUsers() throws SQLException {
		List<User> result = new ArrayList<>();
		ResultSet rs = connection.execute("Select username, password, full_name from users");
		while (rs.next()) {
			result.add(new User(rs.getString(1), rs.getString(2), rs.getString(3)));
		}
		rs.close();
		return result;
	}

	public void addUser(User u) throws SQLException {
		connection.execute("insert into users(username,password,full_name) values (?,?,?)",
		new String[] {u.getUsername(), u.getPassword(), u.getFullName()});
	}

	public void deleteUser(String username) throws SQLException {
		connection.execute("delete from users where username = ?", new String[] {username});
	}

}
