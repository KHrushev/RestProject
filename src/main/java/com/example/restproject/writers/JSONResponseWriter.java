package com.example.restproject.writers;

import com.example.restproject.model.ExtremeWeatherData;
import com.example.restproject.model.WeatherDataList;
import org.eclipse.persistence.jaxb.MarshallerProperties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class JSONResponseWriter {

    public static void write(WeatherDataList data) throws JAXBException, FileNotFoundException {
        System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");

        JAXBContext context = JAXBContext.newInstance(WeatherDataList.class);
        Marshaller marshaller = context.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);

        marshaller.marshal(data, new FileOutputStream("forecastData.json"));
    }

    public static void write(ExtremeWeatherData data) throws JAXBException, FileNotFoundException {
        System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");

        JAXBContext context = JAXBContext.newInstance(ExtremeWeatherData.class);
        Marshaller marshaller = context.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);

        marshaller.marshal(data, new FileOutputStream("extremeWeatherData.json"));
    }
}
