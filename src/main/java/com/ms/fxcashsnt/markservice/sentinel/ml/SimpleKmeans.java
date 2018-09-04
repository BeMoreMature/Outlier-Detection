package com.ms.fxcashsnt.markservice.sentinel.ml;

import com.ms.fxcashsnt.markservice.sentinel.model.Point;
import com.ms.fxcashsnt.markservice.sentinel.model.report.Report;
import com.ms.fxcashsnt.markservice.sentinel.util.MarkCurveDownloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import treadmill.api.StringUtil;
import weka.clusterers.FilteredClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.matrix.LinearRegression;
import weka.filters.unsupervised.attribute.Remove;

import java.io.*;
import java.time.Instant;
import java.util.*;

public class SimpleKmeans {

    Instances cpu = null;

    SimpleKMeans kmeans;

    private static final Logger logger = LoggerFactory.getLogger(MarkCurveDownloader.class);

    public void loadArff(String arffInput){
        DataSource source = null;
        try {
            source = new DataSource(arffInput);
            cpu = source.getDataSet();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public Report clusterData(){
        Report report = new Report();
        List<Point> pointList = new ArrayList<>();
        List<Boolean> booleanList = new ArrayList<>();
        kmeans = new SimpleKMeans();
        kmeans.setSeed(10);
        try {
            kmeans.setPreserveInstancesOrder(true);
            kmeans.setNumClusters(100);
            kmeans.buildClusterer(cpu);

            // print out the cluster centroids
            Instances centroids = kmeans.getClusterCentroids();
            List<Integer> indexList = new ArrayList<Integer>();
            for (int i = 0; i < 100; i++) {
//                System.out.print("Cluster " + i + " size: " + kmeans.getClusterSizes()[i]);
//                System.out.println(" Centroid: " + centroids.instance(i));
                if(kmeans.getClusterSizes()[i]==1){
                    indexList.add(i);
                }
            }

            int[] assignments = kmeans.getAssignments();
            int i = 0;
            for(int clusterNum : assignments) {
                pointList.add(new Point(cpu.instance(i).value(0),Instant.now()));
                if(indexList.contains(clusterNum)){
                    System.out.printf("Instance %d -> Cluster %d\n", i, clusterNum);
                    booleanList.add(true);
                }else{
                    booleanList.add(false);
                }
                i++;
            }
            report.setPointList(pointList);
            report.setBooleanList(booleanList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return report;
    }

    public void addColumn(String path,String fileName, List<Boolean> booleanList) throws IOException {
        BufferedReader br=null;
        BufferedWriter bw=null;
        final String lineSep=System.getProperty("line.separator");

        try {
            File file = new File(path, fileName);
            File file2 = new File(path, "Result_"+fileName);//so the
            //names don't conflict or just use different folders

            br = new BufferedReader(new InputStreamReader(new FileInputStream(file))) ;
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2)));
            String line = br.readLine();
            bw.write(line+", Outlier"+lineSep);
            int i=0;
            for ( line = br.readLine(); line != null; line = br.readLine(),i++)
            {
                booleanList.stream()
                        .anyMatch(Boolean::booleanValue);
                String addedColumn = String.valueOf(booleanList.get(i));
                bw.write(line+","+addedColumn+lineSep);
            }
            logger.info("DONE");
        }catch(Exception e){
            logger.error(e.getMessage(), e);
        }finally  {
            if(br!=null)
                br.close();
            if(bw!=null)
                bw.close();
        }

    }

}
