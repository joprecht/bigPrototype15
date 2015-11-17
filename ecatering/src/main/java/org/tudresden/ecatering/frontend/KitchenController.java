package org.tudresden.ecatering.frontend;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tudresden.ecatering.kitchen.IngredientRepository;
import org.tudresden.ecatering.kitchen.StockManager;


@Controller
@PreAuthorize("hasRole('ROLE_KITCHEN')")
class KitchenController {

	private final StockManager stockManager;

	@Autowired
	public KitchenController(IngredientRepository inventory) {

		this.stockManager = new StockManager(inventory);
	}


	@RequestMapping("/listIngredients")
	public String kitchenData(ModelMap modelMap) {

		modelMap.addAttribute("allIngredients", stockManager.findAllIngredients());

		return "listIngredients";
	}

}
