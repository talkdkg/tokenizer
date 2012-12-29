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
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.crawler.db.WebpageRecord;
import org.tokenizer.ui.MyVaadinApplication;
import org.tokenizer.ui.lists.UrlQuery;
import org.tokenizer.ui.lists.UrlRecordList;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.VerticalSplitPanel;

public class TaskOutputView extends VerticalSplitPanel implements
        Property.ValueChangeListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
            .getLogger(TaskOutputView.class);
    private final MyVaadinApplication app;
    private UrlRecordList urlRecordList;
    private final CrawledContentTabSheet crawledContentTabSheet;

    public TaskOutputView(final MyVaadinApplication app) {
        this.app = app;
        setCaption("Task Output View");
        urlRecordList = new UrlRecordList(app);
        setFirstComponent(urlRecordList);
        urlRecordList.addListener(this);
        crawledContentTabSheet = new CrawledContentTabSheet(app);
        setSecondComponent(crawledContentTabSheet);
        setSplitPosition(40);
    }

    @Override
    public void valueChange(final ValueChangeEvent event) {
        Property property = event.getProperty();
        if (property == this.urlRecordList) {
            Object itemId = this.urlRecordList.getValue();
            byte[] digest = ((UrlRecord) this.urlRecordList
                    .getContainerProperty(itemId, UrlQuery.URL_RECORD)
                    .getValue()).getWebpageDigest();
            WebpageRecord webpage = null;
            try {
                webpage = MyVaadinApplication.getRepository().getWebpageRecord(
                        digest);
            } catch (ConnectionException e) {
                LOG.error("", e);
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
            Property htmlProp = new ObjectProperty<String>(html, String.class);
            crawledContentTabSheet.getHtmlLabel().setPropertyDataSource(
                    htmlProp);
            crawledContentTabSheet.getSourceLabel().setPropertyDataSource(
                    htmlProp);
        }
    }

    public UrlRecordList getUrlRecordList() {
        return urlRecordList;
    }

    public synchronized void setUrlRecordList(
            final UrlRecordList newUrlRecordList) {
        newUrlRecordList.addListener(this);
        replaceComponent(this.urlRecordList, newUrlRecordList);
        this.urlRecordList = newUrlRecordList;
        // this.urlRecordList.requestRepaintAll();
    }
}
