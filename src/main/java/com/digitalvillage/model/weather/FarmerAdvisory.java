package com.digitalvillage.model.weather;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmerAdvisory {
    private String type;       // "IRRIGATION", "SPRAY", "HARVEST", "CROP_DRYING"
    private String icon;       // emoji or css class
    private String titleBn;
    private String titleEn;
    private String messageBn;
    private String messageEn;
    private boolean favorable; // true = good condition, false = warning
}
