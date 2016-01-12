package org.tudresden.ecatering.frontend;


import static org.salespointframework.core.Currencies.EURO;

import java.util.Iterator;

import javax.validation.Valid;

import org.javamoney.moneta.Money;
import org.salespointframework.quantity.Metric;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountIdentifier;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.tudresden.ecatering.model.CustomerRegistrationForm;
import org.tudresden.ecatering.model.customer.CustomerManager;

@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
class UserController {
	
	private final UserAccountManager userAccountManager;
	private final CustomerManager customerManager;

	
	@Autowired
	public UserController(UserAccountManager userAccountManager, CustomerManager customerManager) {

		this.userAccountManager = userAccountManager;
		this.customerManager = customerManager;
	}
	
	@RequestMapping("/users")
	public String usersMethod(ModelMap modelMap) {
		modelMap.addAttribute("allCustomers", customerManager.findAllCustomers());
		
		Iterable<UserAccount> employees = userAccountManager.findAll();
		Iterator<UserAccount> iter = employees.iterator();
		while(iter.hasNext())
		{
			if(iter.next().hasRole(Role.of("ROLE_CUSTOMER")))
				iter.remove();
		}
		modelMap.addAttribute("allEmployees", employees);

		
		return "users";
	}
	
	@RequestMapping(value = "/addRole", method = RequestMethod.POST)
	public String addGrocery(@RequestParam("role") String role,
							 @RequestParam("username") String username){
		
		
		try{
			UserAccount user = userAccountManager.findByUsername(username).get();
			if(user.hasRole(Role.of(role)))
			{
				if((role.equals("ROLE_ADMIN") && user.getUsername().equals("boss")))
					return "redirect:/users";
				
				user.remove(Role.of(role));
				

			}
			else
			{
			user.add(Role.of(role));
			}
			userAccountManager.save(user);
			
		}catch(Exception e) {
			System.out.println(e+"\n");
			return "redirect:/users";
		}
		
		return "redirect:/users";
	}
	
	@RequestMapping(value =  "/toggleUser", method = RequestMethod.GET)
	public String removeUser(@RequestParam("username") String username){
		
		if(username.equals("boss"))
			return "redirect:/users";

		UserAccount user = 	userAccountManager.findByUsername(username).get();
		if(user.isEnabled())
		{
		userAccountManager.disable(user.getIdentifier());
		}
		else
		{
		userAccountManager.enable(user.getIdentifier());
		}
		userAccountManager.save(user);
		
		return "redirect:/users";

	}
	



}
