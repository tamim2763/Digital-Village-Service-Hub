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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        if (!model.containsAttribute("searchForm")) {
            model.addAttribute("searchForm", new TrainSearchForm());
        }
        return "train-search";
    }

    @PostMapping("/search")
    public String searchTrains(@Valid @ModelAttribute("searchForm") TrainSearchForm form,
                               BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "train-search";
        }
        
        List<TrainOption> trains = trainService.searchTrains(form.getFromStation(), form.getToStation(), form.getDate(), form.getSeatClass());
        redirectAttributes.addFlashAttribute("trains", trains);
        redirectAttributes.addFlashAttribute("searchForm", form);
        return "redirect:/services/train";
    }

    @PostMapping("/seats")
    public String showSeatSelectionPost(@ModelAttribute PassengerForm passengerForm, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("passengerForm", passengerForm);
        return "redirect:/services/train/seat-selection";
    }

    @GetMapping("/seat-selection")
    public String showSeatSelectionGet(Model model) {
        if (!model.containsAttribute("passengerForm")) {
            return "redirect:/services/train";
        }
        return "train-seats";
    }

    @PostMapping("/passenger")
    public String showPassengerFormPost(@ModelAttribute PassengerForm passengerForm, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("passengerForm", passengerForm);
        return "redirect:/services/train/passenger-details";
    }

    @GetMapping("/passenger-details")
    public String showPassengerFormGet(Model model) {
        if (!model.containsAttribute("passengerForm")) {
            return "redirect:/services/train";
        }
        return "train-passenger";
    }

    @PostMapping("/confirm")
    public String confirmBooking(@Valid @ModelAttribute("passengerForm") PassengerForm form,
                                 BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.passengerForm", bindingResult);
            redirectAttributes.addFlashAttribute("passengerForm", form);
            return "redirect:/services/train/passenger-details";
        }

        // Generate mock booking data
        String bookingRef = "DV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));

        redirectAttributes.addFlashAttribute("bookingReference", bookingRef);
        redirectAttributes.addFlashAttribute("formattedDate", formattedDate);
        redirectAttributes.addFlashAttribute("passenger", form);
        
        return "redirect:/services/train/booking-confirmed";
    }

    @GetMapping("/booking-confirmed")
    public String showConfirmationGet(Model model) {
        if (!model.containsAttribute("bookingReference")) {
            return "redirect:/services/train";
        }
        return "train-confirmation";
    }
}
