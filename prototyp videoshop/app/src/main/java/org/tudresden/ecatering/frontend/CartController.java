package org.tudresden.ecatering.frontend;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import javax.validation.Valid;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.order.Cart;
import org.salespointframework.order.OrderManager;
import org.salespointframework.quantity.Metric;
import org.salespointframework.quantity.Quantity;
import static org.salespointframework.core.Currencies.EURO;

import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.tudresden.ecatering.model.CheckoutForm;
import org.tudresden.ecatering.model.CustomerRegistrationForm;
import org.tudresden.ecatering.model.accountancy.Address;
import org.tudresden.ecatering.model.accountancy.Debit;
import org.tudresden.ecatering.model.accountancy.Discount;
import org.tudresden.ecatering.model.accountancy.MealOrder;
import org.tudresden.ecatering.model.accountancy.Transfer;
import org.tudresden.ecatering.model.business.Business;
import org.tudresden.ecatering.model.business.BusinessType;
import org.tudresden.ecatering.model.customer.Customer;
import org.tudresden.ecatering.model.customer.CustomerManager;
import org.tudresden.ecatering.model.kitchen.Helping;
import org.tudresden.ecatering.model.kitchen.KitchenManager;
import org.tudresden.ecatering.model.kitchen.Menu;
import org.tudresden.ecatering.model.kitchen.MenuItem;
import org.tudresden.ecatering.model.kitchen.MenuItemRepository;

@Controller
@PreAuthorize("hasRole('ROLE_CUSTOMER')")
@SessionAttributes("cart")
class CartController {


	private final KitchenManager kitchenManager;
	private final CustomerManager customerManager;
	private final OrderManager<MealOrder> mealOrderManager;

	@Autowired
	public CartController(KitchenManager kitchenManager, CustomerManager customerManager, OrderManager<MealOrder> mealOrderManager) {

		this.kitchenManager = kitchenManager;
		this.customerManager = customerManager;
		this.mealOrderManager = mealOrderManager;
	}

	@ModelAttribute("cart")
	public Cart initializeCart() {
		return new Cart();
	}

	
	@RequestMapping(value = "/addMealsToBasket", method = RequestMethod.POST)
	public String addMealToBasket(@RequestParam("quantity") ArrayList<String> quantities,
								@RequestParam("pid") ArrayList<MenuItem> menus,
								@ModelAttribute Cart cart) {
		
		
		try{
		for(int i=0;i<quantities.size();i++)
		{
			if(quantities.get(i)!=null && !quantities.get(i).trim().isEmpty())
			{
				System.out.println("add to cart");
				cart.addOrUpdateItem(menus.get(i), Quantity.of(Integer.valueOf(quantities.get(i))));
			}
				
		}
		}catch(Exception e){
		System.out.println(e+"\n");
		}
		
		return "redirect:/menu";
	}
	
	@RequestMapping(value = "/deleteItem", method = RequestMethod.GET)
	public String deleteItem(@ModelAttribute Cart cart, @RequestParam("pid") String identifier) {
		
		cart.removeItem(identifier);
		
		return "redirect:/cart";
	}
	
	@RequestMapping(value = "/emptyCart", method = RequestMethod.GET)
	public String emptyCart(@ModelAttribute Cart cart) {
		
		cart.clear();

		return "redirect:/cart";
	}

	@RequestMapping(value = "/cart", method = RequestMethod.GET)
	public String cart() {
		
		return "cart";
	}


	@RequestMapping("/checkout")
	public String checkout(@ModelAttribute Cart cart, ModelMap modelMap, @LoggedIn Optional<UserAccount> userAccount) {
		
		Money discountPrice = null;
		System.out.println("checkout\n");
		
		try{
			Discount discount = customerManager.findCustomerByUserAccount(userAccount.get()).get().getDiscount();
			if(discount.equals(Discount.CHILDCARE))
			{
				System.out.println("childcare user\n");
				double tempValue = cart.getPrice().getNumberStripped().multiply(BigDecimal.valueOf(discount.getDiscountFactor())).doubleValue();
				discountPrice = Money.of(BigDecimal.valueOf(tempValue).setScale(2, BigDecimal.ROUND_HALF_DOWN),EURO);
			
			}
		}catch(Exception e) {
			System.out.println(e+"\n");
		}
		
		modelMap.addAttribute("discountPrice", discountPrice);
		modelMap.addAttribute("discount", 100-(Discount.CHILDCARE.getDiscountFactor()*100));
		modelMap.addAttribute("checkoutForm", new CheckoutForm());
				
		return "checkout";
	}
	
	@RequestMapping("/checkoutForm")
	public String checkoutForm(@ModelAttribute("checkoutForm") @Valid CheckoutForm checkoutForm,
			BindingResult result) {

		//TODO: error page for wrong code and nickname
		if (result.hasErrors())  {
			System.out.println("error\n");
			return "/checkout";
		}

		// (｡◕‿◕｡)
		


		return "redirect:/";
	}


	
	//TODO HTML for showing the Menus of the following 3 weeks
	@RequestMapping("/showPlan")
	public String showPlan(ModelMap modelMap, @LoggedIn Optional<UserAccount> userAccount){
		
		//get the next 3 weeks
		LocalDate now = LocalDate.now();
		LocalDate next = now.plusWeeks(1);
		LocalDate secondNext = next.plusWeeks(1);
		
		
		Optional<Customer> cust = customerManager.findCustomerByUserAccount(userAccount.get());
		if(cust.isPresent()){
			Customer cust2 = cust.get();
			Business business = cust2.getBusiness();
			
			if(business.getBusinessType().equals(BusinessType.COMPANY)){
				System.out.println("Company");
				Iterable<Menu> currentBig = kitchenManager.findMenusByDate(now);
				Iterator<Menu> iter = currentBig.iterator();
				while(iter.hasNext()){
					Menu menu = iter.next();
					if(menu.getHelping().equals(Helping.REGULAR)){
						modelMap.addAttribute("currentWeek", menu);
					}
				}
				
				Iterable<Menu> nextBig = kitchenManager.findMenusByDate(secondNext);
				Iterator<Menu> iter2 = nextBig.iterator();
				while(iter.hasNext()){
					Menu menu2 = iter2.next();
					if(menu2.getHelping().equals(Helping.REGULAR)){
						modelMap.addAttribute("nextWeek", menu2);
					}
				}
				
				Iterable<Menu> secondNextBig = kitchenManager.findMenusByDate(now);
				Iterator<Menu> iter3 = secondNextBig.iterator();
				while(iter3.hasNext()){
					Menu menu3 = iter3.next();
					if(menu3.getHelping().equals(Helping.REGULAR)){
						modelMap.addAttribute("secondNextWeek", menu3);
					}
				}
			}else{
				System.out.println("Social");
				
				Iterable<Menu> currentBig = kitchenManager.findMenusByDate(now);
				Iterator<Menu> iter = currentBig.iterator();
				while(iter.hasNext()){
					Menu menu = iter.next();
					if(menu.getHelping().equals(Helping.SMALL)){
						modelMap.addAttribute("currentWeek", menu);
					}
				}
				
				Iterable<Menu> nextBig = kitchenManager.findMenusByDate(secondNext);
				Iterator<Menu> iter2 = nextBig.iterator();
				while(iter.hasNext()){
					Menu menu2 = iter2.next();
					if(menu2.getHelping().equals(Helping.SMALL)){
						modelMap.addAttribute("nextWeek", menu2);
					}
				}
				
				Iterable<Menu> secondNextBig = kitchenManager.findMenusByDate(now);
				Iterator<Menu> iter3 = secondNextBig.iterator();
				while(iter3.hasNext()){
					Menu menu3 = iter3.next();
					if(menu3.getHelping().equals(Helping.SMALL)){
						modelMap.addAttribute("secondNextWeek", menu3);
					}
				}
			}
		}else{
			Iterable<Menu> currentBig = kitchenManager.findMenusByDate(now);
			Iterator<Menu> iter = currentBig.iterator();
			while(iter.hasNext()){
				Menu menu = iter.next();
				if(menu.getHelping().equals(Helping.REGULAR)){
					modelMap.addAttribute("currentWeek", menu);
				}
			}
			
			Iterable<Menu> nextBig = kitchenManager.findMenusByDate(secondNext);
			Iterator<Menu> iter2 = nextBig.iterator();
			while(iter.hasNext()){
				Menu menu2 = iter2.next();
				if(menu2.getHelping().equals(Helping.REGULAR)){
					modelMap.addAttribute("nextWeek", menu2);
				}
			}
			
			Iterable<Menu> secondNextBig = kitchenManager.findMenusByDate(now);
			Iterator<Menu> iter3 = secondNextBig.iterator();
			while(iter3.hasNext()){
				Menu menu3 = iter3.next();
				if(menu3.getHelping().equals(Helping.REGULAR)){
					modelMap.addAttribute("secondNextWeek", menu3);
				}
			}
		}
		
	
		//Map the Menus according to the weeks
		//modelMap.addAttribute("currentWeek", kitchenManager.findMenusByDate(now));
		//modelMap.addAttribute("nextWeek", kitchenManager.findMenusByDate(next));
		//modelMap.addAttribute("afterNextWeek", kitchenManager.findMenusByDate(nextnext));
		return "showPlan";
	}
}
