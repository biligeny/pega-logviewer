/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.report;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.LogTable;
import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogEntryModel;
import com.pega.gcs.logviewer.model.LogSeriesCollection;
import com.pega.gcs.logviewer.report.alert.AlertSummaryJPanel;
import com.pega.gcs.logviewer.report.alert.AlertTypeSummaryJPanel;

public class AlertSystemReportDialog extends SystemReportDialog {

    private static final long serialVersionUID = -4294293375116690122L;

    private static final Log4j2Helper LOG = new Log4j2Helper(AlertSystemReportDialog.class);

    private Map<String, JPanel> alertMessageTabComponentMap;

    private LogTable logTable;

    public AlertSystemReportDialog(LogTableModel logTableModel,
            NavigationTableController<LogEntryKey> navigationTableController, LogTable logTable, ImageIcon appIcon,
            Component parent) {

        super("Alert Overview - " + logTableModel.getModelName(), logTableModel, navigationTableController, appIcon,
                parent);

        this.logTable = logTable;

        setIconImage(appIcon.getImage());

        setPreferredSize(new Dimension(1400, 800));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        setContentPane(getMainJPanel());

        pack();

        setExtendedState(Frame.MAXIMIZED_BOTH);
    }

    @Override
    protected void buildTabs() {

        try {

            addDefaultTabs();

            NavigationTableController<LogEntryKey> navigationTableController;
            navigationTableController = getNavigationTableController();

            LogTableModel logTableModel = getLogTableModel();
            LogEntryModel logEntryModel = logTableModel.getLogEntryModel();
            Locale locale = logTableModel.getLocale();

            Set<LogSeriesCollection> logTimeSeriesCollectionSet;
            logTimeSeriesCollectionSet = logEntryModel.getLogTimeSeriesCollectionSet(false, locale);

            // creating before as AlertTypeSummaryJPanel uses this for setting
            // up mouse listener
            JTabbedPane reportTabbedPane = getReportTabbedPane();

            Dimension labelDim = new Dimension(100, 26);

            alertMessageTabComponentMap = new HashMap<>();

            NumberFormat numberFormat = NumberFormat.getInstance(locale);

            JPanel alertTypeSummaryPanel = new AlertTypeSummaryJPanel(logTimeSeriesCollectionSet, numberFormat,
                    reportTabbedPane, alertMessageTabComponentMap);

            String tabLabelText = "Alerts Summary";

            GUIUtilities.addTab(reportTabbedPane, alertTypeSummaryPanel, tabLabelText, labelDim);

            labelDim = new Dimension(70, 26);

            Iterator<LogSeriesCollection> ltscIt = logTimeSeriesCollectionSet.iterator();

            while (ltscIt.hasNext()) {

                LogSeriesCollection ltsc = ltscIt.next();

                String ltsName = ltsc.getName();

                JPanel alertJPanel = new AlertSummaryJPanel(ltsc, logTable, navigationTableController);

                GUIUtilities.addTab(reportTabbedPane, alertJPanel, ltsName, labelDim);

                alertMessageTabComponentMap.put(ltsName, alertJPanel);
            }
        } catch (Exception e) {
            LOG.error("Error building overview tabs.", e);
        }

    }

}
