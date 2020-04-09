package com.example.restproject.controller;

import com.example.restproject.model.ExtremeWeatherData;
import com.example.restproject.model.WeatherData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class WeatherBitProcessor implements Processor{
    private int dataStartYear = 1990;

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

//            response = "{\"locations\":{\"Sumy,Ukraine\":{\"stationContributions\":null,\"values\":[{\"wdir\":209.47,\"period\":\"1990 1990\",\"temp\":8.0,\"maxt\":30.2,\"visibility\":7.8,\"wspd\":50.4,\"heatindex\":37.5,\"cloudcover\":285.2,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-16.6,\"datetime\":null,\"precip\":354.4,\"snowdepth\":7.1,\"sealevelpressure\":1015.9,\"dew\":3.8,\"humidity\":77.51,\"wgust\":null,\"precipcover\":1.24,\"windchill\":-22.6},{\"wdir\":189.79,\"period\":\"1991 1991\",\"temp\":7.3,\"maxt\":33.2,\"visibility\":7.7,\"wspd\":50.4,\"heatindex\":43.6,\"cloudcover\":279.2,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-22.6,\"datetime\":null,\"precip\":752.1,\"snowdepth\":6.8,\"sealevelpressure\":1018.0,\"dew\":3.2,\"humidity\":77.9,\"wgust\":null,\"precipcover\":1.27,\"windchill\":-32.0},{\"wdir\":206.3,\"period\":\"1992 1992\",\"temp\":7.2,\"maxt\":33.1,\"visibility\":7.6,\"wspd\":61.2,\"heatindex\":32.2,\"cloudcover\":276.4,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-16.9,\"datetime\":null,\"precip\":409.7,\"snowdepth\":7.6,\"sealevelpressure\":1016.6,\"dew\":2.4,\"humidity\":75.76,\"wgust\":null,\"precipcover\":1.15,\"windchill\":-25.0},{\"wdir\":190.68,\"period\":\"1993 1993\",\"temp\":5.7,\"maxt\":31.7,\"visibility\":8.2,\"wspd\":72.0,\"heatindex\":33.9,\"cloudcover\":279.1,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-21.8,\"datetime\":null,\"precip\":1331.5,\"snowdepth\":9.6,\"sealevelpressure\":1018.1,\"dew\":1.9,\"humidity\":78.37,\"wgust\":null,\"precipcover\":1.61,\"windchill\":-34.1},{\"wdir\":191.3,\"period\":\"1994 1994\",\"temp\":6.6,\"maxt\":33.1,\"visibility\":8.0,\"wspd\":86.4,\"heatindex\":31.8,\"cloudcover\":274.5,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-29.9,\"datetime\":null,\"precip\":1719.7,\"snowdepth\":20.6,\"sealevelpressure\":1017.0,\"dew\":2.1,\"humidity\":76.24,\"wgust\":null,\"precipcover\":0.52,\"windchill\":-39.7},{\"wdir\":192.14,\"period\":\"1995 1995\",\"temp\":7.7,\"maxt\":31.4,\"visibility\":8.3,\"wspd\":72.0,\"heatindex\":30.3,\"cloudcover\":282.1,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-26.3,\"datetime\":null,\"precip\":429.0,\"snowdepth\":10.0,\"sealevelpressure\":1016.2,\"dew\":2.9,\"humidity\":75.18,\"wgust\":null,\"precipcover\":0.05,\"windchill\":-26.3},{\"wdir\":153.73,\"period\":\"1996 1996\",\"temp\":6.2,\"maxt\":34.4,\"visibility\":8.4,\"wspd\":43.2,\"heatindex\":36.2,\"cloudcover\":276.6,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-27.6,\"datetime\":null,\"precip\":628.4,\"snowdepth\":15.2,\"sealevelpressure\":1019.4,\"dew\":1.6,\"humidity\":76.01,\"wgust\":null,\"precipcover\":0.06,\"windchill\":-33.1},{\"wdir\":193.35,\"period\":\"1997 1997\",\"temp\":6.4,\"maxt\":29.2,\"visibility\":8.3,\"wspd\":50.4,\"heatindex\":29.4,\"cloudcover\":281.0,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-28.8,\"datetime\":null,\"precip\":31.0,\"snowdepth\":7.3,\"sealevelpressure\":1015.7,\"dew\":2.3,\"humidity\":77.87,\"wgust\":null,\"precipcover\":0.01,\"windchill\":-40.3},{\"wdir\":184.16,\"period\":\"1998 1998\",\"temp\":6.9,\"maxt\":35.2,\"visibility\":8.5,\"wspd\":43.2,\"heatindex\":35.9,\"cloudcover\":285.5,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-24.3,\"datetime\":null,\"precip\":0.0,\"snowdepth\":10.4,\"sealevelpressure\":1016.5,\"dew\":2.4,\"humidity\":75.98,\"wgust\":null,\"precipcover\":0.0,\"windchill\":-31.2},{\"wdir\":185.28,\"period\":\"1999 1999\",\"temp\":8.4,\"maxt\":34.6,\"visibility\":8.3,\"wspd\":46.8,\"heatindex\":33.8,\"cloudcover\":null,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-22.3,\"datetime\":null,\"precip\":124.9,\"snowdepth\":14.0,\"sealevelpressure\":1016.1,\"dew\":3.3,\"humidity\":74.11,\"wgust\":null,\"precipcover\":0.83,\"windchill\":-30.8},{\"wdir\":198.52,\"period\":\"2000 2000\",\"temp\":7.8,\"maxt\":34.4,\"visibility\":8.4,\"wspd\":46.8,\"heatindex\":33.1,\"cloudcover\":272.3,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-23.9,\"datetime\":null,\"precip\":594.2,\"snowdepth\":12.3,\"sealevelpressure\":1016.5,\"dew\":3.5,\"humidity\":77.18,\"wgust\":null,\"precipcover\":3.25,\"windchill\":-32.3},{\"wdir\":197.78,\"period\":\"2001 2001\",\"temp\":7.5,\"maxt\":35.7,\"visibility\":8.6,\"wspd\":39.6,\"heatindex\":34.7,\"cloudcover\":276.5,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-21.8,\"datetime\":null,\"precip\":728.3,\"snowdepth\":5.5,\"sealevelpressure\":1015.7,\"dew\":3.2,\"humidity\":76.93,\"wgust\":null,\"precipcover\":3.46,\"windchill\":-25.7},{\"wdir\":185.99,\"period\":\"2002 2002\",\"temp\":7.8,\"maxt\":32.7,\"visibility\":8.7,\"wspd\":54.0,\"heatindex\":33.2,\"cloudcover\":277.1,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-26.2,\"datetime\":null,\"precip\":604.2,\"snowdepth\":10.2,\"sealevelpressure\":1016.7,\"dew\":2.7,\"humidity\":74.01,\"wgust\":null,\"precipcover\":2.5,\"windchill\":-34.9},{\"wdir\":192.91,\"period\":\"2003 2003\",\"temp\":6.6,\"maxt\":29.9,\"visibility\":8.6,\"wspd\":46.8,\"heatindex\":29.1,\"cloudcover\":268.4,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-26.0,\"datetime\":null,\"precip\":510.8,\"snowdepth\":17.7,\"sealevelpressure\":1018.2,\"dew\":2.3,\"humidity\":77.08,\"wgust\":null,\"precipcover\":1.92,\"windchill\":-32.7},{\"wdir\":195.65,\"period\":\"2004 2004\",\"temp\":7.7,\"maxt\":30.4,\"visibility\":8.6,\"wspd\":50.4,\"heatindex\":30.9,\"cloudcover\":null,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-20.6,\"datetime\":null,\"precip\":543.9,\"snowdepth\":6.9,\"sealevelpressure\":1016.2,\"dew\":3.7,\"humidity\":78.31,\"wgust\":null,\"precipcover\":2.22,\"windchill\":-27.9},{\"wdir\":181.2,\"period\":\"2005 2005\",\"temp\":8.2,\"maxt\":32.2,\"visibility\":8.8,\"wspd\":39.6,\"heatindex\":32.6,\"cloudcover\":null,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-22.3,\"datetime\":null,\"precip\":508.6,\"snowdepth\":12.4,\"sealevelpressure\":1018.1,\"dew\":3.3,\"humidity\":74.74,\"wgust\":null,\"precipcover\":2.12,\"windchill\":-26.7},{\"wdir\":199.16,\"period\":\"2006 2006\",\"temp\":7.4,\"maxt\":34.4,\"visibility\":8.4,\"wspd\":43.2,\"heatindex\":34.7,\"cloudcover\":null,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-27.4,\"datetime\":null,\"precip\":1020.0,\"snowdepth\":9.9,\"sealevelpressure\":1017.7,\"dew\":3.6,\"humidity\":79.21,\"wgust\":null,\"precipcover\":2.25,\"windchill\":-36.7},{\"wdir\":194.71,\"period\":\"2007 2007\",\"temp\":9.1,\"maxt\":36.4,\"visibility\":8.9,\"wspd\":43.2,\"heatindex\":34.6,\"cloudcover\":null,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-21.2,\"datetime\":null,\"precip\":503.6,\"snowdepth\":7.7,\"sealevelpressure\":1016.2,\"dew\":3.9,\"humidity\":73.78,\"wgust\":null,\"precipcover\":1.92,\"windchill\":-28.5},{\"wdir\":192.47,\"period\":\"2008 2008\",\"temp\":8.5,\"maxt\":34.7,\"visibility\":8.9,\"wspd\":50.4,\"heatindex\":33.3,\"cloudcover\":null,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-18.8,\"datetime\":null,\"precip\":534.0,\"snowdepth\":3.9,\"sealevelpressure\":1017.1,\"dew\":4.0,\"humidity\":76.56,\"wgust\":null,\"precipcover\":2.03,\"windchill\":-29.4},{\"wdir\":183.29,\"period\":\"2009 2009\",\"temp\":8.3,\"maxt\":34.1,\"visibility\":8.7,\"wspd\":46.8,\"heatindex\":33.6,\"cloudcover\":null,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-25.5,\"datetime\":null,\"precip\":619.2,\"snowdepth\":10.2,\"sealevelpressure\":1016.6,\"dew\":3.4,\"humidity\":75.23,\"wgust\":null,\"precipcover\":2.43,\"windchill\":-33.5},{\"wdir\":166.01,\"period\":\"2010 2010\",\"temp\":8.8,\"maxt\":38.7,\"visibility\":8.9,\"wspd\":46.8,\"heatindex\":37.0,\"cloudcover\":null,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-26.4,\"datetime\":null,\"precip\":586.9,\"snowdepth\":26.9,\"sealevelpressure\":1016.1,\"dew\":3.2,\"humidity\":72.36,\"wgust\":null,\"precipcover\":1.99,\"windchill\":-35.3},{\"wdir\":209.16,\"period\":\"2011 2011\",\"temp\":7.6,\"maxt\":33.2,\"visibility\":9.1,\"wspd\":43.2,\"heatindex\":34.3,\"cloudcover\":59.5,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-21.5,\"datetime\":null,\"precip\":426.0,\"snowdepth\":13.0,\"sealevelpressure\":1018.0,\"dew\":3.0,\"humidity\":75.67,\"wgust\":null,\"precipcover\":1.83,\"windchill\":-28.1},{\"wdir\":189.84,\"period\":\"2012 2012\",\"temp\":8.2,\"maxt\":36.0,\"visibility\":9.4,\"wspd\":46.8,\"heatindex\":34.7,\"cloudcover\":58.4,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-27.2,\"datetime\":null,\"precip\":790.2,\"snowdepth\":6.7,\"sealevelpressure\":1017.0,\"dew\":3.4,\"humidity\":75.57,\"wgust\":54.0,\"precipcover\":2.97,\"windchill\":-33.1},{\"wdir\":194.94,\"period\":\"2013 2013\",\"temp\":8.4,\"maxt\":31.9,\"visibility\":10.1,\"wspd\":39.4,\"heatindex\":31.0,\"cloudcover\":65.8,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-20.8,\"datetime\":null,\"precip\":1042.2,\"snowdepth\":6.6,\"sealevelpressure\":1015.6,\"dew\":4.1,\"humidity\":77.76,\"wgust\":64.8,\"precipcover\":4.48,\"windchill\":-22.6},{\"wdir\":169.78,\"period\":\"2014 2014\",\"temp\":8.1,\"maxt\":33.9,\"visibility\":9.9,\"wspd\":39.7,\"heatindex\":33.9,\"cloudcover\":55.8,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-25.6,\"datetime\":null,\"precip\":903.0,\"snowdepth\":4.6,\"sealevelpressure\":1018.7,\"dew\":2.4,\"humidity\":71.5,\"wgust\":68.4,\"precipcover\":3.39,\"windchill\":-37.3},{\"wdir\":195.5,\"period\":\"2015 2015\",\"temp\":8.7,\"maxt\":34.0,\"visibility\":10.1,\"wspd\":49.8,\"heatindex\":39.9,\"cloudcover\":59.5,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-22.1,\"datetime\":null,\"precip\":958.3,\"snowdepth\":9.7,\"sealevelpressure\":1017.8,\"dew\":3.5,\"humidity\":73.83,\"wgust\":71.5,\"precipcover\":3.79,\"windchill\":-28.1},{\"wdir\":195.1,\"period\":\"2016 2016\",\"temp\":7.9,\"maxt\":35.4,\"visibility\":10.2,\"wspd\":46.3,\"heatindex\":38.6,\"cloudcover\":65.3,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-24.6,\"datetime\":null,\"precip\":1359.1,\"snowdepth\":9.6,\"sealevelpressure\":1016.4,\"dew\":3.9,\"humidity\":78.63,\"wgust\":72.1,\"precipcover\":4.93,\"windchill\":-31.1},{\"wdir\":196.84,\"period\":\"2017 2017\",\"temp\":8.5,\"maxt\":33.3,\"visibility\":10.3,\"wspd\":42.8,\"heatindex\":34.6,\"cloudcover\":65.0,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-23.3,\"datetime\":null,\"precip\":882.8,\"snowdepth\":13.8,\"sealevelpressure\":1016.1,\"dew\":3.2,\"humidity\":73.77,\"wgust\":78.3,\"precipcover\":4.24,\"windchill\":-29.6},{\"wdir\":169.86,\"period\":\"2018 2018\",\"temp\":8.2,\"maxt\":33.1,\"visibility\":10.2,\"wspd\":42.6,\"heatindex\":32.1,\"cloudcover\":58.0,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-20.9,\"datetime\":null,\"precip\":872.5,\"snowdepth\":11.0,\"sealevelpressure\":1018.0,\"dew\":2.7,\"humidity\":72.61,\"wgust\":82.8,\"precipcover\":4.02,\"windchill\":-26.8},{\"wdir\":197.26,\"period\":\"2019 2019\",\"temp\":9.2,\"maxt\":32.5,\"visibility\":9.9,\"wspd\":35.7,\"heatindex\":33.0,\"cloudcover\":58.8,\"resolvedAddress\":\"Суми, Україна\",\"mint\":-20.0,\"datetime\":null,\"precip\":755.2,\"snowdepth\":20.8,\"sealevelpressure\":1016.1,\"dew\":4.2,\"humidity\":74.8,\"wgust\":79.2,\"precipcover\":3.98,\"windchill\":-23.8}],\"id\":\"Sumy,Ukraine\",\"address\":\"Суми, Україна\",\"name\":null,\"index\":0,\"latitude\":50.90796,\"longitude\":34.79724,\"distance\":0.0,\"time\":0.0,\"alerts\":null}},\"columns\":{\"wdir\":{\"id\":\"wdir\",\"name\":\"Wind Direction\",\"type\":2,\"format\":null,\"unit\":null,\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"period\":{\"id\":\"period\",\"name\":\"Period\",\"type\":1,\"format\":null,\"unit\":null,\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"temp\":{\"id\":\"temp\",\"name\":\"Temperature\",\"type\":2,\"format\":null,\"unit\":\"degC\",\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"address\":{\"id\":\"address\",\"name\":\"Address\",\"type\":1,\"format\":null,\"unit\":null,\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"maxt\":{\"id\":\"maxt\",\"name\":\"Maximum Temperature\",\"type\":2,\"format\":null,\"unit\":\"degC\",\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"visibility\":{\"id\":\"visibility\",\"name\":\"Visibility\",\"type\":2,\"format\":null,\"unit\":\"km\",\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"wspd\":{\"id\":\"wspd\",\"name\":\"Wind Speed\",\"type\":2,\"format\":null,\"unit\":\"kph\",\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"latitude\":{\"id\":\"latitude\",\"name\":\"Latitude\",\"type\":2,\"format\":null,\"unit\":null,\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"heatindex\":{\"id\":\"heatindex\",\"name\":\"Heat Index\",\"type\":2,\"format\":null,\"unit\":\"degC\",\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"cloudcover\":{\"id\":\"cloudcover\",\"name\":\"Cloud Cover\",\"type\":2,\"format\":null,\"unit\":\"%\",\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"resolvedAddress\":{\"id\":\"resolvedAddress\",\"name\":\"Resolved Address\",\"type\":1,\"format\":null,\"unit\":null,\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"mint\":{\"id\":\"mint\",\"name\":\"Minimum Temperature\",\"type\":2,\"format\":null,\"unit\":\"degC\",\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"datetime\":{\"id\":\"datetime\",\"name\":\"Date time\",\"type\":3,\"format\":null,\"unit\":null,\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"precip\":{\"id\":\"precip\",\"name\":\"Precipitation\",\"type\":2,\"format\":null,\"unit\":\"mm\",\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"snowdepth\":{\"id\":\"snowdepth\",\"name\":\"Snow Depth\",\"type\":2,\"format\":null,\"unit\":\"cm\",\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"sealevelpressure\":{\"id\":\"sealevelpressure\",\"name\":\"Sea Level Pressure\",\"type\":2,\"format\":null,\"unit\":\"Pa\",\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"dew\":{\"id\":\"dew\",\"name\":\"Dew Point\",\"type\":2,\"format\":null,\"unit\":\"degC\",\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"name\":{\"id\":\"name\",\"name\":\"Name\",\"type\":1,\"format\":null,\"unit\":null,\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"humidity\":{\"id\":\"humidity\",\"name\":\"Relative Humidity\",\"type\":2,\"format\":null,\"unit\":\"%\",\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"wgust\":{\"id\":\"wgust\",\"name\":\"Wind Gust\",\"type\":2,\"format\":null,\"unit\":\"kph\",\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"precipcover\":{\"id\":\"precipcover\",\"name\":\"Precipitation Cover\",\"type\":2,\"format\":null,\"unit\":\"%\",\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"windchill\":{\"id\":\"windchill\",\"name\":\"Wind Chill\",\"type\":2,\"format\":null,\"unit\":\"degC\",\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1},\"longitude\":{\"id\":\"longitude\",\"name\":\"Longitude\",\"type\":2,\"format\":null,\"unit\":null,\"expression\":null,\"isSummary\":false,\"aggregateStyle\":-1}}}";

            data = this.extractData(response, date.getYear(), location);
        } catch (IOException e) {
            e.printStackTrace();
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
                this.dataStartYear = Integer.parseInt(dataStartYearString);

                int yearDifference = maxYear - this.dataStartYear - 1;

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
            System.out.println("Got JsonMappingException trying to read data from API Response");
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            System.out.println("Got JsonProcessingException trying to read data from API Response");
            e.printStackTrace();
        }

        return extremeWeatherData;
    }
}
