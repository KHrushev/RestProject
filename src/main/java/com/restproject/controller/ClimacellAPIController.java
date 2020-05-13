package com.restproject.controller;

import com.restproject.model.Alert;
import com.restproject.model.InaccessibleAPIException;
import com.restproject.model.WeatherData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ClimacellAPIController implements Controller {
    private Logger logger = new LoggingController().logger;
    private final int FORECAST_DAY_LIMIT = 3;
    private String key;

    public ClimacellAPIController (String key) {
        this.key = key;
    }

    @Async
    @Override
    public WeatherData today(float lat, float lon) throws InaccessibleAPIException {
        HttpResponse<String> response = null;
        try {
            response = Unirest.get("https://climacell-microweather-v1.p.rapidapi.com/weather/realtime?fields=temp&lat=" + lat + "&lon=" + lon)
                    .header("x-rapidapi-host", "climacell-microweather-v1.p.rapidapi.com")
                    .header("x-rapidapi-key", key)
                    .asString();

            return format(response.getBody());
        } catch (Exception e) {
            logger.error("Exception occurred while trying to get ClimaCell API response. " + e.getClass().getName());
            throw new InaccessibleAPIException("Unable to get weather data from this API (Climacell) because it is inaccessible right now.");
        }
    }

    @Override
    public WeatherData future(LocalDate date, float lat, float lon) {
        LocalDateTime localDateTime = LocalDateTime.of(date, LocalTime.now());

        try {
            HttpResponse<String> response = Unirest.get("https://climacell-microweather-v1.p.rapidapi.com/weather/forecast/hourly?lat=42.8237618&lon=-71.2216286&start_time=now&" +
                    "end_time=" + localDateTime + "&fields=temp")
                    .header("x-rapidapi-host", "climacell-microweather-v1.p.rapidapi.com")
                    .header("x-rapidapi-key", "97455ed33amsha8b9ff0ed65c295p1e9f82jsn06747edd46fc")
                    .asString();

            ObjectMapper mapper = new ObjectMapper();

            JsonNode responseNode = mapper.readTree(response.getBody());
            JsonNode node = responseNode.get(responseNode.size()-1);

            return format(node.toString());
        } catch (UnirestException e) {
            logger.error("Got UnirestException while trying to get weather forecast via ClimaCell");
            e.printStackTrace();
        } catch (JsonMappingException e) {
            logger.error("Got JsonMappingException while trying to get weather forecast via ClimaCell");
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            logger.error("Got JsonProcessingException while trying to get weather forecast via ClimaCell");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Alert getAlerts(double lat, double lon) {
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
    public boolean canProcessAlerts() {
        return false;
    }

    @Override
    public WeatherData format(String data) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JsonNode forecast = null;
        try {
            WeatherData weatherData = new WeatherData();

            forecast = mapper.readTree(data);
            weatherData.setLat((float) forecast.get("lat").asDouble());
            weatherData.setLon((float) forecast.get("lon").asDouble());

            JsonNode forecastTemperatureNode = forecast.get("temp");
            weatherData.setTemp((float) forecastTemperatureNode.get("value").asDouble());

            JsonNode forecastObsTimeNode = forecast.get("observation_time");
            String obsTimeString = forecastObsTimeNode.get("value").asText();
            obsTimeString = obsTimeString.substring(0, obsTimeString.lastIndexOf("."));
            weatherData.setOb_time(LocalDateTime.parse(obsTimeString));

            weatherData.setTimezone("Unable to get a timezone with given API right now");

            return weatherData;
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException occurred while trying to format API response.");
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            logger.error("JsonProcessingException occurred while trying to format API response.");
            e.printStackTrace();
        }

        return null;
    }
}
