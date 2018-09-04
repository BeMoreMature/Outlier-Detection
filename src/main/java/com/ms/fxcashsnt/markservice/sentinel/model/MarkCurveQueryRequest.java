package com.ms.fxcashsnt.markservice.sentinel.model;

import msjava.hdom.Document;
import msjava.hdom.Element;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * user: yandong.liu
 * date: 7/16/2018
 */
public class MarkCurveQueryRequest implements Serializable {
    private LocalDate positionDate;
    private String context;

    public MarkCurveQueryRequest(LocalDate positionDate, String context) {
        this.positionDate = positionDate;
        this.context = context;
    }

    public Document toHdomDocument() {
        Document doc = new Document();

        Element queryRequest = new Element("MarkCurveQueryRequest", "http://xml.ms.com/ns/fxmessage");
        queryRequest.setAttribute("MessageVersion", "1");
        Element MarkCurveQuery = new Element("MarkCurveQuery", "http://xml.ms.com/ns/fxmessage");
        MarkCurveQuery.setAttribute("Context", context);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        MarkCurveQuery.setAttribute("PositionDate", positionDate.format(formatter));
        queryRequest.addContent(MarkCurveQuery);

        doc.setRootElement(queryRequest);

        return doc;
    }
}
