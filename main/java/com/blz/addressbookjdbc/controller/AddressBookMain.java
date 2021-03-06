/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.blz.addressbookjdbc.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.blz.addressbookjdbc.model.AddressBookCustomException;
import com.blz.addressbookjdbc.model.AddressBookDBService;
import com.blz.addressbookjdbc.model.Contact;

public class AddressBookMain {
	private static Logger log=Logger.getLogger(AddressBookMain.class.getName());
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
		this.contactByCityMap = contactByCityMap;
	}

	public List<Contact> readContactData() throws AddressBookCustomException {
		this.contactList = addressBookDBServiceObj.readData();
		return this.contactList;
	}

	public void updateContactDetails(String name, String city) throws AddressBookCustomException {
		int noOfRowsAffected = addressBookDBServiceObj.updateContactDB(name, city);
		if (noOfRowsAffected == 0) {
			return;
		}
		Contact contact = this.getContact(name);
		if (contact != null)
			contact.city = city;
	}

	private Contact getContact(String name) {
		return contactList.stream().filter(contact -> contact.name.equals(name)).findFirst().orElse(null);
	}

	public boolean checkContactInSyncWithDB(String name) throws AddressBookCustomException {
		List<Contact> contactDataList = addressBookDBServiceObj.getContact(name);
		return contactDataList.get(0).equals(getContact(name));
	}

	public List<Contact> readContactsForGivenDateRange(LocalDate startDate, LocalDate endDate)
			throws AddressBookCustomException {
		return addressBookDBServiceObj.readContactForGivenDateRangeFromDB(startDate, endDate);
	}

	public Map<String, Integer> readCountOfContactsByCity() throws AddressBookCustomException {
		contactByCityMap = addressBookDBServiceObj.getContactCountByCityFromDB();
		return contactByCityMap;
	}

	public void addContactToAddressBookServiceDB(String name, String address, String city, long phoneNo, String email,
			LocalDate startDate, String addressBookName, String addressBookType) throws AddressBookCustomException {
		contactList.add(addressBookDBServiceObj.addContactDB(name, address, city, phoneNo, email, startDate,
				addressBookName, addressBookType));
	}

	public void addMultipleContact(List<Contact> contactList) {
		contactList.forEach(contactData -> {
			log.info("Employee being added : " + contactData.name);
			try {
				this.addContactToAddressBookServiceDB(contactData.name,  contactData.address, contactData.city, contactData.phoneNo, contactData.email,contactData.startDate, 
						contactData.addressBookName, contactData.type);
			} catch (AddressBookCustomException e) {
				e.printStackTrace();
			}
			log.info("Employee added : " + contactData.name);
		});
		log.info("" + this.contactList);
	}

	public long countEntries() {
		return contactList.size();
	}

	public void addEmployeeToPayrollWithThreads(List<Contact> contactList) {
		Map<Integer, Boolean> employeeAdditionStatus = new HashMap<>();
		contactList.forEach(contactData -> {
			Runnable task = () -> {
				employeeAdditionStatus.put(contactData.hashCode(), false);
				log.info("Employee being added : " + Thread.currentThread().getName());
				try {
					this.addContactToAddressBookServiceDB(contactData.name,  contactData.address, contactData.city, contactData.phoneNo, contactData.email,contactData.startDate, 
							contactData.addressBookName, contactData.type);
				} catch (AddressBookCustomException e) {
					e.printStackTrace();
				}
				employeeAdditionStatus.put(contactData.hashCode(), true);
				log.info("Employee added : " + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, contactData.name);
			thread.start();
		});
		while (employeeAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		log.info("" + this.contactList);
	}
}
