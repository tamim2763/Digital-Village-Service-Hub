package com.digitalvillage.model.weather;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentWeather {
    private double temperature;
    private double feelsLike;
    private int humidity;
    private double windSpeed;
    private int windDirection;
    private double uvIndex;
    private double visibility;
    private int weatherCode;
    private double rainToday;
    private double expectedRain;
    private int precipitationProbability;
    private String sunrise;
    private String sunset;
}
