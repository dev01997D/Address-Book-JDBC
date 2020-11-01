package com.blz.addressbookjdbc.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcConnection {
	// Loading Driver and getting connection object
		public Connection getConnection() throws AddressBookCustomException, SQLException {
			String jdbcURL = "jdbc:mysql://localhost:3306/address_book_service";
			String userName = "root";
			String password = "Kumar@12345";
			Connection con;

			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				throw new AddressBookCustomException("Error!!! Unable to load the driver");
			}

			try {
				con = DriverManager.getConnection(jdbcURL, userName, password);
			} catch (SQLException e) {
				throw new AddressBookCustomException("Error!!! Unable to establish the Connection with JDBC");
			}
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select * from contact");
			while (rs.next()) {
				System.out.println(rs.getString(1) + "\t" + rs.getString(2));
			}
			return con;
		}
		
}
