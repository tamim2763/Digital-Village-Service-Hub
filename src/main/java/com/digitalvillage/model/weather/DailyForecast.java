package com.digitalvillage.model.weather;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyForecast {
    private String date;           // e.g. "2026-07-25"
    private String dayNameBn;      // e.g. "শনি"
    private String dayNameEn;      // e.g. "Sat"
    private double tempMax;
    private double tempMin;
    private int weatherCode;
    private int precipitationProbabilityMax;
    private double precipitationSum; // mm
    private double rainSum;          // mm
    private double windSpeedMax;
    private String sunrise;
    private String sunset;
}
