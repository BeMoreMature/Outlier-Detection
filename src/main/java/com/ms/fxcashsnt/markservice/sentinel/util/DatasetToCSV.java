package com.ms.fxcashsnt.markservice.sentinel.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.ms.fxcashsnt.markservice.sentinel.model.Point;
import com.ms.fxcashsnt.markservice.sentinel.model.spot.SpotDataSet;
import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatasetToCSV {

    private static final String CSV_SEPARATOR = ",";

    private static final Logger logger = LoggerFactory.getLogger(MarkCurveDownloader.class);

    public void writeToCSV(SpotDataSet spotDataSet){

        try {

            String currency = spotDataSet.getCurrencyPair();
            String region = spotDataSet.getContext();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = Date.from(spotDataSet.getTrainStartTimestamp());
            String formattedStartDate = formatter.format(startDate);
            Date endDate = Date.from(spotDataSet.getTrainEndTimestamp());
            String formattedEndDate = formatter.format(endDate);
            List<Point> pointList = spotDataSet.getTrainPointList();
            File file = new File("C:\\Users\\carwu\\Desktop\\csv\\"+currency+'_'+region+'_'+formattedStartDate+'_'+formattedEndDate+".csv");
            PrintWriter pw = new PrintWriter(file);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Value");
            stringBuffer.append(CSV_SEPARATOR);
            stringBuffer.append("Date");
            stringBuffer.append('\n');
            pw.write(stringBuffer.toString());
            for(int i = 1; i < pointList.size(); i = i + 1){
                StringBuffer oneLine = new StringBuffer();
                oneLine.append(pointList.get(i).getValue());
                oneLine.append(CSV_SEPARATOR);
                Date date = Date.from(pointList.get(i).getTimestamp());
                String formatedDate = formatter2.format(date);
                oneLine.append(formatedDate);
                oneLine.append('\n');
                pw.write(oneLine.toString());
            }
            pw.close();
            logger.info("Successful write");
        } catch (FileNotFoundException e){ logger.error(e.getMessage(), e);}
    }
}
