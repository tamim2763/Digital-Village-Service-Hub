package com.digitalvillage.service;

import com.digitalvillage.model.weather.CurrentWeather;
import com.digitalvillage.model.weather.DailyForecast;
import com.digitalvillage.model.weather.WeatherAlert;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DerivedAlertProvider implements AlertProvider {

    @Override
    public List<WeatherAlert> getAlerts(CurrentWeather current, List<DailyForecast> dailyForecasts) {
        List<WeatherAlert> alerts = new ArrayList<>();
        DailyForecast today = dailyForecasts.isEmpty() ? null : dailyForecasts.get(0);

        if (today != null) {
            // Heavy Rain
            if (today.getPrecipitationSum() > 30) {
                alerts.add(WeatherAlert.builder()
                        .type("HEAVY_RAIN").severity("WARNING")
                        .titleBn("ভারী বৃষ্টির সতর্কতা").titleEn("Heavy Rain Warning")
                        .messageBn("আজ ভারী বৃষ্টি হতে পারে (" + Math.round(today.getPrecipitationSum()) + " মিমি)। সাবধানে থাকুন।")
                        .messageEn("Heavy rain expected today (" + Math.round(today.getPrecipitationSum()) + "mm). Stay safe.")
                        .build());
            }

            // Flood Risk
            if (today.getPrecipitationSum() > 60) {
                alerts.add(WeatherAlert.builder()
                        .type("FLOOD_RISK").severity("WARNING")
                        .titleBn("বন্যার ঝুঁকি").titleEn("Flood Risk")
                        .messageBn("অতিরিক্ত বৃষ্টিপাত (" + Math.round(today.getPrecipitationSum()) + " মিমি)। নিচু এলাকায় বন্যার ঝুঁকি আছে।")
                        .messageEn("Excessive rainfall (" + Math.round(today.getPrecipitationSum()) + "mm). Low-lying areas may flood.")
                        .build());
            }

            // Strong Wind
            if (today.getWindSpeedMax() > 40) {
                alerts.add(WeatherAlert.builder()
                        .type("STRONG_WIND").severity("WARNING")
                        .titleBn("ঝড়ো বাতাসের সতর্কতা").titleEn("Strong Wind Warning")
                        .messageBn("আজ ঝড়ো বাতাস বইবে (" + Math.round(today.getWindSpeedMax()) + " কিমি/ঘণ্টা)।")
                        .messageEn("Strong winds expected (" + Math.round(today.getWindSpeedMax()) + " km/h).")
                        .build());
            }
        }

        // Heat Wave
        if (current.getTemperature() > 38 || current.getFeelsLike() > 42) {
            alerts.add(WeatherAlert.builder()
                    .type("HEAT_WAVE").severity("WARNING")
                    .titleBn("তীব্র গরমের সতর্কতা").titleEn("Heat Wave Warning")
                    .messageBn("তাপমাত্রা অনেক বেশি। পানি বেশি পান করুন এবং রোদ এড়িয়ে চলুন।")
                    .messageEn("Temperature is extremely high. Drink plenty of water and avoid sun exposure.")
                    .build());
        }

        // Thunderstorm (WMO codes 95-99)
        if (current.getWeatherCode() >= 95) {
            alerts.add(WeatherAlert.builder()
                    .type("THUNDERSTORM").severity("WARNING")
                    .titleBn("বজ্রপাতসহ ঝড়ের সতর্কতা").titleEn("Thunderstorm Warning")
                    .messageBn("বজ্রপাতসহ ঝড় হতে পারে। খোলা মাঠে থাকবেন ঘনিয়ে এলে নিরাপদ স্থানে আশ্রয় নিন।")
                    .messageEn("Thunderstorm possible. Take shelter in a safe place.")
                    .build());
        }

        return alerts;
    }
}
