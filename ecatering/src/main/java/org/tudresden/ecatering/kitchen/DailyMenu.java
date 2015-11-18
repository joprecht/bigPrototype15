package org.tudresden.ecatering.kitchen;

import java.util.*;

public class DailyMenu {

	//Attributen
	private List<Meal> dailyMeals;
	private Day day;
	
	//Methoden
	public DailyMenu(Day day, List<Meal> dailyMeals)
	{
		this.day = day;
		this.dailyMeals = dailyMeals;
	}
	
	public Day getDay()
	{
		return day;
	}
	
	public List<Meal> getDailyMeals()
	{
		
		return dailyMeals;
	}
}
