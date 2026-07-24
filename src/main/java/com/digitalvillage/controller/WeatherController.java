package com.digitalvillage.controller;

import com.digitalvillage.model.weather.WeatherResponse;
import com.digitalvillage.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/services/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    // Default: Tangail (MBSTU, Santosh)
    private static final double DEFAULT_LAT = 24.2513;
    private static final double DEFAULT_LON = 89.9170;
    private static final String DEFAULT_LOCATION_BN = "টাঙ্গাইল";
    private static final String DEFAULT_LOCATION_EN = "Tangail";

    /**
     * Server-rendered weather page.
     * Initial load uses default location (Tangail).
     * Optional lat/lon params for direct linking.
     */
    @GetMapping
    public String showWeatherPage(
            @RequestParam(value = "lat", required = false) Double lat,
            @RequestParam(value = "lon", required = false) Double lon,
            @RequestParam(value = "locationBn", required = false) String locationBn,
            @RequestParam(value = "locationEn", required = false) String locationEn,
            @org.springframework.web.bind.annotation.CookieValue(value = "weather_lat", required = false) Double cookieLat,
            @org.springframework.web.bind.annotation.CookieValue(value = "weather_lon", required = false) Double cookieLon,
            @org.springframework.web.bind.annotation.CookieValue(value = "weather_location_bn", required = false) String cookieLocBn,
            @org.springframework.web.bind.annotation.CookieValue(value = "weather_location_en", required = false) String cookieLocEn,
            Model model) {

        double useLat = DEFAULT_LAT;
        double useLon = DEFAULT_LON;
        String useBn = DEFAULT_LOCATION_BN;
        String useEn = DEFAULT_LOCATION_EN;

        // URL parameters take highest precedence
        if (lat != null && lon != null) {
            useLat = lat;
            useLon = lon;
            useBn = locationBn != null ? locationBn : DEFAULT_LOCATION_BN;
            useEn = locationEn != null ? locationEn : DEFAULT_LOCATION_EN;
        } 
        // Cookies take second precedence
        else if (cookieLat != null && cookieLon != null) {
            useLat = cookieLat;
            useLon = cookieLon;
            // Decode URL-encoded cookie values
            try {
                useBn = cookieLocBn != null ? java.net.URLDecoder.decode(cookieLocBn, "UTF-8") : DEFAULT_LOCATION_BN;
                useEn = cookieLocEn != null ? java.net.URLDecoder.decode(cookieLocEn, "UTF-8") : DEFAULT_LOCATION_EN;
            } catch (Exception e) {
                // Ignore decoding errors and fallback
            }
        }

        WeatherResponse weather = weatherService.getWeather(useLat, useLon, useBn, useEn);
        model.addAttribute("weather", weather);

        return "weather";
    }

    /**
     * REST API endpoint for client-side location changes.
     * Called by JavaScript when user changes location.
     */
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<WeatherResponse> getWeatherApi(
            @RequestParam("lat") double lat,
            @RequestParam("lon") double lon,
            @RequestParam(value = "locationBn", required = false) String locationBn,
            @RequestParam(value = "locationEn", required = false) String locationEn) {

        WeatherResponse weather = weatherService.getWeather(lat, lon, locationBn, locationEn);
        return ResponseEntity.ok(weather);
    }
}
