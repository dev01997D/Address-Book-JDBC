package com.blz.addressbookjdbc.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AddressBookDBService {
	private static AddressBookDBService addressBookDBServiceObj;

	public static AddressBookDBService getInstance() {
		if (addressBookDBServiceObj == null)
			addressBookDBServiceObj = new AddressBookDBService();
		return addressBookDBServiceObj;
	}

	// Reading all the Contact data from the DB
	public List<Contact> readData() throws AddressBookCustomException {
		String sql = "SELECT * FROM Contact;";
		return this.executeSQLAndReturnContactList(sql);
	}

	// Execute SQL statement, operate on resultSet and return Contact list
	public List<Contact> executeSQLAndReturnContactList(String sql) throws AddressBookCustomException {
		List<Contact> contactList = new ArrayList<>();
		try (Connection con = addressBookDBServiceObj.getConnection();) {
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);
			contactList = this.getContactDBData(resultSet);
		} catch (SQLException e) {
			throw new AddressBookCustomException("Unable to execute SQL query!!");
		}
		return contactList;
	}

	private List<Contact> getContactDBData(ResultSet resultSet) throws AddressBookCustomException {
		List<Contact> contactList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				String name = resultSet.getString("Name");
				String address = resultSet.getString("Address");
				String city = resultSet.getString("city");
				Long phone = resultSet.getLong("PhoneNumber");
				String email = resultSet.getString("Email");
				String addressBookName = resultSet.getString("Address_Book_Name");
				contactList.add(new Contact(name, address, city, phone, email, addressBookName));
			}
		} catch (SQLException e) {
			throw new AddressBookCustomException("Unable to read data Contact data from DB");
		}
		return contactList;
	}

	// Loading Driver and getting connection object
	private Connection getConnection() throws AddressBookCustomException {
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
		return con;
	}
}
