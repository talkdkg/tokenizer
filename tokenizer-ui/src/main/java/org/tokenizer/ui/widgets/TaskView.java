package org.tokenizer.ui.widgets;

import com.vaadin.ui.VerticalSplitPanel;

@SuppressWarnings("serial")
public class TaskView extends VerticalSplitPanel {
  
 
  public TaskView(TaskList taskList, TaskForm taskForm) {
    
    setCaption("Tokenizer: Tasks");
    setSizeFull();
    
    setFirstComponent(taskList);
    setSecondComponent(taskForm);
    setSplitPosition(40);
    
  }
  
}
