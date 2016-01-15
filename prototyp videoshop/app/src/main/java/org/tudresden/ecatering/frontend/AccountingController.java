package org.tudresden.ecatering.frontend;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.tudresden.ecatering.model.accountancy.Address;
import org.tudresden.ecatering.model.accountancy.MealOrder;
import org.tudresden.ecatering.model.business.Business;
import org.tudresden.ecatering.model.business.BusinessManager;
import org.tudresden.ecatering.model.kitchen.DailyMenu;
import org.tudresden.ecatering.model.kitchen.Day;
import org.tudresden.ecatering.model.kitchen.Helping;
import org.tudresden.ecatering.model.kitchen.Ingredient;
import org.tudresden.ecatering.model.kitchen.KitchenManager;
import org.tudresden.ecatering.model.kitchen.MealType;
import org.tudresden.ecatering.model.kitchen.MenuItem;
import org.tudresden.ecatering.model.stock.Grocery;

@Controller
@PreAuthorize("hasRole('ROLE_ACCOUNTING')")
public class AccountingController {
	
	private final OrderManager<MealOrder> orderManager;
	private final BusinessManager businessManager;
	private final KitchenManager kitchenManager;
	
	@Autowired
	public AccountingController(OrderManager<MealOrder> orderManager, BusinessManager businessManager, KitchenManager kitchenManager){
		this.orderManager = orderManager;
		this.businessManager = businessManager;
		this.kitchenManager = kitchenManager;
	}
	
	
	@RequestMapping("/orders")
	public String orders(ModelMap modelMap) {

		modelMap.addAttribute("ordersCompleted", orderManager.findBy((OrderStatus.COMPLETED)));
		modelMap.addAttribute("ordersOpen", orderManager.findBy((OrderStatus.OPEN)));
		modelMap.addAttribute("ordersPaid", orderManager.findBy((OrderStatus.PAID)));



		return "orders";
	}
	
	@RequestMapping("/mealManagement")
	public String mealManagement(ModelMap modelMap) {

		modelMap.addAttribute("allMeals", kitchenManager.findAllMeals());
		modelMap.addAttribute("helping", Helping.REGULAR);
		modelMap.addAttribute("regular", MealType.REGULAR);
		modelMap.addAttribute("diet", MealType.DIET);
		modelMap.addAttribute("special", MealType.SPECIAL);
		modelMap.addAttribute("kitchenManager", kitchenManager);
		modelMap.addAttribute("unusedRecipes", kitchenManager.findUnusedRecipes());
		modelMap.addAttribute("menus", kitchenManager.findAllMenus());
		

		return "mealManagement";
	}
	
	@RequestMapping("/business")
	public String business(ModelMap modelMap) {

		modelMap.addAttribute("allBusinesses", businessManager.findAllBusinesses());
	

		return "business";
	}
	
	@RequestMapping("/retrieveVacantPositions")
	public String retrieveVacantPositions(ModelMap modelMap){
			OrderStatus o1 = OrderStatus.OPEN;
			modelMap.addAttribute("allVacantPostions",orderManager.findBy(o1));
		return "retrieveVacantPositions";
	}
	
	@RequestMapping(value = "/addMeal", method = RequestMethod.POST)
	public String addMeal(@RequestParam("recipe") String recipeName,
							@RequestParam("type") String type,
							@RequestParam("gainFactor") String gainFactor
							 ) 
	{
		try{
		kitchenManager.saveMeal(kitchenManager.createMeal(kitchenManager.findRecipeByName(recipeName).get(), MealType.valueOf(type), Double.valueOf(gainFactor)));
		}
		catch(Exception e)
		{
			System.out.println(e+"\n");
		}

		
		return "redirect:/mealManagement";
	}
	
	@RequestMapping(value = "/addMenu", method = RequestMethod.POST)
	public String addMenu(@RequestParam("calendarWeek") String calendarWeek,
							@RequestParam("type") String type,
							@RequestParam("mondayRegular") String mondayRegular,
							@RequestParam("mondayDiet") String mondayDiet,
							@RequestParam("mondaySpecial") String mondaySpecial,
							@RequestParam("tuesdayRegular") String tuesdayRegular,
							@RequestParam("tuesdayDiet") String tuesdayDiet,
							@RequestParam("tuesdaySpecial") String tuesdaySpecial,
							@RequestParam("wednesdayRegular") String wednesdayRegular,
							@RequestParam("wednesdayDiet") String wednesdayDiet,
							@RequestParam("wednesdaySpecial") String wednesdaySpecial,
							@RequestParam("thursdayRegular") String thursdayRegular,
							@RequestParam("thursdayDiet") String thursdayDiet,
							@RequestParam("thursdaySpecial") String thursdaySpecial,
							@RequestParam("fridayRegular") String fridayRegular,
							@RequestParam("fridayDiet") String fridayDiet,
							@RequestParam("fridaySpecial") String fridaySpecial
							 ) 
	{
		try{
			List<MenuItem> mondayMeals = new ArrayList<MenuItem>();
			mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(mondayRegular).get(),Helping.valueOf(type),Day.MONDAY));
			mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(mondaySpecial).get(),Helping.valueOf(type),Day.MONDAY));
			mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(mondayDiet).get(),Helping.valueOf(type),Day.MONDAY));

			List<MenuItem> tuesdayMeals = new ArrayList<MenuItem>();
			tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(tuesdayRegular).get(),Helping.valueOf(type),Day.TUESDAY));
			tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(tuesdaySpecial).get(),Helping.valueOf(type),Day.TUESDAY));
			tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(tuesdayDiet).get(),Helping.valueOf(type),Day.TUESDAY));
			
			List<MenuItem> wednesdayMeals = new ArrayList<MenuItem>();
			wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(wednesdayRegular).get(),Helping.valueOf(type),Day.WEDNESDAY));
			wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(wednesdaySpecial).get(),Helping.valueOf(type),Day.WEDNESDAY));
			wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(wednesdayDiet).get(),Helping.valueOf(type),Day.WEDNESDAY));

			List<MenuItem> thursdayMeals = new ArrayList<MenuItem>();
			thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(thursdayRegular).get(),Helping.valueOf(type),Day.THURSDAY));
			thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(thursdaySpecial).get(),Helping.valueOf(type),Day.THURSDAY));
			thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(thursdayDiet).get(),Helping.valueOf(type),Day.THURSDAY));
			
			List<MenuItem> fridayMeals = new ArrayList<MenuItem>();
			fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(fridayRegular).get(),Helping.valueOf(type),Day.FRIDAY));
			fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(fridaySpecial).get(),Helping.valueOf(type),Day.FRIDAY));
			fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealByName(fridayDiet).get(),Helping.valueOf(type),Day.FRIDAY));
				
			List<DailyMenu> dailyMenus = new ArrayList<DailyMenu>();
			dailyMenus.add(kitchenManager.createDailyMenu(mondayMeals));
			dailyMenus.add(kitchenManager.createDailyMenu(tuesdayMeals));
			dailyMenus.add(kitchenManager.createDailyMenu(wednesdayMeals));
			dailyMenus.add(kitchenManager.createDailyMenu(thursdayMeals));
			dailyMenus.add(kitchenManager.createDailyMenu(fridayMeals));

			kitchenManager.saveMenu(kitchenManager.createMenu(Integer.valueOf(calendarWeek), dailyMenus));
		}
		catch(Exception e)
		{
			System.out.println(e+"\n");
		}

		
		return "redirect:/mealManagement";
	}
	
	@RequestMapping(value = "/completeOrder", method = RequestMethod.POST)
	public String completeOrder(@RequestParam("OrderId") OrderIdentifier orderId){
		Optional<MealOrder> o1 = orderManager.get(orderId);
		orderManager.payOrder(o1.get());
		orderManager.completeOrder(o1.get());
		return "redirect:/retrieveVacantPositions";
	}
	

	
	@RequestMapping(value = "/addBusiness", method = RequestMethod.POST)
	public String addBusiness(@RequestParam("name") String name,
							  @RequestParam("type") String type,
							  @RequestParam("firstname") String firstname,
							  @RequestParam("lastname") String lastname,
							  @RequestParam("street") String streetname,
							  @RequestParam("streetnumber") String streetnumber,
							  @RequestParam("zip") String zip,
							  @RequestParam("city") String city,
							  @RequestParam("country") String country,
							  @RequestParam("membercode") String membercode,
							  @RequestParam(value = "institutioncode") String institutioncode){
		
		//country = "Germany";
		Address deliveryAddress = new Address(firstname,lastname,streetname,streetnumber,zip,city,country);
		
		try{
		if(type.equals("CHILDCARE")){
			System.out.println("Social");
			Business child = businessManager.createChildcareBusiness(name,deliveryAddress,membercode,institutioncode);
			businessManager.saveBusiness(child);
			
		}else if(type.equals("COMPANY")){
			System.out.println("Company");
			Business comp = businessManager.createCompanyBusiness(name,deliveryAddress,membercode);
			businessManager.saveBusiness(comp);
		}
		}catch(Exception e){
			System.out.println(e+"\n");

		}
		
		
		return "redirect:/business";
	}
	

	
	
}
