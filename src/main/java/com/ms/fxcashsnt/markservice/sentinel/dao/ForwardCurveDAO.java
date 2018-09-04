package com.ms.fxcashsnt.markservice.sentinel.dao;

import com.ms.fxcashsnt.markservice.sentinel.model.forward.ForwardCurve;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * user: yandong.liu
 * date: 7/23/2018
 */
public interface ForwardCurveDAO {
    ForwardCurve query(String currencyPair, String context, Instant startTimestamp, Instant endTimestamp);

    List<ForwardCurve> queryForList(Instant startTimestamp, Instant endTimestamp);

    List<String> queryUniqueCurrencyPair();
}
