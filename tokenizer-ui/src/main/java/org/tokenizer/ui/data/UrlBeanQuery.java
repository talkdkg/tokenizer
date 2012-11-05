package org.tokenizer.ui.data;

import java.util.List;
import java.util.Map;

import org.tokenizer.crawler.db.*;
import org.vaadin.addons.lazyquerycontainer.AbstractBeanQuery;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;

public class UrlBeanQuery extends AbstractBeanQuery<UrlRecord> {

    public UrlBeanQuery(QueryDefinition definition,
            Map<String, Object> queryConfiguration, Object[] sortPropertyIds,
            boolean[] sortStates) {
        super(definition, queryConfiguration, sortPropertyIds, sortStates);
    }

    @Override
    protected UrlRecord constructBean() {
        return new UrlRecord();
    }

    @Override
    public int size() {

        return 0;

    }

    @Override
    protected List<UrlRecord> loadBeans(int startIndex, int count) {
        return null;
    }

    @Override
    protected void saveBeans(List<UrlRecord> addedTasks,
            List<UrlRecord> modifiedTasks, List<UrlRecord> removedTasks) {
        // TaskService taskService=
        // (TaskService)queryConfiguration.get("taskService");
        // taskService.saveTasks(addedTasks, modifiedTasks, removedTasks);
    }

}