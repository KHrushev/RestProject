package com.example.restproject.controller;

import com.example.restproject.model.WeatherDataList;

import java.time.LocalDate;

public class ProcessThread extends Thread{
    private Processor processor;
    private WeatherDataList weatherDataList;
    private LocalDate date;
    private float lat;
    private float lon;

    public ProcessThread(Processor processor, WeatherDataList weatherDataList, float lat, float lon) {
        this.processor = processor;
        this.weatherDataList = weatherDataList;
        this.lat = lat;
        this.lon = lon;
    }

    public ProcessThread(Processor processor, WeatherDataList weatherDataList, LocalDate localDate, float lat, float lon) {
        this.processor = processor;
        this.weatherDataList = weatherDataList;
        this.date = localDate;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public void run() {
        if (this.date != null) {
            weatherDataList.addWeatherData(processor.future(date, lat, lon));
        } else {
            weatherDataList.addWeatherData(processor.today(lat, lon));
        }
    }
}
