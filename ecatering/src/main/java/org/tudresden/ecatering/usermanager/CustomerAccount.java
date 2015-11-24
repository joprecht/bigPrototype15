package org.tudresden.ecatering.usermanager;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;

import org.joda.time.LocalDate;
import org.salespointframework.useraccount.UserAccount;
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class CustomerAccount  {
	private @Id @GeneratedValue Long id;
	private String businessIdentifier;
	private Date expirationDate;
	@OneToOne
	private UserAccount userAccount;
	public CustomerAccount(String businessIdentifier, Date expirationDate) {
		super();
		this.businessIdentifier = businessIdentifier;
		this.expirationDate = expirationDate;
	}
	public String getBusinessIdentifier() {
		return businessIdentifier;
	}
	public Date getExpirationDate() {
		return expirationDate;
	}

	public boolean isExpired(){
		LocalDate now = new LocalDate();
		return expirationDate.before(now.toDate());
	}
}