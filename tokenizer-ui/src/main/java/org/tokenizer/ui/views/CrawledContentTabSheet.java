package org.tokenizer.ui.views;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.ui.MyVaadinUI;

import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class CrawledContentTabSheet extends TabSheet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
            .getLogger(CrawledContentTabSheet.class);
    private final MyVaadinUI app;
    private final Label htmlLabel;
    private final Label sourceLabel;
    private final VerticalLayout xmlLayout;
    private final VerticalLayout messageLayout;

    public CrawledContentTabSheet(final MyVaadinUI app) {
        this.app = app;
        setCaption("Crawled Content");
        // HTML Formatted:
        htmlLabel = new Label();
        htmlLabel.setContentMode(Label.CONTENT_RAW);
        Panel htmlPanel = new Panel("HTML", htmlLabel);
        addTab(htmlPanel);
        // HTML Source:
        sourceLabel = new Label();
        sourceLabel.setContentMode(Label.CONTENT_TEXT);
        Panel sourcePanel = new Panel("Source", sourceLabel);
        addTab(sourcePanel);
        // XML Snippets:
        xmlLayout = new VerticalLayout();
        addTab(xmlLayout);
        // Parsed XML:
        messageLayout = new VerticalLayout();
        addTab(messageLayout);
        setHeight(100, UNITS_PERCENTAGE);
    }

    public Label getHtmlLabel() {
        return htmlLabel;
    }

    public Label getSourceLabel() {
        return sourceLabel;
    }

    public VerticalLayout getXmlLayout() {
        return xmlLayout;
    }

    public VerticalLayout getMessageLayout() {
        return messageLayout;
    }
}
