package com.digitalvillage.model.weather;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    private String locationNameBn;
    private String locationNameEn;
    private double latitude;
    private double longitude;
    private CurrentWeather current;
    private String summaryBn;
    private String summaryEn;
    private List<HourlyForecast> hourlyForecasts;
    private List<DailyForecast> dailyForecasts;
    private List<WeatherAlert> alerts;
    private List<FarmerAdvisory> advisories;
}
