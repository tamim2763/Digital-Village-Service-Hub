package com.digitalvillage.model.weather;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HourlyForecast {
    private String time;       // e.g. "04:00 PM"
    private double temperature;
    private int weatherCode;
    private int precipitationProbability;
    private double rain;       // mm
}
