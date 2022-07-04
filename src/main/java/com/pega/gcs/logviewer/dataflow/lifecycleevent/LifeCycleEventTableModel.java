
package com.pega.gcs.logviewer.dataflow.lifecycleevent;

import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jfree.chart.axis.DateAxis;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.time.SimpleTimePeriod;

import com.pega.gcs.fringecommon.guiutilities.CheckBoxMenuItemPopupEntry;
import com.pega.gcs.fringecommon.guiutilities.FilterColumn;
import com.pega.gcs.fringecommon.guiutilities.FilterTableModel;
import com.pega.gcs.fringecommon.guiutilities.FilterTableModelNavigation;
import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.search.SearchData;
import com.pega.gcs.fringecommon.guiutilities.search.SearchModel;
import com.pega.gcs.fringecommon.guiutilities.treetable.AbstractTreeTableNode;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.DateTimeUtilities;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.message.LifeCycleEventMessage;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.message.ProcessingThreadLifecycleMessage;

public class LifeCycleEventTableModel extends FilterTableModel<LifeCycleEventKey> {

    private static final long serialVersionUID = -5776740688714889560L;

    private static final Log4j2Helper LOG = new Log4j2Helper(LifeCycleEventTableModel.class);

    private List<LifeCycleEventColumn> lifeCycleEventColumnList;

    private List<LifeCycleEventKey> lifeCycleEventkeyList;

    private Map<LifeCycleEventKey, LifeCycleEventMessage> lifeCycleEventMap;

    // large files cause hanging during search, because of getIndex call, hence building a map to store these
    private HashMap<LifeCycleEventKey, Integer> keyIndexMap;

    // search
    private SearchData<LifeCycleEventKey> searchData;
    private SearchModel<LifeCycleEventKey> searchModel;

    private static DateFormat displayDateFormat;

    private DateAxis domainAxis;

    private long lowerDomainRange;

    private long upperDomainRange;

    private Map<String, Set<LifeCycleEventKey>> partitionProcessingThreadLceKeyMap;

    private boolean rebuildPartitionTaskSeriesMap;

    private Map<String, TaskSeries> partitionTaskSeriesMap;

    static {

        displayDateFormat = new SimpleDateFormat(DateTimeUtilities.DATEFORMAT_ISO8601);

        displayDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public LifeCycleEventTableModel(RecentFile recentFile, SearchData<LifeCycleEventKey> searchData) {

        super(recentFile);
        this.searchData = searchData;

        lifeCycleEventColumnList = LifeCycleEventColumn.getLifeCycleEventColumnList();

        this.lowerDomainRange = -1;
        this.upperDomainRange = -1;

        // updateDomainAxis(displayTimezone, null);

        rebuildPartitionTaskSeriesMap = true;

        resetModel();

    }

    public static DateFormat getDisplayDateFormat() {
        return displayDateFormat;
    }

    private List<LifeCycleEventColumn> getLifeCycleEventColumnList() {
        return lifeCycleEventColumnList;
    }

    private Map<LifeCycleEventKey, LifeCycleEventMessage> getLifeCycleEventMap() {

        if (lifeCycleEventMap == null) {
            lifeCycleEventMap = new TreeMap<>();
        }

        return lifeCycleEventMap;
    }

    public DateAxis getDomainAxis() {

        if (domainAxis == null) {

            domainAxis = new DateAxis();
            domainAxis.setLowerMargin(0.02);
            domainAxis.setUpperMargin(0.02);

            Font labelFont = new Font("Arial", Font.PLAIN, 10);
            domainAxis.setLabelFont(labelFont);
        }

        return domainAxis;
    }

    public long getLowerDomainRange() {
        return lowerDomainRange;
    }

    private void setLowerDomainRange(long lowerDomainRange) {
        this.lowerDomainRange = lowerDomainRange;
    }

    public long getUpperDomainRange() {
        return upperDomainRange;
    }

    private void setUpperDomainRange(long upperDomainRange) {
        this.upperDomainRange = upperDomainRange;
    }

    private Map<String, Set<LifeCycleEventKey>> getPartitionProcessingThreadLceKeyMap() {

        if (partitionProcessingThreadLceKeyMap == null) {
            partitionProcessingThreadLceKeyMap = new HashMap<>();
        }

        return partitionProcessingThreadLceKeyMap;
    }

    private boolean isRebuildPartitionTaskSeriesMap() {
        return rebuildPartitionTaskSeriesMap;
    }

    private void setRebuildPartitionTaskSeriesMap(boolean rebuildPartitionTaskSeriesMap) {
        this.rebuildPartitionTaskSeriesMap = rebuildPartitionTaskSeriesMap;
    }

    @Override
    public int getColumnCount() {
        return lifeCycleEventColumnList.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        List<LifeCycleEventKey> lceKeyList = getFtmEntryKeyList();

        LifeCycleEventKey lceKey = lceKeyList.get(rowIndex);
        LifeCycleEventMessage lifeCycleEventMessage = getEventForKey(lceKey);

        return lifeCycleEventMessage;
    }

    @Override
    protected int getModelColumnIndex(int column) {
        return column;
    }

    @Override
    protected boolean search(LifeCycleEventKey key, Object searchStrObj) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected FilterTableModelNavigation<LifeCycleEventKey> getNavigationRowIndex(List<LifeCycleEventKey> resultList,
            int currSelectedRowIndex, boolean forward, boolean first, boolean last, boolean wrap) {

        int navigationIndex = 0;
        int navigationRowIndex = 0;

        if ((resultList != null) && (resultList.size() > 0)) {

            int resultListSize = resultList.size();

            List<LifeCycleEventKey> lceKeyList = getFtmEntryKeyList();

            int logEntryKeyListSize = lceKeyList.size();

            LifeCycleEventKey lceKey = null;

            if (first) {

                lceKey = resultList.get(0);
                navigationIndex = 1;

            } else if (last) {

                int lastIndex = resultListSize - 1;
                lceKey = resultList.get(lastIndex);
                navigationIndex = resultListSize;

            } else if (forward) {
                // NEXT
                if (currSelectedRowIndex >= 0) {

                    if (currSelectedRowIndex < (logEntryKeyListSize - 1)) {
                        currSelectedRowIndex++;
                    } else {
                        if (wrap) {
                            currSelectedRowIndex = 0;
                        }
                    }
                } else {
                    currSelectedRowIndex = 0;
                }

                LifeCycleEventKey currSelectedLceKey = lceKeyList.get(currSelectedRowIndex);

                int searchIndex = Collections.binarySearch(resultList, currSelectedLceKey);

                if (searchIndex >= 0) {
                    // exact search found
                    lceKey = resultList.get(searchIndex);
                } else {

                    searchIndex = (searchIndex * -1) - 1;

                    if (searchIndex == resultListSize) {

                        if (wrap) {
                            searchIndex = 0;
                        } else {
                            searchIndex = resultListSize - 1;
                        }
                    }

                    lceKey = resultList.get(searchIndex);
                }

                navigationIndex = resultList.indexOf(lceKey) + 1;

            } else {
                // PREVIOUS
                if (currSelectedRowIndex >= 0) {

                    if (currSelectedRowIndex > 0) {
                        currSelectedRowIndex--;
                    } else {
                        if (wrap) {
                            currSelectedRowIndex = logEntryKeyListSize - 1;
                        }
                    }
                } else {
                    currSelectedRowIndex = 0;
                }

                LifeCycleEventKey currSelectedLceKey = lceKeyList.get(currSelectedRowIndex);

                int searchIndex = Collections.binarySearch(resultList, currSelectedLceKey);

                if (searchIndex >= 0) {
                    // exact search found
                    lceKey = resultList.get(searchIndex);
                } else {

                    searchIndex = (searchIndex * -1) - 1;

                    if (searchIndex == 0) {

                        if (wrap) {
                            searchIndex = resultListSize - 1;
                        } else {
                            searchIndex = 0;
                        }
                    } else {
                        searchIndex--;
                    }

                    lceKey = resultList.get(searchIndex);
                }

                navigationIndex = resultList.indexOf(lceKey) + 1;
            }

            if (lceKey != null) {

                navigationRowIndex = lceKeyList.indexOf(lceKey);

            } else {
                navigationRowIndex = currSelectedRowIndex;
            }

        }

        FilterTableModelNavigation<LifeCycleEventKey> ttmn = new FilterTableModelNavigation<>();
        ttmn.setNavigationIndex(navigationIndex);
        ttmn.setNavigationRowIndex(navigationRowIndex);

        return ttmn;

    }

    @Override
    public List<LifeCycleEventKey> getFtmEntryKeyList() {

        if (lifeCycleEventkeyList == null) {
            lifeCycleEventkeyList = new ArrayList<>();
        }

        return lifeCycleEventkeyList;
    }

    @Override
    protected HashMap<LifeCycleEventKey, Integer> getKeyIndexMap() {

        if (keyIndexMap == null) {
            keyIndexMap = new HashMap<>();
        }

        return keyIndexMap;
    }

    @Override
    public void resetModel() {

        List<LifeCycleEventKey> lifeCycleEventKeyList = getFtmEntryKeyList();
        lifeCycleEventKeyList.clear();

        HashMap<LifeCycleEventKey, Integer> keyIndexMap = getKeyIndexMap();
        keyIndexMap.clear();

        Map<LifeCycleEventKey, LifeCycleEventMessage> lifeCycleEventMap = getLifeCycleEventMap();
        lifeCycleEventMap.clear();

        Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<LifeCycleEventKey>>> columnFilterMap;
        columnFilterMap = getColumnFilterMap();
        columnFilterMap.clear();

        Map<String, Set<LifeCycleEventKey>> partitionProcessingThreadLceKeyMap;
        partitionProcessingThreadLceKeyMap = getPartitionProcessingThreadLceKeyMap();
        partitionProcessingThreadLceKeyMap.clear();

        List<LifeCycleEventColumn> lifeCycleEventColumnList;
        lifeCycleEventColumnList = getLifeCycleEventColumnList();

        for (int columnIndex = 0; columnIndex < lifeCycleEventColumnList.size(); columnIndex++) {

            LifeCycleEventColumn lifeCycleEventColumn = lifeCycleEventColumnList.get(columnIndex);

            // preventing unnecessary buildup of filter map
            if (lifeCycleEventColumn.isFilterable()) {

                FilterColumn filterColumn = new FilterColumn(columnIndex);

                filterColumn.setColumnFilterEnabled(true);

                columnFilterMap.put(filterColumn, null);
            }
        }

        clearSearchResults(true);

        fireTableDataChanged();
    }

    @Override
    public LifeCycleEventMessage getEventForKey(LifeCycleEventKey key) {

        LifeCycleEventMessage lifeCycleEventMessage = null;

        if (key != null) {
            Map<LifeCycleEventKey, LifeCycleEventMessage> lifeCycleEventMap = getLifeCycleEventMap();
            lifeCycleEventMessage = lifeCycleEventMap.get(key);
        }

        return lifeCycleEventMessage;
    }

    @Override
    public AbstractTreeTableNode getTreeNodeForKey(LifeCycleEventKey key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void clearSearchResults(boolean clearResults) {
        // TODO Auto-generated method stub

    }

    @Override
    public SearchModel<LifeCycleEventKey> getSearchModel() {

        if (searchModel == null) {

            searchModel = new SearchModel<LifeCycleEventKey>(searchData) {

                @Override
                public void resetResults(boolean clearResults) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void searchInEvents(Object searchStrObj, ModalProgressMonitor modalProgressMonitor) {
                    // TODO Auto-generated method stub

                }
            };
        }

        return searchModel;
    }

    @Override
    public String getColumnValue(Object valueAtObject, int columnIndex) {

        LifeCycleEventMessage lifeCycleEventMessage = (LifeCycleEventMessage) valueAtObject;

        String columnValue = null;

        if (lifeCycleEventMessage != null) {

            LifeCycleEventColumn lifeCycleEventColumn;
            lifeCycleEventColumn = LifeCycleEventColumn.values()[columnIndex];

            columnValue = lifeCycleEventMessage.getColumnValueForLifeCycleEventColumn(lifeCycleEventColumn);

        }

        return columnValue;

    }

    @Override
    public TableColumnModel getTableColumnModel() {

        TableColumnModel tableColumnModel = new DefaultTableColumnModel();

        for (int i = 0; i < getColumnCount(); i++) {

            TableColumn tableColumn = new TableColumn(i);

            LifeCycleEventColumn traceTableModelColumn = getColumn(i);

            String text = traceTableModelColumn.getName();
            int horizontalAlignment = traceTableModelColumn.getHorizontalAlignment();
            int colWidth = traceTableModelColumn.getPrefColumnWidth();

            tableColumn.setHeaderValue(text);

            LifeCycleEventTableCellRenderer lifeCycleEventTableCellRenderer;
            lifeCycleEventTableCellRenderer = new LifeCycleEventTableCellRenderer();

            lifeCycleEventTableCellRenderer.setBorder(new EmptyBorder(1, 3, 1, 1));
            lifeCycleEventTableCellRenderer.setHorizontalAlignment(horizontalAlignment);

            tableColumn.setCellRenderer(lifeCycleEventTableCellRenderer);

            tableColumn.setPreferredWidth(colWidth);
            tableColumn.setResizable(true);

            tableColumnModel.addColumn(tableColumn);
        }

        return tableColumnModel;
    }

    public LifeCycleEventColumn getColumn(int column) {

        List<LifeCycleEventColumn> lifeCycleEventColumnList = getLifeCycleEventColumnList();

        LifeCycleEventColumn lifeCycleEventColumn = lifeCycleEventColumnList.get(column);

        return lifeCycleEventColumn;
    }

    public void addLifeCycleMessageEntry(LifeCycleEventMessage lifeCycleEventMessage) {

        String messageId = lifeCycleEventMessage.getMessageId();
        long timestamp = lifeCycleEventMessage.getTimestamp();

        LifeCycleEventKey lifeCycleEventKey = new LifeCycleEventKey(timestamp, messageId);

        List<LifeCycleEventKey> lifeCycleEventKeyList = getFtmEntryKeyList();
        lifeCycleEventKeyList.add(lifeCycleEventKey);

        // removing because timestamp sorting changes the key order.
        // HashMap<LogEntryKey, Integer> keyIndexMap = getKeyIndexMap();
        // keyIndexMap.put(logEntryKey, logEntryKeyList.size() - 1);

        Map<LifeCycleEventKey, LifeCycleEventMessage> lifeCycleEventMap = getLifeCycleEventMap();
        lifeCycleEventMap.put(lifeCycleEventKey, lifeCycleEventMessage);

        updateColumnFilterMap(lifeCycleEventKey, lifeCycleEventMessage);

        long lowerDomainRange = getLowerDomainRange();
        long upperDomainRange = getUpperDomainRange();

        if (lowerDomainRange == -1) {
            lowerDomainRange = timestamp - 1;
        } else {
            lowerDomainRange = Math.min(lowerDomainRange, timestamp);
        }

        if (upperDomainRange == -1) {
            upperDomainRange = timestamp;
        } else {
            upperDomainRange = Math.max(upperDomainRange, timestamp);
        }

        setLowerDomainRange(lowerDomainRange);
        setUpperDomainRange(upperDomainRange);

        postProcess(lifeCycleEventKey, lifeCycleEventMessage);
    }

    private void postProcess(LifeCycleEventKey lifeCycleEventKey, LifeCycleEventMessage lifeCycleEventMessage) {

        Map<String, Set<LifeCycleEventKey>> partitionProcessingThreadLceKeyMap;
        partitionProcessingThreadLceKeyMap = getPartitionProcessingThreadLceKeyMap();

        String messageType = lifeCycleEventMessage.getType();

        switch (messageType) {

        case ".IntentChangedMessage":
            break;
        case ".PartitionStatusTransitionMessage":
            break;
        case ".ProcessingThreadLifecycleMessage":

            ProcessingThreadLifecycleMessage processingThreadLifecycleMessage;
            processingThreadLifecycleMessage = (ProcessingThreadLifecycleMessage) lifeCycleEventMessage;

            List<String> partitions = processingThreadLifecycleMessage.getPartitions();

            if ((partitions != null) && (partitions.size() > 0)) {

                String partitionsStr = partitions.toString();

                Set<LifeCycleEventKey> partitionProcessingThreadLceKeySet;
                partitionProcessingThreadLceKeySet = partitionProcessingThreadLceKeyMap.get(partitionsStr);

                if (partitionProcessingThreadLceKeySet == null) {

                    partitionProcessingThreadLceKeySet = new TreeSet<>();

                    partitionProcessingThreadLceKeyMap.put(partitionsStr, partitionProcessingThreadLceKeySet);
                }

                partitionProcessingThreadLceKeySet.add(lifeCycleEventKey);
            }
            break;
        case ".RunStatusTransitionMessage":
            break;

        default:
            LOG.error("Unknown message type: " + messageType);
            break;
        }

    }

    // clearing the columnFilterMap will skip the below loop
    private void updateColumnFilterMap(LifeCycleEventKey lifeCycleEventKey,
            LifeCycleEventMessage lifeCycleEventMessage) {

        if (lifeCycleEventMessage != null) {

            Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<LifeCycleEventKey>>> columnFilterMap = getColumnFilterMap();

            Iterator<FilterColumn> fcIterator = columnFilterMap.keySet().iterator();

            while (fcIterator.hasNext()) {

                FilterColumn filterColumn = fcIterator.next();

                int columnIndex = filterColumn.getIndex();

                LifeCycleEventColumn lifeCycleEventColumn = getColumn(columnIndex);

                List<CheckBoxMenuItemPopupEntry<LifeCycleEventKey>> columnFilterEntryList;
                columnFilterEntryList = columnFilterMap.get(filterColumn);

                if (columnFilterEntryList == null) {
                    columnFilterEntryList = new ArrayList<CheckBoxMenuItemPopupEntry<LifeCycleEventKey>>();
                    columnFilterMap.put(filterColumn, columnFilterEntryList);
                }

                String columnValue = lifeCycleEventMessage.getColumnValueForLifeCycleEventColumn(lifeCycleEventColumn);

                if (columnValue == null) {
                    columnValue = FilterTableModel.NULL_STR;
                } else if ("".equals(columnValue)) {
                    columnValue = FilterTableModel.EMPTY_STR;
                }

                CheckBoxMenuItemPopupEntry<LifeCycleEventKey> columnFilterEntry;

                CheckBoxMenuItemPopupEntry<LifeCycleEventKey> searchKey;
                searchKey = new CheckBoxMenuItemPopupEntry<>(columnValue);

                int index = columnFilterEntryList.indexOf(searchKey);

                if (index == -1) {
                    columnFilterEntry = new CheckBoxMenuItemPopupEntry<>(columnValue);
                    columnFilterEntryList.add(columnFilterEntry);
                } else {
                    columnFilterEntry = columnFilterEntryList.get(index);
                }

                columnFilterEntry.addRowIndex(lifeCycleEventKey);

            }
        }
    }

    public Map<String, TaskSeries> getPartitionTaskSeriesMap() {

        if (partitionTaskSeriesMap == null) {

            partitionTaskSeriesMap = new TreeMap<>();
        }

        if (isRebuildPartitionTaskSeriesMap()) {
            buildPartitionTaskSeriesMap();
        }

        return partitionTaskSeriesMap;
    }

    private void buildPartitionTaskSeriesMap() {

        partitionTaskSeriesMap.clear();

        Map<LifeCycleEventKey, LifeCycleEventMessage> lifeCycleEventMap = getLifeCycleEventMap();

        Map<String, Set<LifeCycleEventKey>> processingThreadLifecycleMessageMap;
        processingThreadLifecycleMessageMap = getPartitionProcessingThreadLceKeyMap();

        for (Map.Entry<String, Set<LifeCycleEventKey>> entry : processingThreadLifecycleMessageMap.entrySet()) {

            String partition = entry.getKey();

            TaskSeries taskSeries = new TaskSeries(partition);

            Set<LifeCycleEventKey> partitionProcessingThreadLceKeySet = entry.getValue();

            long pickedUpPartitionStartTime = -1;
            long startedPartitionStartTime = -1;
            long stoppedPartitionStartTime = -1;

            for (LifeCycleEventKey lifeCycleEventKey : partitionProcessingThreadLceKeySet) {

                ProcessingThreadLifecycleMessage processingThreadLifecycleMessage;

                processingThreadLifecycleMessage = (ProcessingThreadLifecycleMessage) lifeCycleEventMap
                        .get(lifeCycleEventKey);

                String event = processingThreadLifecycleMessage.getEvent();
                String threadName = processingThreadLifecycleMessage.getThreadName();

                switch (event) {

                case "PICKED_UP_PARTITIONS":
                    pickedUpPartitionStartTime = processingThreadLifecycleMessage.getTimestamp();
                    break;

                case "STARTED_PROCESSING":

                    startedPartitionStartTime = processingThreadLifecycleMessage.getTimestamp();

                    if ((startedPartitionStartTime != -1) && (pickedUpPartitionStartTime != -1)) {

                        SimpleTimePeriod simpleTimePeriod;
                        simpleTimePeriod = new SimpleTimePeriod(pickedUpPartitionStartTime, startedPartitionStartTime);

                        Task task = new Task(threadName, simpleTimePeriod);

                        // task.setPercentComplete(10D);

                        taskSeries.add(task);
                    } else {
                        LOG.error("Error in STARTED_PROCESSING Block");
                    }

                    break;

                case "STOPPED_PROCESSING":

                    stoppedPartitionStartTime = processingThreadLifecycleMessage.getTimestamp();

                    if ((stoppedPartitionStartTime != -1) && (startedPartitionStartTime != -1)) {

                        SimpleTimePeriod simpleTimePeriod;
                        simpleTimePeriod = new SimpleTimePeriod(startedPartitionStartTime, stoppedPartitionStartTime);

                        Task task = new Task(threadName, simpleTimePeriod);

                        // task.setPercentComplete(100D);

                        taskSeries.add(task);
                    } else {
                        LOG.error("Error in STOPPED_PROCESSING Block");
                    }
                    break;

                default:
                    break;

                }
            }

            partitionTaskSeriesMap.put(partition, taskSeries);
        }

        setRebuildPartitionTaskSeriesMap(false);
    }

}
