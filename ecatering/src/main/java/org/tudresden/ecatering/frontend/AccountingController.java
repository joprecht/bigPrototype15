package org.tudresden.ecatering.frontend;


import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.tudresden.ecatering.usermanager.CustomerAccount;
import org.tudresden.ecatering.usermanager.CustomerRepository;

@Controller
public class AccountingController {
	
	private final OrderManager<Order> orderManager;
	private final CustomerRepository customerRepository;
	@Autowired
	public AccountingController(OrderManager<Order> orderManager, CustomerRepository customerRepository){
		this.orderManager = orderManager;
		this.customerRepository = customerRepository;
	}
	
	@RequestMapping("/retrieveVacantPositions")
	public String retrieveVacantPositions(ModelMap modelMap){
			//public static final OrderStatus orderStatus;
			OrderStatus o1 = OrderStatus.OPEN;
			modelMap.addAttribute("allVacantPostions",orderManager.find(o1));
		return "retrieveVacantPositions";
	}
	
//	@RequestMapping(value = "/completeOrder", method = RequestMethod.POST)
//	public String completeOrder(@RequestParam("OrderId") String orderId,@){
//		Order o1 = orderManager.find(userAccount, from, to)
//		return "redirect:/retrieveVacantPositions";
//	}
	//TODO
	@RequestMapping(value = "/retrieveCustomerData/{pid}", method = RequestMethod.GET)
	public String retrieveCustomerData(@PathVariable("pid") CustomerAccount customerAccount ,ModelMap modelMap){
		
		return "redirect:/retrieve ";
	}
	
	//TODO
	@RequestMapping("/createBill")
	public String createBill(ModelMap modelMap){
		return "";
	}
	
	
	
	
	
	
}
