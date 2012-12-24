package org.tokenizer.ui.views;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.ui.MyVaadinApplication;

import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;

public class CrawledContentTabSheet extends TabSheet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
            .getLogger(CrawledContentTabSheet.class);
    private final MyVaadinApplication app;
    private final Label htmlLabel;
    private final Label sourceLabel;

    public CrawledContentTabSheet(final MyVaadinApplication app) {
        this.app = app;
        setCaption("Crawled Content");
        this.htmlLabel = new Label();
        this.htmlLabel.setContentMode(Label.CONTENT_RAW);
        this.sourceLabel = new Label();
        this.sourceLabel.setContentMode(Label.CONTENT_TEXT);
        Panel htmlPanel = new Panel("HTML");
        htmlPanel.addComponent(this.htmlLabel);
        Panel sourcePanel = new Panel("Source");
        sourcePanel.addComponent(this.sourceLabel);
        addTab(htmlPanel);
        addTab(sourcePanel);
        setHeight(100, UNITS_PERCENTAGE);
    }

    public Label getHtmlLabel() {
        return htmlLabel;
    }

    public Label getSourceLabel() {
        return sourceLabel;
    }
}
