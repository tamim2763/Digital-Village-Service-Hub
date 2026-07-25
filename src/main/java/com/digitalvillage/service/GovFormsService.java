package com.digitalvillage.service;

import com.digitalvillage.model.govforms.GovService;
import com.digitalvillage.model.govforms.GovServiceCategory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class GovFormsService {

    private final ObjectMapper objectMapper;
    private List<GovServiceCategory> categories = new ArrayList<>();

    public GovFormsService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("static/data/government-services.json");
            try (InputStream inputStream = resource.getInputStream()) {
                categories = objectMapper.readValue(inputStream, new TypeReference<List<GovServiceCategory>>() {});
                log.info("Loaded {} government service categories", categories.size());
            }
        } catch (IOException e) {
            log.error("Failed to load government services data", e);
        }
    }

    /**
     * Get all categories.
     * @return List of categories
     */
    public List<GovServiceCategory> getAllCategories() {
        return categories;
    }

    /**
     * Get a specific category by its slug.
     * @param slug The category slug
     * @return Optional containing the category if found
     */
    public Optional<GovServiceCategory> getCategoryBySlug(String slug) {
        if (categories == null || categories.isEmpty()) {
            init(); // Retry loading if not loaded
        }
        return categories.stream()
                .filter(c -> c.getSlug().equals(slug))
                .findFirst();
    }

    /**
     * Get a specific service within a specific category.
     * @param categorySlug The category slug
     * @param serviceSlug The service slug
     * @return Optional containing the service if found
     */
    public Optional<GovService> getServiceBySlug(String categorySlug, String serviceSlug) {
        return getCategoryBySlug(categorySlug)
                .flatMap(category -> category.getServices().stream()
                        .filter(s -> s.getSlug().equals(serviceSlug))
                        .findFirst());
    }
}
