package com.example.restproject.controller;

import com.example.restproject.model.WeatherData;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.jni.Local;
import org.apache.tomcat.jni.Time;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.TimeZone;

public class DarkSkyProcessor implements Processor {
    @Override
    public WeatherData process(float lat, float lon) {
        String url = "https://api.darksky.net/forecast/c9667ada3c4d06b8e1f7fd4b856337d3/" + lat + "," + lon + "?exclude=[minutely,hourly,daily,alerts,flags]";

        try {
            String response = getAPIResponse(url);

            WeatherData weatherData = format(response);

            if (response == null || weatherData == null) {
                throw new IOException("Null response or formatted response");
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

        JsonNode forecast;
        try {
            forecast = mapper.readTree(data);

            WeatherData weatherData = new WeatherData();
            weatherData.setLat((float) forecast.get("latitude").asDouble());
            weatherData.setLon((float) forecast.get("longitude").asDouble());
            weatherData.setTimezone(forecast.get("timezone").asText());

            forecast = forecast.get("currently");

            int epochTime = forecast.get("time").asInt();
            ZoneId zoneId = ZoneId.of(weatherData.getTimezone());
            LocalDateTime localDateTime = LocalDateTime.now(zoneId);
            ZoneOffset zoneOffset = zoneId.getRules().getOffset(localDateTime);

            LocalDateTime dateTime = LocalDateTime.ofEpochSecond(epochTime, 0, zoneOffset);
            weatherData.setOb_time(dateTime);
            weatherData.setTemp((float) forecast.get("temperature").asDouble());

            return weatherData;
        } catch (JsonProcessingException e) {
            System.out.println("Error occurred while trying to format API response.");
            return null;
        }
    }
}
