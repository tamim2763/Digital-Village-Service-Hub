package com.digitalvillage.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.digitalvillage.entity.Appointment;
import com.digitalvillage.entity.Doctor;
import com.digitalvillage.model.TelemedicineBookingForm;
import com.digitalvillage.service.TelemedicineService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/services/telemedicine")
@RequiredArgsConstructor
public class TelemedicineController {

    private static final List<String> CONSULTATION_TYPES = List.of("VIDEO", "AUDIO", "CHAT");

    private final TelemedicineService telemedicineService;

    @GetMapping
    public String listDoctors(@RequestParam(value = "specialization", required = false) String specialization,
                              @RequestParam(value = "q", required = false) String q,
                              Model model) {
        model.addAttribute("doctors", telemedicineService.searchDoctors(specialization, q));
        model.addAttribute("specialization", specialization);
        model.addAttribute("query", q);
        model.addAttribute("specializationOptions", List.of(
                "General Physician",
                "Gynecologist",
                "Pediatrician",
                "Dermatologist",
                "Orthopedic",
                "Internal Medicine"
        ));
        return "telemedicine-list";
    }

    @GetMapping("/{id}")
    public String doctorDetail(@PathVariable Long id, Model model) {
        var doctorOpt = telemedicineService.findDoctorById(id);
        if (doctorOpt.isEmpty()) {
            return "redirect:/services/telemedicine";
        }

        model.addAttribute("doctor", doctorOpt.get());
        model.addAttribute("consultationTypes", CONSULTATION_TYPES);
        return "telemedicine-doctor-detail";
    }

    @PostMapping("/{id}/book")
    public String startBooking(@PathVariable Long id,
                               @RequestParam("consultationType") String consultationType,
                               RedirectAttributes redirectAttributes) {
        var doctorOpt = telemedicineService.findDoctorById(id);
        if (doctorOpt.isEmpty()) {
            return "redirect:/services/telemedicine";
        }

        TelemedicineBookingForm bookingForm = new TelemedicineBookingForm();
        bookingForm.setDoctorId(id);
        bookingForm.setConsultationType(consultationType);
        redirectAttributes.addFlashAttribute("bookingForm", bookingForm);
        return "redirect:/services/telemedicine/" + id + "/booking";
    }

    @GetMapping("/{id}/booking")
    public String bookingForm(@PathVariable Long id, Model model) {
        if (!model.containsAttribute("bookingForm")) {
            return "redirect:/services/telemedicine";
        }

        TelemedicineBookingForm bookingForm = (TelemedicineBookingForm) model.getAttribute("bookingForm");
        if (bookingForm == null || bookingForm.getDoctorId() == null) {
            return "redirect:/services/telemedicine";
        }

        var doctorOpt = telemedicineService.findDoctorById(bookingForm.getDoctorId());
        if (doctorOpt.isEmpty()) {
            return "redirect:/services/telemedicine";
        }

        model.addAttribute("doctor", doctorOpt.get());
        model.addAttribute("consultationTypes", CONSULTATION_TYPES);
        return "telemedicine-booking";
    }

    @PostMapping("/{id}/confirm")
    public String confirmBooking(@PathVariable Long id,
                                 @Valid @ModelAttribute("bookingForm") TelemedicineBookingForm bookingForm,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        var doctorOpt = telemedicineService.findDoctorById(id);
        if (doctorOpt.isEmpty()) {
            return "redirect:/services/telemedicine";
        }

        if (bookingForm.getDoctorId() != null && !bookingForm.getDoctorId().equals(id)) {
            return "redirect:/services/telemedicine";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.bookingForm", bindingResult);
            bookingForm.setDoctorId(id);
            redirectAttributes.addFlashAttribute("bookingForm", bookingForm);
            return "redirect:/services/telemedicine/" + id + "/booking";
        }

        Doctor doctor = doctorOpt.get();
        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patientName(bookingForm.getPatientName())
                .patientAge(bookingForm.getPatientAge())
                .patientGender(bookingForm.getPatientGender())
                .symptoms(bookingForm.getSymptoms())
                .consultationType(Appointment.ConsultationType.valueOf(bookingForm.getConsultationType()))
                .appointmentDate(bookingForm.getAppointmentDate())
                .appointmentTime(bookingForm.getAppointmentTime())
                .build();

        Appointment savedAppointment = telemedicineService.bookAppointment(appointment);
        redirectAttributes.addFlashAttribute("bookingReference", savedAppointment.getBookingReference());
        redirectAttributes.addFlashAttribute("doctor", doctor);
        redirectAttributes.addFlashAttribute("appointment", savedAppointment);
        redirectAttributes.addFlashAttribute("formattedDate", bookingForm.getAppointmentDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        redirectAttributes.addFlashAttribute("formattedTime", bookingForm.getAppointmentTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
        return "redirect:/services/telemedicine/booking-confirmed";
    }

    @GetMapping("/booking-confirmed")
    public String bookingConfirmed(Model model) {
        if (!model.containsAttribute("bookingReference") || !model.containsAttribute("appointment") || !model.containsAttribute("doctor")) {
            return "redirect:/services/telemedicine";
        }
        return "telemedicine-confirmation";
    }
}