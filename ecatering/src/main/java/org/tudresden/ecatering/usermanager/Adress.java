package org.tudresden.ecatering.usermanager;

import javax.persistence.Entity;

@Entity
public class Adress {
	
	private String firstname;
	private String lastname;
	private String street;
	private int streetNumber;
	private int postalCode;
	private String country;
	
	
	public Adress(String firstname, String lastname, String street, int streetNumber, int postalCode, String country) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
		this.street = street;
		this.streetNumber = streetNumber;
		this.postalCode = postalCode;
		this.country = country;
	}
	public String getFirstname() {
		return firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public String getStreet() {
		return street;
	}
	public int getStreetNumber() {
		return streetNumber;
	}
	public int getPostalCode() {
		return postalCode;
	}
	public String getCountry() {
		return country;
	}	
}