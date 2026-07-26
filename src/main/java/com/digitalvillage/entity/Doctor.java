package com.digitalvillage.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nameBn;

    @Column(nullable = false, length = 150)
    private String nameEn;

    @Column(nullable = false, length = 150)
    private String specializationBn;

    @Column(nullable = false, length = 150)
    private String specializationEn;

    @Column(nullable = false, length = 255)
    private String qualification;

    @Column(nullable = false)
    private Integer experienceYears;

    @Column(nullable = false, length = 255)
    private String languagesSpoken;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal consultationFee;

    @Column(nullable = false)
    private Double rating;

    @Column(length = 500)
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AvailableStatus availableStatus;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public enum AvailableStatus {
        ONLINE,
        OFFLINE,
        BUSY
    }
}