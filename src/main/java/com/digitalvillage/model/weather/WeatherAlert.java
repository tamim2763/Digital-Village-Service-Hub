package com.digitalvillage.model.weather;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherAlert {
    private String type;       // e.g. "HEAVY_RAIN", "STRONG_WIND", "HEAT_WAVE", "THUNDERSTORM", "FLOOD_RISK"
    private String severity;   // "WARNING", "WATCH"
    private String titleBn;
    private String titleEn;
    private String messageBn;
    private String messageEn;
}
