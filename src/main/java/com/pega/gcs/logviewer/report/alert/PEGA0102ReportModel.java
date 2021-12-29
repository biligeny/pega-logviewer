/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.report.alert;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.alert.AlertMessageList.AlertMessage;

public class PEGA0102ReportModel extends AlertMessageReportModel {

    private static final long serialVersionUID = -8889727175209305065L;

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0102ReportModel.class);

    private List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList;

    private Pattern pattern;

    public PEGA0102ReportModel(AlertMessage alertMessage, long thresholdKPI, AlertLogEntryModel alertLogEntryModel,
            Locale locale) {

        super(alertMessage, thresholdKPI, alertLogEntryModel, locale);

        // "Job Scheduler \""+config.getName()+"\" activity \""+config.getAssociatedClass()+":"+config.getActivityName()+"\" execution
        // failed in node %s which will cause delayed Queue Processor use cases to stop processing"
        String regex = "Job Scheduler \"(.*?)\" activity \"(.*?)\" execution failed in node";

        pattern = Pattern.compile(regex);
    }

    @Override
    protected List<AlertBoxAndWhiskerReportColumn> getAlertMessageReportColumnList() {

        if (alertMessageReportColumnList == null) {
            alertMessageReportColumnList = new ArrayList<AlertBoxAndWhiskerReportColumn>();

            String displayName;
            int prefColWidth;
            int horizontalAlignment;
            boolean filterable;

            // first column data is the key
            displayName = "Alert Subject (\"Job Scheduler [Activity Name]\")";
            prefColWidth = 500;
            horizontalAlignment = SwingConstants.LEFT;
            filterable = true;

            AlertBoxAndWhiskerReportColumn amReportColumn;
            amReportColumn = new AlertBoxAndWhiskerReportColumn(AlertBoxAndWhiskerReportColumn.KEY, displayName,
                    prefColWidth, horizontalAlignment, filterable);

            alertMessageReportColumnList.add(amReportColumn);

            List<AlertBoxAndWhiskerReportColumn> defaultAlertMessageReportColumnList = AlertBoxAndWhiskerReportColumn
                    .getDefaultAlertMessageReportColumnList();

            alertMessageReportColumnList.addAll(defaultAlertMessageReportColumnList);
        }

        return alertMessageReportColumnList;
    }

    @Override
    public String getAlertMessageReportEntryKey(String dataText) {

        String alertMessageReportEntryKey = null;

        Matcher patternMatcher = pattern.matcher(dataText);
        boolean matches = patternMatcher.find();

        if (matches) {
            String jobScheduler = patternMatcher.group(1).trim();
            String activityName = patternMatcher.group(2).trim();

            StringBuilder sb = new StringBuilder();
            sb.append(jobScheduler);
            sb.append(" [");
            sb.append(activityName);
            sb.append("]");

            alertMessageReportEntryKey = sb.toString();
        }

        return alertMessageReportEntryKey;

    }

    @Override
    public String getAlertMessageReportEntryKey(ArrayList<String> logEntryValueList) {

        String alertMessageReportEntryKey = null;

        AlertLogEntryModel alertLogEntryModel = getAlertLogEntryModel();

        List<String> logEntryColumnList = alertLogEntryModel.getLogEntryColumnList();

        int messageIndex = logEntryColumnList.indexOf(LogEntryColumn.MESSAGE.getColumnId());
        String message = logEntryValueList.get(messageIndex);

        alertMessageReportEntryKey = getAlertMessageReportEntryKey(message);

        if (alertMessageReportEntryKey == null) {
            LOG.info("PEGA0102ReportModel - Could'nt match - [" + message + "]");
        }

        return alertMessageReportEntryKey;
    }

}