package com.ms.fxcashsnt.markservice.sentinel.detector;

import com.ms.fxcashsnt.markservice.sentinel.model.forward.ForwardPoint;
import com.ms.fxcashsnt.markservice.sentinel.model.MarkCurveQueryResponse;
import com.ms.fxcashsnt.markservice.sentinel.model.MarkCurveQueryResult;
import com.ms.fxcashsnt.markservice.sentinel.model.report.Report;
import com.ms.fxcashsnt.markservice.sentinel.util.MarkCurveDownloader;
import com.ms.fxcashsnt.markservice.sentinel.util.MarkServiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static com.ms.fxcashsnt.markservice.sentinel.util.Utility.changeInRange;
import static com.ms.fxcashsnt.markservice.sentinel.util.Utility.findAllPairsInList;

/**
 * user: Carl,Wu
 * date: 7/26/2018
 * this detector is used to detect the difference between four places at the end of day
 */

@Service
public class CrossRegionDetector {
    @Autowired
    private MarkCurveDownloader markCurveDownloader;
    // key is the currencyPair and context, value is the which forwardpoints or all of them
    public TreeMap<String,Map> outlierList = new TreeMap<>();

    public List<Report> res = new ArrayList<>();

    public TreeMap<String,Map> detectRegion(double threshold, LocalDate positionDate){
        List <MarkCurveQueryResponse> list = markCurveDownloader.downloadMarkCurve(MarkServiceConstants.EndContextList, Collections.singletonList(positionDate));

        // find all possible currencyPair
        HashSet <String> currenryPairSet = new HashSet<>();
        for(MarkCurveQueryResponse response : list){
            currenryPairSet.addAll(response.getMarkCurveQueryResultMap().keySet());
        }
        // remove the legacy ccys
        HashSet <String> removeCurrencyPairSet = new HashSet<>();
        for(String key : currenryPairSet){
            for(String ignoreCurrency : MarkServiceConstants.IgnoreCurrencyList){
                if(key.contains(ignoreCurrency)){
                    removeCurrencyPairSet.add(key);
                }
            }
        }
        currenryPairSet.removeAll(removeCurrencyPairSet);
        for(String key : currenryPairSet){
            ArrayList <MarkCurveQueryResult> resultsForFourRegion = new ArrayList<>();
            // for each currencyPair after filtered, store all existed pairs in the list
            for(MarkCurveQueryResponse response:list){
                if (response.getMarkCurveQueryResultMap().containsKey(key)){
                    resultsForFourRegion.add(response.getMarkCurveQueryResultMap().get(key));
                }
            }
            if(resultsForFourRegion.size() > 1){
                ArrayList <List> pairList = findAllPairsInList(resultsForFourRegion);
                for(List<MarkCurveQueryResult> resultsPair : pairList){
                    MarkCurveQueryResult result1 = resultsPair.get(0);
                    MarkCurveQueryResult result2 = resultsPair.get(1);
                    if(!changeInRange(result1.getSpotRate(),result2.getSpotRate(),threshold)){
                        HashMap<String,Double> regionMap = new HashMap<>();
                        for (MarkCurveQueryResult result : resultsForFourRegion){
                            regionMap.put(result.getContext(),result.getSpotRate());
                        }
                        outlierList.put(key.replace("USD","").replace("_","")+"_SpotRate",regionMap);
                    }else{
                        for(ForwardPoint forwardPoint1:result1.getForwardPointList()){
                            for(ForwardPoint forwardPoint2:result2.getForwardPointList()){
                                if (forwardPoint1.getTenor().equals(forwardPoint2.getTenor()) && !changeInRange(forwardPoint1.getPts(),forwardPoint2.getPts(),threshold)){
                                    HashMap<String,Double> regionMap = new HashMap<>();
                                    for (MarkCurveQueryResult result : resultsForFourRegion){
                                        for(ForwardPoint fwdpoint : result.getForwardPointList()){
                                            if(fwdpoint.getTenor().equals(forwardPoint1.getTenor())){
                                                regionMap.put(result.getContext(),fwdpoint.getPts());
                                            }
                                        }
                                    }
                                    outlierList.put(key.replace("USD","").replace("_","")+"_"+forwardPoint1.getTenor(),regionMap);
                                }
                            }
                        }
                    }
                }
            }
        }
//        RegionReportBuilder regionReportBuilder = new RegionReportBuilder();
//        List<RegionReport> res = regionReportBuilder.build(outlierList);
        return outlierList;
    }

}
