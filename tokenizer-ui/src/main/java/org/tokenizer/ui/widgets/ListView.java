package org.tokenizer.ui.widgets;

import com.vaadin.ui.VerticalSplitPanel;

public class ListView extends VerticalSplitPanel {
  public ListView(PersonListTable personList, PersonForm personForm) {
    setFirstComponent(personList);
    setSecondComponent(personForm);
    setSplitPosition(40);
} }