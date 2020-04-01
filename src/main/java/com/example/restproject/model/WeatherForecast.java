package com.example.restproject.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "weatherForecast")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class WeatherForecast implements Serializable {
    private List<WeatherData> weatherDataList = new ArrayList<>();

    public void addWeatherData(WeatherData data) {
        this.weatherDataList.add(data);
    }

    @XmlElement
    public List<WeatherData> getWeatherDataList() {
        return weatherDataList;
    }

    public void setWeatherDataList(List<WeatherData> weatherDataList) {
        this.weatherDataList = weatherDataList;
    }

    @Override
    public String toString() {
        return "WeatherForecast{" +
                "weatherDataList=" + weatherDataList +
                '}';
    }
}
