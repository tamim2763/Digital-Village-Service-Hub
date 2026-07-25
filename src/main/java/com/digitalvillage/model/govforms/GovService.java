package com.digitalvillage.model.govforms;

import lombok.Data;
import java.util.List;

@Data
public class GovService {
    private String slug;
    private String nameBn;
    private String nameEn;
    private String purposeBn;
    private String purposeEn;
    private String feeBn;
    private String feeEn;
    private String processingTimeBn;
    private String processingTimeEn;
    private String submissionLocationBn;
    private String submissionLocationEn;
    private boolean online;
    
    private List<String> eligibilityBn;
    private List<String> eligibilityEn;
    
    private List<String> documentsBn;
    private List<String> documentsEn;
    
    private List<ProcessStep> steps;
    
    private List<String> commonMistakesBn;
    private List<String> commonMistakesEn;
    
    private List<String> warningsBn;
    private List<String> warningsEn;
    
    private List<ProcessStep> faqs; // Resusing ProcessStep structure (title/desc) for FAQ questions/answers
    
    private String officialWebsiteUrl;
    private String officialContactBn;
    private String officialContactEn;
}
