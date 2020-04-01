package com.example.restproject.writers;

import com.example.restproject.model.WeatherForecast;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xwpf.usermodel.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class WordWriter {

    public void write(String fileName) {
        XWPFDocument document = new XWPFDocument();

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText("Forecast:");
        titleRun.setBold(true);
        titleRun.setFontFamily("Courier");
        titleRun.setFontSize(20);

        XWPFParagraph content = document.createParagraph();
        content.setAlignment(ParagraphAlignment.BOTH);

        XWPFRun contentRun = content.createRun();
        String forecastString = "";

        if (fileName.endsWith("xml")) {
            forecastString = getForecastStringXML(fileName);
        } else if (fileName.endsWith("json")) {
            forecastString = getForecastStringJSON(fileName);
        }

        contentRun.setText(forecastString);
        contentRun.setBold(false);
        contentRun.setFontFamily("Courier");

        try (FileOutputStream out = new FileOutputStream("output.docx")) {
            document.write(out);
            out.close();
            document.close();
        } catch (FileNotFoundException fnfe) {
            System.out.println("Not able to write data into a Word file because one of the reasons: 1) File is being used; 2) File is not found.");
        } catch (IOException e) {
            System.out.println("Got IOException while trying to write data into Word file.");
        }
    }


    private String getForecastStringXML(String fileName) {
        File forecastFile = new File(fileName);

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(WeatherForecast.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            WeatherForecast forecast = (WeatherForecast) jaxbUnmarshaller.unmarshal(forecastFile);

            return forecast.toString();
        }
        catch (JAXBException e) {
            System.out.println("Got JAXBException trying to read forecast from API response file.");
            e.printStackTrace();
            return "";
        }
    }

    private String getForecastStringJSON(String fileName) {
        try {
            String forecastString = FileUtils.readFileToString(new File(fileName), StandardCharsets.UTF_8);
            forecastString = forecastString.replaceAll(" ", "");

            return forecastString;
        } catch (IOException e) {
            System.out.println("Got IOException trying to read forecast data from JSON file.");
            e.printStackTrace();
            return "";
        }
    }
}