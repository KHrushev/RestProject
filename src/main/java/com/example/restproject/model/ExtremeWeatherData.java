package com.example.restproject.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "extremeWeatherData")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ExtremeWeatherData implements Data {
    private String location;
    private double minTemp;
    private int minTempYear;
    private double maxTemp;
    private int maxTempYear;

    @XmlElement
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @XmlElement
    public double getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(double minTemp) {
        this.minTemp = minTemp;
    }

    @XmlElement
    public int getMinTempYear() {
        return minTempYear;
    }

    public void setMinTempYear(int minTempYear) {
        this.minTempYear = minTempYear;
    }

    @XmlElement
    public double getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }

    @XmlElement
    public int getMaxTempYear() {
        return maxTempYear;
    }

    public void setMaxTempYear(int maxTempYear) {
        this.maxTempYear = maxTempYear;
    }

    @Override
    public String toString() {
        return "ExtremeWeatherData{" +
                "location='" + location + '\'' +
                ", minTemp=" + minTemp +
                ", minTempYear=" + minTempYear +
                ", maxTemp=" + maxTemp +
                ", maxTempYear=" + maxTempYear +
                '}';
    }
}
