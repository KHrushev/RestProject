package com.example.restproject.controller;

import com.example.restproject.model.Alert;
import com.example.restproject.model.Data;
import com.example.restproject.model.WeatherDataList;
import com.example.restproject.writers.JSONResponseWriter;
import com.example.restproject.writers.WordWriter;
import com.example.restproject.writers.XMLResponseWriter;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


@RestController
public class ProcessManager {
    private Logger logger = new LoggingController().logger;
    private List<Processor> processors = new ArrayList<>();

    public ProcessManager() {
        this.processors.add(new WeatherAPIProcessor());
        this.processors.add(new DarkSkyProcessor());
        this.processors.add(new ClimacellAPIProcessor());
    }

    @RequestMapping("/today")
    public String todayForecast(@RequestParam(value = "lat", defaultValue = "51") float lat, @RequestParam(value = "lon", defaultValue = "0") float lon, @RequestParam(value = "save", defaultValue = "xml") String saveMethod) {
        WeatherDataList weatherDataList = new WeatherDataList();

        if (Math.abs(lon) >= 180 || Math.abs(lat) >= 90) {
            return "Invalid coordinates";
        }

        ArrayList<ProcessThread> processThreads = new ArrayList<>();

        for (Processor processor: processors) {
            if (processor.canNowcast()) {
                ProcessThread processThread = new ProcessThread(processor, weatherDataList, lat, lon);
                processThread.start();
                processThreads.add(processThread);
            }
        }

        for (ProcessThread processThread: processThreads) {
            try {
                processThread.join();
            } catch (InterruptedException e) {
                logger.error("Got InterruptedException trying to access thread that was getting the API response.");
                return "Got InterruptedException trying to access thread that was getting the API response.";
            }
        }

        return writeData(saveMethod, weatherDataList);
    }

    @RequestMapping("/future")
    public String futureForecast(@RequestParam(value = "date") String date, @RequestParam(value = "lat", defaultValue = "51") float lat, @RequestParam(value = "lon", defaultValue = "0") float lon, @RequestParam(value = "save", defaultValue = "xml") String saveMethod) {
        WeatherDataList weatherDataList = new WeatherDataList();

        if (Math.abs(lon) >= 180 || Math.abs(lat) >= 90) {
            return "Invalid coordinates";
        }

        LocalDate localDate = null;
        try {
            localDate = LocalDate.parse(date);

            if (localDate.isBefore(LocalDate.now()) || localDate.toEpochDay() > LocalDate.now().plusDays(16).toEpochDay()) {
                throw new IncorrectDateException();
            }

        } catch (DateTimeParseException dateTimeParseException) {
            logger.error("Unable to parse given date, required format is: 'date=YYYY-MM-DD'");
            return "Unable to parse given date, required format is: 'date=YYYY-MM-DD'";
        } catch (IncorrectDateException e) {
            logger.error("Entered date is either too far into the future or it is already in the past, so our services cannot process such request. Maximum forecast available right now is a 16 day forecast.");
            return "Entered date is either too far into the future or it is already in the past, so our services cannot process such request. Maximum forecast available right now is a 16 day forecast.";
        }

        ArrayList<ProcessThread> processThreads = new ArrayList<>();

        for (Processor processor: processors) {
            if(processor.canProcessDate(localDate)) {
                ProcessThread processThread = new ProcessThread(processor, weatherDataList, localDate, lat, lon);
                processThread.start();
                processThreads.add(processThread);
            }
        }

        for (ProcessThread processThread: processThreads) {
            try {
                processThread.join();
            } catch (InterruptedException e) {
                logger.error("Got InterruptedException trying to access thread that was getting the API response.");
                return "Got InterruptedException trying to access thread that was getting the API response.";
            }
        }

        return writeData(saveMethod, weatherDataList);
    }

    @RequestMapping("/alerts")
    public String alerts(@RequestParam(value = "lat", defaultValue = "51") float lat, @RequestParam(value = "lon", defaultValue = "0") float lon, @RequestParam(value = "save", defaultValue = "xml") String saveMethod) {
        Alert alert = null;

        if (Math.abs(lon) >= 180 || Math.abs(lat) >= 90) {
            return "Invalid coordinates";
        }

        try {
            for (Processor processor: processors) {
                if (processor.canProcessAlerts()) {
                    alert = processor.getAlerts(lat, lon);
                }
            }
        } catch (IncorrectLocationException locationExc) {
            logger.error("No rows were returned. Please verify the location and dates requested");
            return "No rows were returned. Please verify the location and dates requested";
        }

        return writeData(saveMethod, alert);
    }

    private String writeData(String saveMethod, Data data) {
        WordWriter wordWriter = new WordWriter();

        if (data instanceof WeatherDataList) {
            WeatherDataList weatherDataList = (WeatherDataList) data;
            try {
                if (saveMethod.equals("xml")) {
                    XMLResponseWriter.write(weatherDataList);
                    wordWriter.write("forecastData.xml");
                } else if (saveMethod.equals("json")) {
                    JSONResponseWriter.write(weatherDataList);
                    wordWriter.write("forecastData.json");
                } else {
                    return "Incorrect Save Parameter.";
                }
            } catch (JAXBException e) {
                logger.error("Got JAXBException trying to marshall data into file");
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                logger.error("Couldn't find file to marshall data into.");
                e.printStackTrace();
            }

            return weatherDataList.getWeatherDataList().toString();
        } else if (data instanceof Alert) {
            Alert alert = (Alert) data;
            try {
                if (saveMethod.equals("xml")) {
                    XMLResponseWriter.write(alert);
                    wordWriter.write("alert.xml");
                } else if (saveMethod.equals("json")) {
                    JSONResponseWriter.write(alert);
                    wordWriter.write("alert.json");
                } else {
                    return "Incorrect Save Parameter.";
                }
            } catch (JAXBException e) {
                logger.error("Got JAXBException trying to marshall data into file");
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                logger.error("Couldn't find file to marshall data into.");
                e.printStackTrace();
            }

            return data.toString();
        }
        return "";
    }
}
