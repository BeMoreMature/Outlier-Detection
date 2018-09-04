package com.ms.fxcashsnt.markservice.sentinel.dao;

import com.ms.fxcashsnt.markservice.sentinel.model.MarkCurveQueryResult;

import java.util.List;

/**
 * user: yandong.liu
 * date: 7/17/2018
 */
public interface MarkCurveQueryResultDAO {
    void batchSave(List<MarkCurveQueryResult> resultList);

    void printAllRecord();

}
