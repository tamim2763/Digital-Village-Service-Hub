package com.digitalvillage.controller;

import com.digitalvillage.service.FosolMarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/services/fosol")
@RequiredArgsConstructor
public class FosolMarketController {

    private final FosolMarketService marketService;

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q, Model model) {
        model.addAttribute("items", marketService.search(q));
        model.addAttribute("query", q);
        return "fosol-list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        var itemOpt = marketService.findById(id);
        if (itemOpt.isEmpty()) {
            return "redirect:/services/fosol";
        }
        model.addAttribute("item", itemOpt.get());
        return "fosol-detail";
    }
}
