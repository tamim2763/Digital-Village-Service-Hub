package com.digitalvillage.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.digitalvillage.model.ServiceCard;

/**
 * Public page controller for the Thymeleaf UI.
 * Page titles and text content are resolved from i18n message bundles.
 */
@Controller
public class PageController {

	@GetMapping({"/", "/home"})
	public String home(Model model) {
		model.addAttribute("serviceCards", buildServiceCards());
		return "index";
	}

	@GetMapping("/about")
	public String about(Model model) {
		return "about";
	}

	@GetMapping("/services")
	public String services(Model model) {
		model.addAttribute("serviceCards", buildServiceCards());
		return "services";
	}

	@GetMapping("/contact")
	public String contact(Model model) {
		return "contact";
	}

	private List<ServiceCard> buildServiceCards() {
		return List.of(
				ServiceCard.builder()
						.iconClass("fa-solid fa-train-subway")
						.titleKey("service.train.title")
						.descriptionKey("service.train.description")
						.link("/services/train")
						.build(),
				ServiceCard.builder()
						.iconClass("fa-solid fa-file-lines")
						.titleKey("service.forms.title")
						.descriptionKey("service.forms.description")
						.link("/services/forms")
						.build(),
				ServiceCard.builder()
						.iconClass("fa-solid fa-cloud-sun-rain")
						.titleKey("service.weather.title")
						.descriptionKey("service.weather.description")
						.link("/services/weather")
						.build(),
				ServiceCard.builder()
						.iconClass("fa-solid fa-wheat-awn-circle-exclamation")
						.titleKey("service.crop.title")
						.descriptionKey("service.crop.description")
						.link("/services/fosol")
						.build(),
				ServiceCard.builder()
						.iconClass("fa-solid fa-stethoscope")
						.titleKey("service.telemedicine.title")
						.descriptionKey("service.telemedicine.description")
						.build());
	}
}
