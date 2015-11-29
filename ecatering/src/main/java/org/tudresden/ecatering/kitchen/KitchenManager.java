package org.tudresden.ecatering.kitchen;

import java.util.List;
import java.util.Optional;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.quantity.Quantity;
import org.tudresden.ecatering.kitchen.MealType;
import org.tudresden.ecatering.stock.Ingredient;

public class KitchenManager {
	
	private MealRepository meals;
	private RecipeRepository recipes;
	private MenuRepository menus;

	
public KitchenManager(MealRepository meals, RecipeRepository recipes, MenuRepository menus) {
		
		this.meals = meals;
		this.recipes = recipes;
		this.menus = menus;

}

public Iterable<Meal> findAllMeals() {
	
	return this.meals.findAll();
}

public Iterable<Meal> findMealsByName(String name) {
	
	return this.meals.findByName(name);
}

public Iterable<Meal> findMealsByMealType(MealType type) {
	
	
	return this.meals.findByType(type);
}

public Optional<Meal> findMealByIdentifier(ProductIdentifier identifier) {
	
	return this.meals.findOne(identifier);
}

public Iterable<Recipe> findAllRecipes() {
	
	return this.recipes.findAll();
}

public Optional<Recipe> findRecipeByMealIdentifier(ProductIdentifier mealID) {
	
	return this.recipes.findByMealID(mealID);
	
}

public Iterable<Menu> findAllMenus() { 
	
	return this.menus.findAll();
}

public Optional<Menu> findMenuOfCalendarWeek(int calendarWeek) {
	
	return this.menus.findByCalendarWeek(calendarWeek);
}

public Ingredient createIngredient(String name,Quantity quantity) {
	Ingredient ingredient = new Ingredient(name,quantity);
	return ingredient;
}

public Recipe createRecipe(String description,List<Ingredient> ingredients, ProductIdentifier mealID) {
	
	Optional<Recipe> recipeExist = this.findRecipeByMealIdentifier(mealID);
	Optional<Meal> mealExist = this.findMealByIdentifier(mealID);
	
	if(!mealExist.isPresent())
	throw new IllegalArgumentException ( "Meal for recipe doesnt exist!" ) ;
	
	if(recipeExist.isPresent())
		throw new IllegalArgumentException ( "Recipe for this mealID already exists!" ) ;

	Recipe recipe = new Recipe(description, ingredients, mealID);
	return recipe;
}



public Meal createMeal(String name, Money price, MealType type ) {
	
	Meal meal = new Meal(name,price,type);
	
	return meal;	
}

public DailyMenu createDailyMenu(Day day, List<Meal> dailyMeals)
{
	DailyMenu dailyMenu = new DailyMenu(day, dailyMeals);
	
	return dailyMenu;
}

public Menu createMenu(int calendarWeek, List<DailyMenu> dailyMenus)
{
	if(this.findMenuOfCalendarWeek(calendarWeek).isPresent())
		throw new IllegalArgumentException("Menu for this calendarWeek already exists!");
	
	Menu menu = new Menu(calendarWeek, dailyMenus);
	return menu;
}

public Menu saveMenu(Menu menu) {
	
	return this.menus.save(menu);
}

public Meal saveMeal(Meal meal) {
	
	return this.meals.save(meal);
}

public Recipe saveRecipe(Recipe recipe) {
	
	return this.recipes.save(recipe);
}


}
