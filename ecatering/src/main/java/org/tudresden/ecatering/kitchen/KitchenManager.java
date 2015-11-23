package org.tudresden.ecatering.kitchen;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.ProductIdentifier;
import org.springframework.util.Assert;
import org.tudresden.ecatering.kitchen.MealType;
import org.tudresden.ecatering.stock.Ingredient;

public class KitchenManager {
	
	private MealRepository meals;
	private RecipeRepository recipes;
	
public KitchenManager(MealRepository meals, RecipeRepository recipes) {
		
		this.meals = meals;
		this.recipes = recipes;

}

public Iterable<Meal> findAllMeals() {
	
	return this.meals.findAll();
}

public Iterable<Meal> findMealsByName(String name) {
	
	return this.meals.findByName(name);
}

public Iterable<Meal> findMealsByMealType(MealType type) {
	
	Iterable<Meal> allMeals = this.findAllMeals();
	Iterator<Meal> iter = allMeals.iterator();
	
	while(iter.hasNext())
	{
		if(!iter.next().getMealType().equals(type))
			iter.remove();
	}
	
	return allMeals;
}

public Optional<Meal> findMealByIdentifier(ProductIdentifier identifier) {
	
	Iterable<Meal> allMeals = this.findAllMeals();
	Iterator<Meal> iter = allMeals.iterator();
	
	while(iter.hasNext())
	{
		if(iter.next().getIdentifier().equals(identifier))
		{
			return Optional.of(iter.next());
		}
	}
	
	return Optional.empty();
}

public Iterable<Recipe> findAllRecipes() {
	
	return this.recipes.findAll();
}

public Optional<Recipe> findRecipeByMealIdentifier(ProductIdentifier mealID) {
	Iterable<Recipe> allRecipes = this.findAllRecipes();
	Iterator<Recipe> iter = allRecipes.iterator();
	while(iter.hasNext())
	{
		if(iter.next().getMealID().equals(mealID))
		{
			return Optional.of(iter.next());
		}
		
	}
	
	return Optional.empty();
	
}

public Recipe createRecipe(String description,List<Ingredient> ingredients, ProductIdentifier mealID) {
	
	
	Assert.isTrue(!this.findRecipeByMealIdentifier(mealID).equals(Optional.empty()), "Recipe for this meal already exists");
	Assert.isTrue(!this.findMealByIdentifier(mealID).equals(Optional.empty()), "Meal doesnt exist");
	Recipe recipe = new Recipe(description, ingredients, mealID);
	return recipe;
}



public Meal createMeal(String name, Money price, MealType type ) {
	
	Meal meal = new Meal(name,price,type);
	
	return meal;	
}

public Meal saveMeal(Meal meal) {
	
	return this.meals.save(meal);
}

public Recipe saveRecipe(Recipe recipe) {
	
	return this.recipes.save(recipe);
}


}
