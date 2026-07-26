package com.digitalvillage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digitalvillage.entity.FosolPriceOption;

import java.util.List;

@Repository
public interface FosolPriceOptionRepository extends JpaRepository<FosolPriceOption, Long> {

    List<FosolPriceOption> findByItemIdOrderByPriceAsc(Long itemId);
}
