package org.tokenizer.ui;

import org.springframework.context.ApplicationContext;
import org.tokenizer.core.context.ApplicationContextProvider;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.ui.components.MessageSearchComponent;
import org.tokenizer.ui.components.TaskInfoComponent;
import org.tokenizer.ui.components.UrlSearchComponent;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * The Application's "main" class
 */
@Theme("mytheme")
public class MyVaadinUI extends UI {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(MyVaadinUI.class);

    private TaskInfoComponent taskInfoComponent;
    private UrlSearchComponent urlSearchComponent;
    private MessageSearchComponent messageSearchComponent;

    VerticalLayout layout;
    Panel mainPanel;

    private static ApplicationContext applicationContext = null;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    private static CrawlerRepository repository;
    private static WritableExecutorModel model;

    public static CrawlerRepository getRepository() {
        return repository;
    }

    public static WritableExecutorModel getModel() {
        return model;
    }

    @Override
    public void init(final VaadinRequest request) {
        applicationContext = ApplicationContextProvider.getApplicationContext();
        model = (WritableExecutorModel) applicationContext
                .getBean("executorModel");
        repository = (CrawlerRepository) applicationContext
                .getBean("crawlerRepository");
        buildMainLayout();
    }

    private void buildMainLayout() {
        layout = new VerticalLayout();
        layout.addComponent(buildToolbar());
        mainPanel = new Panel();
        layout.addComponent(mainPanel);
        setContent(layout);
    }

    private Component buildToolbar() {
        ComponentContainer componentContainer = new HorizontalLayout();
        Button tasksManager = new Button("Tasks Manager",
                new Button.ClickListener() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(final ClickEvent event) {
                        if (taskInfoComponent == null) {
                            taskInfoComponent = new TaskInfoComponent(
                                    MyVaadinUI.this);
                        }
                        mainPanel.setContent(taskInfoComponent);
                    }
                });
        Button urlSearch = new Button("URL Search", new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                if (urlSearchComponent == null) {
                    urlSearchComponent = new UrlSearchComponent();
                }
                mainPanel.setContent(urlSearchComponent);
            }
        });
        Button messageSearch = new Button("Message Search",
                new Button.ClickListener() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(final ClickEvent event) {
                        if (messageSearchComponent == null) {
                            messageSearchComponent = new MessageSearchComponent();
                        }
                        mainPanel.setContent(messageSearchComponent);
                    }
                });
        componentContainer.addComponent(tasksManager);
        componentContainer.addComponent(urlSearch);
        componentContainer.addComponent(messageSearch);
        return componentContainer;
    }

    @Deprecated
    public String getSelectedHost() {
        return null;
    }

    @Deprecated
    public int getSelectedHttpResponseCode() {
        return 200;
    }

}