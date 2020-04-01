package com.example.restproject.controller;

import com.example.restproject.model.WeatherData;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

public class WeatherAPIProcessor implements Processor {

    @Override
    public WeatherData process(float lat, float lon) {
        String url = "https://api.weatherbit.io/v2.0/current?lat=" + lat + "&lon=" + lon + "&key=10d23961b91f476abe5dd2cae0314424";
        try {
            String response = getAPIResponse(url);

            if (response == null) {
                throw new IOException("Null Response");
            }

            return format(response);
        } catch (IOException e) {
            System.out.println("Error occurred while trying to get/format API response.");
            return null;
        }
    }

    @Override
    public WeatherData format(String data) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

        JsonNode forecast = null;
        try {
            forecast = mapper.readTree(data);
            JsonNode forecastData = forecast.get("data");
            forecastData = forecastData.get(0);

            WeatherData weatherData = new WeatherData();
            weatherData.setLat((float) forecastData.get("lat").asDouble());
            weatherData.setLon((float) forecastData.get("lon").asDouble());
            weatherData.setTimezone(forecastData.get("timezone").asText());
            weatherData.setOb_time(LocalDateTime.parse(forecastData.get("last_ob_time").asText()));
            weatherData.setTemp((float) forecastData.get("temp").asDouble());

            return weatherData;
        } catch (JsonProcessingException e) {
            System.out.println("Error occurred while trying to format API response.");
            return null;
        }
    }

}
