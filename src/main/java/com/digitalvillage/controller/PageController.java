package com.digitalvillage.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.digitalvillage.model.ServiceCard;
import com.digitalvillage.util.AppConstants;

/**
 * Public page controller for the starter Thymeleaf experience.
 */
@Controller
public class PageController {

	@GetMapping({"/", "/home"})
	public String home(Model model) {
		model.addAttribute("pageTitle", "Home");
		model.addAttribute("serviceCards", buildServiceCards());
		model.addAttribute("applicationName", AppConstants.APPLICATION_NAME);
		return "index";
	}

	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("pageTitle", "About");
		model.addAttribute("applicationName", AppConstants.APPLICATION_NAME);
		return "about";
	}

	@GetMapping("/services")
	public String services(Model model) {
		model.addAttribute("pageTitle", "Services");
		model.addAttribute("serviceCards", buildServiceCards());
		model.addAttribute("applicationName", AppConstants.APPLICATION_NAME);
		return "services";
	}

	@GetMapping("/contact")
	public String contact(Model model) {
		model.addAttribute("pageTitle", "Contact");
		model.addAttribute("applicationName", AppConstants.APPLICATION_NAME);
		return "contact";
	}

	private List<ServiceCard> buildServiceCards() {
		return List.of(
				ServiceCard.builder()
						.iconClass("fa-solid fa-train-subway")
						.title("Train Ticket")
						.description("Plan rural travel with a simple digital booking entry point.")
						.build(),
				ServiceCard.builder()
						.iconClass("fa-solid fa-file-lines")
						.title("Government Forms")
						.description("Access essential forms and application assistance in one place.")
						.build(),
				ServiceCard.builder()
						.iconClass("fa-solid fa-cloud-sun-rain")
						.title("Weather Alerts")
						.description("Stay informed with timely weather advisories for villages and farms.")
						.build(),
				ServiceCard.builder()
						.iconClass("fa-solid fa-wheat-awn-circle-exclamation")
						.title("Crop Prices")
						.description("Check indicative market prices before making selling decisions.")
						.build(),
				ServiceCard.builder()
						.iconClass("fa-solid fa-stethoscope")
						.title("Telemedicine")
						.description("Connect remote citizens with future virtual care support.")
						.build());
	}
}
