package org.tudresden.ecatering.companymanager;

import org.tudresden.ecatering.usermanager.Address;

public class Childcare extends Company {
	
	private String institutionCode;

	public Childcare(String name, Address deliveryAddress, String memberCode, String institutionCode) {
		super(name, deliveryAddress, memberCode);
		this.institutionCode = institutionCode;
	}

	public String getInstitutionCode() {
		return institutionCode;
	}

	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}
	
}