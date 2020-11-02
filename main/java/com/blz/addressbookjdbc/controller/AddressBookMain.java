/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.blz.addressbookjdbc.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.blz.addressbookjdbc.model.AddressBookCustomException;
import com.blz.addressbookjdbc.model.AddressBookDBService;
import com.blz.addressbookjdbc.model.Contact;

public class AddressBookMain {
	private List<Contact> contactList;
	private AddressBookDBService addressBookDBServiceObj;
	private Map<String, Integer> contactByCityMap;

	public AddressBookMain() {
		addressBookDBServiceObj = AddressBookDBService.getInstance();
	}

	public AddressBookMain(List<Contact> contactList) {
		this();
		this.contactList = contactList;
	}
	
	public AddressBookMain(Map<String, Integer> contactByCityMap) {
		this();
		this.contactByCityMap=contactByCityMap;
	}

	public List<Contact> readContactData() throws AddressBookCustomException {
		this.contactList=addressBookDBServiceObj.readData();
		return  this.contactList;
	}

	public void updateContactDetails(String name, String city) throws AddressBookCustomException {
		int noOfRowsAffected=addressBookDBServiceObj.updateContactDB(name, city);
		if (noOfRowsAffected == 0) {
			return;
		}
		Contact contact = this.getContact(name);
		if (contact != null)
			contact.city = city;
	}

	private Contact getContact(String name) {
		return contactList.stream().filter(contact->contact.name.equals(name)).findFirst().orElse(null);
	}

	public boolean checkContactInSyncWithDB(String name) throws AddressBookCustomException {
		List<Contact> contactDataList = addressBookDBServiceObj.getContact(name);
		System.out.println(contactDataList);
		return contactDataList.get(0).equals(getContact(name));
	}

	public List<Contact> readContactsForGivenDateRange(LocalDate startDate, LocalDate endDate) throws AddressBookCustomException {
		return addressBookDBServiceObj.readContactForGivenDateRangeFromDB(startDate, endDate);
	}

	public Map<String, Integer> readCountOfContactsByCity() throws AddressBookCustomException {
		contactByCityMap=addressBookDBServiceObj.getContactCountByCityFromDB();
		return contactByCityMap;
	}
}
