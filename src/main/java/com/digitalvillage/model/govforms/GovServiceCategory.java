package com.digitalvillage.model.govforms;

import lombok.Data;
import java.util.List;

@Data
public class GovServiceCategory {
    private String slug;
    private String iconClass;
    private String iconEmoji;
    private String nameBn;
    private String nameEn;
    private List<GovService> services;
}
