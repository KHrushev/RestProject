package com.example.restproject.writers;

import com.example.restproject.model.Alert;
import com.example.restproject.model.WeatherDataList;
import org.eclipse.persistence.jaxb.MarshallerProperties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class JSONResponseWriter {

    public static void write(WeatherDataList data) throws JAXBException, FileNotFoundException {
        try (FileOutputStream stream = new FileOutputStream("forecastData.json")){
            System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");

            JAXBContext context = JAXBContext.newInstance(WeatherDataList.class);
            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
            marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);

            marshaller.marshal(data, stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(Alert data) throws JAXBException, FileNotFoundException {
        try (FileOutputStream stream = new FileOutputStream("alert.json")) {
            System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");

            JAXBContext context = JAXBContext.newInstance(Alert.class);
            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
            marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);

            marshaller.marshal(data, stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
