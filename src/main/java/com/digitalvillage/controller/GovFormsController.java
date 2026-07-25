package com.digitalvillage.controller;

import com.digitalvillage.model.govforms.GovService;
import com.digitalvillage.model.govforms.GovServiceCategory;
import com.digitalvillage.service.GovFormsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/services/forms")
public class GovFormsController {

    private final GovFormsService govFormsService;

    public GovFormsController(GovFormsService govFormsService) {
        this.govFormsService = govFormsService;
    }

    @GetMapping
    public String getFormsHome(Model model) {
        List<GovServiceCategory> categories = govFormsService.getAllCategories();
        model.addAttribute("categories", categories);
        // Extract all services for the dropdown
        List<GovService> allServices = categories.stream()
                .flatMap(c -> c.getServices().stream())
                .toList();
        model.addAttribute("allServices", allServices);
        return "gov-forms";
    }

    @GetMapping("/{categorySlug}")
    public String getCategoryPage(@PathVariable String categorySlug, Model model) {
        Optional<GovServiceCategory> categoryOpt = govFormsService.getCategoryBySlug(categorySlug);
        if (categoryOpt.isEmpty()) {
            return "redirect:/services/forms";
        }
        model.addAttribute("category", categoryOpt.get());
        return "gov-forms-category";
    }

    @GetMapping("/{categorySlug}/{serviceSlug}")
    public String getServiceDetailPage(@PathVariable String categorySlug, @PathVariable String serviceSlug, Model model) {
        Optional<GovServiceCategory> categoryOpt = govFormsService.getCategoryBySlug(categorySlug);
        Optional<GovService> serviceOpt = govFormsService.getServiceBySlug(categorySlug, serviceSlug);
        
        if (categoryOpt.isEmpty() || serviceOpt.isEmpty()) {
            return "redirect:/services/forms";
        }
        
        model.addAttribute("category", categoryOpt.get());
        model.addAttribute("service", serviceOpt.get());
        return "gov-forms-detail";
    }
}
