package com.digitalvillage.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TelemedicineBookingForm {

    private Long doctorId;

    @NotBlank(message = "{telemedicine.error.consultation_type}")
    private String consultationType;

    @NotBlank(message = "{telemedicine.error.patient_name}")
    private String patientName;

    @NotNull(message = "{telemedicine.error.patient_age}")
    @Min(value = 1, message = "{telemedicine.error.patient_age}")
    @Max(value = 120, message = "{telemedicine.error.patient_age}")
    private Integer patientAge;

    @NotBlank(message = "{telemedicine.error.patient_gender}")
    private String patientGender;

    @NotBlank(message = "{telemedicine.error.symptoms}")
    @Size(max = 1000, message = "{telemedicine.error.symptoms}")
    private String symptoms;

    @NotNull(message = "{telemedicine.error.appointment_date}")
    private LocalDate appointmentDate;

    @NotNull(message = "{telemedicine.error.appointment_time}")
    private LocalTime appointmentTime;
}