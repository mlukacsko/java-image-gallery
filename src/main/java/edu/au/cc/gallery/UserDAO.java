package edu.au.cc.gallery;

import java.util.List;

public interface UserDAO {
	List<User> getUsers() throws Exception;
	void addUser(User u) throws Exception;
	void deleteUser(String username) throws Exception;

}


