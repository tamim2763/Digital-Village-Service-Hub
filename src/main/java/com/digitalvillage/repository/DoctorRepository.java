package com.digitalvillage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digitalvillage.entity.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findBySpecializationBnContainingIgnoreCaseOrSpecializationEnContainingIgnoreCase(String specializationBn, String specializationEn);

    List<Doctor> findByNameBnContainingIgnoreCaseOrNameEnContainingIgnoreCase(String nameBn, String nameEn);
}