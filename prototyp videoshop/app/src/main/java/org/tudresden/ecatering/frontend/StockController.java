package org.tudresden.ecatering.frontend;


import static org.salespointframework.core.Currencies.EURO;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Optional;

import org.javamoney.moneta.Money;
import org.salespointframework.quantity.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.tudresden.ecatering.model.stock.Grocery;
import org.tudresden.ecatering.model.stock.StockItem;
import org.tudresden.ecatering.model.stock.StockManager;


@Controller
@PreAuthorize("hasRole('ROLE_STOCK') OR hasRole('ROLE_ACCOUNTING')")
class StockController {

	private final StockManager stockManager;

	@Autowired
	public StockController(StockManager stockManager) {

		this.stockManager = stockManager;
	}


	@RequestMapping("/stock")
	public String stockMethodsForMap(ModelMap modelMap) {

		modelMap.addAttribute("stockItems", stockManager.findNonExpiredStockItems());
		modelMap.addAttribute("expiredStockItems", stockManager.findExpiredStockItems());
		modelMap.addAttribute("groceries", stockManager.findAllGroceries());
		modelMap.addAttribute("stockReport", stockManager.getStockReportForDate(LocalDate.now()));
		return "stock";
	}
	

	
	@RequestMapping("/removeExpiredStockItems")
	public String removeExpiredStock(){
		
		//get all expired Stock items
		Iterable<StockItem> expired = stockManager.findExpiredStockItems();
		Iterator<StockItem> iter = expired.iterator();
		
		//Delete all expired Stock items
		while(iter.hasNext())
		{
				stockManager.deleteStockItem(iter.next());
		}
		
		return "redirect:/stock";
	}
	
	//TODO Needs new HTML
	@RequestMapping(value = "/addGrocery", method = RequestMethod.POST)
	public String addGrocery(@RequestParam("grocery") String name,
								@RequestParam("metric") String metric,
								@RequestParam("price") String price){
		
		
		try{
			Double priceValue = Double.valueOf(price);
			stockManager.saveGrocery(stockManager.createGrocery(name, Metric.valueOf(metric), Money.of(priceValue,EURO)));
		}catch(Exception e) {
			System.out.println(e+"\n");
			return "redirect:/stock";
		}
		
		return "redirect:/stock";
	}
	

	
	@RequestMapping(value = "/setGroceryPrice", method = RequestMethod.POST)
	public String setGroceryPrice(@RequestParam("grocery") String name,
								@RequestParam("price") String price)
							  {
		
		try{
			Double priceValue = Double.valueOf(price);
			Grocery grocery = stockManager.findGroceryByName(name).get();
			grocery.setPrice(Money.of(priceValue, EURO));
			stockManager.saveGrocery(grocery);
		}catch(Exception e)
		{
			System.out.println(e+"\n");
			return "redirect:/stock";
		}
		
		return "redirect:/stock";
	}
	
	//TODO Needs new HTML
	@RequestMapping("/listGrocery")
	public String listGrocery(ModelMap modelMap){
		modelMap.addAttribute("allGroceries", stockManager.findAllGroceries());
		return "listGrocery";
	}
	
	
	//TODO Needs new HTML
		@RequestMapping("/addStock")
		public String addStock(){
			return "addStock";
		}
	
	@RequestMapping(value = "/addStockItem", method = RequestMethod.POST)
	public String newStock(@RequestParam("grocery") String grocery,
							 @RequestParam("quantity") String quantity,
							 @RequestParam("day") Integer day,
							 @RequestParam("month") Integer month,
							 @RequestParam("year") Integer year){
		
		if(!stockManager.findGroceryByName(grocery).isPresent())
			return "redirect:/stock";
		
		try{
			Double quantityValue = Double.valueOf(quantity);
			stockManager.saveStockItem(stockManager.createStockItem(stockManager.findGroceryByName(grocery).get(), quantityValue, LocalDate.of(year, month, day)));
		}
		catch(Exception e) {
			System.out.println(e+"\n");
			return "redirect:/stock";
		}
		
		

		return "redirect:/stock";
	}
	
	//TODO order management required for further work
		@RequestMapping("/inventory")
		public String orderReport(ModelMap modelMap){
			//create modelMap and fill with required Groceries based on Orders
			LocalDate date = LocalDate.now();
			modelMap.addAttribute("requiredStockItems", stockManager.getStockReportForDate(date));
			modelMap.addAttribute("allStockItems", stockManager.findAllStockItems());
			return "inventory";
		}

}
