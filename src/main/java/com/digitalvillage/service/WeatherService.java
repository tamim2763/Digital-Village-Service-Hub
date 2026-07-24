package com.digitalvillage.service;

import com.digitalvillage.model.weather.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class WeatherService {

    @Value("${weather.api.open-meteo.url:https://api.open-meteo.com/v1/forecast}")
    private String openMeteoBase;

    private static final String HOURLY_PARAMS = "temperature_2m,relative_humidity_2m,apparent_temperature,precipitation_probability,precipitation,rain,weather_code,wind_speed_10m,wind_direction_10m,uv_index,visibility";

    private static final String DAILY_PARAMS = "weather_code,temperature_2m_max,temperature_2m_min,apparent_temperature_max,apparent_temperature_min,sunrise,sunset,uv_index_max,precipitation_sum,rain_sum,precipitation_probability_max,wind_speed_10m_max,wind_direction_10m_dominant";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final AlertProvider alertProvider;

    public WeatherService(ObjectMapper objectMapper, AlertProvider alertProvider) {
        this.objectMapper = objectMapper;
        this.alertProvider = alertProvider;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Cacheable(value = "weather", key = "#lat + '_' + #lon")
    public WeatherResponse getWeather(double lat, double lon, String locationBn, String locationEn) {
        try {
            String url = String.format("%s?latitude=%.4f&longitude=%.4f&hourly=%s&daily=%s&timezone=Asia%%2FDhaka&forecast_days=7",
                    openMeteoBase, lat, lon, HOURLY_PARAMS, DAILY_PARAMS);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Open-Meteo API returned status {}", response.statusCode());
                return buildErrorResponse(lat, lon, locationBn, locationEn);
            }

            JsonNode root = objectMapper.readTree(response.body());
            return parseWeatherResponse(root, lat, lon, locationBn, locationEn);

        } catch (Exception e) {
            log.error("Failed to fetch weather data: {}", e.getMessage(), e);
            return buildErrorResponse(lat, lon, locationBn, locationEn);
        }
    }

    private WeatherResponse parseWeatherResponse(JsonNode root, double lat, double lon, String locationBn, String locationEn) {
        JsonNode hourlyNode = root.get("hourly");
        JsonNode dailyNode = root.get("daily");

        // Determine current hour index
        int currentHourIndex = LocalDateTime.now().getHour();
        // For today's date - the API returns from midnight of the first day
        LocalDate today = LocalDate.now();

        // Current Weather
        CurrentWeather current = buildCurrentWeather(hourlyNode, dailyNode, currentHourIndex);

        // Hourly Forecasts (next 12 hours from now)
        List<HourlyForecast> hourlyForecasts = buildHourlyForecasts(hourlyNode, currentHourIndex);

        // Daily Forecasts
        List<DailyForecast> dailyForecasts = buildDailyForecasts(dailyNode);

        // Alerts (derived from data or external provider)
        List<WeatherAlert> alerts = alertProvider.getAlerts(current, dailyForecasts);

        // Farmer Advisories
        List<FarmerAdvisory> advisories = generateAdvisories(current, hourlyForecasts, dailyForecasts);

        // Weather Summary
        String summaryBn = generateSummaryBn(current, hourlyForecasts);
        String summaryEn = generateSummaryEn(current, hourlyForecasts);

        return WeatherResponse.builder()
                .locationNameBn(locationBn != null ? locationBn : "টাঙ্গাইল")
                .locationNameEn(locationEn != null ? locationEn : "Tangail")
                .latitude(lat)
                .longitude(lon)
                .current(current)
                .summaryBn(summaryBn)
                .summaryEn(summaryEn)
                .hourlyForecasts(hourlyForecasts)
                .dailyForecasts(dailyForecasts)
                .alerts(alerts)
                .advisories(advisories)
                .build();
    }

    private CurrentWeather buildCurrentWeather(JsonNode hourly, JsonNode daily, int hourIndex) {
        JsonNode temps = hourly.get("temperature_2m");
        JsonNode feelsLike = hourly.get("apparent_temperature");
        JsonNode humidity = hourly.get("relative_humidity_2m");
        JsonNode windSpeed = hourly.get("wind_speed_10m");
        JsonNode windDir = hourly.get("wind_direction_10m");
        JsonNode uvIndex = hourly.get("uv_index");
        JsonNode visibility = hourly.get("visibility");
        JsonNode weatherCode = hourly.get("weather_code");
        JsonNode precipProb = hourly.get("precipitation_probability");
        JsonNode rain = hourly.get("rain");

        // Calculate rain today: sum of rain from midnight to current hour
        double rainToday = 0;
        for (int i = 0; i <= hourIndex; i++) {
            rainToday += rain.get(i).asDouble(0);
        }

        // Expected rain: sum of remaining hours today
        double expectedRain = 0;
        for (int i = hourIndex + 1; i < 24; i++) {
            expectedRain += rain.get(i).asDouble(0);
        }

        return CurrentWeather.builder()
                .temperature(temps.get(hourIndex).asDouble())
                .feelsLike(feelsLike.get(hourIndex).asDouble())
                .humidity(humidity.get(hourIndex).asInt())
                .windSpeed(windSpeed.get(hourIndex).asDouble())
                .windDirection(windDir.get(hourIndex).asInt())
                .uvIndex(uvIndex.get(hourIndex).asDouble())
                .visibility(visibility.get(hourIndex).asDouble() / 1000.0) // Convert m to km
                .weatherCode(weatherCode.get(hourIndex).asInt())
                .rainToday(Math.round(rainToday * 10.0) / 10.0)
                .expectedRain(Math.round(expectedRain * 10.0) / 10.0)
                .precipitationProbability(precipProb.get(hourIndex).asInt())
                .sunrise(formatTime(daily.get("sunrise").get(0).asText()))
                .sunset(formatTime(daily.get("sunset").get(0).asText()))
                .build();
    }

    private List<HourlyForecast> buildHourlyForecasts(JsonNode hourly, int currentHourIndex) {
        List<HourlyForecast> forecasts = new ArrayList<>();
        JsonNode times = hourly.get("time");
        JsonNode temps = hourly.get("temperature_2m");
        JsonNode codes = hourly.get("weather_code");
        JsonNode probs = hourly.get("precipitation_probability");
        JsonNode rains = hourly.get("rain");

        // Next 12 hours from current hour
        for (int i = currentHourIndex; i < Math.min(currentHourIndex + 12, times.size()); i++) {
            String rawTime = times.get(i).asText(); // "2026-07-24T14:00"
            String displayTime = formatHourAmPm(rawTime);

            forecasts.add(HourlyForecast.builder()
                    .time(displayTime)
                    .temperature(temps.get(i).asDouble())
                    .weatherCode(codes.get(i).asInt())
                    .precipitationProbability(probs.get(i).asInt())
                    .rain(rains.get(i).asDouble())
                    .build());
        }
        return forecasts;
    }

    private List<DailyForecast> buildDailyForecasts(JsonNode daily) {
        List<DailyForecast> forecasts = new ArrayList<>();
        JsonNode times = daily.get("time");
        JsonNode codes = daily.get("weather_code");
        JsonNode maxTemps = daily.get("temperature_2m_max");
        JsonNode minTemps = daily.get("temperature_2m_min");
        JsonNode precipProbs = daily.get("precipitation_probability_max");
        JsonNode precipSums = daily.get("precipitation_sum");
        JsonNode rainSums = daily.get("rain_sum");
        JsonNode windMaxes = daily.get("wind_speed_10m_max");
        JsonNode sunrises = daily.get("sunrise");
        JsonNode sunsets = daily.get("sunset");

        for (int i = 0; i < times.size(); i++) {
            String dateStr = times.get(i).asText();
            LocalDate date = LocalDate.parse(dateStr);

            String dayNameBn = getBengaliDayName(date);
            String dayNameEn = date.getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH);

            forecasts.add(DailyForecast.builder()
                    .date(dateStr)
                    .dayNameBn(dayNameBn)
                    .dayNameEn(dayNameEn)
                    .tempMax(maxTemps.get(i).asDouble())
                    .tempMin(minTemps.get(i).asDouble())
                    .weatherCode(codes.get(i).asInt())
                    .precipitationProbabilityMax(precipProbs.get(i).asInt())
                    .precipitationSum(precipSums.get(i).asDouble())
                    .rainSum(rainSums.get(i).asDouble())
                    .windSpeedMax(windMaxes.get(i).asDouble())
                    .sunrise(formatTime(sunrises.get(i).asText()))
                    .sunset(formatTime(sunsets.get(i).asText()))
                    .build());
        }
        return forecasts;
    }

    // --- Alert Derivation ---
    // Delegated to AlertProvider (injected)


    // --- Farmer Advisory Generation ---
    private List<FarmerAdvisory> generateAdvisories(CurrentWeather current, List<HourlyForecast> hourly, List<DailyForecast> daily) {
        List<FarmerAdvisory> advisories = new ArrayList<>();

        double expectedRainNext24h = current.getRainToday() + current.getExpectedRain();
        if (!daily.isEmpty() && daily.size() > 1) {
            expectedRainNext24h += daily.get(1).getRainSum();
        }

        // Irrigation Advisory
        if (current.getExpectedRain() > 10 || current.getPrecipitationProbability() > 70) {
            advisories.add(FarmerAdvisory.builder()
                    .type("IRRIGATION").icon("💧").favorable(true)
                    .titleBn("সেচ").titleEn("Irrigation")
                    .messageBn("বৃষ্টির সম্ভাবনা বেশি। আজ সেচ না দিলেও চলবে।")
                    .messageEn("Rain likely. Irrigation not needed today.")
                    .build());
        } else if (current.getPrecipitationProbability() < 30 && current.getExpectedRain() < 2) {
            advisories.add(FarmerAdvisory.builder()
                    .type("IRRIGATION").icon("💧").favorable(false)
                    .titleBn("সেচ").titleEn("Irrigation")
                    .messageBn("আগামী ২৪ ঘণ্টায় বৃষ্টির সম্ভাবনা কম। সেচ দেওয়া উচিত।")
                    .messageEn("Low rain expected. Consider irrigating today.")
                    .build());
        }

        // Spray Advisory
        boolean noRainNext6h = hourly.stream().limit(6).allMatch(h -> h.getRain() < 0.5);
        if (current.getWindSpeed() < 15 && noRainNext6h && current.getPrecipitationProbability() < 20) {
            advisories.add(FarmerAdvisory.builder()
                    .type("SPRAY").icon("🌿").favorable(true)
                    .titleBn("কীটনাশক স্প্রে").titleEn("Pesticide Spray")
                    .messageBn("বাতাস শান্ত ও আগামী ৬ ঘণ্টায় বৃষ্টির সম্ভাবনা নেই। স্প্রে করার ভালো সময়।")
                    .messageEn("Calm wind and no rain expected in next 6 hours. Good time to spray.")
                    .build());
        } else if (current.getWindSpeed() > 15 || current.getExpectedRain() > 5) {
            advisories.add(FarmerAdvisory.builder()
                    .type("SPRAY").icon("🌿").favorable(false)
                    .titleBn("কীটনাশক স্প্রে").titleEn("Pesticide Spray")
                    .messageBn("আজ স্প্রে করবেন না — বাতাস বা বৃষ্টিতে ওষুধ নষ্ট হবে।")
                    .messageEn("Do not spray today — wind or rain will waste chemicals.")
                    .build());
        }

        // Harvest Advisory
        if (daily.size() >= 3) {
            boolean nextTwoDaysDry = daily.get(1).getRainSum() < 2 && daily.get(1).getPrecipitationProbabilityMax() < 40
                                  && daily.get(2).getRainSum() < 2 && daily.get(2).getPrecipitationProbabilityMax() < 40;
            if (nextTwoDaysDry && current.getExpectedRain() < 2 && current.getPrecipitationProbability() < 40) {
                advisories.add(FarmerAdvisory.builder()
                        .type("HARVEST").icon("🌾").favorable(true)
                        .titleBn("ফসল কাটা").titleEn("Harvest")
                        .messageBn("আগামী ২ দিন শুকনো থাকবে। ফসল কাটার ভালো সময়।")
                        .messageEn("Next 2 days will be dry. Good time to harvest.")
                        .build());
            } else if (daily.get(1).getPrecipitationProbabilityMax() > 50 || daily.get(1).getRainSum() > 2) {
                advisories.add(FarmerAdvisory.builder()
                        .type("HARVEST").icon("🌾").favorable(false)
                        .titleBn("ফসল কাটা").titleEn("Harvest")
                        .messageBn("আগামীকাল বৃষ্টি হতে পারে। পাকা ফসল থাকলে তাড়াতাড়ি কাটুন।")
                        .messageEn("Rain expected tomorrow. Harvest ripe crops soon.")
                        .build());
            }
        }

        // Crop Drying Advisory
        if (current.getExpectedRain() < 2 && current.getHumidity() < 75 && current.getWeatherCode() < 3) {
            advisories.add(FarmerAdvisory.builder()
                    .type("CROP_DRYING").icon("☀️").favorable(true)
                    .titleBn("ফসল শুকানো").titleEn("Crop Drying")
                    .messageBn("আজ ফসল/ধান শুকানোর ভালো দিন।")
                    .messageEn("Good day for drying crops/paddy.")
                    .build());
        }

        return advisories;
    }

    // --- Summary Generation ---
    private String generateSummaryBn(CurrentWeather current, List<HourlyForecast> hourly) {
        if (current.getWeatherCode() >= 95) {
            return "⚠️ সতর্কতা: বজ্রপাতসহ ঝড় হতে পারে। বাইরে যাওয়া থেকে বিরত থাকুন।";
        }
        if (current.getTemperature() > 38) {
            return "আজ খুব গরম পড়বে (অনুভূতি " + Math.round(current.getFeelsLike()) + "°সে)। পানি বেশি পান করুন।";
        }

        // Check if rain expected in afternoon
        boolean afternoonRain = hourly.stream()
                .filter(h -> {
                    try { return Integer.parseInt(h.getTime().split(":")[0]) >= 2 && h.getTime().contains("PM"); }
                    catch (Exception e) { return false; }
                })
                .anyMatch(h -> h.getPrecipitationProbability() > 60);

        if (afternoonRain) {
            return "বিকেলে বৃষ্টির সম্ভাবনা বেশি। বাইরে কাজ করলে ছাতা সঙ্গে রাখুন।";
        }
        if (current.getPrecipitationProbability() > 60) {
            return "আজ বৃষ্টির সম্ভাবনা " + current.getPrecipitationProbability() + "%। ছাতা সঙ্গে রাখুন।";
        }
        if (current.getWeatherCode() <= 1) {
            return "আজ রোদ থাকবে। সর্বোচ্চ তাপমাত্রা " + Math.round(current.getTemperature()) + "°সে।";
        }
        return "আজ আংশিক মেঘলা আকাশ। তাপমাত্রা " + Math.round(current.getTemperature()) + "°সে।";
    }

    private String generateSummaryEn(CurrentWeather current, List<HourlyForecast> hourly) {
        if (current.getWeatherCode() >= 95) {
            return "⚠️ Warning: Thunderstorm possible. Avoid going outdoors.";
        }
        if (current.getTemperature() > 38) {
            return "Very hot today (feels like " + Math.round(current.getFeelsLike()) + "°C). Drink plenty of water.";
        }
        if (current.getPrecipitationProbability() > 60) {
            return "Rain likely today (" + current.getPrecipitationProbability() + "%). Carry an umbrella.";
        }
        if (current.getWeatherCode() <= 1) {
            return "Clear skies today. High of " + Math.round(current.getTemperature()) + "°C.";
        }
        return "Partly cloudy today. Temperature " + Math.round(current.getTemperature()) + "°C.";
    }

    // --- Utility Methods ---
    private String formatTime(String isoTime) {
        // "2026-07-24T05:24" → "05:24 AM"
        try {
            LocalDateTime dt = LocalDateTime.parse(isoTime);
            return dt.format(DateTimeFormatter.ofPattern("hh:mm a"));
        } catch (Exception e) {
            return isoTime;
        }
    }

    private String formatHourAmPm(String isoTime) {
        // "2026-07-24T14:00" → "02 PM"
        try {
            LocalDateTime dt = LocalDateTime.parse(isoTime);
            return dt.format(DateTimeFormatter.ofPattern("hh a"));
        } catch (Exception e) {
            return isoTime;
        }
    }

    private String getBengaliDayName(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case SATURDAY -> "শনি";
            case SUNDAY -> "রবি";
            case MONDAY -> "সোম";
            case TUESDAY -> "মঙ্গল";
            case WEDNESDAY -> "বুধ";
            case THURSDAY -> "বৃহস্পতি";
            case FRIDAY -> "শুক্র";
        };
    }

    public static String getWeatherConditionBn(int code) {
        return switch (code) {
            case 0 -> "পরিষ্কার আকাশ";
            case 1 -> "প্রায় পরিষ্কার";
            case 2 -> "আংশিক মেঘলা";
            case 3 -> "মেঘলা";
            case 45, 48 -> "কুয়াশা";
            case 51, 53, 55 -> "গুঁড়ি গুঁড়ি বৃষ্টি";
            case 56, 57 -> "হিমশীতল গুঁড়ি বৃষ্টি";
            case 61, 63, 65 -> "বৃষ্টি";
            case 66, 67 -> "হিমশীতল বৃষ্টি";
            case 71, 73, 75 -> "তুষারপাত";
            case 77 -> "তুষারকণা";
            case 80, 81, 82 -> "ঝড়ো বৃষ্টি";
            case 85, 86 -> "তুষার ঝড়";
            case 95 -> "বজ্রপাতসহ ঝড়";
            case 96, 99 -> "শিলাবৃষ্টিসহ ঝড়";
            default -> "অজানা";
        };
    }

    public static String getWeatherConditionEn(int code) {
        return switch (code) {
            case 0 -> "Clear Sky";
            case 1 -> "Mostly Clear";
            case 2 -> "Partly Cloudy";
            case 3 -> "Overcast";
            case 45, 48 -> "Foggy";
            case 51, 53, 55 -> "Drizzle";
            case 56, 57 -> "Freezing Drizzle";
            case 61, 63, 65 -> "Rain";
            case 66, 67 -> "Freezing Rain";
            case 71, 73, 75 -> "Snowfall";
            case 77 -> "Snow Grains";
            case 80, 81, 82 -> "Rain Showers";
            case 85, 86 -> "Snow Showers";
            case 95 -> "Thunderstorm";
            case 96, 99 -> "Thunderstorm with Hail";
            default -> "Unknown";
        };
    }

    public static String getWeatherIcon(int code) {
        return switch (code) {
            case 0 -> "fa-solid fa-sun";
            case 1 -> "fa-solid fa-sun";
            case 2 -> "fa-solid fa-cloud-sun";
            case 3 -> "fa-solid fa-cloud";
            case 45, 48 -> "fa-solid fa-smog";
            case 51, 53, 55, 56, 57 -> "fa-solid fa-cloud-rain";
            case 61, 63, 65, 66, 67 -> "fa-solid fa-cloud-showers-heavy";
            case 71, 73, 75, 77, 85, 86 -> "fa-solid fa-snowflake";
            case 80, 81, 82 -> "fa-solid fa-cloud-showers-heavy";
            case 95, 96, 99 -> "fa-solid fa-cloud-bolt";
            default -> "fa-solid fa-cloud";
        };
    }

    public static String getWeatherEmoji(int code) {
        return switch (code) {
            case 0, 1 -> "☀️";
            case 2 -> "⛅";
            case 3 -> "☁️";
            case 45, 48 -> "🌫️";
            case 51, 53, 55, 56, 57 -> "🌦️";
            case 61, 63, 65, 66, 67, 80, 81, 82 -> "🌧️";
            case 71, 73, 75, 77, 85, 86 -> "❄️";
            case 95, 96, 99 -> "⛈️";
            default -> "🌤️";
        };
    }

    public static String toBengaliDigits(String input) {
        if (input == null) return null;
        return input.replace("0", "০")
                .replace("1", "১")
                .replace("2", "২")
                .replace("3", "৩")
                .replace("4", "৪")
                .replace("5", "৫")
                .replace("6", "৬")
                .replace("7", "৭")
                .replace("8", "৮")
                .replace("9", "৯")
                .replace("AM", "am")
                .replace("PM", "pm");
    }

    public static String getRainfallDescription(Double rainMm, String language) {
        if (rainMm == null || rainMm < 0.1) {
            return "bn".equals(language) ? "বৃষ্টি নেই" : "No Rain";
        } else if (rainMm <= 2.5) {
            return "bn".equals(language) ? "হালকা বৃষ্টি" : "Light Rain";
        } else if (rainMm <= 10.0) {
            return "bn".equals(language) ? "মাঝারি বৃষ্টি" : "Moderate Rain";
        } else if (rainMm <= 30.0) {
            return "bn".equals(language) ? "ভারী বৃষ্টি" : "Heavy Rain";
        } else {
            return "bn".equals(language) ? "অতি ভারী বৃষ্টি" : "Very Heavy Rain";
        }
    }

    private WeatherResponse buildErrorResponse(double lat, double lon, String locationBn, String locationEn) {
        return WeatherResponse.builder()
                .locationNameBn(locationBn != null ? locationBn : "টাঙ্গাইল")
                .locationNameEn(locationEn != null ? locationEn : "Tangail")
                .latitude(lat)
                .longitude(lon)
                .current(null)
                .summaryBn("আবহাওয়ার তথ্য লোড হচ্ছে না। কিছুক্ষণ পর আবার চেষ্টা করুন।")
                .summaryEn("Weather data could not be loaded. Please try again later.")
                .hourlyForecasts(List.of())
                .dailyForecasts(List.of())
                .alerts(List.of())
                .advisories(List.of())
                .build();
    }
}
