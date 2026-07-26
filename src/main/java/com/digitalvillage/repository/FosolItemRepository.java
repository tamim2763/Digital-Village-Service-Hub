package com.digitalvillage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digitalvillage.entity.FosolItem;

@Repository
public interface FosolItemRepository extends JpaRepository<FosolItem, Long> {

    List<FosolItem> findByNameBnContainingIgnoreCaseOrNameEnContainingIgnoreCase(String nameBn, String nameEn);
}
