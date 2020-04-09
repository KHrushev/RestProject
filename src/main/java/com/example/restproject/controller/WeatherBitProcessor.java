package com.example.restproject.controller;

import com.example.restproject.model.ExtremeWeatherData;
import com.example.restproject.model.WeatherData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class WeatherBitProcessor implements Processor{
    private Logger logger = new LoggingController().logger;

    @Override
    public WeatherData today(float lat, float lon) {
        return null;
    }

    @Override
    public WeatherData future(LocalDate date, float lat, float lon) {
        return null;
    }

    @Override
    public ExtremeWeatherData getExtremeWeatherData(LocalDate date, String location) throws IncorrectLocationException {
        String url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/weatherdata/historysummary?chronoUnit=years&breakBy=years&unitGroup=metric&contentType=json&location=" + location + "&key=78XK9IHHB7RHSM6MUXBFL3PJ0";

        ExtremeWeatherData data = null;
        try {
            String response = getAPIResponse(url);

            if (response.contains("No rows were returned. Please verify the location and dates requested")) {
                throw new IncorrectLocationException();
            }

            data = this.extractData(response, date.getYear(), location);
        } catch (IOException e) {
            logger.error("Got Input/Output Exception while trying to get API response from WeatherBit.");
            e.printStackTrace();
            return null;
        }

        return data;
    }

    @Override
    public boolean canNowcast() {
        return false;
    }

    @Override
    public boolean canProcessDate(LocalDate date) {
        return false;
    }

    @Override
    public boolean canProcessExtremeData() {
        return true;
    }

    @Override
    public WeatherData format(String data) {
        return null;
    }

    private ExtremeWeatherData extractData(String response, int maxYear, String location) {
        ExtremeWeatherData extremeWeatherData = new ExtremeWeatherData();
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode data = mapper.readTree(response);
            Iterator<Map.Entry<String, JsonNode>> nodes = data.get("locations").fields();
            while (nodes.hasNext()) {
                Map.Entry<String, JsonNode> entry = nodes.next();

                JsonNode values = entry.getValue();
                values = values.get("values");

                String dataStartYearString = values.get(0).get("period").asText();
                dataStartYearString = dataStartYearString.substring(0, dataStartYearString.lastIndexOf(" "));
                int dataStartYear = Integer.parseInt(dataStartYearString);

                int yearDifference = maxYear - dataStartYear;

                TreeMap<Double, Integer> temperatureByYear = new TreeMap<>();
                for (int i = 0; i < yearDifference; i++) {
                    JsonNode valueNode = values.get(i);

                    extremeWeatherData.setLocation(valueNode.get("resolvedAddress").asText());

                    String yearString = valueNode.get("period").asText();
                    yearString = yearString.substring(0, yearString.lastIndexOf(" "));
                    int year = Integer.parseInt(yearString);

                    double temperature = valueNode.get("temp").asDouble();

                    temperatureByYear.put(temperature, year);
                }

                Map.Entry<Double, Integer> minEntry = temperatureByYear.firstEntry();
                Map.Entry<Double, Integer> maxEntry = temperatureByYear.lastEntry();

                extremeWeatherData.setMinTemp(minEntry.getKey());
                extremeWeatherData.setMinTempYear(minEntry.getValue());

                extremeWeatherData.setMaxTemp(maxEntry.getKey());
                extremeWeatherData.setMaxTempYear(maxEntry.getValue());
            }
        } catch (JsonMappingException e) {
            logger.error("Got JsonMappingException trying to read data from API Response");
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            logger.error("Got JsonProcessingException trying to read data from API Response");
            e.printStackTrace();
        }

        return extremeWeatherData;
    }
}
