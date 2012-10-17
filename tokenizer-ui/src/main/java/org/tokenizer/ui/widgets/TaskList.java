package org.tokenizer.ui.widgets;

import org.tokenizer.ui.MyVaadinApplication;
import org.tokenizer.ui.data.Person;
import org.tokenizer.ui.data.PersonContainer;
import org.tokenizer.ui.data.TaskContainer;

import com.vaadin.data.Property;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;

public class TaskList extends Table {
  
  TaskContainer taskContainer;
  
  public TaskList(MyVaadinApplication app) {
    
    setSizeFull();

    try {
      taskContainer = new TaskContainer(app);
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    
    setContainerDataSource(taskContainer);

    
    //setColumnHeaderMode(COLUMN_HEADER_MODE_EXPLICIT);
    //setVisibleColumns(PersonContainer.NATURAL_COL_ORDER);
    //setColumnHeaders(PersonContainer.COL_HEADERS_ENGLISH);
    setSelectable(true);
    setImmediate(true);
    
    //addListener((Property.ValueChangeListener) taskContainer);
    
    //taskContainer.addListener((Property.ValueChangeListener)  this);
    
    
    /* We don't want to allow users to de-select a row */
    setNullSelectionAllowed(false);
    //setColumnCollapsingAllowed(true);
    //setColumnReorderingAllowed(true);
  }
  
  

}
