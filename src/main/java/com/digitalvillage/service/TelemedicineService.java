package com.digitalvillage.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitalvillage.entity.Appointment;
import com.digitalvillage.entity.Doctor;
import com.digitalvillage.repository.AppointmentRepository;
import com.digitalvillage.repository.DoctorRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TelemedicineService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public List<Doctor> listAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> searchDoctors(String specialization, String query) {
        boolean hasSpecialization = specialization != null && !specialization.trim().isEmpty();
        boolean hasQuery = query != null && !query.trim().isEmpty();

        if (!hasSpecialization && !hasQuery) {
            return listAllDoctors();
        }

        if (hasSpecialization && !hasQuery) {
            return doctorRepository.findBySpecializationBnContainingIgnoreCaseOrSpecializationEnContainingIgnoreCase(specialization, specialization);
        }

        if (!hasSpecialization) {
            return doctorRepository.findByNameBnContainingIgnoreCaseOrNameEnContainingIgnoreCase(query, query);
        }

        LinkedHashMap<Long, Doctor> merged = new LinkedHashMap<>();
        doctorRepository.findBySpecializationBnContainingIgnoreCaseOrSpecializationEnContainingIgnoreCase(specialization, specialization)
                .forEach(doctor -> merged.putIfAbsent(doctor.getId(), doctor));
        doctorRepository.findByNameBnContainingIgnoreCaseOrNameEnContainingIgnoreCase(query, query)
                .forEach(doctor -> merged.putIfAbsent(doctor.getId(), doctor));
        return new ArrayList<>(merged.values());
    }

    public Optional<Doctor> findDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    @Transactional
    public Appointment bookAppointment(Appointment appointment) {
        String bookingReference = generateUniqueBookingReference();
        appointment.setBookingReference(bookingReference);
        if (appointment.getStatus() == null) {
            appointment.setStatus(Appointment.AppointmentStatus.PENDING);
        }
        return appointmentRepository.save(appointment);
    }

    public Optional<Appointment> findAppointmentByReference(String ref) {
        return appointmentRepository.findByBookingReference(ref);
    }

    @PostConstruct
    private void seedDemo() {
        if (doctorRepository.count() > 0) {
            return;
        }

        doctorRepository.saveAll(List.of(
                Doctor.builder()
                        .nameBn("ডা. রাহিমা আক্তার")
                        .nameEn("Dr. Rahima Akter")
                        .specializationBn("জেনারেল ফিজিশিয়ান")
                        .specializationEn("General Physician")
                        .qualification("MBBS, FCPS (Medicine)")
                        .experienceYears(12)
                        .languagesSpoken("বাংলা, ইংরেজি")
                        .consultationFee(new BigDecimal("450.00"))
                        .rating(4.8)
                        .availableStatus(Doctor.AvailableStatus.ONLINE)
                        .build(),
                Doctor.builder()
                        .nameBn("ডা. নাহিদা সুলতানা")
                        .nameEn("Dr. Nahida Sultana")
                        .specializationBn("গাইনি")
                        .specializationEn("Gynecologist")
                        .qualification("MBBS, FCPS (Gynecology)")
                        .experienceYears(15)
                        .languagesSpoken("বাংলা, ইংরেজি")
                        .consultationFee(new BigDecimal("650.00"))
                        .rating(4.9)
                        .availableStatus(Doctor.AvailableStatus.BUSY)
                        .build(),
                Doctor.builder()
                        .nameBn("ডা. শামীম হোসেন")
                        .nameEn("Dr. Shamim Hossain")
                        .specializationBn("শিশু বিশেষজ্ঞ")
                        .specializationEn("Pediatrician")
                        .qualification("MBBS, DCH")
                        .experienceYears(9)
                        .languagesSpoken("বাংলা, ইংরেজি")
                        .consultationFee(new BigDecimal("500.00"))
                        .rating(4.7)
                        .availableStatus(Doctor.AvailableStatus.ONLINE)
                        .build(),
                Doctor.builder()
                        .nameBn("ডা. সারা ইসলাম")
                        .nameEn("Dr. Sara Islam")
                        .specializationBn("চর্মরোগ")
                        .specializationEn("Dermatologist")
                        .qualification("MBBS, DVD")
                        .experienceYears(11)
                        .languagesSpoken("বাংলা, ইংরেজি")
                        .consultationFee(new BigDecimal("700.00"))
                        .rating(4.6)
                        .availableStatus(Doctor.AvailableStatus.OFFLINE)
                        .build(),
                Doctor.builder()
                        .nameBn("ডা. কামরুল হাসান")
                        .nameEn("Dr. Kamrul Hasan")
                        .specializationBn("হাড়জোড়া")
                        .specializationEn("Orthopedic")
                        .qualification("MBBS, MS (Orthopedics)")
                        .experienceYears(14)
                        .languagesSpoken("বাংলা, ইংরেজি")
                        .consultationFee(new BigDecimal("800.00"))
                        .rating(4.9)
                        .availableStatus(Doctor.AvailableStatus.BUSY)
                        .build(),
                Doctor.builder()
                        .nameBn("ডা. ফারজানা হক")
                        .nameEn("Dr. Farzana Haque")
                        .specializationBn("মেডিসিন")
                        .specializationEn("Internal Medicine")
                        .qualification("MBBS, MD (Medicine)")
                        .experienceYears(10)
                        .languagesSpoken("বাংলা, ইংরেজি")
                        .consultationFee(new BigDecimal("550.00"))
                        .rating(4.5)
                        .availableStatus(Doctor.AvailableStatus.ONLINE)
                        .build()
        ));
    }

    private String generateUniqueBookingReference() {
        String bookingReference;
        do {
            bookingReference = "DV-TM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (appointmentRepository.findByBookingReference(bookingReference).isPresent());
        return bookingReference;
    }
}