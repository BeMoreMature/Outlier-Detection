package com.ms.fxcashsnt.markservice.sentinel.ml;

import it.unimi.dsi.fastutil.Hash;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.*;

public class SmoothedZScore {
    private List<Boolean> booleanList;
    public void thresholdingAlgo(List<Double> y, int lag, Double threshold, Double influence) {
        HashMap<String, List<Object>> res = new HashMap<>();
        //init stats instance
        SummaryStatistics stats = new SummaryStatistics();

        //the results (peaks, 1 or -1) of our algorithm
        List<Integer> signals = new ArrayList<Integer>(Collections.nCopies(y.size(), 0));
        //filter out the signals (peaks) from our original list (using influence arg)
        List<Double> filteredY = new ArrayList<Double>(y);
        //the current average of the rolling window
        List<Double> avgFilter = new ArrayList<Double>(Collections.nCopies(y.size(), 0.0d));
        //the current standard deviation of the rolling window
        List<Double> stdFilter = new ArrayList<Double>(Collections.nCopies(y.size(), 0.0d));
        //init avgFilter and stdFilter
        for(int i = 0 ; i < lag - 1; i++){
            stats.addValue(y.get(i));
        }
        avgFilter.set((lag - 1), stats.getMean());
        stdFilter.set((lag - 1), Math.sqrt(stats.getPopulationVariance())); //getStandardDeviation() uses sample variance (not what we want)
        stats.clear();
        //loop input starting at end of rolling window
        for(int i = lag; i < y.size(); i++) {
            //if the distance between the current value and average is enough standard deviations (threshold) away
            if (Math.abs((y.get(i) - avgFilter.get(i-1))) > threshold * stdFilter.get(i-1)) {
                //this is a signal (i.e. peak), determine if it is a positive or negative signal
                signals.set(i, (y.get(i) > avgFilter.get(i-1)) ? 1 : -1);
                //filter this signal out using influence
                filteredY.set(i, (influence * y.get(i)) + ((1-influence) * filteredY.get(i-1)));
            } else {
                //ensure this signal remains a zero
                signals.set(i, 0);
                //ensure this value is not filtered
                filteredY.set(i, y.get(i));
            }
            //update rolling average and deviation
            for(int j = (i - lag); j < i; j++){
                stats.addValue(filteredY.get(j));
            }
            avgFilter.set(i, stats.getMean());
            stdFilter.set(i, Math.sqrt(stats.getPopulationVariance())); //getStandardDeviation() uses sample variance (not what we want)
            stats.clear();
        }
        booleanList = new LinkedList<>();
        for(int i : signals){
            if(Math.abs(i) == 1){
                booleanList.add(true);
            }else{
                booleanList.add(false);
            }
        }
    }

    public void setBooleanList(List<Boolean> booleanList) {
        this.booleanList = booleanList;
    }

    public List<Boolean> getBooleanList() {

        return booleanList;
    }
}
