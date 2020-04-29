package com.restproject.model;

import com.restproject.controller.Controller;

import java.time.LocalDate;

public class ProcessThread extends Thread {
    private Controller controller;
    private WeatherDataList weatherDataList;
    private LocalDate date;
    private float lat;
    private float lon;
    private Exception thrownException;

    public ProcessThread(Controller controller, WeatherDataList weatherDataList, float lat, float lon) {
        this.controller = controller;
        this.weatherDataList = weatherDataList;
        this.lat = lat;
        this.lon = lon;
    }

    public ProcessThread(Controller controller, WeatherDataList weatherDataList, LocalDate localDate, float lat, float lon) {
        this.controller = controller;
        this.weatherDataList = weatherDataList;
        this.date = localDate;
        this.lat = lat;
        this.lon = lon;
    }

    public Exception getThrownException() {
        return thrownException;
    }

    public void setThrownException(Exception thrownException) {
        this.thrownException = thrownException;
    }

    public WeatherDataList getWeatherDataList() {
        return weatherDataList;
    }

    @Override
    public void run() {
        if (this.date != null) {
            weatherDataList.addWeatherData(controller.future(date, lat, lon));
        } else {
            try {
                weatherDataList.addWeatherData(controller.today(lat, lon));
            } catch (InaccessibleAPIException e) {
                this.setThrownException(e);
            }
        }
    }
}
