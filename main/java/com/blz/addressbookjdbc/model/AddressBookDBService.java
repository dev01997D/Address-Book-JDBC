package com.blz.addressbookjdbc.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressBookDBService {
	private static AddressBookDBService addressBookDBServiceObj;
	private PreparedStatement preparedStmt = null;

	public static AddressBookDBService getInstance() {
		if (addressBookDBServiceObj == null)
			addressBookDBServiceObj = new AddressBookDBService();
		return addressBookDBServiceObj;
	}

	// Reading all the Contact data from the DB
	public List<Contact> readData() throws AddressBookCustomException {
		String sql = "SELECT c.*, ad.Type FROM Contact c RIGHT JOIN address_book_dict ad using (Address_Book_Name);";
		return this.executeSQLAndReturnContactList(sql);
	}

	// Execute SQL statement, operate on resultSet and return Contact list
	public List<Contact> executeSQLAndReturnContactList(String sql) throws AddressBookCustomException {
		List<Contact> contactList = new ArrayList<>();
		try (Connection con = getConnection();) {
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
				String type = resultSet.getString("Type");
				contactList.add(new Contact(name, address, city, phone, email, addressBookName, type));
			}
		} catch (SQLException e) {
			throw new AddressBookCustomException("Unable to read data Contact data from DB");
		}
		return contactList;
	}

	public int updateContactDB(String name, String city) throws AddressBookCustomException {
		return this.updateContactDataUsingPreparedStatement(name, city);
	}

	private int updateContactDataUsingPreparedStatement(String name, String city) throws AddressBookCustomException {
		String sql = "UPDATE Contact set city =? where name =?";
		if (preparedStmt == null)
			preparedStatementForContactData(sql);
		int noOfRowsAffected = 0;
		try {
			preparedStmt.setString(1, city);
			preparedStmt.setString(2, name);
			noOfRowsAffected = preparedStmt.executeUpdate();
		} catch (SQLException e) {
			throw new AddressBookCustomException("Unable to fetch data from Database!!");
		}
		preparedStmt = null;
		return noOfRowsAffected;
	}

	public synchronized Contact addContactDB(String name, String address, String city, long phoneNo, String email,
			LocalDate startDate, String addressBookName, String addressBookType) throws AddressBookCustomException {
		Connection connection = null;
		Contact contact = null;
		try {
			connection = getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try (Statement statement = connection.createStatement();) {
			String sql = String.format(
					"INSERT INTO Contact (Name, Address, city, PhoneNumber, Email, start_Date, Address_Book_Name) values ('%s','%s','%s','%s','%s','%s','%s')",
					name, address, city, phoneNo, email, Date.valueOf(startDate), addressBookName);
			int rowAffected = statement.executeUpdate(sql);
			if (rowAffected == 1) {
				contact = new Contact(name, address, city, phoneNo, email, startDate, addressBookName, addressBookType);
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e.printStackTrace();
			}
			throw new AddressBookCustomException("Unable to insert into Contact table");
		}

		try {
			connection.commit();
		} catch (SQLException e) {
			throw new AddressBookCustomException("Unable to commit for adding new contact to DB");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return contact;
	}

	public List<Contact> getContact(String name) throws AddressBookCustomException {
		List<Contact> contactList = null;
		String sql = "SELECT c.*, ad.Type FROM Contact c RIGHT JOIN address_book_dict ad using (Address_Book_Name) where name =?;";
		if (this.preparedStmt == null)
			this.preparedStatementForContactData(sql);
		try {
			preparedStmt.setString(1, name);
			ResultSet resultSet = preparedStmt.executeQuery();
			contactList = this.getContactDBData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		preparedStmt = null;
		return contactList;
	}

	public List<Contact> readContactForGivenDateRangeFromDB(LocalDate startDate, LocalDate endDate)
			throws AddressBookCustomException {
		String sql = String.format(
				"SELECT c.*, ad.Type FROM Contact c RIGHT JOIN address_book_dict ad using (Address_Book_Name) WHERE start_Date BETWEEN  '%s'  and '%s';",
				Date.valueOf(startDate), Date.valueOf(endDate));
		return this.executeSQLAndReturnContactList(sql);
	}

	public Map<String, Integer> getContactCountByCityFromDB() throws AddressBookCustomException {
		String sql = "SELECT City, count(city) from contact group by city;";
		String operation = "count(city)";
		return this.executeSQLAndReturnMap(sql, operation);
	}

	private Map<String, Integer> executeSQLAndReturnMap(String sql, String operation)
			throws AddressBookCustomException {
		Map<String, Integer> contactCountByCityMap = new HashMap<>();
		try (Connection con = getConnection()) {
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);
			while (resultSet.next()) {
				String city = resultSet.getString("city");
				int count = resultSet.getInt(operation);
				contactCountByCityMap.put(city, count);
			}
		} catch (SQLException e) {
			throw new AddressBookCustomException("Unable to execute query of function on salary");
		}
		return contactCountByCityMap;
	}

	// Use of prepared statement to get employee data from DB
	private void preparedStatementForContactData(String sql) throws AddressBookCustomException {
		try {
			Connection con = getConnection();
			preparedStmt = con.prepareStatement(sql);
		} catch (SQLException e) {
			throw new AddressBookCustomException("Error!! during prepared statemennt");
		}
	}

	// Loading Driver and getting connection object
	private synchronized static Connection getConnection() throws AddressBookCustomException {
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
