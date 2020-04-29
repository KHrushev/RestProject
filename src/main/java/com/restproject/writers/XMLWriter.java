package com.restproject.writers;

import com.restproject.model.Alert;
import com.restproject.model.Data;
import com.restproject.model.WeatherDataList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.FileOutputStream;
import java.io.IOException;

public class XMLWriter {

    public static String write(Data data) throws JAXBException {
        try (FileOutputStream stream = new FileOutputStream("response.xml")){
            JAXBContext context;
            WeatherDataList list = null;
            Alert alert = null;
            if (data instanceof WeatherDataList) {
                list = (WeatherDataList) data;
                context = JAXBContext.newInstance(WeatherDataList.class);
            } else {
                alert = (Alert) data;
                context = JAXBContext.newInstance(Alert.class);
            }

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.marshal(data, stream);

            if (data instanceof WeatherDataList) {
                return list.getWeatherDataList().toString();
            } else {
                return alert.toString();
            }
        } catch (IOException e) {
            return "Unable to write data into an xml file. (IOException - " + e.getMessage() + ")";
        }
    }
}
