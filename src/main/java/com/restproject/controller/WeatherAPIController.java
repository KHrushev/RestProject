package com.restproject.controller;

import com.restproject.model.Alert;
import com.restproject.model.InaccessibleAPIException;
import com.restproject.model.WeatherData;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class WeatherAPIController implements Controller {
    private Logger logger = new LoggingController().logger;
    private final int FORECAST_DAY_LIMIT = 16;
    private String key;

    public WeatherAPIController (String key) {
        this.key = key;
    }

    @Async
    @Override
    public WeatherData today(float lat, float lon) throws InaccessibleAPIException {
        String url = "https://api.weatherbit.io/v2.0/current?lat=" + lat + "&lon=" + lon + "&key=" + key;
        try {
            String response = getAPIResponse(url);

            if (response == null) {
                throw new IOException("Null Response (nowcast)");
            }

            return format(response);
        } catch (Exception e) {
            logger.error("Exception occurred while trying to get/format WeatherBit API response. " + e.getClass().getName());
            throw new InaccessibleAPIException("Unable to get weather data from this API (WeatherBit) because it is inaccessible right now.");
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
            logger.error("Got IOExceptin while getting/formatting the API Response");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Alert getAlerts(double lat, double lon) {
        HttpResponse<String> response = null;
        try {
            response = Unirest.get("https://weatherbit-v1-mashape.p.rapidapi.com/alerts?lat=" + lat + "&lon=" + lon)
                    .header("x-rapidapi-host", "weatherbit-v1-mashape.p.rapidapi.com")
                    .header("x-rapidapi-key", "97455ed33amsha8b9ff0ed65c295p1e9f82jsn06747edd46fc")
                    .asString();
        } catch (UnirestException e) {
            logger.error("Got UnirestException while getting/formatting the API Response");
            e.printStackTrace();
        }

        if (response != null && response.getBody() != null) {
            return formatAlert(response.getBody());
        } else {
            return null;
        }
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
        return true;
    }

    @Override
    public WeatherData format(String response) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

        JsonNode forecast = null;
        try {
            forecast = mapper.readTree(response);
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
            logger.error("JsonProcessingException occurred while trying to format API response.");
            e.printStackTrace();
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
            logger.error("JsonProcessingException occurred while trying to format API response.");
            e.printStackTrace();
            return null;
        }
    }

    private Alert formatAlert(String response) {
        Alert alert = new Alert();
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode data = mapper.readTree(response);

            alert.setLat(data.get("lat").asDouble());
            alert.setLon(data.get("lon").asDouble());
            alert.setTimezone(data.get("timezone").asText());

            if (data.get("alerts").size() > 0) {
                data = data.get("alerts").get(0);

                alert.setDescription(data.get("title").asText());
                alert.setSeverity(data.get("severity").asText());
            } else {
                alert.setDescription("No active alerts");
                alert.setSeverity("No active alerts");
            }
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException occurred while trying to format API response.");
            e.printStackTrace();
            return null;
        } catch (JsonProcessingException e) {
            logger.error("JsonProcessingException occurred while trying to format API response.");
            e.printStackTrace();
            return null;
        }

        return alert;
    }
}
