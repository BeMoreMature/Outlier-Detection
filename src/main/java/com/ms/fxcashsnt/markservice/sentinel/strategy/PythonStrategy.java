package com.ms.fxcashsnt.markservice.sentinel.strategy;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import com.ms.fxcashsnt.markservice.sentinel.model.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * user: yandongl
 * date: 8/14/2018
 */
public class PythonStrategy implements Strategy {
    private String scriptPath;
    private BufferedWriter writer;
    private BufferedReader reader;
    private Process process;

    private TimeLimiter timeLimiter;
    private Logger logger = LoggerFactory.getLogger(PythonStrategy.class);

    public PythonStrategy(String scriptPath) {
        this.scriptPath = scriptPath;
        timeLimiter = SimpleTimeLimiter.create(
                Executors.newSingleThreadExecutor(
                        new ThreadFactoryBuilder()
                                .setDaemon(true)
                                .setNameFormat("readline-from-python-checker-thread-%d")
                                .build()
                )
        );
    }

    @Override
    public Strategy fit(List<Point> pointList) {
        sendDateToPythonProcess(pointList);
        return this;
    }

    @Override
    public List<Boolean> predict(List<Point> pointList) {
        sendDateToPythonProcess(pointList);
        logger.info("prepare to get predict data from python");
        String boolString = receiveSingleLineFromPythonProcess();
        logger.info("get predict string, size " + boolString.length());
        if (boolString.equals("")) {
            return new LinkedList<>();
        }
        String[] boolStrings = boolString.split(",");
        return Arrays.stream(boolStrings).map(flag -> flag.equals("1")).collect(Collectors.toList());
    }

    public List<Double> getDecisionValues() {
        logger.info("prepare to get decison data from python");
        String decisionString = receiveSingleLineFromPythonProcess();
        logger.info("get decision sting, size " + decisionString.length());
        String[] decisionStrings = decisionString.split(",");
        return Arrays.stream(decisionStrings).map(Double::parseDouble).collect(Collectors.toList());
    }

    public void initPythonProcess() {
        this.close();
        try {
            Properties properties = new Properties();
            properties.load(PythonStrategy.class.getClassLoader().getResourceAsStream("configure.properties"));
            boolean check = new File(properties.getProperty("base_dir") + properties.getProperty("python_strategy_file_folder", scriptPath)).exists();
            if (!check) {
                String message = "No Python Script File In " + properties.getProperty("base_dir") + properties.getProperty("python_strategy_file_folder");
                logger.error(message);
                throw new FileNotFoundException(message);
            }
            ProcessBuilder builder = new ProcessBuilder(properties.getProperty("python_interpreter"), "-W", "ignore", scriptPath);
            builder.directory(new File(properties.getProperty("base_dir") + properties.getProperty("python_strategy_file_folder")));
            process = builder.start();
            logger.info("Loading python process...");
            Thread.sleep(10 * 1000);
        } catch (IOException e) {
            logger.error("CANNOT START PYTHON PROCESS.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        OutputStream stdin = process.getOutputStream();
        InputStream stdout = process.getInputStream();

        writer = new BufferedWriter(new OutputStreamWriter(stdin));
        reader = new BufferedReader(new InputStreamReader(stdout));
    }

    public void close() {
        if (process != null && process.isAlive()) {
            try {
                writer.close();
                reader.close();
            } catch (IOException e) {
                logger.error("CANNOT CLOSE PYTHON PROCESS PROPERLY.");
            }
            process.destroy();
        }
    }

    private void sendDateToPythonProcess(List<Point> pointList) {
        List<String> valueList = pointList.stream()
                .map(Point::getValue)
                .map(String::valueOf)
                .collect(Collectors.toList());
        try {
            writer.write(String.join(",", valueList) + "\n");
            writer.flush();
        } catch (IOException e) {
            logger.error("CANNOT SEND DATA FROM JAVA TO PYTHON PROCESS.", e);
        }
    }


    public String receiveSingleLineFromPythonProcess() {

        String line = "";
        try {
            line = timeLimiter.callWithTimeout(reader::readLine, 120, TimeUnit.SECONDS);
        } catch (TimeoutException | UncheckedTimeoutException | InterruptedException | ExecutionException e) {
            logger.error("TIMEOUT CANNOT READ DATA FROM PYTHON PROCESS TO JAVA");
            initPythonProcess();
        }
//        catch (IOException e) {
//            logger.error("CANNOT READ DATA FROM PYTHON PROCESS TO JAVA.", e);
//        }
        if (line == null) line = "";
        return line;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public BufferedWriter getWriter() {
        return writer;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public Process getProcess() {
        return process;
    }
}
