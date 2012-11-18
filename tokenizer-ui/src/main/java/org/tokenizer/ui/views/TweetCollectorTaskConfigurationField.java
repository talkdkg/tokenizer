package org.tokenizer.ui.views;

import org.tokenizer.executor.engine.twitter.TweetCollectorTaskConfiguration;
import org.tokenizer.executor.model.configuration.ClassicRobotTaskConfiguration;
import org.vaadin.addon.customfield.CustomField;

import com.vaadin.data.Buffered;
import com.vaadin.data.Item;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.TextArea;

public class TweetCollectorTaskConfigurationField extends CustomField {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Form taskConfigurationForm;

    /**
     * Field factory creating a custom field for city selection.
     */
    protected static class TweetCollectorTaskConfigurationFieldFormFactory
            extends DefaultFieldFactory {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Field createField(Item item, Object propertyId,
                Component uiContext) {
            Field field = null;
            if (propertyId.equals("keywords")) {
                TextArea textArea = new TextArea("Keywords",
                        item.getItemProperty(propertyId));
                textArea.setRows(20);
                field = textArea;
            } else {
                field = super.createField(item, propertyId, uiContext);
            }
            return field;
        }
    }

    public TweetCollectorTaskConfigurationField() {
        taskConfigurationForm = new Form();
        taskConfigurationForm.setCaption("Task Configuration for "
                + getType().getSimpleName());
        taskConfigurationForm.setWriteThrough(false);
        taskConfigurationForm
                .setFormFieldFactory(new TweetCollectorTaskConfigurationFieldFormFactory());
        setCompositionRoot(taskConfigurationForm);
    }

    @Override
    public void setInternalValue(Object newValue) throws ReadOnlyException,
            ConversionException {
        TweetCollectorTaskConfiguration taskConfiguration = (newValue instanceof TweetCollectorTaskConfiguration) ? 
                (TweetCollectorTaskConfiguration) newValue
                : new TweetCollectorTaskConfiguration();
        super.setInternalValue(taskConfiguration);
        taskConfigurationForm
                .setItemDataSource(new BeanItem<TweetCollectorTaskConfiguration>(
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
        return ClassicRobotTaskConfiguration.class;
    }
}
