package org.tokenizer.ui.widgets;

import org.tokenizer.ui.MyVaadinApplication;
import org.tokenizer.ui.data.Person;
import org.tokenizer.ui.data.PersonContainer;

import com.vaadin.data.Property;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;

public class PersonListTable extends Table {
  private static final long serialVersionUID = 1L;
  
  public PersonListTable(MyVaadinApplication app) {
    
    setSizeFull();
    
    // customize email column to have mailto: links using column generator
    addGeneratedColumn("email", new ColumnGenerator() {
      public Component generateCell(Table source, Object itemId, Object columnId) {
        Person p = (Person) itemId;
        Link l = new Link();
        l.setResource(new ExternalResource("mailto:" + p.getEmail()));
        l.setCaption(p.getEmail());
        return l;
      }
    });
    
    setContainerDataSource(app.getDataSource());
    
    setColumnHeaderMode(COLUMN_HEADER_MODE_EXPLICIT);
    
    setVisibleColumns(PersonContainer.NATURAL_COL_ORDER);
    setColumnHeaders(PersonContainer.COL_HEADERS_ENGLISH);
    
    /*
     * Make table selectable, react immediatedly to user events, and pass events
     * to the controller (our main application)
     */
    setSelectable(true);
    setImmediate(true);
    addListener((Property.ValueChangeListener) app);
    /* We don't want to allow users to de-select a row */
    setNullSelectionAllowed(false);
    
    
    setColumnCollapsingAllowed(true);
    setColumnReorderingAllowed(true);
    
  }
  
}