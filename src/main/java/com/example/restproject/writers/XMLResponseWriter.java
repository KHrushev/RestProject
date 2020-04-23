package com.example.restproject.writers;

import com.example.restproject.model.Alert;
import com.example.restproject.model.WeatherDataList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class XMLResponseWriter {

    public static void write(WeatherDataList data) throws JAXBException, FileNotFoundException {
        try (FileOutputStream stream = new FileOutputStream("forecastData.xml")){
            JAXBContext context = JAXBContext.newInstance(WeatherDataList.class);
            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.marshal(data, stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(Alert data) throws JAXBException, FileNotFoundException {
        try (FileOutputStream fileOutputStream = new FileOutputStream("alert.xml")) {
            JAXBContext context = JAXBContext.newInstance(Alert.class);

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.marshal(data, fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
