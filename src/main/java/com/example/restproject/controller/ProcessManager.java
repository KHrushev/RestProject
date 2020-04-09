package com.example.restproject.controller;

import com.example.restproject.model.Data;
import com.example.restproject.model.ExtremeWeatherData;
import com.example.restproject.model.WeatherDataList;
import com.example.restproject.writers.JSONResponseWriter;
import com.example.restproject.writers.WordWriter;
import com.example.restproject.writers.XMLResponseWriter;
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
    private List<Processor> processors = new ArrayList<>();

    public ProcessManager() {
        this.processors.add(new WeatherAPIProcessor());
        this.processors.add(new DarkSkyProcessor());
        this.processors.add(new ClimacellAPIProcessor());
        this.processors.add(new WeatherBitProcessor());
    }

    @RequestMapping("/today")
    public String todayForecast(@RequestParam(value = "lat", defaultValue = "51") float lat, @RequestParam(value = "lon", defaultValue = "0") float lon, @RequestParam(value = "save", defaultValue = "xml") String saveMethod) {
        WeatherDataList weatherDataList = new WeatherDataList();

        ArrayList<ProcessThread> processThreads = new ArrayList<>();

        for (Processor processor: processors) {
            ProcessThread processThread = new ProcessThread(processor, weatherDataList, lat, lon);
            processThread.start();
            processThreads.add(processThread);
        }

        for (ProcessThread processThread: processThreads) {
            try {
                processThread.join();
            } catch (InterruptedException e) {
                return "Got InterruptedException trying to access thread that was getting the API response.";
            }
        }

        return writeData(saveMethod, weatherDataList);
    }

    @RequestMapping("/future")
    public String futureForecast(@RequestParam(value = "date") String date, @RequestParam(value = "lat", defaultValue = "51") float lat, @RequestParam(value = "lon", defaultValue = "0") float lon, @RequestParam(value = "save", defaultValue = "xml") String saveMethod) {
        WeatherDataList weatherDataList = new WeatherDataList();

        LocalDate localDate = null;
        try {
            localDate = LocalDate.parse(date);

            if (localDate.isBefore(LocalDate.now()) || localDate.toEpochDay() > LocalDate.now().plusDays(16).toEpochDay()) {
                throw new IncorrectDateException();
            }

        } catch (DateTimeParseException dateTimeParseException) {
            return "Unable to parse given date, required format is: 'date=YYYY-MM-DD'";
        } catch (IncorrectDateException e) {
            return "Entered date is either too far into the future or it is already in the past, so our services cannot process such request. Maximum forecast available right now is a 16 day forecast.";
        }

        ArrayList<ProcessThread> processThreads = new ArrayList<>();

        for (Processor processor: processors) {
            ProcessThread processThread = new ProcessThread(processor, weatherDataList, localDate, lat, lon);
            processThread.start();
            processThreads.add(processThread);
        }

        for (ProcessThread processThread: processThreads) {
            try {
                processThread.join();
            } catch (InterruptedException e) {
                return "Got InterruptedException trying to access thread that was getting the API response.";
            }
        }

        return writeData(saveMethod, weatherDataList);
    }

    @RequestMapping("/minmaxdata")
    public String extremeWeatherData(@RequestParam(value = "date") String date, @RequestParam(value = "location", defaultValue = "51,50") String location, @RequestParam(value = "save", defaultValue = "xml") String saveMethod) {
        LocalDate localDate = null;
        try {
            localDate = LocalDate.parse(date);
        } catch (DateTimeParseException dateTimeParseException) {
            return "Unable to parse given date, required format for date is: 'YYYY-MM-DD'";
        }

        ExtremeWeatherData extremeWeatherData = new ExtremeWeatherData();

        try {
            for (Processor processor: processors) {
                if (processor.canProcessExtremeData()) {
                    extremeWeatherData = processor.getExtremeWeatherData(localDate, location);
                }
            }
        } catch (IncorrectLocationException locationExc) {
            return "No rows were returned. Please verify the location and dates requested";
        }

        return writeData(saveMethod, extremeWeatherData);
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
                System.out.println("Got JAXBException trying to marshall data into file");
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                System.out.println("Couldn't find file to marshall data into.");
                e.printStackTrace();
            }

            return weatherDataList.getWeatherDataList().toString();
        } else if (data instanceof ExtremeWeatherData) {
            ExtremeWeatherData extremeWeatherData = (ExtremeWeatherData) data;
            try {
                if (saveMethod.equals("xml")) {
                    XMLResponseWriter.write(extremeWeatherData);
                    wordWriter.write("extremeWeatherData.xml");
                } else if (saveMethod.equals("json")) {
                    JSONResponseWriter.write(extremeWeatherData);
                    wordWriter.write("extremeWeatherData.json");
                } else {
                    return "Incorrect Save Parameter.";
                }
            } catch (JAXBException e) {
                System.out.println("Got JAXBException trying to marshall data into file");
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                System.out.println("Couldn't find file to marshall data into.");
                e.printStackTrace();
            }

            return data.toString();
        }
        return "";
    }
}
