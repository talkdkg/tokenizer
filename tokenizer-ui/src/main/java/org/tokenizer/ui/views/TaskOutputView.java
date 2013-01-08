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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.crawler.db.MessageRecord;
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.crawler.db.WebpageRecord;
import org.tokenizer.crawler.db.XmlRecord;
import org.tokenizer.ui.MyVaadinApplication;
import org.tokenizer.ui.lists.UrlQuery;
import org.tokenizer.ui.lists.UrlRecordList;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
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
            if (webpage != null) {
                try {
                    List<XmlRecord> xmlRecords = MyVaadinApplication
                            .getRepository().listXmlRecords(
                                    webpage.getXmlLinks());
                    crawledContentTabSheet.getXmlPanel().removeAllComponents();
                    for (XmlRecord xmlRecord : xmlRecords) {
                        if (xmlRecord == null) {
                            continue;
                        }
                        LOG.debug(xmlRecord.toString());
                        Label xmlLabel = new Label();
                        xmlLabel.setContentMode(Label.CONTENT_PREFORMATTED);
                        xmlLabel.setPropertyDataSource(new ObjectProperty<String>(
                                new String(xmlRecord.getContent(), "UTF-8"),
                                String.class));
                        crawledContentTabSheet.getXmlPanel().addComponent(
                                xmlLabel);
                    }
                } catch (ConnectionException e) {
                    LOG.error("", e);
                } catch (UnsupportedEncodingException e) {
                    LOG.error("", e);
                }
            }
            if (webpage != null) {
                try {
                    List<MessageRecord> messageRecords = MyVaadinApplication
                            .getRepository().listMessageRecords(
                                    webpage.getXmlLinks());
                    crawledContentTabSheet.getMessagePanel()
                            .removeAllComponents();
                    int i = 0;
                    for (MessageRecord messageRecord : messageRecords) {
                        if (messageRecord == null) {
                            continue;
                        }
                        Form form = new Form();
                        form.setCaption("Message #" + i);
                        i++;
                        BeanItem<MessageRecord> item = new BeanItem(
                                messageRecord);
                        form.setFormFieldFactory(new MessageFieldFactory());
                        form.setItemDataSource(item);
                        form.setReadOnly(true);
                        crawledContentTabSheet.getMessagePanel().addComponent(
                                form);
                        /*
                         * LOG.debug(messageRecord.toString()); Label
                         * messageTitleLabel = new Label("Message #");
                         * messageTitleLabel.setValue(i); i++;
                         * crawledContentTabSheet
                         * .getMessagePanel().addComponent( messageTitleLabel);
                         * Label topic = new Label(); topic.setCaption("Topic");
                         * topic.setValue(messageRecord.getTopic());
                         * crawledContentTabSheet
                         * .getMessagePanel().addComponent( topic); Label date =
                         * new Label(); date.setCaption("Date");
                         * date.setValue(messageRecord.getDate());
                         * crawledContentTabSheet
                         * .getMessagePanel().addComponent( date); Label author
                         * = new Label(); author.setCaption("Author");
                         * author.setValue(messageRecord.getAuthor());
                         * crawledContentTabSheet
                         * .getMessagePanel().addComponent( author); Label age =
                         * new Label(); age.setCaption("Age");
                         * age.setValue(messageRecord.getAge());
                         * crawledContentTabSheet
                         * .getMessagePanel().addComponent( age); Label sex =
                         * new Label(); sex.setCaption("Sex");
                         * sex.setValue(messageRecord.getSex());
                         * crawledContentTabSheet
                         * .getMessagePanel().addComponent( topic); Label title
                         * = new Label(); title.setCaption("Title");
                         * title.setValue(messageRecord.getTitle());
                         * crawledContentTabSheet
                         * .getMessagePanel().addComponent( title); Label
                         * content = new Label(); content.setCaption("Content");
                         * content.setValue(messageRecord.getContent());
                         * crawledContentTabSheet
                         * .getMessagePanel().addComponent( content); Label
                         * userRating = new Label();
                         * userRating.setCaption("User Rating");
                         * userRating.setValue(messageRecord.getUserRating());
                         * crawledContentTabSheet
                         * .getMessagePanel().addComponent( userRating);
                         */
                    }
                } catch (ConnectionException e) {
                    LOG.error("", e);
                }
            }
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

    private class MessageFieldFactory extends DefaultFieldFactory {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public MessageFieldFactory() {
        }

        @Override
        public Field createField(final Item item, final Object propertyId,
                final Component uiContext) {
            TextArea field = new TextArea();
            field.setRows(3);
            field.setColumns(120);
            field.setCaption("### " + createCaptionByPropertyId(propertyId)
                    + " ###");
            return field;
        }
    }
}
