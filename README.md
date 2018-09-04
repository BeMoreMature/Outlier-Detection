# Outlier-Detection
> This project helps to detect anomaly points in marking curve system. 

## Introduction
This sentinel project consists of three main parts.
1. Collect spot and forward market data of different currencies into database
2. Rule-based anomaly detection
3. Machine learning based anomaly detection

![](demo.png)

## Getting Started
This project is developed with spring mvc, angular6 and python. To get started the Git project needs to be cloned.
```sh
$ git clone http://yandongl@stashblue.ms.com/atlassian-stash/scm/~yandongl/sentinel.git
```

**gradle**
The following steps must be followed to install the dependencies and build the war package.
```sh
$ module load msjava/oraclejdk/1.8.0_92
$ module load ossjava/gradle/4.2.1ms
$ cd sentinel/sentinel/trunk/src
$ gradle build war
```

**configuration**
In order to deploy the war package into tomcat and run successfully, some path need to be configured properly.
```
\sentinel\sentinel\trunk\src\src\main\resources\configure.properties
```
In the above configure file, there are some variables need to be configured. 
```yaml
base_dir=C:\\Users\\abc  # the absolute path of the git folder
jdbc.url=jdbc:sqlite:C:\\Users\\abc\\mark_history.sqlite  # the absolute path of the sqlite db file
mark_server_address=ktcp://nyfxriskuat4:15514  # the mark curve SOAP server address
python_interpreter=M:\\dist\\python\\PROJ\\core\\3.4.4\\bin\\python.exe  # the absolute path of python interpreter in AFS. If this project is deployed on a linux machine, change the M:\\ driver to the \\v folder.
mark_curve_downloader.query_interval=60  # the interval of intraday marking data downloader
```

## Strategies
For now, we have already implemented eight strategies. They are 
>* CHANGE_SO_FAST 
>* REMAIN_UNCHANGED
>* CROSS_REGION
>* ELLIPTIC_ENVELOP
>* ISOLATION_FOREST
>* ONE_CLASS_SVM
>* LOCAL_OUTLIER_FACTOR
>* SMOOTHED_Z_SCORE

If you want to change the threshold or the range of time for each strategy, please modify the following file. Each strategy has a load function, the threshold, start timestamp of training set, end timestamp of training set, start timestamp of test set and end timestamp of test set are configured in each load function. 
```
\sentinel\sentinel\trunk\src\src\main\java\com\ms\fxcashsnt\markservice\sentinel\util\ReportCache.java
```
**add a new strategy**
This framework can be extended to new strategies, whether it is rule based or machine learning based. The following steps guide how to add a strategy and how to display the result in the angular webpage.
##### step 1:
Implement the **Strategy** interface, which contains two methods **fit** method and **predict** method.
```
\sentinel\sentinel\trunk\src\src\main\java\com\ms\fxcashsnt\markservice\sentinel\strategy
```

##### step 2:
For java based strategy, extend the **AbstractAbnormalityDetector**. For python based strategy, extend the **AbstractPythonAbnormalityDetector** in the following folder. These two abstract class implement the basic method for load data from database and use the strategy to analyze the data.
```
\sentinel\sentinel\trunk\src\src\main\java\com\ms\fxcashsnt\markservice\sentinel\detector
```

##### step 3:
Set the threshold and time range in the above **ReportCache.java**.

##### step 4:
Return the results to the angular application. The api method is implemented in the following file.
```
\sentinel\sentinel\trunk\src\src\main\java\com\ms\fxcashsnt\markservice\sentinel\controller\api\AnomalyReportController.java
```

## Webpage
This webpage is developed with angular 6. The source code of the angular project is located in 
```
\sentinel\sentinel\trunk\src\angular\src\main\app
```
Please read the document in [Artifactory NPM](http://wiki.ms.com/NodeJS/Artifactory/WebHome#Getting_Started) to enable the npm and yarn command in Morgan Stanley.
When the development of angular is finished, the production code can be deployed with the spring mvc framework. You can run the following bat file in command line to compile the angular project and move the build result into the spring mvc folder.
```
\sentinel\sentinel\trunk\src\angular\src\deploy.bat
```

## Email
The email is sent by the [SMTP protocol](https://www.tutorialspoint.com/javamail_api/javamail_api_send_html_in_email.htm). And the information was displayed by [freemarker template](http://wiki.ms.com/MSJavaCookbook/HowDoIProcessATemplateInSpring). <br />
For different receivers, you can change the parameter in Bean
```
<bean id="emailNotifier" class="com.ms.fxcashsnt.markservice.sentinel.mail.EmailNotifier">
        <constructor-arg index="0" value="Carl.Wu@morganstanley.com"/>
</bean>
```
In order to adding new strategy

##### step 1:
In the **getTemplate** method of **FTLMail** class. Add a parameter and keep into the map.

**Attention ! ! !**

We just receive pararmeters in the form of List of **Report** Object (self-defined)
```java
    private String currencyPair;
    private String context;
    private String tenor;
    private double score;
    private Instant startTimestamp;
    private Instant endTimestamp;
    private List<Point> pointList;
    private List<Boolean> booleanList;
```
##### step 2: 
We also need to add new table in the **mail-template.ftl**. 

If you still have some problems, please followed by **sendEmailTest**.

## Collect data into database
This part is mainly focus on parse **MarkCurveQueryResponse** and keep into SQLite database. 

* In class **MarkCurveDownloader**

    * Method **downloadMarkCurve** is responsible for getting list of **MarkCurveQueryResponse** from certain region and positionDate

        Each **MarkCurveQueryResponse** have a markCurveQueryResultMap, key is currencyPair (currency1_currency2), value is **MarkCurveQueryResult**.

        The class **MarkCurveQueryResult** have attributes like:
        ```java
            private LocalDate spotDate;
            private double spotRate;
            private double forwardPrecision;
            private String currencyPair;
            private Boolean CombinedYieldCurve;
            private String context;

            private LocalDate positionDate;

            private Instant timestamp;

            private List<ForwardPoint> forwardPointList;
        ```

    * Method **writeResponseListIntoDatabase** is responsible for **batchSave** each **MarkCurveQueryResponse** came from method **downloadMarkCurve**.

        Method **batchSave** in the **MarkCurveQueryResultDAO**, have two parts.

* In the implement of **MarkCurveQueryResultDAO**

    Firstly, create two tables like:

    ![](tables.png)

    Then, follow SQL as below, merge constant and adjacent records into one record
    ```
        String insertSql = "INSERT INTO SpotTable (ID, CurrencyPair, Region, PositionDate, " +
                "SpotDate, SpotRate, StartTime, EndTime, Cnt) VALUES (?, ?, ?, date(?), date(?), ?, ?, ?, ?)";
        String updateSql = "UPDATE SpotTable SET EndTime = ? , Cnt = Cnt + 1 WHERE ID = ?";
    ```
If you still have some problems, please follow **MarkCurveDownloaderTest**.

## Create data structure from database
In **model** folder, we can find the **SpotDataSet** and **ForwardDataSet** which are provided for our detector.

* In class **SpotDataSetBuilder**, we explore the database and form the data structure like this:
```java
    private String currencyPair;
    private String context;

    private Instant trainStartTimestamp;
    private Instant trainEndTimestamp;

    private Instant testStartTimestamp;
    private Instant testEndTimestamp;

    private List<Point> trainPointList;
    private List<Point> testPointList;
```

* In class **ForwardDataSetBuilder**, we explore the database and form the data structure like this:
```java
    private String currencyPair;
    private String context;

    private Instant trainStartTimestamp;
    private Instant trainEndTimestamp;

    private Instant testStartTimestamp;
    private Instant testEndTimestamp;

    private Map<String, List<Point>> trainPointListMap;
    private Map<String, List<Point>> testPointListMap;
```

Each point contains
```java
    private double value;
    private Instant timestamp;
```

## Detect and apply different strategies

* In class **AbstractAbnormalityDetector**

    * Method **getSpotAnomalyBooleanList** and **getForwardAnomalyBooleanListMap** 

        For different strategies, fit trainPointList and predict testPointList, get booleanList

    * Method **detectSpotAnomaly** and **detectForwardAnomaly**

        Input booleanList, judge if is need report or not, if true, form the List of **Report** (which is received by email and web page)

* In class **AbstractPythonAbnormalityDetector**

    * Same as **AbstractAbnormalityDetector**, add maxReportSize and forwardAnomalyDecisionListMap to filter result# Sentinel Project
> This project helps to detect anomaly points in marking curve system. 

## Introduction
This sentinel project consists of three main parts.
1. Collect spot and forward market data of different currencies into database
2. Rule-based anomaly detection
3. Machine learning based anomaly detection

![](demo.png)

## Getting Started
This project is developed with spring mvc, angular6 and python. To get started the Git project needs to be cloned.
```sh
$ git clone http://yandongl@stashblue.ms.com/atlassian-stash/scm/~yandongl/sentinel.git
```

**gradle**
The following steps must be followed to install the dependencies and build the war package.
```sh
$ module load msjava/oraclejdk/1.8.0_92
$ module load ossjava/gradle/4.2.1ms
$ cd sentinel/sentinel/trunk/src
$ gradle build war
```

**configuration**
In order to deploy the war package into tomcat and run successfully, some path need to be configured properly.
```
\sentinel\sentinel\trunk\src\src\main\resources\configure.properties
```
In the above configure file, there are some variables need to be configured. 
```yaml
base_dir=C:\\Users\\abc  # the absolute path of the git folder
jdbc.url=jdbc:sqlite:C:\\Users\\abc\\mark_history.sqlite  # the absolute path of the sqlite db file
mark_server_address=ktcp://nyfxriskuat4:15514  # the mark curve SOAP server address
python_interpreter=M:\\dist\\python\\PROJ\\core\\3.4.4\\bin\\python.exe  # the absolute path of python interpreter in AFS. If this project is deployed on a linux machine, change the M:\\ driver to the \\v folder.
mark_curve_downloader.query_interval=60  # the interval of intraday marking data downloader
```

## Strategies
For now, we have already implemented eight strategies. They are 
>* CHANGE_SO_FAST 
>* REMAIN_UNCHANGED
>* CROSS_REGION
>* ELLIPTIC_ENVELOP
>* ISOLATION_FOREST
>* ONE_CLASS_SVM
>* LOCAL_OUTLIER_FACTOR
>* SMOOTHED_Z_SCORE

If you want to change the threshold or the range of time for each strategy, please modify the following file. Each strategy has a load function, the threshold, start timestamp of training set, end timestamp of training set, start timestamp of test set and end timestamp of test set are configured in each load function. 
```
\sentinel\sentinel\trunk\src\src\main\java\com\ms\fxcashsnt\markservice\sentinel\util\ReportCache.java
```
**add a new strategy**
This framework can be extended to new strategies, whether it is rule based or machine learning based. The following steps guide how to add a strategy and how to display the result in the angular webpage.
##### step 1:
Implement the **Strategy** interface, which contains two methods **fit** method and **predict** method.
```
\sentinel\sentinel\trunk\src\src\main\java\com\ms\fxcashsnt\markservice\sentinel\strategy
```

##### step 2:
For java based strategy, extend the **AbstractAbnormalityDetector**. For python based strategy, extend the **AbstractPythonAbnormalityDetector** in the following folder. These two abstract class implement the basic method for load data from database and use the strategy to analyze the data.
```
\sentinel\sentinel\trunk\src\src\main\java\com\ms\fxcashsnt\markservice\sentinel\detector
```

##### step 3:
Set the threshold and time range in the above **ReportCache.java**.

##### step 4:
Return the results to the angular application. The api method is implemented in the following file.
```
\sentinel\sentinel\trunk\src\src\main\java\com\ms\fxcashsnt\markservice\sentinel\controller\api\AnomalyReportController.java
```

## Webpage
This webpage is developed with angular 6. The source code of the angular project is located in 
```
\sentinel\sentinel\trunk\src\angular\src\main\app
```
Please read the document in [Artifactory NPM](http://wiki.ms.com/NodeJS/Artifactory/WebHome#Getting_Started) to enable the npm and yarn command in Morgan Stanley.
When the development of angular is finished, the production code can be deployed with the spring mvc framework. You can run the following bat file in command line to compile the angular project and move the build result into the spring mvc folder.
```
\sentinel\sentinel\trunk\src\angular\src\deploy.bat
```

## Email
The email is sent by the [SMTP protocol](https://www.tutorialspoint.com/javamail_api/javamail_api_send_html_in_email.htm). And the information was displayed by [freemarker template](http://wiki.ms.com/MSJavaCookbook/HowDoIProcessATemplateInSpring). <br />
For different receivers, you can change the parameter in Bean
```
<bean id="emailNotifier" class="com.ms.fxcashsnt.markservice.sentinel.mail.EmailNotifier">
        <constructor-arg index="0" value="Carl.Wu@morganstanley.com"/>
</bean>
```
In order to adding new strategy

##### step 1:
In the **getTemplate** method of **FTLMail** class. Add a parameter and keep into the map.

**Attention ! ! !**

We just receive pararmeters in the form of List of **Report** Object (self-defined)
```java
    private String currencyPair;
    private String context;
    private String tenor;
    private double score;
    private Instant startTimestamp;
    private Instant endTimestamp;
    private List<Point> pointList;
    private List<Boolean> booleanList;
```
##### step 2: 
We also need to add new table in the **mail-template.ftl**. 

If you still have some problems, please followed by **sendEmailTest**.

## Collect data into database
This part is mainly focus on parse **MarkCurveQueryResponse** and keep into SQLite database. 

* In class **MarkCurveDownloader**

    * Method **downloadMarkCurve** is responsible for getting list of **MarkCurveQueryResponse** from certain region and positionDate

        Each **MarkCurveQueryResponse** have a markCurveQueryResultMap, key is currencyPair (currency1_currency2), value is **MarkCurveQueryResult**.

        The class **MarkCurveQueryResult** have attributes like:
        ```java
            private LocalDate spotDate;
            private double spotRate;
            private double forwardPrecision;
            private String currencyPair;
            private Boolean CombinedYieldCurve;
            private String context;

            private LocalDate positionDate;

            private Instant timestamp;

            private List<ForwardPoint> forwardPointList;
        ```

    * Method **writeResponseListIntoDatabase** is responsible for **batchSave** each **MarkCurveQueryResponse** came from method **downloadMarkCurve**.

        Method **batchSave** in the **MarkCurveQueryResultDAO**, have two parts.

* In the implement of **MarkCurveQueryResultDAO**

    Firstly, create two tables like:

    ![](tables.png)

    Then, follow SQL as below, merge constant and adjacent records into one record
    ```
        String insertSql = "INSERT INTO SpotTable (ID, CurrencyPair, Region, PositionDate, " +
                "SpotDate, SpotRate, StartTime, EndTime, Cnt) VALUES (?, ?, ?, date(?), date(?), ?, ?, ?, ?)";
        String updateSql = "UPDATE SpotTable SET EndTime = ? , Cnt = Cnt + 1 WHERE ID = ?";
    ```
If you still have some problems, please follow **MarkCurveDownloaderTest**.

## Create data structure from database
In **model** folder, we can find the **SpotDataSet** and **ForwardDataSet** which are provided for our detector.

* In class **SpotDataSetBuilder**, we explore the database and form the data structure like this:
```java
    private String currencyPair;
    private String context;

    private Instant trainStartTimestamp;
    private Instant trainEndTimestamp;

    private Instant testStartTimestamp;
    private Instant testEndTimestamp;

    private List<Point> trainPointList;
    private List<Point> testPointList;
```

* In class **ForwardDataSetBuilder**, we explore the database and form the data structure like this:
```java
    private String currencyPair;
    private String context;

    private Instant trainStartTimestamp;
    private Instant trainEndTimestamp;

    private Instant testStartTimestamp;
    private Instant testEndTimestamp;

    private Map<String, List<Point>> trainPointListMap;
    private Map<String, List<Point>> testPointListMap;
```

Each point contains
```java
    private double value;
    private Instant timestamp;
```

## Detect and apply different strategies

* In class **AbstractAbnormalityDetector**

    * Method **getSpotAnomalyBooleanList** and **getForwardAnomalyBooleanListMap** 

        For different strategies, fit trainPointList and predict testPointList, get booleanList

    * Method **detectSpotAnomaly** and **detectForwardAnomaly**

        Input booleanList, judge if is need report or not, if true, form the List of **Report** (which is received by email and web page)

* In class **AbstractPythonAbnormalityDetector**

    * Same as **AbstractAbnormalityDetector**, add maxReportSize and forwardAnomalyDecisionListMap to filter result
