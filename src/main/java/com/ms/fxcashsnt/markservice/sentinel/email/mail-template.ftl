<#--
  Created by IntelliJ IDEA.
  User: carwu
  Date: 8/3/2018
  Time: 2:13 PM
  To change this template use File | Settings | File Templates.
-->
<html>
<head>
    <title>Region Report</title>
    <style type="text/css">
        body {
            font: normal 11px auto "Trebuchet MS", Verdana, Arial, Helvetica, sans-serif;
            color: #4f6b72;
            /*background: #E6EAE9;*/
        }

        a {
            color: #c75f3e;
        }

        #mytable {
            width: 700px;
            padding: 0;
            margin: 0;
        }

        caption {
            padding: 0 0 5px 0;
            width: 700px;
            font: italic 11px "Trebuchet MS", Verdana, Arial, Helvetica, sans-serif;
            text-align: right;
        }

        th {
            font: bold 11px "Trebuchet MS", Verdana, Arial, Helvetica, sans-serif;
            color: #4f6b72;
            border-right: 1px solid #C1DAD7;
            border-bottom: 1px solid #C1DAD7;
            border-top: 1px solid #C1DAD7;
            letter-spacing: 2px;
            text-transform: uppercase;
            text-align: left;
            padding: 6px 6px 6px 12px;
            background: #CAE8EA url(images/bg_header.jpg) no-repeat;
        }

        th.nobg {
            border-top: 0;
            border-left: 0;
            border-right: 1px solid #C1DAD7;
            background: none;
        }

        td {
            border-right: 1px solid #C1DAD7;
            border-bottom: 1px solid #C1DAD7;
            background: #fff;
            padding: 6px 6px 6px 12px;
            color: #4f6b72;
        }


        td.alt {
            background: #F5FAFA;
            color: #797268;
        }

        th.spec {
            border-left: 1px solid #C1DAD7;
            border-top: 0;
            background: #fff url(images/bullet1.gif) no-repeat;
            font: bold 10px "Trebuchet MS", Verdana, Arial, Helvetica, sans-serif;
        }

        th.specalt {
            border-left: 1px solid #C1DAD7;
            border-top: 0;
            background: #f5fafa url(images/bullet2.gif) no-repeat;
            font: bold 10px "Trebuchet MS", Verdana, Arial, Helvetica, sans-serif;
            color: #797268;
        }
        .gradientTable {
            width: auto;
            padding: 0;
            border-spacing: 0px;
        }

        .gradientTable th {
            font: bold 11px "Trebuchet MS", Verdana, Arial, Helvetica, sans-serif;
            color: #4f6b72;
            border-right: 1px solid #C1DAD7;
            border-bottom: 1px solid #C1DAD7;
            border-top: 1px solid #C1DAD7;
            letter-spacing: 2px;
            text-transform: uppercase;
            text-align: left;
            padding: 6px 6px 6px 12px;
            background: #CAE8EA url(/images/bg_header.jpg) no-repeat;
        }

        .gradientTable th.nobg {
            border-top: 0;
            border-left: 0;
            border-right: 1px solid #C1DAD7;
            background: none;
        }

        .gradientTable td {
            border-right: 1px solid #C1DAD7;
            border-bottom: 1px solid #C1DAD7;
            font-size: 11px;
            padding: 6px 6px 6px 12px;
            color: #4f6b72;
        }

        .gradientTable tr:nth-child(odd) {
            background: #fff;
        }

        .gradientTable tr:nth-child(even) {
            background: #F5FAFA;
        }
        #main {
            margin: 10px 10px 15px 10px;
            background-color: #fff;
        }
    </style>
</head>
<body>
    <div id="main">
        <h3>Table 1: The overview of outliers by region</h3>
        <table id="Table1" class="gradientTable" cellspacing="0">
            <tr>
                <th scope="col">Currency</th>
                <th scope="col">Tenor</th>
                <th scope="col">NY</th>
                <th scope="col">LN</th>
                <th scope="col">TK</th>
                <th scope="col">HK</th>
            </tr>
    <#list regionReportList as report>
        <tr>
            <th scope="row" class="spec"><input name="regionReportList[${report_index}].currency" value="${report.currency}" readonly="readonly"/></th>
            <th scope="row" class="specalt"><input name="regionReportList[${report_index}].tenor" value="${report.tenor}" readonly="readonly"/></th>
            <td scope="row" class="spec"><input name="regionReportList[${report_index}].NY" value="${report.NY!'-'}" readonly="readonly"/></td>
            <td scope="row" class="specalt"><input name="regionReportList[${report_index}].LN" value="${report.LN!'-'}" readonly="readonly"/></td>
            <td scope="row" class="spec"><input name="regionReportList[${report_index}].TK" value="${report.TK!'-'}" readonly="readonly"/></td>
            <td scope="row" class="specalt"><input name="regionReportList[${report_index}].HK" value="${report.HK!'-'}" readonly="readonly"/></td>
        </tr>
    </#list>
        </table>
        <h3>Table 2: The overview of outliers by changeSoFast</h3>
        <table id="Table2" class="gradientTable" cellspacing="0">
            <tr>
                <th scope="col">Region</th>
                <th scope="col">Currency</th>
                <th scope="col">Tenor</th>
                <th scope="col">Time</th>
                <th scope="col">LastValue</th>
                <th scope="col">Value</th>
            </tr>
        <#list changeSoFastReportList?sort_by("context") as changeSoFastReport>
            <#list 0..changeSoFastReport.booleanList?size-1 as i>
                <#if changeSoFastReport.booleanList[i] == true>
                    <tr>
                        <th scope="row" class="specalt"><input value="${changeSoFastReport.context?replace('ASIA','HK')?replace('FRM','')?replace('EOD','')}" readonly="readonly"/></th>
                        <th scope="row" class="spec"><input  value="${changeSoFastReport.currencyPair?replace("_","")?replace("USD","")}" readonly="readonly"/></th>
                        <th scope="row" class="spec"><input  value="${changeSoFastReport.tenor}" readonly="readonly"/></th>
                        <td scope="row" class="specalt"><input  value="${changeSoFastReport.pointList[i].timestamp}" readonly="readonly"/></td>
                        <td scope="row" class="spec"><input value="${changeSoFastReport.pointList[i-1].value}" readonly="readonly"/></td>
                        <td scope="row" class="spec"><input  value="${changeSoFastReport.pointList[i].value}" readonly="readonly"/></td>
                    </tr>
                </#if>
            </#list>
        </#list>
        </table>
        <h3>Table 3: The overview of outliers by remainUnchanged</h3>
        <table id="Table3" class="gradientTable" cellspacing="0">
            <tr>
                <th scope="col">Region</th>
                <th scope="col">Currency</th>
                <th scope="col">Tenor</th>
                <th scope="col">Time</th>
                <th scope="col">Value</th>
            </tr>
            <#list remainUnchangedReportList?sort_by("context") as remainUnchangedReport>
                <#if remainUnchangedReport.booleanList[remainUnchangedReport.booleanList?size-1] == true>
                    <tr>
                        <th scope="row" class="specalt"><input value="${remainUnchangedReport.context?replace('ASIA','HK')?replace('FRM','')?replace('EOD','')}" readonly="readonly"/></th>
                        <th scope="row" class="spec"><input value="${remainUnchangedReport.currencyPair?replace("_","")?replace("USD","")}" readonly="readonly"/></th>
                        <th scope="row" class="spec"><input value="${remainUnchangedReport.tenor}" readonly="readonly"/></th>
                        <td scope="row" class="specalt"><input value="${remainUnchangedReport.pointList[remainUnchangedReport.booleanList?size-1].timestamp}" readonly="readonly"/></td>
                        <td scope="row" class="spec"><input value="${remainUnchangedReport.pointList[remainUnchangedReport.booleanList?size-1].value}" readonly="readonly"/></td>
                    </tr>
                </#if>
            </#list>
        </table>
        <h3>Table 4: The overview of outliers by ellipticEnvelop</h3>
        <table id="Table4" class="gradientTable" cellspacing="0">
            <tr>
                <th scope="col">Region</th>
                <th scope="col">Currency</th>
                <th scope="col">Tenor</th>
                <th scope="col">Time</th>
                <th scope="col">Value</th>
            </tr>
            <#list ellipticEnvelopReportList?sort_by("context") as ellipticEnvelopReport>
                <#if ellipticEnvelopReport.booleanList[ellipticEnvelopReport.booleanList?size-1] == true>
                    <tr>
                        <th scope="row" class="specalt"><input value="${ellipticEnvelopReport.context?replace('ASIA','HK')?replace('FRM','')?replace('EOD','')}" readonly="readonly"/></th>
                        <th scope="row" class="spec"><input value="${ellipticEnvelopReport.currencyPair?replace("_","")?replace("USD","")}" readonly="readonly"/></th>
                        <th scope="row" class="spec"><input value="${ellipticEnvelopReport.tenor}" readonly="readonly"/></th>
                        <td scope="row" class="specalt"><input value="${ellipticEnvelopReport.pointList[ellipticEnvelopReport.booleanList?size-1].timestamp}" readonly="readonly"/></td>
                        <td scope="row" class="spec"><input value="${ellipticEnvelopReport.pointList[ellipticEnvelopReport.booleanList?size-1].value}" readonly="readonly"/></td>
                    </tr>
                </#if>
            </#list>
        </table>
        <h3>Table 5: The overview of outliers by isolationForest</h3>
        <table id="Table5" class="gradientTable" cellspacing="0">
            <tr>
                <th scope="col">Region</th>
                <th scope="col">Currency</th>
                <th scope="col">Tenor</th>
                <th scope="col">Time</th>
                <th scope="col">Value</th>
            </tr>
            <#list isolationForestReportList?sort_by("context") as isolationForestReport>
                <#if isolationForestReport.booleanList[isolationForestReport.booleanList?size-1] == true>
                    <tr>
                        <th scope="row" class="specalt"><input value="${isolationForestReport.context?replace('ASIA','HK')?replace('FRM','')?replace('EOD','')}" readonly="readonly"/></th>
                        <th scope="row" class="spec"><input value="${isolationForestReport.currencyPair?replace("_","")?replace("USD","")}" readonly="readonly"/></th>
                        <th scope="row" class="spec"><input value="${isolationForestReport.tenor}" readonly="readonly"/></th>
                        <td scope="row" class="specalt"><input value="${isolationForestReport.pointList[isolationForestReport.booleanList?size-1].timestamp}" readonly="readonly"/></td>
                        <td scope="row" class="spec"><input value="${isolationForestReport.pointList[isolationForestReport.booleanList?size-1].value}" readonly="readonly"/></td>
                    </tr>
                </#if>
            </#list>
        </table>
        <h3>Table 6: The overview of outliers by oneClassSvmReport</h3>
        <table id="Table6" class="gradientTable" cellspacing="0">
            <tr>
                <th scope="col">Region</th>
                <th scope="col">Currency</th>
                <th scope="col">Tenor</th>
                <th scope="col">Time</th>
                <th scope="col">Value</th>
            </tr>
            <#list oneClassSvmReportList?sort_by("context") as oneClassSvmReport>
                <#if oneClassSvmReport.booleanList[oneClassSvmReport.booleanList?size-1] == true>
                    <tr>
                        <th scope="row" class="specalt"><input value="${oneClassSvmReport.context?replace('ASIA','HK')?replace('FRM','')?replace('EOD','')}" readonly="readonly"/></th>
                        <th scope="row" class="spec"><input value="${oneClassSvmReport.currencyPair?replace("_","")?replace("USD","")}" readonly="readonly"/></th>
                        <th scope="row" class="spec"><input value="${oneClassSvmReport.tenor}" readonly="readonly"/></th>
                        <td scope="row" class="specalt"><input value="${oneClassSvmReport.pointList[oneClassSvmReport.booleanList?size-1].timestamp}" readonly="readonly"/></td>
                        <td scope="row" class="spec"><input value="${oneClassSvmReport.pointList[oneClassSvmReport.booleanList?size-1].value}" readonly="readonly"/></td>
                    </tr>
                </#if>
            </#list>
        </table>
        <h3>Table 7: The overview of outliers by localOutlierFactorReport</h3>
        <table id="Table7" class="gradientTable" cellspacing="0">
            <tr>
                <th scope="col">Region</th>
                <th scope="col">Currency</th>
                <th scope="col">Tenor</th>
                <th scope="col">Time</th>
                <th scope="col">Value</th>
            </tr>
            <#list localOutlierFactorReportList?sort_by("context") as localOutlierFactorReport>
                <#if localOutlierFactorReport.booleanList[localOutlierFactorReport.booleanList?size-1] == true>
                    <tr>
                        <th scope="row" class="specalt"><input value="${localOutlierFactorReport.context?replace('ASIA','HK')?replace('FRM','')?replace('EOD','')}" readonly="readonly"/></th>
                        <th scope="row" class="spec"><input value="${localOutlierFactorReport.currencyPair?replace("_","")?replace("USD","")}" readonly="readonly"/></th>
                        <th scope="row" class="spec"><input value="${localOutlierFactorReport.tenor}" readonly="readonly"/></th>
                        <td scope="row" class="specalt"><input value="${localOutlierFactorReport.pointList[localOutlierFactorReport.booleanList?size-1].timestamp}" readonly="readonly"/></td>
                        <td scope="row" class="spec"><input value="${localOutlierFactorReport.pointList[localOutlierFactorReport.booleanList?size-1].value}" readonly="readonly"/></td>
                    </tr>
                </#if>
            </#list>
        </table>
        <h3>Table 8: The overview of outliers by smoothedZScoreReport</h3>
        <table id="Table8" class="gradientTable" cellspacing="0">
            <tr>
                <th scope="col">Region</th>
                <th scope="col">Currency</th>
                <th scope="col">Tenor</th>
                <th scope="col">Time</th>
                <th scope="col">Value</th>
            </tr>
            <#list smoothedZScoreReportList?sort_by("context") as smoothedZScoreReport>
                <#if smoothedZScoreReport.booleanList[smoothedZScoreReport.booleanList?size-1] == true>
                    <tr>
                        <th scope="row" class="specalt"><input value="${smoothedZScoreReport.context?replace('ASIA','HK')?replace('FRM','')?replace('EOD','')}" readonly="readonly"/></th>
                        <th scope="row" class="spec"><input value="${smoothedZScoreReport.currencyPair?replace("_","")?replace("USD","")}" readonly="readonly"/></th>
                        <th scope="row" class="spec"><input value="${smoothedZScoreReport.tenor}" readonly="readonly"/></th>
                        <td scope="row" class="specalt"><input value="${smoothedZScoreReport.pointList[smoothedZScoreReport.booleanList?size-1].timestamp}" readonly="readonly"/></td>
                        <td scope="row" class="spec"><input value="${smoothedZScoreReport.pointList[smoothedZScoreReport.booleanList?size-1].value}" readonly="readonly"/></td>
                    </tr>
                </#if>
            </#list>
        </table>
        <div style="clear:both"></div>
    </div>
</body>
</html>
