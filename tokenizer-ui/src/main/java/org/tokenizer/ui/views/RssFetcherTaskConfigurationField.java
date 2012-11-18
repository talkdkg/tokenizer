package org.tokenizer.ui.views;

import org.tokenizer.executor.model.configuration.RssFetcherTaskConfiguration;
import org.vaadin.addon.customfield.CustomField;

import com.vaadin.data.Buffered;
import com.vaadin.data.Item;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;

public class RssFetcherTaskConfigurationField extends CustomField {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Form taskConfigurationForm;

    /**
     * Field factory creating a custom field for city selection.
     */
    protected static class RssFetcherTaskConfigurationFieldFormFactory extends
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

    public RssFetcherTaskConfigurationField() {
        taskConfigurationForm = new Form();
        taskConfigurationForm.setCaption("Task Configuration for "
                + getType().getSimpleName());
        taskConfigurationForm.setWriteThrough(false);
        taskConfigurationForm
                .setFormFieldFactory(new RssFetcherTaskConfigurationFieldFormFactory());
        setCompositionRoot(taskConfigurationForm);
    }

    @Override
    public void setInternalValue(Object newValue) throws ReadOnlyException,
            ConversionException {
        RssFetcherTaskConfiguration taskConfiguration = (newValue instanceof RssFetcherTaskConfiguration) ? (RssFetcherTaskConfiguration) newValue
                : new RssFetcherTaskConfiguration();
        super.setInternalValue(taskConfiguration);
        // set item data source and visible properties in a single operation to
        // avoid creating fields multiple times
        // List<String> visibleProperties = new ArrayList<String>();
        // visibleProperties.add("host");
        // visibleProperties.add("followRedirects");
        // visibleProperties.add("followExternal");
        // visibleProperties.add("agentName");
        // visibleProperties.add("emailAddress");
        // visibleProperties.add("webAddress");
        // taskConfigurationForm.setItemDataSource(
        // new BeanItem<ClassicRobotTaskConfiguration>(taskConfiguration),
        // visibleProperties);
        taskConfigurationForm
                .setItemDataSource(new BeanItem<RssFetcherTaskConfiguration>(
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
        return RssFetcherTaskConfiguration.class;
    }
}
