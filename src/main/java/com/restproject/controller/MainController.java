package com.restproject.controller;

import com.restproject.model.*;
import com.restproject.writers.JSONWriter;
import com.restproject.writers.WordWriter;
import com.restproject.writers.XMLWriter;
import org.slf4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


@RestController
public class MainController {
    private Logger logger = new LoggingController().logger;
    private List<Controller> controllers = new ArrayList<>();
    private String lastActionExtension;

    public MainController() {
        this.controllers.add(new WeatherAPIController());
        this.controllers.add(new DarkSkyController());
        this.controllers.add(new ClimacellAPIController());
    }

    @RequestMapping("/today")
    public String todayForecast(@RequestParam(value = "lat", defaultValue = "51") float lat, @RequestParam(value = "lon", defaultValue = "0") float lon, @RequestParam(value = "save", defaultValue = "xml") String saveMethod) {
        WeatherDataList weatherDataList = new WeatherDataList();

        if (Math.abs(lon) >= 180 || Math.abs(lat) >= 90) {
            return "Invalid coordinates";
        }

        ArrayList<ProcessThread> processThreads = new ArrayList<>();

        for (Controller controller : controllers) {
            if (controller.canNowcast()) {
                ProcessThread processThread = new ProcessThread(controller, weatherDataList, lat, lon);
                processThread.start();
                processThreads.add(processThread);
            }
        }

        ArrayList<ProcessThread> finishedThreads = new ArrayList<>();
        while (finishedThreads.size() < 3) {
            for (ProcessThread thread: processThreads) {
                if (thread.getThrownException() != null) {
                    return "One or more of the threads got this exception while processing API requests: " + thread.getThrownException().getMessage();
                }
                if (!thread.isAlive()) {
                    finishedThreads.add(thread);
                }
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

        for (Controller controller : controllers) {
            if(controller.canProcessDate(localDate)) {
                ProcessThread processThread = new ProcessThread(controller, weatherDataList, localDate, lat, lon);
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
            for (Controller controller : controllers) {
                if (controller.canProcessAlerts()) {
                    alert = controller.getAlerts(lat, lon);
                }
            }
        } catch (IncorrectLocationException locationExc) {
            logger.error("No rows were returned. Please verify the location and dates requested");
            return "No rows were returned. Please verify the location and dates requested";
        }

        return writeData(saveMethod, alert);
    }

    @RequestMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody ResponseEntity<ByteArrayResource> download() throws IOException {
        String fileName = "response." + this.lastActionExtension;
        File file = new File(fileName);

        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        HttpHeaders headers = new HttpHeaders();
        if (this.lastActionExtension.equals("docx") || this.lastActionExtension.equals("word")) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=response.docx");
        } else if (this.lastActionExtension.equals("xml")) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=response.xml");
        } else if (this.lastActionExtension.equals("json")) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=response.json");
        }

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    private String writeData(String saveMethod, Data data) {
        if (saveMethod.equals("docx") || saveMethod.equals("word")) {
            WordWriter wordWriter = new WordWriter();

            this.lastActionExtension = saveMethod;

            try {
                return wordWriter.write(data);
            } catch (IOException e) {
                logger.error("Got IOException trying to write data into a word file.", e);
                return "Unable to save data to a word file. (IOException -" + e.getMessage() + ")";
            }
        } else if (saveMethod.equals("xml")) {
            this.lastActionExtension = saveMethod;

            try {
                return XMLWriter.write(data);
            } catch (JAXBException e) {
                return "Unable to write data into xml file.";
            }
        } else if (saveMethod.equals("json")) {
            this.lastActionExtension = saveMethod;
            try {
                return JSONWriter.write(data);
            } catch (JAXBException e) {
                return "Unable to write data into json file.";
            }
        } else {
            return "Invalid save method";
        }
    }
}
