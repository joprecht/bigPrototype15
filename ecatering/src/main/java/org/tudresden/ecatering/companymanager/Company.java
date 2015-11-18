package org.tudresden.ecatering.companymanager;

import org.tudresden.ecatering.usermanager.Address;

public class Company {
	
	private String name;
	private Address deliveryAddress;
	private String memberCode;
	
	
	public Company(String name, Address deliveryAddress, String memberCode) {
		super();
		this.name = name;
		this.deliveryAddress = deliveryAddress;
		this.memberCode = memberCode;
	}


	public String getName() {
		return name;
	}

	public Address getDeliveryAddress() {
		return deliveryAddress;
	}

	public String getMemberCode() {
		return memberCode;
	}


	public void setName(String name) {
		this.name = name;
	}

	public void setDeliveryAddress(Address deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
}