package org.tudresden.ecatering.frontend;

import static org.salespointframework.core.Currencies.EURO;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.tudresden.ecatering.model.kitchen.KitchenManager;
import org.tudresden.ecatering.model.kitchen.Meal;
import org.tudresden.ecatering.model.kitchen.MealRepository;
import org.tudresden.ecatering.model.kitchen.MealType;
import org.tudresden.ecatering.model.kitchen.MenuRepository;
import org.tudresden.ecatering.model.kitchen.RecipeRepository;
import org.tudresden.ecatering.model.stock.Ingredient;
import org.tudresden.ecatering.model.stock.IngredientRepository;
import org.tudresden.ecatering.model.stock.StockManager;
import org.javamoney.moneta.Money;
import org.salespointframework.quantity.Quantity;


@Controller
@PreAuthorize("hasRole('ROLE_KITCHEN')")
class KitchenController {

	private final StockManager stockManager;
	private final KitchenManager kitchenManager;

	@Autowired
	public KitchenController(IngredientRepository inventory, MealRepository mealRepo,RecipeRepository recipes, MenuRepository menus) {

		this.stockManager = new StockManager(inventory);
		this.kitchenManager = new KitchenManager(mealRepo, recipes, menus);
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
	public String addIngredient(@RequestParam("name") String name, @RequestParam("quantity") int quantity,@RequestParam("price") double price,@RequestParam("DD") int day,@RequestParam("MM") int month,@RequestParam("YYYY") int year) {
		//Actually create the Ingredient
		Quantity menge = Quantity.of(quantity);
		Ingredient ingredient = StockManager.createIngredient(name,Money.of(price, EURO),menge,LocalDate.of(year, month, day));
		//Need to find out how we can save the date right
		stockManager.saveIngredient(ingredient);
		return "createIngredient";
	}
	
	@RequestMapping("/createMeal")
	public String createMenue(ModelMap modelMap) {
		modelMap.addAttribute("allIngredients", stockManager.findAllIngredients());
		modelMap.addAttribute("allMeals", kitchenManager.findAllMeals());
		return "createMeal";
	}
	
	@RequestMapping(value = "/addMeal", method = RequestMethod.POST)
	public String addMenue(@RequestParam("name") String name, @RequestParam("price") double price, @RequestParam("type") String mealType){
		
		Meal m1 = KitchenManager.createMeal(name, Money.of(price, EURO), MealType.REGULAR);
		
		if(mealType.equals("Diet")){
			m1 = KitchenManager.createMeal(name, Money.of(price, EURO), MealType.DIET);
		}else if(mealType.equals("Special")){
			m1 = KitchenManager.createMeal(name, Money.of(price, EURO), MealType.SPECIAL);
		}
		
		kitchenManager.saveMeal(m1);
		
		return "redirect:/createMeal";
	}
	
	@RequestMapping("/listMeals")
	public String listMeals(ModelMap modelMap) {
		modelMap.addAttribute("allMeals", kitchenManager.findAllMeals());
		return "listMeals";
	}

}
