package com.example.restproject.controller;

import com.example.restproject.model.WeatherData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


public class ClimacellAPIProcessor implements Processor{
    private final int FORECAST_DAY_LIMIT = 3;

    @Override
    public WeatherData today(float lat, float lon) {
        HttpResponse<String> response = null;
        try {
            response = Unirest.get("https://climacell-microweather-v1.p.rapidapi.com/weather/realtime?fields=temp&lat=42.8237618&lon=-71.2216286")
                    .header("x-rapidapi-host", "climacell-microweather-v1.p.rapidapi.com")
                    .header("x-rapidapi-key", "97455ed33amsha8b9ff0ed65c295p1e9f82jsn06747edd46fc")
                    .asString();

            return format(response.getBody());
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
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
            System.out.println("Got UnirestException while trying to get weather forecast via ClimaCell");
            e.printStackTrace();
        } catch (JsonMappingException e) {
            System.out.println("Got JsonMappingException while trying to get weather forecast via ClimaCell");
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            System.out.println("Got JsonProcessingException while trying to get weather forecast via ClimaCell");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean canProcessDate(LocalDate date) {
        return !(date.toEpochDay() > LocalDate.now().plusDays(FORECAST_DAY_LIMIT).toEpochDay());
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
            System.out.println("JsonMappingException occurred while trying to format API response.");
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            System.out.println("JsonProcessingException occurred while trying to format API response.");
            e.printStackTrace();
        }

        return null;
    }
}
