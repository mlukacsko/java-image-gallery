package edu.au.cc.gallery;

import java.sql.SQLException;

public class Postgres {
	public static UserDAO getUserDAO() throws SQLException { return new PostgresUserDAO(); }
}
