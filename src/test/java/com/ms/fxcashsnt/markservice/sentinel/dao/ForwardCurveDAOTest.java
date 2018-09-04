package com.ms.fxcashsnt.markservice.sentinel.dao;

import com.ms.fxcashsnt.markservice.sentinel.model.forward.ForwardCurve;
import com.ms.fxcashsnt.markservice.sentinel.model.forward.ForwardPoint;
import com.ms.fxcashsnt.markservice.sentinel.util.MarkCurveDownloader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.util.List;
/**
 * user: Carl,Wu
 * date: 7/20/2018
 */
@ContextConfiguration(locations = {"classpath:spring-core-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ForwardCurveDAOTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ForwardCurveDAO forwardCurveDAO;

    @Autowired
    private MarkCurveDownloader markCurveDownloader;
    @Test
    public void testQuery(){
        String currencyPair = "USD_RSD";
        String context = "LNFRM";
        Instant startTimestamp = Instant.parse("2018-07-20T00:00:00Z");
        Instant endTimestamp = Instant.parse("2018-07-27T23:37:14.922Z");
        markCurveDownloader.downloadEODMarkCurve();
        ForwardCurve forwardCurve=forwardCurveDAO.query(currencyPair,context,startTimestamp,endTimestamp);

        System.out.println("CurrencyPair is "+forwardCurve.getCurrencyPair()+" context is "+forwardCurve.getContext());

        for(List<ForwardPoint> forwardPointList:forwardCurve.getForwardPointMap().values()){
            for(ForwardPoint forwardPoint:forwardPointList) {
                System.out.println(forwardPoint.getPositionDate()+","+forwardPoint.getTenor()+","+forwardPoint.getPts()+","+forwardPoint.getOutright()+","+forwardPoint.getTimestmap());
            }
        }
    }

    @Test
    public void testFwdQueryUniqueCurrencyPairAndContext(){
        List<String> list= forwardCurveDAO.queryUniqueCurrencyPair();
    }

    @Test
    public void testQueryForList(){
        Instant startTimestamp = Instant.parse("2018-07-25T00:00:00.922Z");
        Instant endTimestamp = Instant.parse("2018-07-25T23:37:14.922Z");
        List<ForwardCurve> forwardCurveList=forwardCurveDAO.queryForList(startTimestamp,endTimestamp);
        for(ForwardCurve forwardCurve:forwardCurveList){
            System.out.println("CurrencyPair is "+forwardCurve.getCurrencyPair()+" context is "+forwardCurve.getContext());

            for(List<ForwardPoint> forwardPointList:forwardCurve.getForwardPointMap().values()){
                for(ForwardPoint forwardPoint:forwardPointList) {
                    System.out.println(forwardPoint.getPositionDate()+","+forwardPoint.getTenor()+","+forwardPoint.getPts()+","+forwardPoint.getOutright()+","+forwardPoint.getTimestmap());
                }
            }
        }
    }
}
