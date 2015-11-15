package org.tudresden.ecatering;

import static org.salespointframework.core.Currencies.*;

import java.time.LocalDateTime;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.tudresden.ecatering.inventory.Ingredient;


@Component
public class ECateringDataInitializer implements DataInitializer {

	private final Inventory<Ingredient> inventory;
	private final UserAccountManager userAccountManager;

	@Autowired
	public ECateringDataInitializer(Inventory<Ingredient> inventory,
			UserAccountManager userAccountManager) {

		Assert.notNull(inventory, "Inventory must not be null!");
		Assert.notNull(userAccountManager, "UserAccountManager must not be null!");

		this.inventory = inventory;
		this.userAccountManager = userAccountManager;
	}
	
	@Override
	public void initialize() {

		initializeUsers(userAccountManager);
		initializeInventory(inventory);

	}

	private void initializeInventory(Inventory<Ingredient> inventory) {

		Product product1 = new Product("Quark",Money.of(0.79, EURO));
		Quantity menge = Quantity.of(1);
		Ingredient in1 = new Ingredient(product1,menge,LocalDateTime.of(2015, 12, 24, 0, 0));
		inventory.save(in1);
		Product product2 = new Product("Jagdwurst",Money.of(1.45, EURO));
		Ingredient in2 = new Ingredient(product2,menge,LocalDateTime.of(2015, 11, 30, 0, 0));
		inventory.save(in2);
	
		
	}

	private void initializeUsers(UserAccountManager userAccountManager) {

		
		if (userAccountManager.findByUsername("koch").isPresent()) {
			return;
		}

		UserAccount ua1 = userAccountManager.create("koch", "123", new Role("ROLE_KITCHEN"));
		userAccountManager.save(ua1);

	}
}
