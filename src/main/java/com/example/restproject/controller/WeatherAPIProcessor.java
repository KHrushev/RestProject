package com.example.restproject.controller;

import com.example.restproject.model.ExtremeWeatherData;
import com.example.restproject.model.WeatherData;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class WeatherAPIProcessor implements Processor {
    private final int FORECAST_DAY_LIMIT = 16;

    @Override
    public WeatherData today(float lat, float lon) {
        String url = "https://api.weatherbit.io/v2.0/current?lat=" + lat + "&lon=" + lon + "&key=10d23961b91f476abe5dd2cae0314424";
        try {
            String response = getAPIResponse(url);

            if (response == null) {
                throw new IOException("Null Response (nowcast)");
            }

            return format(response);
        } catch (IOException e) {
            System.out.println("Got IOException while trying to get/format API response.");
            return null;
        }
    }

    @Override
    public WeatherData future(LocalDate date, float lat, float lon) {
        String url = "https://api.weatherbit.io/v2.0/forecast/daily?lat=" + lat + "&lon=" + lon + "&key=10d23961b91f476abe5dd2cae0314424";

        try {
            String response = getAPIResponse(url);

            if (response == null) {
                throw new IOException("Null Response (forecast)");
            }


            return formatFuture(response, date);
        } catch (IOException e) {
            System.out.println("Got IOExceptin while getting/formatting the API Response");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ExtremeWeatherData getExtremeWeatherData(LocalDate date, String location) {
        return null;
    }

    @Override
    public boolean canNowcast() {
        return true;
    }

    @Override
    public boolean canProcessDate(LocalDate date) {
        return !(date.toEpochDay() > LocalDate.now().plusDays(FORECAST_DAY_LIMIT).toEpochDay());
    }

    @Override
    public boolean canProcessExtremeData() {
        return false;
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
            System.out.println("JsonProcessingException occurred while trying to format API response.");
            return null;
        }
    }

    private WeatherData formatFuture(String response, LocalDate date) {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode responseNode = null;
        try {
            responseNode = mapper.readTree(response);

            WeatherData weatherData = new WeatherData();

            weatherData.setLat((float) responseNode.get("lat").asDouble());
            weatherData.setLon((float) responseNode.get("lon").asDouble());
            weatherData.setTimezone(responseNode.get("timezone").asText());

            JsonNode dataNode = responseNode.get("data");
            dataNode = dataNode.get(15);

            weatherData.setTemp((float) dataNode.get("temp").asDouble());
            LocalDate localDate = LocalDate.parse(dataNode.get("datetime").asText());
            weatherData.setOb_time(LocalDateTime.of(localDate, LocalTime.MIN));

            return weatherData;
        } catch (JsonProcessingException e) {
            System.out.println("JsonProcessingException occurred while trying to format API response.");
            e.printStackTrace();
            return null;
        }
    }
}
