package org.tokenizer.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.tokenizer.executor.model.configuration.HtmlSplitterTaskConfiguration;
import org.vaadin.addon.customfield.CustomField;

import com.vaadin.data.Buffered;
import com.vaadin.data.Item;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;

public class HtmlSplitterTaskConfigurationField extends CustomField {
    private static final long serialVersionUID = 1L;
    private Form taskConfigurationForm;

    /**
     * Field factory creating a custom field for city selection.
     */
    protected static class HtmlSplitterTaskConfigurationFieldFormFactory extends
            DefaultFieldFactory {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Field createField(Item item, Object propertyId,
                Component uiContext) {
            return super.createField(item, propertyId, uiContext);
        }
    }

    public HtmlSplitterTaskConfigurationField() {
        super();
        taskConfigurationForm = new Form();
        taskConfigurationForm.setCaption("Task Configuration for "
                + getType().getSimpleName());
        taskConfigurationForm.setWriteThrough(false);
        taskConfigurationForm
                .setFormFieldFactory(new HtmlSplitterTaskConfigurationFieldFormFactory());
        setCompositionRoot(taskConfigurationForm);
    }

    @Override
    public void setInternalValue(Object newValue) throws ReadOnlyException,
            ConversionException {
        HtmlSplitterTaskConfiguration taskConfiguration = (newValue instanceof HtmlSplitterTaskConfiguration) ? (HtmlSplitterTaskConfiguration) newValue
                : new HtmlSplitterTaskConfiguration();
        super.setInternalValue(taskConfiguration);
        taskConfigurationForm
                .setItemDataSource(new BeanItem<HtmlSplitterTaskConfiguration>(
                        taskConfiguration));
    }

    /**
     * commit changes
     */
    @Override
    public void commit() throws Buffered.SourceException, InvalidValueException {
        super.commit();
        taskConfigurationForm.commit();
    }

    /**
     * discard changes
     */
    @Override
    public void discard() throws Buffered.SourceException {
        super.discard();
        taskConfigurationForm.discard();
    }

    @Override
    public Class<?> getType() {
        return HtmlSplitterTaskConfiguration.class;
    }
}
