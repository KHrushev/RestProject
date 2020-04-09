package com.example.restproject.writers;

import com.example.restproject.model.ExtremeWeatherData;
import com.example.restproject.model.WeatherDataList;
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
        titleRun.setText("Weather Data:");
        titleRun.setBold(true);
        titleRun.setFontFamily("Courier");
        titleRun.setFontSize(20);

        XWPFParagraph content = document.createParagraph();
        content.setAlignment(ParagraphAlignment.BOTH);

        XWPFRun contentRun = content.createRun();
        String forecastString = "";

        if (fileName.endsWith("xml") && fileName.contains("forecast")) {
            forecastString = getForecastStringXML(fileName);
        } else if (fileName.endsWith("json") && (fileName.contains("forecast") || fileName.contains("extreme"))) {
            forecastString = getDataStringJSON(fileName);
        } else if (fileName.endsWith("xml") && fileName.contains("extreme")){
            forecastString = getExtremeDataStringXML(fileName);
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

    private String getExtremeDataStringXML(String fileName) {
        File forecastFile = new File(fileName);

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(ExtremeWeatherData.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            ExtremeWeatherData data = (ExtremeWeatherData) jaxbUnmarshaller.unmarshal(forecastFile);

            return data.toString();
        }
        catch (JAXBException e) {
            System.out.println("Got JAXBException trying to read forecast from API response file.");
            e.printStackTrace();
            return "";
        }
    }

    private String getForecastStringXML(String fileName) {
        File forecastFile = new File(fileName);

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(WeatherDataList.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            WeatherDataList forecast = (WeatherDataList) jaxbUnmarshaller.unmarshal(forecastFile);

            return forecast.getWeatherDataList().toString();
        }
        catch (JAXBException e) {
            System.out.println("Got JAXBException trying to read forecast from API response file.");
            e.printStackTrace();
            return "";
        }
    }

    private String getDataStringJSON(String fileName) {
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
