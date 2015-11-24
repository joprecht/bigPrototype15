package org.tudresden.ecatering.kitchen;

import java.util.*;

import javax.persistence.*;

@Entity
public class DailyMenu {
	
	@Id @GeneratedValue
	private Long id;

	@ManyToOne
	private Menu menu;
	//Attributen
	@OneToMany
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
