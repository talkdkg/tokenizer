/*
 * Copyright 2007-2012 Tokenizer Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tokenizer.ui.views;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.crawler.db.WebpageRecord;
import org.tokenizer.ui.MyVaadinApplication;
import org.tokenizer.ui.lists.UrlRecordList;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalSplitPanel;

public class CrawledContentView extends VerticalSplitPanel implements
        Property.ValueChangeListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
            .getLogger(CrawledContentView.class);
    private final MyVaadinApplication app;
    private final UrlRecordList urlRecordList;
    private final CrawlerRepository repository;
    private final Label htmlLabel;
    private final Label sourceLabel;

    public CrawledContentView(final MyVaadinApplication app) {
        this.app = app;
        this.repository = app.getRepository();
        setCaption("Crawled Content");
        setSizeFull();
        this.urlRecordList = new UrlRecordList(app);
        setFirstComponent(this.urlRecordList);
        this.urlRecordList.addListener(this);
        this.htmlLabel = new Label();
        this.htmlLabel.setContentMode(Label.CONTENT_RAW);
        this.sourceLabel = new Label();
        this.sourceLabel.setContentMode(Label.CONTENT_TEXT);
        Panel htmlPanel = new Panel("HTML");
        htmlPanel.addComponent(this.htmlLabel);
        Panel sourcePanel = new Panel("Source");
        sourcePanel.addComponent(this.sourceLabel);
        // Accordion a = new Accordion();
        TabSheet a = new TabSheet();
        a.addTab(htmlPanel);
        a.addTab(sourcePanel);
        setSecondComponent(a);
        setSplitPosition(40);
    }

    @Override
    public void valueChange(final ValueChangeEvent event) {
        Property property = event.getProperty();
        if (property == this.urlRecordList) {
            Object itemId = this.urlRecordList.getValue();
            UrlRecord urlRecord = (UrlRecord) itemId;
            if (urlRecord.getHttpResponseCode() != 200) {
                Property htmlProp = new ObjectProperty<String>("<h1>N/A</h1>",
                        String.class);
                this.htmlLabel.setPropertyDataSource(htmlProp);
                this.sourceLabel.setPropertyDataSource(htmlProp);
            } else {
                byte[] digest = urlRecord.getDigest();
                WebpageRecord webpage = null;
                try {
                    webpage = repository.getWebpageRecord(digest);
                    LOG.warn("webpage: {}", webpage);
                } catch (ConnectionException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                String html = null;
                try {
                    if (webpage != null) {
                        html = new String(webpage.getContent(),
                                webpage.getCharset());
                    }
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
