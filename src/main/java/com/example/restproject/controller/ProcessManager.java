package com.example.restproject.controller;

import com.example.restproject.model.WeatherData;
import com.example.restproject.model.WeatherForecast;
import com.example.restproject.writers.JSONResponseWriter;
import com.example.restproject.writers.WordWriter;
import com.example.restproject.writers.XMLResponseWriter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


@RestController
public class ProcessManager {
    private List<Processor> processors = new ArrayList<>();

    public ProcessManager() {
        this.processors.add(new WeatherAPIProcessor());
        this.processors.add(new DarkSkyProcessor());
        this.processors.add(new ClimacellAPIProcessor());
    }

    @RequestMapping("/today")
    @ResponseBody
    public String todayForecast(@RequestParam(value = "lat", defaultValue = "51") float lat, @RequestParam(value = "lon", defaultValue = "0") float lon, @RequestParam(value = "save", defaultValue = "xml") String saveMethod) {
        WeatherForecast forecast = new WeatherForecast();

        for (Processor processor: processors) {
            forecast.addWeatherData(processor.process(lat,lon));
        }

        WordWriter wordWriter = new WordWriter();

        try {
            if (saveMethod.equals("xml")) {
                XMLResponseWriter.write(forecast);
            } else if (saveMethod.equals("json")) {
                JSONResponseWriter.write(forecast);
            } else {
                return "Incorrect Save Parameter.";
            }

            wordWriter.write("forecast.json");
        } catch (JAXBException e) {
            System.out.println("Got JAXBException trying to marshall data into file");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't find file to marshall data into.");
            e.printStackTrace();
        }

        return forecast.getWeatherDataList().toString();
    }

    @RequestMapping("/future")
    public void futureForecast(@RequestParam(value = "date") String date, @RequestParam(value = "lat", defaultValue = "51") float lat, @RequestParam(value = "lon", defaultValue = "0") float lon) {
        for (Processor processor: processors) {
            System.out.println(processor.process(lat, lon));
        }
    }

    @RequestMapping("/minmaxdata")
    public void extremeWeatherData(@RequestParam(value = "lat", defaultValue = "51") float lat, @RequestParam(value = "lon", defaultValue = "0") float lon) {
        for (Processor processor: processors) {
            System.out.println(processor.process(lat, lon));
        }
    }

    @RequestMapping("/")
    public String welcome() {
        return "welcome";
    }
}
