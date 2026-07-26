package com.digitalvillage.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitalvillage.entity.FosolItem;
import com.digitalvillage.entity.FosolPriceOption;
import com.digitalvillage.repository.FosolItemRepository;
import com.digitalvillage.repository.FosolPriceOptionRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FosolMarketService {

    private final FosolItemRepository repo;
    private final FosolPriceOptionRepository optionRepo;

    public List<FosolItem> listAll() {
        return repo.findAll();
    }

    public List<FosolItem> search(String q) {
        if (q == null || q.trim().isEmpty()) {
            return listAll();
        }
        return repo.findByNameBnContainingIgnoreCaseOrNameEnContainingIgnoreCase(q, q);
    }

    public Optional<FosolItem> findById(Long id) {
        Optional<FosolItem> opt = repo.findById(id);
        opt.ifPresent(item -> {
            // ensure price options are loaded while within transactional/service layer
            var opts = optionRepo.findByItemIdOrderByPriceAsc(id);
            item.setPriceOptions(opts);
        });
        return opt;
    }

    @Transactional
    public FosolItem save(FosolItem item) {
        return repo.save(item);
    }

    @PostConstruct
    private void seedDemo() {
        if (repo.count() == 0) {
            FosolItem rice = repo.save(FosolItem.builder()
                    .nameBn("ধান")
                    .nameEn("Rice")
                    .descriptionBn("উন্নত মানের ধান - ছোট বস্তা")
                    .descriptionEn("High-quality rice - small bag")
                    .price(new BigDecimal("320.00"))
                    .quantityAvailable(50)
                    .build());

            // price options for rice
            optionRepo.save(FosolPriceOption.builder()
                    .item(rice)
                    .labelBn("১ কেজি")
                    .labelEn("1 kg")
                    .unit("1 kg")
                    .price(new BigDecimal("30.00"))
                    .build());

            optionRepo.save(FosolPriceOption.builder()
                    .item(rice)
                    .labelBn("৫ কেজি")
                    .labelEn("5 kg")
                    .unit("5 kg")
                    .price(new BigDecimal("150.00"))
                    .build());

            optionRepo.save(FosolPriceOption.builder()
                    .item(rice)
                    .labelBn("প্রতি পিস")
                    .labelEn("per piece")
                    .unit("piece")
                    .price(new BigDecimal("20.00"))
                    .build());

            FosolItem wheat = repo.save(FosolItem.builder()
                    .nameBn("গম")
                    .nameEn("Wheat")
                    .descriptionBn("স্থানীয় গম - পুষ্টিকর")
                    .descriptionEn("Local wheat - nutritious")
                    .price(new BigDecimal("280.00"))
                    .quantityAvailable(30)
                    .build());

            optionRepo.save(FosolPriceOption.builder()
                    .item(wheat)
                    .labelBn("১ কেজি")
                    .labelEn("1 kg")
                    .unit("1 kg")
                    .price(new BigDecimal("28.00"))
                    .build());

        }
    }
}
