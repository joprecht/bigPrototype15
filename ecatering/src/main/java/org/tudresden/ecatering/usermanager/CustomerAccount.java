package org.tudresden.ecatering.usermanager;

import java.time.LocalDateTime;

import org.salespointframework.useraccount.UserAccount;
public class CustomerAccount extends UserAccount {
	private String businessIdentifier;
	private LocalDateTime expirationDate;
	public CustomerAccount(String businessIdentifier, LocalDateTime expirationDate) {
		super();
		this.businessIdentifier = businessIdentifier;
		this.expirationDate = expirationDate;
	}
	public String getBusinessIdentifier() {
		return businessIdentifier;
	}
	public LocalDateTime getExpirationDate() {
		return expirationDate;
	}
	public boolean isExpired(){
		return LocalDateTime.now().isBefore(expirationDate);
	}
}