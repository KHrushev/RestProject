package com.restproject.writers;

import com.restproject.model.Alert;
import com.restproject.model.Data;
import com.restproject.model.WeatherDataList;
import org.eclipse.persistence.jaxb.MarshallerProperties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.FileOutputStream;
import java.io.IOException;

public class JSONWriter {

    public static String write(Data data) throws JAXBException {
        try (FileOutputStream stream = new FileOutputStream("response.json")){
            System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");

            JAXBContext context = null;
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
            marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
            marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);

            marshaller.marshal(data, stream);

            if (data instanceof WeatherDataList) {
                return list.getWeatherDataList().toString();
            } else {
                return alert.toString();
            }
        } catch (IOException e) {
            return "Unable to write data into json file. (IOException - " + e.getMessage() + ")";
        }
    }

//    public static void write(Alert data) throws JAXBException, FileNotFoundException {
//        try (FileOutputStream stream = new FileOutputStream("alert.json")) {
//            System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
//
//            JAXBContext context = JAXBContext.newInstance(Alert.class);
//            Marshaller marshaller = context.createMarshaller();
//
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
//            marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
//
//            marshaller.marshal(data, stream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
