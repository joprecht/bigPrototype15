package org.tudresden.ecatering.frontend;

import static org.salespointframework.core.Currencies.EURO;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Quantity;
import org.tudresden.ecatering.kitchen.Ingredient;
import org.tudresden.ecatering.kitchen.IngredientRepository;
import org.tudresden.ecatering.kitchen.StockManager;


@Controller
@PreAuthorize("hasRole('ROLE_KITCHEN')")
class KitchenController {

	private final StockManager stockManager;

	@Autowired
	public KitchenController(IngredientRepository inventory) {

		this.stockManager = new StockManager(inventory);
	}


	@RequestMapping("/listIngredients")
	public String kitchenData(ModelMap modelMap) {

		modelMap.addAttribute("allIngredients", stockManager.findAllIngredients());

		return "listIngredients";
	}
	
	@RequestMapping("/createIngredient")
	public String createIngredient() {
		return "createIngredient";
	}
	
	@RequestMapping(value = "/addIngredient", method = RequestMethod.POST)
	public String comment(@RequestParam("name") String name, @RequestParam("quantity") int quantity,@RequestParam("price") double price,@RequestParam("date") String date) {
		//Actually create the Ingredient
		Product product = new Product(name,Money.of(price, EURO));
		Quantity menge = Quantity.of(quantity);
		Ingredient ingredient = stockManager.createIngredient(product,menge,LocalDateTime.of(2015, 12, 24, 0, 0));
		//Need to find out how we can save the date right
		stockManager.saveIngredient(ingredient);
		return "createIngredient";
	}

}
