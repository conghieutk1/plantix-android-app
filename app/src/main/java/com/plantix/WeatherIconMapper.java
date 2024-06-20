package com.plantix;

import java.util.HashMap;
import java.util.Map;

public class WeatherIconMapper {

    private static final Map<Integer, String> weatherIconMap;

    static {
        weatherIconMap = new HashMap<>();

        weatherIconMap.put(113, "sunny.png");
        weatherIconMap.put(116, "partlycloudy.png");
        weatherIconMap.put(119, "cloudy.png");
        weatherIconMap.put(122, "cloudy.png"); // Using cloudy icon for overcast as well
        weatherIconMap.put(143, "fog.png"); // Using fog icon for mist
        weatherIconMap.put(176, "chancerain.png");
        weatherIconMap.put(179, "chancesnow.png");
        weatherIconMap.put(182, "chancesleet.png");
        weatherIconMap.put(185, "chancesleet.png"); // Using sleet icon for freezing drizzle
        weatherIconMap.put(200, "chancetstorms.png");
        weatherIconMap.put(227, "snow.png"); // Using snow icon for blowing snow
        weatherIconMap.put(230, "snow.png"); // Using snow icon for blizzard
        weatherIconMap.put(248, "fog.png");
        weatherIconMap.put(260, "fog.png"); // Using fog icon for freezing fog
        weatherIconMap.put(263, "chancerain.png"); // Using rain icon for drizzle
        weatherIconMap.put(266, "chancerain.png"); // Using rain icon for drizzle
        weatherIconMap.put(281, "chancerain.png"); // Using rain icon for freezing drizzle
        weatherIconMap.put(284, "rain.png"); // Using rain icon for heavy freezing drizzle
        weatherIconMap.put(293, "chancerain.png");
        weatherIconMap.put(296, "rain.png");
        weatherIconMap.put(299, "rain.png");
        weatherIconMap.put(302, "rain.png");
        weatherIconMap.put(305, "rain.png");
        weatherIconMap.put(308, "rain.png");
        weatherIconMap.put(311, "sleet.png");
//        weatherIconMap.put("Moderate or heavy freezing rain", "sleet.png");
//        weatherIconMap.put("Light sleet", "sleet.png");
//        weatherIconMap.put("Moderate or heavy sleet", "sleet.png");
//        weatherIconMap.put("Patchy light snow", "chancesnow.png");
//        weatherIconMap.put("Light snow", "snow.png");
//        weatherIconMap.put("Patchy moderate snow", "snow.png"); // Using snow icon for patchy moderate snow
//        weatherIconMap.put("Moderate snow", "snow.png");
//        weatherIconMap.put("Patchy heavy snow", "snow.png"); // Using snow icon for patchy heavy snow
//        weatherIconMap.put("Heavy snow", "snow.png");
//        weatherIconMap.put("Ice pellets", "sleet.png"); // Using sleet icon for ice pellets
//        weatherIconMap.put("Light rain shower", "chancerain.png");
//        weatherIconMap.put("Moderate or heavy rain shower", "rain.png");
//        weatherIconMap.put("Torrential rain shower", "rain.png"); // Using rain icon for torrential rain
//        weatherIconMap.put("Light sleet showers", "chancesleet.png");
//        weatherIconMap.put("Moderate or heavy sleet showers", "sleet.png");
//        weatherIconMap.put("Light snow showers", "chancesnow.png");
//        weatherIconMap.put("Moderate or heavy snow showers", "snow.png");
//        weatherIconMap.put("Light showers of ice pellets", "chancesleet.png"); // Using sleet icon for ice pellet showers
//        weatherIconMap.put("Moderate or heavy showers of ice pellets", "sleet.png");
//        weatherIconMap.put("Patchy light rain with thunder", "chancetstorms.png");
//        weatherIconMap.put("Moderate or heavy rain with thunder", "tstorms.png");
//        weatherIconMap.put("Patchy light snow with thunder", "chancesnow.png"); // Using snow icon for patchy snow with thunder
//        weatherIconMap.put("Moderate or heavy snow with thunder", "snow.png");
    }

    public static String getDescription(int weatherCode) {
        return weatherIconMap.getOrDefault(weatherCode, "Unknown"); // Mô tả mặc định nếu không tìm thấy ánh xạ
    }
}

