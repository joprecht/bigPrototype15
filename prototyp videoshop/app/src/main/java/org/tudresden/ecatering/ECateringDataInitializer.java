package org.tudresden.ecatering;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertThat;
import static org.salespointframework.core.Currencies.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.order.OrderManager;
import org.salespointframework.quantity.Metric;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.tudresden.ecatering.model.accountancy.Address;
import org.tudresden.ecatering.model.accountancy.MealOrder;
import org.tudresden.ecatering.model.business.BusinessManager;
import org.tudresden.ecatering.model.customer.CustomerManager;
import org.tudresden.ecatering.model.kitchen.DailyMenu;
import org.tudresden.ecatering.model.kitchen.Day;
import org.tudresden.ecatering.model.kitchen.Helping;
import org.tudresden.ecatering.model.kitchen.Ingredient;
import org.tudresden.ecatering.model.kitchen.KitchenManager;
import org.tudresden.ecatering.model.kitchen.MealType;
import org.tudresden.ecatering.model.kitchen.MenuItem;
import org.tudresden.ecatering.model.stock.Grocery;
import org.tudresden.ecatering.model.stock.StockManager;


@Component
public class ECateringDataInitializer implements DataInitializer {

	private final UserAccountManager userAccountManager;
	private final StockManager stockManager;
	private final KitchenManager kitchenManager;
	private final CustomerManager customerManager;
	private final BusinessManager businessManager;



	@Autowired
	public ECateringDataInitializer(StockManager stockManager, KitchenManager kitchenManager,
			UserAccountManager userAccountManager, CustomerManager customerManager, BusinessManager businessManager, OrderManager<MealOrder> orderManager) {

		Assert.notNull(stockManager, "StockManager must not be null!");
		Assert.notNull(kitchenManager, "KitchenManager must not be null!");
		Assert.notNull(userAccountManager, "UserAccountManager must not be null!");
		Assert.notNull(customerManager, "CustomerManager must not be null!");
		Assert.notNull(businessManager, "BusinessManager must not be null!");


		this.userAccountManager = userAccountManager;
		this.stockManager = stockManager;
		this.kitchenManager = kitchenManager;
		this.businessManager = businessManager;
		this.customerManager = customerManager;
	}
	
	@Override
	public void initialize() {

		initializeBusiness();
		initializeUsers();
		initializeStock();
		initializeKitchen();

	}
	
private void initializeBusiness() {
	
	Address address = new Address("Max","Mustermann","Musterstrasse","12","01307","Dresden","Deutschland");
	
	businessManager.saveBusiness(businessManager.createChildcareBusiness("Kindergarten Marienhof", address, "1111", "2222"));
	
Address address1 = new Address("Jochen","Tester","Teststrasse","13","01307","Dresden","Deutschland");
	
	businessManager.saveBusiness(businessManager.createCompanyBusiness("Druckerei Farbenfroh", address1, "3333"));
}
	
private void initializeUsers() {

		
		if (userAccountManager.findByUsername("koch").isPresent()) {
			return;
		}

		UserAccount ua0 = userAccountManager.create("boss", "123", Role.of("ROLE_ADMIN"),Role.of("ROLE_ACCOUNTING"));
		userAccountManager.save(ua0);
		UserAccount ua1 = userAccountManager.create("koch", "123", Role.of("ROLE_KITCHEN"));
		userAccountManager.save(ua1);
		UserAccount ua2 = userAccountManager.create("lager", "123", Role.of("ROLE_STOCK"));
		userAccountManager.save(ua2);
		
		//customer
		UserAccount ua3 = userAccountManager.create("kunde", "123", Role.of("ROLE_CUSTOMER"));
		ua3.setFirstname("Max");
		ua3.setLastname("Mustermann");
		ua3.setEmail("max@mustermann.de");
		userAccountManager.save(ua3);
		customerManager.saveCustomer(customerManager.createCustomer(ua3, "2222"));

	}
	
private void initializeStock() {
		
		//Lebensmittel werden hinzugefuegt
		Grocery sahne = stockManager.saveGrocery(stockManager.createGrocery("Sahne", Metric.LITER, Money.of(0.79, EURO)));
		stockManager.saveGrocery(stockManager.createGrocery("Schweinefleisch", Metric.KILOGRAM, Money.of(1.50, EURO)));
		stockManager.saveGrocery(stockManager.createGrocery("Fisch", Metric.KILOGRAM, Money.of(4.30, EURO)));
		stockManager.saveGrocery(stockManager.createGrocery("Bandnudeln", Metric.KILOGRAM, Money.of(0.50, EURO)));
		stockManager.saveGrocery(stockManager.createGrocery("Kartoffeln", Metric.KILOGRAM, Money.of(0.80, EURO)));

		
		//Sahne im Stock verfügbar
		stockManager.saveStockItem(stockManager.createStockItem(sahne, 0.525, LocalDate.of(2016, 12, 30)));
		stockManager.saveStockItem(stockManager.createStockItem(sahne, 1.325, LocalDate.of(2015, 12, 24)));

		
	}
	
private void initializeKitchen() {
		
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Sahne").get(), 0.200));
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Bandnudeln").get(), 0.100));

	
		kitchenManager.saveRecipe(kitchenManager.createRecipe("Nudeln special", "Es beginnt mit...", ingredients));
		
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Schweinefleisch").get(), 0.200));
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Bandnudeln").get(), 0.100));
		
		kitchenManager.saveRecipe(kitchenManager.createRecipe("Schweinefleisch mit Nudeln", "Hier wird zuerst...", ingredients));
		
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.300));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("Kartoffeln ohne allem", "Kartoffeln auf den Teller legen...", ingredients));
		
		//freies Rezept ohne Mahlzeit
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Kartoffeln").get(), 0.300));
		ingredients.add(kitchenManager.createIngredient(stockManager.findGroceryByName("Schweinefleisch").get(), 0.200));

		kitchenManager.saveRecipe(kitchenManager.createRecipe("Kartoffeln mit Schweinefleisch", "Zubereitung...", ingredients));
		
		
		//meals für rezepte
		
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Nudeln special").get(), MealType.SPECIAL, 1.3));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Schweinefleisch mit Nudeln").get(), MealType.REGULAR, 1.3));
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName("Kartoffeln ohne allem").get(), MealType.DIET, 1.3));

		
		
		//speiseplan für erwachsene und kinder 
		
		
		
		List<MenuItem> mondayMeals = new ArrayList<MenuItem>();
		mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.REGULAR,Day.MONDAY));
		mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.REGULAR,Day.MONDAY));
		mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.REGULAR,Day.MONDAY));

		List<MenuItem> tuesdayMeals = new ArrayList<MenuItem>();
		tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.REGULAR,Day.TUESDAY));
		tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.REGULAR,Day.TUESDAY));
		tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.REGULAR,Day.TUESDAY));
		
		List<MenuItem> wednesdayMeals = new ArrayList<MenuItem>();
		wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.REGULAR,Day.WEDNESDAY));
		wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.REGULAR,Day.WEDNESDAY));
		wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.REGULAR,Day.WEDNESDAY));

		List<MenuItem> thursdayMeals = new ArrayList<MenuItem>();
		thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.REGULAR,Day.THURSDAY));
		thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.REGULAR,Day.THURSDAY));
		thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.REGULAR,Day.THURSDAY));
		
		List<MenuItem> fridayMeals = new ArrayList<MenuItem>();
		fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.REGULAR,Day.FRIDAY));
		fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.REGULAR,Day.FRIDAY));
		fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.REGULAR,Day.FRIDAY));
			
		List<DailyMenu> dailyMenus = new ArrayList<DailyMenu>();
		dailyMenus.add(kitchenManager.createDailyMenu(mondayMeals));
		dailyMenus.add(kitchenManager.createDailyMenu(tuesdayMeals));
		dailyMenus.add(kitchenManager.createDailyMenu(wednesdayMeals));
		dailyMenus.add(kitchenManager.createDailyMenu(thursdayMeals));
		dailyMenus.add(kitchenManager.createDailyMenu(fridayMeals));

		kitchenManager.saveMenu(kitchenManager.createMenu(4, dailyMenus));
		
		
		//Es wird auch noch ein Kinder Speiseplan für diese Woche angeboten
		
		
	    mondayMeals = new ArrayList<MenuItem>();
		mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.SMALL,Day.MONDAY));
		mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.SMALL,Day.MONDAY));
		mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.SMALL,Day.MONDAY));

		tuesdayMeals = new ArrayList<MenuItem>();
		tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.SMALL,Day.TUESDAY));
		tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.SMALL,Day.TUESDAY));
		tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.SMALL,Day.TUESDAY));
		
		wednesdayMeals = new ArrayList<MenuItem>();
		wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.SMALL,Day.WEDNESDAY));
		wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.SMALL,Day.WEDNESDAY));
		wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.SMALL,Day.WEDNESDAY));

		thursdayMeals = new ArrayList<MenuItem>();
		thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.SMALL,Day.THURSDAY));
		thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.SMALL,Day.THURSDAY));
		thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.SMALL,Day.THURSDAY));
		
		fridayMeals = new ArrayList<MenuItem>();
		fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.SMALL,Day.FRIDAY));
		fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.SMALL,Day.FRIDAY));
		fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.SMALL,Day.FRIDAY));
			
		dailyMenus = new ArrayList<DailyMenu>();
		dailyMenus.add(kitchenManager.createDailyMenu(mondayMeals));
		dailyMenus.add(kitchenManager.createDailyMenu(tuesdayMeals));
		dailyMenus.add(kitchenManager.createDailyMenu(wednesdayMeals));
		dailyMenus.add(kitchenManager.createDailyMenu(thursdayMeals));
		dailyMenus.add(kitchenManager.createDailyMenu(fridayMeals));

		kitchenManager.saveMenu(kitchenManager.createMenu(4, dailyMenus));
		
		
		
		//weiterer plan für kinder
		
		mondayMeals = new ArrayList<MenuItem>();
		mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.SMALL,Day.MONDAY));
		mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.SMALL,Day.MONDAY));
		mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.SMALL,Day.MONDAY));

		tuesdayMeals = new ArrayList<MenuItem>();
		tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.SMALL,Day.TUESDAY));
		tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.SMALL,Day.TUESDAY));
		tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.SMALL,Day.TUESDAY));
		
		wednesdayMeals = new ArrayList<MenuItem>();
		wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.SMALL,Day.WEDNESDAY));
		wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.SMALL,Day.WEDNESDAY));
		wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.SMALL,Day.WEDNESDAY));

		thursdayMeals = new ArrayList<MenuItem>();
		thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.SMALL,Day.THURSDAY));
		thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.SMALL,Day.THURSDAY));
		thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.SMALL,Day.THURSDAY));
		
		fridayMeals = new ArrayList<MenuItem>();
		fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.SMALL,Day.FRIDAY));
		fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.SMALL,Day.FRIDAY));
		fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.SMALL,Day.FRIDAY));
			
		dailyMenus = new ArrayList<DailyMenu>();
		dailyMenus.add(kitchenManager.createDailyMenu(mondayMeals));
		dailyMenus.add(kitchenManager.createDailyMenu(tuesdayMeals));
		dailyMenus.add(kitchenManager.createDailyMenu(wednesdayMeals));
		dailyMenus.add(kitchenManager.createDailyMenu(thursdayMeals));
		dailyMenus.add(kitchenManager.createDailyMenu(fridayMeals));

		kitchenManager.saveMenu(kitchenManager.createMenu(5, dailyMenus));
		

	}
	

	


	

}

