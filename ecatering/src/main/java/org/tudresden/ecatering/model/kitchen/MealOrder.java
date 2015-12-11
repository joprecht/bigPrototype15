package org.tudresden.ecatering.model.kitchen;

import javax.persistence.Entity;

import org.salespointframework.order.*;
import org.tudresden.ecatering.model.accountancy.Address;
import org.tudresden.ecatering.model.business.BusinessType;
import org.tudresden.ecatering.model.customer.Customer;



public class MealOrder extends Order {
	
	
	//Attributen
	private Address inoviceAddress;
	private OrderType orderType;
	private BusinessType businessType;
	
	//Methoden
	public MealOrder(Customer customerAccount)
	{
		this.inoviceAddress = null;
		this.orderType = OrderType.SINGLE;
		this.businessType = BusinessType.COMPANY; //Solltet geändert werden(?)
	}

	public Address getInoviceAddress() {
		return inoviceAddress;
	}

	public void setInoviceAddress(Address inoviceAddress) {
		this.inoviceAddress = inoviceAddress;
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public BusinessType getBusinessType() {
		return businessType;
	}
		

}
