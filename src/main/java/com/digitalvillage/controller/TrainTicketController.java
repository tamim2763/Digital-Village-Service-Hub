package com.digitalvillage.controller;

import com.digitalvillage.model.PassengerForm;
import com.digitalvillage.model.TrainOption;
import com.digitalvillage.model.TrainSearchForm;
import com.digitalvillage.service.TrainTicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/services/train")
@RequiredArgsConstructor
public class TrainTicketController {

    private final TrainTicketService trainService;

    @GetMapping
    public String showSearchForm(Model model) {
        model.addAttribute("searchForm", new TrainSearchForm());
        return "train-search";
    }

    @PostMapping("/search")
    public String searchTrains(@Valid @ModelAttribute("searchForm") TrainSearchForm form,
                               BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "train-search";
        }
        
        List<TrainOption> trains = trainService.searchTrains(form.getFromStation(), form.getToStation(), form.getDate(), form.getSeatClass());
        model.addAttribute("trains", trains);
        return "train-search"; // Re-render search page with results
    }

    @PostMapping("/seats")
    public String showSeatSelection(@ModelAttribute PassengerForm passengerForm, Model model) {
        model.addAttribute("passengerForm", passengerForm);
        return "train-seats";
    }

    @PostMapping("/passenger")
    public String showPassengerForm(@ModelAttribute PassengerForm passengerForm, Model model) {
        model.addAttribute("passengerForm", passengerForm);
        return "train-passenger";
    }

    @PostMapping("/confirm")
    public String confirmBooking(@Valid @ModelAttribute("passengerForm") PassengerForm form,
                                 BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "train-passenger";
        }

        // Generate mock booking data
        String bookingRef = "DV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));

        model.addAttribute("bookingReference", bookingRef);
        model.addAttribute("formattedDate", formattedDate);
        model.addAttribute("passenger", form);
        
        return "train-confirmation";
    }
}
