/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tudresden.ecatering.frontend;


import javax.validation.Valid;

import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.tudresden.ecatering.model.CustomerRegistrationForm;
import org.tudresden.ecatering.model.EmployeeRegistrationForm;
import org.tudresden.ecatering.model.business.BusinessManager;
import org.tudresden.ecatering.model.customer.CustomerManager;

@Controller
public class MainController {
	
	private final UserAccountManager userAccountManager;
	private final CustomerManager customerManager;
	private final BusinessManager businessManager;
	
	@Autowired
	  public MainController(UserAccountManager userAccountManager, CustomerManager customerManager, BusinessManager businessManager) {
		this.userAccountManager = userAccountManager;
		this.businessManager = businessManager;
	    this.customerManager = customerManager;
	  }

	@RequestMapping({ "/", "/index" })
	public String index() {
		return "index";
	}
	
	@RequestMapping("/registerNewEmployee")
	public String registerNewEmployeer(@ModelAttribute("registrationForm") @Valid EmployeeRegistrationForm registrationForm,
			BindingResult result) {

		//TODO: error page for wrong code and nickname
		if (result.hasErrors() || 
				userAccountManager.findByUsername(registrationForm.getNickname()).isPresent()) {
			return "register";
		}
		


		// (｡◕‿◕｡)
		UserAccount userAccount = userAccountManager.create(registrationForm.getNickname(), registrationForm.getPassword(),
				 Role.of(registrationForm.getRole()));
		userAccount.setFirstname(registrationForm.getFirstname());
		userAccount.setLastname(registrationForm.getLastname());
		userAccount.setEmail(registrationForm.getEmail());
	
		userAccountManager.save(userAccount);

		return "redirect:/";
	}
	
	@RequestMapping("/registerNewCustomer")
	public String registerNewCustomer(@ModelAttribute("registrationForm") @Valid CustomerRegistrationForm registrationForm,
			BindingResult result) {

		//TODO: error page for wrong code and nickname
		if (result.hasErrors() || !businessManager.findBusinessByCode(registrationForm.getCode()).isPresent() ||
				userAccountManager.findByUsername(registrationForm.getNickname()).isPresent()) {
			return "register";
		}

		// (｡◕‿◕｡)
		UserAccount userAccount = userAccountManager.create(registrationForm.getNickname(), registrationForm.getPassword(),
				 Role.of("ROLE_CUSTOMER"));
		userAccount.setFirstname(registrationForm.getFirstname());
		userAccount.setLastname(registrationForm.getLastname());
		userAccount.setEmail(registrationForm.getEmail());
	
		userAccountManager.save(userAccount);
		customerManager.saveCustomer(customerManager.createCustomer(userAccount, registrationForm.getCode()));


		return "redirect:/";
	}

	@RequestMapping("/register")
	public String register(ModelMap modelMap) {
		modelMap.addAttribute("registrationForm", new CustomerRegistrationForm());
		return "register";
	}
}
