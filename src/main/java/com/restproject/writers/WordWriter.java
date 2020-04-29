package com.restproject.writers;

import com.restproject.model.Alert;
import com.restproject.model.Data;
import com.restproject.model.WeatherDataList;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class WordWriter {

    public String write(Data data) throws IOException {
        XWPFDocument templateDocument = new XWPFDocument(new FileInputStream("template.docx"));
        XWPFParagraph templateParagraph = templateDocument.getLastParagraph();

        XWPFDocument document = new XWPFDocument(new FileInputStream("template.docx"));

        if (data instanceof WeatherDataList) {
            WeatherDataList dataList = (WeatherDataList) data;

            addParagraphs(document, templateParagraph, dataList);

            document.write(new FileOutputStream("response.docx"));

            document.close();

            return dataList.toString();
        } else if (data instanceof Alert) {
            Alert alert = (Alert) data;

            addParagraph(document, alert);

            document.write(new FileOutputStream("response.docx"));

            document.close();

            return alert.toString();
        }

        return "Error";
    }

    private void addParagraphs(XWPFDocument document, XWPFParagraph templateParagraph, WeatherDataList data) {
        for (int i = 0; i < data.getWeatherDataList().size() && i < document.getParagraphs().size(); i++) {

            document.createParagraph();
            document.setParagraph(deepCloneParagraph(document.getLastParagraph(), templateParagraph), document.getParagraphs().size()-1);

            XWPFParagraph paragraph = document.getParagraphs().get(i);

            for (XWPFRun run: paragraph.getRuns()) {
                if (run.getText(0) != null) {
                    String text = run.getText(0);
                    text = text.replace("lat", String.valueOf(data.getWeatherDataList().get(i).getLat()));
                    text = text.replace("lon", String.valueOf(data.getWeatherDataList().get(i).getLon()));
                    text = text.replace("tz", String.valueOf(data.getWeatherDataList().get(i).getTimezone()));
                    text = text.replace("temp", String.valueOf(data.getWeatherDataList().get(i).getTemp()));
                    text = text.replace("_time", String.valueOf(data.getWeatherDataList().get(i).getOb_time()));
                    run.setText(text, 0);
                }
            }
        }
    }

    private void addParagraph(XWPFDocument document, Alert alert) {
        for (XWPFRun run: document.getLastParagraph().getRuns()) {
            if (run.getText(0) != null) {
                String text = run.getText(0);
                text = text.replace("lat", String.valueOf(alert.getLat()));
                text = text.replace("lon", String.valueOf(alert.getLon()));
                text = text.replace("tz", String.valueOf(alert.getTimezone()));
                text = text.replace("temp", String.valueOf(alert.getSeverity()));
                text = text.replace("_time", String.valueOf(alert.getDescription()));
                run.setText(text, 0);
            }
        }
    }

    private XWPFParagraph  deepCloneParagraph(XWPFParagraph clone, XWPFParagraph source) {
        CTPPr pPr = clone.getCTP().isSetPPr() ? clone.getCTP().getPPr() : clone.getCTP().addNewPPr();
        pPr.set(source.getCTP().getPPr());
        for (XWPFRun r : source.getRuns()) {
            XWPFRun nr = clone.createRun();
            nr.getCTR().set(r.getCTR());
        }

        return clone;
    }
}
