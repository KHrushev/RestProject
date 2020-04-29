package com.restproject.model;

import com.restproject.writers.LocalDateTimeAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;

@XmlRootElement(name = "weatherData")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class WeatherData implements Serializable {
    private Float lat;
    private Float lon;
    private Float temp;
    private String timezone;
    private LocalDateTime ob_time;

    public WeatherData(float lat, float lon, float temp, String timezone, LocalDateTime ob_time) {
        this.lat = lat;
        this.lon = lon;
        this.temp = temp;
        this.timezone = timezone;
        this.ob_time = ob_time;
    }

    public WeatherData() {}

    @XmlElement
    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    @XmlElement
    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    @XmlElement
    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    @XmlElement
    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @XmlElement
    @XmlJavaTypeAdapter(value = LocalDateTimeAdapter.class)
    public LocalDateTime getOb_time() {
        return ob_time;
    }

    public void setOb_time(LocalDateTime ob_time) {
        this.ob_time = ob_time;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", temp=" + temp +
                ", timezone='" + timezone + '\'' +
                ", observationTime=" + ob_time +
                "}";
    }
}
