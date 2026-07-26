package com.digitalvillage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digitalvillage.entity.FosolItem;

public interface FosolItemRepository extends JpaRepository<FosolItem, Long> {

    List<FosolItem> findByNameBnContainingIgnoreCaseOrNameEnContainingIgnoreCase(String nameBn, String nameEn);
}
