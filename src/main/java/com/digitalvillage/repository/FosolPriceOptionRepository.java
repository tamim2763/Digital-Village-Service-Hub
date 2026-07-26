package com.digitalvillage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digitalvillage.entity.FosolPriceOption;

import java.util.List;

public interface FosolPriceOptionRepository extends JpaRepository<FosolPriceOption, Long> {

    List<FosolPriceOption> findByItemIdOrderByPriceAsc(Long itemId);
}
