/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.blz.addressbookjdbc.controller;

import java.util.List;

import com.blz.addressbookjdbc.model.AddressBookCustomException;
import com.blz.addressbookjdbc.model.AddressBookDBService;
import com.blz.addressbookjdbc.model.Contact;

public class AddressBookMain {
	private List<Contact> contactList;
	private AddressBookDBService addressBookDBServiceObj;

	public AddressBookMain() {
		addressBookDBServiceObj = AddressBookDBService.getInstance();
	}

	public AddressBookMain(List<Contact> contactList) {
		this();
		this.contactList = contactList;
	}

	public List<Contact> readContactData() throws AddressBookCustomException {
		this.contactList=addressBookDBServiceObj.readData();
		System.out.println(this.contactList);
		return  this.contactList;
	}
}
