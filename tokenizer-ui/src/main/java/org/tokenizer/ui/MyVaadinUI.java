package org.tokenizer.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.reflections.Reflections;
import org.springframework.context.ApplicationContext;
import org.tokenizer.core.context.ApplicationContextProvider;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.ui.components.MessageSearchComponent;
import org.tokenizer.ui.components.TaskInfoComponent;
import org.tokenizer.ui.components.UrlSearchComponent;
import org.tokenizer.ui.demo.AbstractVaadinChartExample;
import org.tokenizer.ui.demo.SkipFromDemo;

import com.vaadin.addon.charts.ChartOptions;
import com.vaadin.addon.charts.themes.GrayTheme;
import com.vaadin.addon.charts.themes.GridTheme;
import com.vaadin.addon.charts.themes.HighChartsDefaultTheme;
import com.vaadin.addon.charts.themes.SkiesTheme;
import com.vaadin.addon.charts.themes.VaadinTheme;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Tree;
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
    private Tree tree;
    private OptionGroup themeSelector;

    private static Map<String, List<Class<? extends AbstractVaadinChartExample>>> tests;

    static {
        Reflections reflections = new Reflections("org.tokenizer.ui.demo");

        Set<Class<? extends AbstractVaadinChartExample>> subTypes = reflections
                .getSubTypesOf(AbstractVaadinChartExample.class);

        Map<String, List<Class<? extends AbstractVaadinChartExample>>> grouped = new HashMap<String, List<Class<? extends AbstractVaadinChartExample>>>();

        for (Class<? extends AbstractVaadinChartExample> class1 : subTypes) {
            if (class1.getAnnotation(SkipFromDemo.class) != null) {
                continue;
            }
            Package package1 = class1.getPackage();
            String name = package1.getName();
            name = name.substring(name.lastIndexOf(".") + 1);

            List<Class<? extends AbstractVaadinChartExample>> list = grouped
                    .get(name);
            if (list == null) {
                list = new ArrayList<Class<? extends AbstractVaadinChartExample>>();
                grouped.put(name, list);
            }
            list.add(class1);
            Collections
                    .sort(list,
                            new Comparator<Class<? extends AbstractVaadinChartExample>>() {

                                @Override
                                public int compare(
                                        final Class<? extends AbstractVaadinChartExample> o1,
                                        final Class<? extends AbstractVaadinChartExample> o2) {
                                    String simpleName = o1.getSimpleName();
                                    String simpleName2 = o2.getSimpleName();
                                    return simpleName.compareTo(simpleName2);
                                }
                            });
        }
        tests = grouped;
    }

    private static final String[] GROUP_ORDER = { "Admin", "Messages", "URLs" };

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

        /*
        layout = new VerticalLayout();
        layout.addComponent(buildToolbar());
        mainPanel = new Panel();
        layout.addComponent(mainPanel);
        setContent(layout);
        */

        final TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();

        HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();
        horizontalSplitPanel.setSecondComponent(tabSheet);
        horizontalSplitPanel.setSplitPosition(20);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        //verticalLayout.addComponent(logoc);
        verticalLayout.addComponent(horizontalSplitPanel);
        verticalLayout.setExpandRatio(horizontalSplitPanel, 1);
        setContent(verticalLayout);

        themeSelector = new OptionGroup("Theme");
        themeSelector.addItem(VaadinTheme.class);
        themeSelector.setItemCaption(VaadinTheme.class, "Vaadin");
        themeSelector.addItem(SkiesTheme.class);
        themeSelector.setItemCaption(SkiesTheme.class, "Skies");
        themeSelector.addItem(GridTheme.class);
        themeSelector.setItemCaption(GridTheme.class, "Grid");
        themeSelector.addItem(GrayTheme.class);
        themeSelector.setItemCaption(GrayTheme.class, "Gray");
        themeSelector.addItem(HighChartsDefaultTheme.class);
        themeSelector
                .setItemCaption(HighChartsDefaultTheme.class, "Highcharts");
        themeSelector.setImmediate(true);
        themeSelector.select(VaadinTheme.class);
        themeSelector.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(final ValueChangeEvent event) {
                @SuppressWarnings("unchecked")
                Class<? extends Theme> value = (Class<? extends Theme>) event
                        .getProperty().getValue();
                try {
                    ChartOptions.get().setTheme(
                            (com.vaadin.addon.charts.model.style.Theme) value
                                    .newInstance());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        });

        tree = new Tree("Chart examples");
        tree.setImmediate(true);
        tree.setContainerDataSource(getContainer());
        tree.setItemCaptionPropertyId("displayName");

        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        content.setSpacing(true);

        content.addComponent(tree);
        content.addComponent(themeSelector);
        horizontalSplitPanel.setFirstComponent(content);

        tree.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(final ValueChangeEvent event) {
                Object value2 = event.getProperty().getValue();
                if (value2 instanceof Class) {
                    try {
                        Class value = (Class) value2;
                        AbstractVaadinChartExample newInstance = (AbstractVaadinChartExample) value
                                .newInstance();
                        tabSheet.removeAllComponents();
                        tabSheet.addTab(newInstance, "Graph");

                        String r = "/" + value.getName().replace(".", "/")
                                + ".java";
                        r = value.getSimpleName() + ".java";
                        InputStream resourceAsStream = newInstance.getClass()
                                .getResourceAsStream(r);
                        String code = IOUtils.toString(resourceAsStream);
                        Label c = new Label("<pre class='prettyprint'>" + code
                                + "</pre>");
                        c.setContentMode(ContentMode.HTML);
                        tabSheet.addTab(c, "Source");

                        Page.getCurrent().setUriFragment(value.getSimpleName(),
                                false);

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        // e.printStackTrace();
                    }
                } else {
                    tree.expandItemsRecursively(value2);
                }
            }
        });

    }

    private Component buildToolbar() {
        ComponentContainer componentContainer = new HorizontalLayout();
        Button tasksManager = new Button("Tasks Manager",
                new Button.ClickListener() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(final ClickEvent event) {
                        if (taskInfoComponent == null) {
                            taskInfoComponent = new TaskInfoComponent();
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

    private HierarchicalContainer getContainer() {

        HierarchicalContainer hierarchicalContainer = new HierarchicalContainer();
        hierarchicalContainer.addContainerProperty("displayName", String.class,
                "");

        for (String g : GROUP_ORDER) {
            String group = g.replaceAll(" ", "").toLowerCase();
            Item groupItem = hierarchicalContainer.addItem(group);
            groupItem.getItemProperty("displayName").setValue(g);
            List<Class<? extends AbstractVaadinChartExample>> list = tests
                    .get(group);
            for (Class<? extends AbstractVaadinChartExample> class1 : list) {
                Item testItem = hierarchicalContainer.addItem(class1);
                testItem.getItemProperty("displayName").setValue(
                        class1.getSimpleName());
                hierarchicalContainer.setParent(class1, group);
                hierarchicalContainer.setChildrenAllowed(class1, false);
            }

        }

        return hierarchicalContainer;
    }

}