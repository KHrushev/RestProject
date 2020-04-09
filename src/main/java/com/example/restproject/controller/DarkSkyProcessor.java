package com.example.restproject.controller;

import com.example.restproject.model.ExtremeWeatherData;
import com.example.restproject.model.WeatherData;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.*;

public class DarkSkyProcessor implements Processor {
    private final int FORECAST_DAY_LIMIT = 7;

    @Override
    public WeatherData today(float lat, float lon) {
        String url = "https://api.darksky.net/forecast/c9667ada3c4d06b8e1f7fd4b856337d3/" + lat + "," + lon + "?exclude=[minutely,hourly,daily,alerts,flags]";

        try {
            String response = getAPIResponse(url);

            WeatherData weatherData = format(response);

            if (response == null || weatherData == null) {
                throw new IOException("Null response or formatted response");
            }

            return weatherData;
        } catch (IOException e) {
            System.out.println("Error occurred while trying to get/format API response.");
            return null;
        }
    }

    @Override
    public WeatherData future(LocalDate date, float lat, float lon) {
        String url = "https://api.darksky.net/forecast/c9667ada3c4d06b8e1f7fd4b856337d3/" + lat + "," + lon + "/?exclude=[currently,minutely,hourly,alerts,flags]&extend=daily";

        try {
            String response = getAPIResponse(url);

            long dateEpochDays = date.toEpochDay();
            long nowEpochDays = LocalDate.now().toEpochDay();
            int difference = (int) (dateEpochDays - nowEpochDays);

            return formatForecast(response, difference);
        } catch (IOException e) {
            System.out.println("Got IOException trying to get forecast from DarkSky API.");
            e.printStackTrace();
            return null;
        }
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

        JsonNode forecast;
        try {
            forecast = mapper.readTree(data);

            WeatherData weatherData = new WeatherData();
            weatherData.setLat((float) forecast.get("latitude").asDouble());
            weatherData.setLon((float) forecast.get("longitude").asDouble());
            weatherData.setTimezone(forecast.get("timezone").asText());

            forecast = forecast.get("currently");

            weatherData.setOb_time(convertEpochTime(forecast, weatherData));

            weatherData.setTemp((float) forecast.get("temperature").asDouble());

            return weatherData;
        } catch (JsonProcessingException e) {
            System.out.println("Error occurred while trying to format API response.");
            return null;
        }
    }

    private WeatherData formatForecast(String data, int dayDifference) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JsonNode forecast;
        try {
            forecast = mapper.readTree(data);

            WeatherData weatherData = new WeatherData();
            weatherData.setLat((float) forecast.get("latitude").asDouble());
            weatherData.setLon((float) forecast.get("longitude").asDouble());
            weatherData.setTimezone(forecast.get("timezone").asText());

            forecast = forecast.get("daily");
            forecast = forecast.get("data");
            forecast = forecast.get(dayDifference);

            weatherData.setOb_time(convertEpochTime(forecast, weatherData));

            float tempMax = (float) forecast.get("temperatureMax").asDouble();
            float tempMin = (float) forecast.get("temperatureMax").asDouble();

            weatherData.setTemp((tempMax+tempMin)/2);

            return weatherData;
        } catch (JsonMappingException e) {
            System.out.println("Got JsonMappingException trying to format forecast data.");
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            System.out.println("Got JsonProcessingException trying to format forecast data.");
            e.printStackTrace();
        }

        return null;
    }

    private LocalDateTime convertEpochTime(JsonNode forecast, WeatherData weatherData) {
        int epochTime = forecast.get("time").asInt();
        ZoneId zoneId = ZoneId.of(weatherData.getTimezone());
        LocalDateTime localDateTime = LocalDateTime.now(zoneId);
        ZoneOffset zoneOffset = zoneId.getRules().getOffset(localDateTime);

        return LocalDateTime.ofEpochSecond(epochTime, 0, zoneOffset);
    }
}
