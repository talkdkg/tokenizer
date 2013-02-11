package org.tokenizer.ui;

import java.util.UUID;

import org.springframework.context.ApplicationContext;
import org.tokenizer.core.context.ApplicationContextProvider;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.executor.model.api.TaskGeneralState;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.tokenizer.ui.components.TaskInfoComponent;
import org.tokenizer.ui.components.UrlSearchComponent;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
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
    VerticalLayout layout;
    Panel mainPanel;
    private static ApplicationContext applicationContext = null;
    private String selectedHost = null;
    private int selectedHttpResponseCode = 200;

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
        // Use the factory globally in the application
        VaadinSession.getCurrent()
                .setConverterFactory(new MyConverterFactory());
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
        // mainPanel.setSizeFull();
        layout.addComponent(mainPanel);
        setContent(layout);
    }

    private Component buildToolbar() {
        ComponentContainer componentContainer = new HorizontalLayout();
        @SuppressWarnings("serial")
        Button tasksManager = new Button("Tasks Manager",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(final ClickEvent event) {
                        if (taskInfoComponent == null) {
                            taskInfoComponent = new TaskInfoComponent(
                                    MyVaadinUI.this);
                        }
                        mainPanel.setContent(taskInfoComponent);
                    }
                });
        @SuppressWarnings("serial")
        Button urlSearch = new Button("URL Search", new Button.ClickListener() {

            @Override
            public void buttonClick(final ClickEvent event) {
                if (urlSearchComponent == null) {
                    urlSearchComponent = new UrlSearchComponent();
                }
                mainPanel.setContent(urlSearchComponent);
            }
        });
        componentContainer.addComponent(tasksManager);
        componentContainer.addComponent(urlSearch);
        return componentContainer;
    }

    public String getSelectedHost() {
        return selectedHost;
    }

    public void setSelectedHost(final String selectedHost) {
        this.selectedHost = selectedHost;
        LOG.debug("selectedHost: {}", selectedHost);
    }

    public int getSelectedHttpResponseCode() {
        return selectedHttpResponseCode;
    }

    public void setSelectedHttpResponseCode(final int selectedHttpResponseCode) {
        this.selectedHttpResponseCode = selectedHttpResponseCode;
    }

    class MyConverterFactory extends DefaultConverterFactory {

        @Override
        public <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> createConverter(
                final Class<PRESENTATION> presentationType,
                final Class<MODEL> modelType) {
            // LOG.warn("Model type: {}", modelType.getCanonicalName());
            // Handle one particular type conversion
            if (String.class == presentationType && UUID.class == modelType)
                return (Converter<PRESENTATION, MODEL>) new StringToUuidConverter();
            if (String.class == presentationType
                    && TaskGeneralState.class == modelType)
                return (Converter<PRESENTATION, MODEL>) new StringToTaskGeneralStateConverter();
            if (String.class == presentationType && byte[].class == modelType)
                return (Converter<PRESENTATION, MODEL>) new StringToByteArrayConverter();
            if (TaskConfiguration.class == modelType)
                return null;
            // Default to the supertype
            return super.createConverter(presentationType, modelType);
        }
    }
}