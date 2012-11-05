package org.tokenizer.ui.views;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.crawler.db.CrawlerHBaseRepository;
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.crawler.db.WebpageRecord;
import org.tokenizer.ui.MyVaadinApplication;
import org.tokenizer.ui.lists.UrlRecordList;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalSplitPanel;

public class CrawledContentView extends VerticalSplitPanel implements
        Property.ValueChangeListener {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
            .getLogger(CrawledContentView.class);
    private MyVaadinApplication app;
    private UrlRecordList urlRecordList;
    private CrawlerHBaseRepository repository;
    private Label htmlLabel;
    private Label sourceLabel;

    public CrawledContentView(MyVaadinApplication app) {
        this.app = app;
        this.repository = app.getRepository();
        setCaption("Crawled Content");
        setSizeFull();
        this.urlRecordList = new UrlRecordList(app);
        setFirstComponent(this.urlRecordList);
        this.urlRecordList.addListener((Property.ValueChangeListener) this);
        this.htmlLabel = new Label();
        this.htmlLabel.setContentMode(Label.CONTENT_RAW);
        this.sourceLabel = new Label();
        this.sourceLabel.setContentMode(Label.CONTENT_TEXT);
        Panel htmlPanel = new Panel("HTML");
        htmlPanel.addComponent(this.htmlLabel);
        Panel sourcePanel = new Panel("Source");
        sourcePanel.addComponent(this.sourceLabel);
        Accordion a = new Accordion();
        a.addTab(htmlPanel);
        a.addTab(sourcePanel);
        setSecondComponent(a);
        setSplitPosition(40);
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        Property property = event.getProperty();
        if (property == this.urlRecordList) {
            Object itemId = this.urlRecordList.getValue();
            String url = (String) itemId;
            UrlRecord urlRecord = repository.getUrlRecord(url);
            if (urlRecord.getHttpResponseCode() != 200) {
                Property htmlProp = new ObjectProperty<String>("<h1>N/A</h1>",
                        String.class);
                this.htmlLabel.setPropertyDataSource(htmlProp);
                this.sourceLabel.setPropertyDataSource(htmlProp);
            } else {
                byte[] digest = urlRecord.getDigest();
                WebpageRecord webpage = repository.getWebpageRecord(digest);
                String html;
                try {
                    html = new String(webpage.getContent(),
                            webpage.getCharset());
                } catch (UnsupportedEncodingException e) {
                    LOG.error("", e);
                    html = e.getMessage();
                }
                Property htmlProp = new ObjectProperty<String>(html,
                        String.class);
                this.htmlLabel.setPropertyDataSource(htmlProp);
                this.sourceLabel.setPropertyDataSource(htmlProp);
            }
        }
    }
}
