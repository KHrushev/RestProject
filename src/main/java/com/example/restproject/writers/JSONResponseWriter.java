package com.example.restproject.writers;

import com.example.restproject.model.WeatherForecast;
import org.eclipse.persistence.jaxb.MarshallerProperties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class JSONResponseWriter {

    public static void write(WeatherForecast data) throws JAXBException, FileNotFoundException {
        System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");

        JAXBContext context = JAXBContext.newInstance(WeatherForecast.class);
        Marshaller marshaller = context.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);

        marshaller.marshal(data, new FileOutputStream("forecast.json"));
    }

}