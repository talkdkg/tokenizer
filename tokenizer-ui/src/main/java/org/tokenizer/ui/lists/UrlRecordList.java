package org.tokenizer.ui.lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.ui.MyVaadinApplication;
import org.tokenizer.ui.data.PersonContainer;
import org.tokenizer.ui.hbase.UrlContainer;

import com.vaadin.data.Property;
import com.vaadin.ui.Table;

public class UrlRecordList extends Table {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
            .getLogger(UrlRecordList.class);

    public UrlRecordList(MyVaadinApplication app) {
        setSizeFull();
        setContainerDataSource(new UrlContainer(app));
        // setColumnHeaderMode(COLUMN_HEADER_MODE_EXPLICIT);
        // setVisibleColumns(PersonContainer.NATURAL_COL_ORDER);
        // setColumnHeaders(PersonContainer.COL_HEADERS_ENGLISH);
        setSelectable(true);
        setImmediate(true);
        addListener((Property.ValueChangeListener) app);
        setNullSelectionAllowed(false);
        setColumnCollapsingAllowed(true);
        setColumnReorderingAllowed(true);
    }
}
