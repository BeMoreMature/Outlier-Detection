package com.ms.fxcashsnt.markservice.sentinel.model;

import msjava.hdom.Document;
import msjava.hdom.Element;
import msjava.hdom.Namespace;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * user: Carl, WU
 * date: 7/7/2018
 */
public class MarkCurveQueryResponse implements Serializable {
    public Map<String, MarkCurveQueryResult> markCurveQueryResultMap;

    public MarkCurveQueryResponse(Document document) {
        Element root = document.getRootElement();
        Element ress = root.getChild("MarkCurveQueryResults", Namespace.getNamespace("http://xml.ms.com/ns/fxmessage"));
        List<Element> queryResult = ress.getChildren("MarkCurveQueryResult", Namespace.getNamespace("http://xml.ms.com/ns/fxmessage"));     //get result list
        // for each result, we choose currencyPair as key and new object as value
        markCurveQueryResultMap = queryResult.stream()
                .map(MarkCurveQueryResult::new)
                .collect(Collectors.toMap(
                        e -> e.getCurrencyPair(),
                        e -> e
                ));
    }

    public Map<String, MarkCurveQueryResult> getMarkCurveQueryResultMap() {
        return markCurveQueryResultMap;
    }
}
