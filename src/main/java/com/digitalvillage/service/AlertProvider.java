package com.digitalvillage.service;

import com.digitalvillage.model.weather.CurrentWeather;
import com.digitalvillage.model.weather.DailyForecast;
import com.digitalvillage.model.weather.WeatherAlert;

import java.util.List;

public interface AlertProvider {
    List<WeatherAlert> getAlerts(CurrentWeather current, List<DailyForecast> dailyForecasts);
}
