package com.blz.addressbookjdbc.model;

public class Contact {
	public String name;
	public String address;
	public String city;
	public long phoneNo;
	public String email;
	public String addressBookName;
	public String type;
	
	public Contact() {
		
	}

	public Contact(String name, String address, String city, long phoneNo, String email, String addressBookName, String type) {
		this.name = name;
		this.address = address;
		this.city = city;
		this.phoneNo = phoneNo;
		this.email = email;
		this.addressBookName = addressBookName;
		this.type=type;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contact other = (Contact) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (addressBookName == null) {
			if (other.addressBookName != null)
				return false;
		} else if (!addressBookName.equals(other.addressBookName))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (phoneNo != other.phoneNo)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Contact [name=" + name + ", address=" + address + ", city=" + city + ", phoneNo=" + phoneNo + ", email="
				+ email + ", addressBookName=" + addressBookName + ", Type=" + type + "]";
	}
	
	
}
