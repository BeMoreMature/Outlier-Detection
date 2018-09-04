package com.ms.fxcashsnt.markservice.sentinel;

import msjava.base.messaging.ResponseContext;
import msjava.cxfutils.client.messaging.SOAPHDOMMessageSender;
import msjava.hdom.Document;
import msjava.hdom.Element;
import msjava.hdom.Namespace;
import msjava.hdom.messaging.HDOMRequestSender;
import msjava.hdom.output.XMLOutputter;
import org.apache.cxf.binding.soap.Soap12;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.endpoint.EndpointException;
import org.apache.cxf.headers.Header;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class SoapMessageSendTest {

    private static void outputDocument(Document doc, OutputStream out, boolean isPrint) throws IOException {
        if (!isPrint) return;
        XMLOutputter outputter = new XMLOutputter();
        outputter.setIndent("  ");
        outputter.setNewlines(true);
        outputter.setEncoding("UTF-8");
        outputter.output(doc, out);
    }


    private static Document createMarkDocument() {
        Document doc = new Document();

        Element queryRequest = new Element("MarkCurveQueryRequest", "http://xml.ms.com/ns/fxmessage");
        queryRequest.setAttribute("MessageVersion", "1");
        Element MarkCurveQuery = new Element("MarkCurveQuery", "http://xml.ms.com/ns/fxmessage");
        MarkCurveQuery.setAttribute("Context", "LNEOD");
        MarkCurveQuery.setAttribute("PositionDate", "2018-07-11");
        queryRequest.addContent(MarkCurveQuery);

        doc.setRootElement(queryRequest);

        return doc;
    }

    private static void getMarkDocument(Document doc) {
        Element root = doc.getRootElement();
        Element ress = root.getChild("MarkCurveQueryResults", Namespace.getNamespace("http://xml.ms.com/ns/fxmessage"));
        List queryResult = ress.getChildren("MarkCurveQueryResult", Namespace.getNamespace("http://xml.ms.com/ns/fxmessage"));     //get result list
        Iterator iter = queryResult.iterator();
        // for each result, get corresponding attributes
        while (iter.hasNext()) {
            Element res = (Element) iter.next();
            Element curve = res.getChild("MarkCurve", Namespace.getNamespace("http://xml.ms.com/ns/fxmessage"));
            String rate = curve.getAttributeValue("SpotRate");
            String precision = curve.getAttributeValue("ForwardPrecision");
            String curr1 = curve.getAttributeValue("Currency1");
            String curr2 = curve.getAttributeValue("Currency2");

            List<Element> FwdPoints = curve.getChildren("FwdPoint", Namespace.getNamespace("http://xml.ms.com/ns/fxmessage"));
            Iterator iter2 = FwdPoints.iterator();
            while (iter2.hasNext()) {
                Element point = (Element) iter2.next();
                String pts = point.getAttributeValue("Pts");
                String tenor = point.getAttributeValue("Tenor");
                System.out.println(pts + "**" + tenor);
            }
        }

    }

//    @Test
    public void soapMessageSendTest() {
        try {
            HDOMRequestSender soapRequestSender = new SOAPHDOMMessageSender("ktcp://nyfxriskuat5:15514");
            ResponseContext<Document> responseContext = soapRequestSender.request(createMarkDocument());
            Document responseDoc = responseContext.getMessage();
            outputDocument(responseDoc, System.out, false);
            getMarkDocument(responseDoc);
            assertThat(responseDoc.getContent().size(), Matchers.greaterThan(0));
        } catch (TimeoutException | IOException | EndpointException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void emptyHeaderTest() {
        SoapMessage soapMessage = new SoapMessage(Soap12.getInstance());

        List<Header> headers = soapMessage.getHeaders();
        assertThat(headers.size(), is(0));
    }


}
