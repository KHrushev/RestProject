package com.example.restproject.controller;

import com.example.restproject.model.WeatherData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public interface Processor {
    WeatherData process(float lat, float lon);
    WeatherData format(String data);

    default String getAPIResponse(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        StringBuilder builder = new StringBuilder();

        if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String result;

            while ((result = reader.readLine()) != null) {
                builder.append(result);
            }

            return builder.toString();
        } else {
            System.out.println("Error: " + connection.getResponseCode() + " " + connection.getResponseMessage());
            return null;
        }
    }
}
