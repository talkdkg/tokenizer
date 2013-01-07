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
    private final Panel xmlPanel;

    public CrawledContentTabSheet(final MyVaadinApplication app) {
        this.app = app;
        setCaption("Crawled Content");
        // HTML Formatted:
        htmlLabel = new Label();
        htmlLabel.setContentMode(Label.CONTENT_RAW);
        Panel htmlPanel = new Panel("HTML");
        htmlPanel.addComponent(htmlLabel);
        addTab(htmlPanel);
        // HTML Source:
        sourceLabel = new Label();
        sourceLabel.setContentMode(Label.CONTENT_TEXT);
        Panel sourcePanel = new Panel("Source");
        sourcePanel.addComponent(sourceLabel);
        addTab(sourcePanel);
        // XML Snippets:
        Label xmlLabel = new Label();
        xmlLabel.setContentMode(Label.CONTENT_TEXT);
        xmlPanel = new Panel("XML Snippets");
        xmlPanel.addComponent(xmlLabel);
        xmlPanel.removeAllComponents();
        addTab(xmlPanel);
        setHeight(100, UNITS_PERCENTAGE);
    }

    public Label getHtmlLabel() {
        return htmlLabel;
    }

    public Label getSourceLabel() {
        return sourceLabel;
    }

    public Panel getXmlPanel() {
        return xmlPanel;
    }
}
