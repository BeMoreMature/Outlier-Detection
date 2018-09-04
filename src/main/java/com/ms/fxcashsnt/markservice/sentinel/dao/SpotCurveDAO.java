package com.ms.fxcashsnt.markservice.sentinel.dao;

import com.ms.fxcashsnt.markservice.sentinel.model.spot.SpotCurve;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * user: yandong.liu
 * date: 7/23/2018
 */
public interface SpotCurveDAO {
    SpotCurve query(String currencyPair, String context, Instant startTimestamp, Instant endTimestamp);

    List<SpotCurve> queryForList(Instant startTimestamp, Instant endTimestamp);

    List<String> queryUniqueCurrencyPair();
}
