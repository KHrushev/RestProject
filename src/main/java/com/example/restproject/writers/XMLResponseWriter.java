package com.example.restproject.writers;

import com.example.restproject.model.WeatherDataList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class XMLResponseWriter {

    public static void write(WeatherDataList data) throws JAXBException, FileNotFoundException {
        JAXBContext context = JAXBContext.newInstance(WeatherDataList.class);
        Marshaller marshaller = context.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        marshaller.marshal(data, new FileOutputStream("forecast.xml"));
    }

}
