package org.tokenizer.ui.views;

import java.util.ArrayList;
import java.util.List;

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

public class ClassicRobotTaskConfigurationField extends CustomField {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Form taskConfigurationForm;

    /**
     * Field factory creating a custom field for city selection.
     */
    protected static class ClassicRobotTaskConfigurationFieldFormFactory extends
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

    public ClassicRobotTaskConfigurationField() {
        taskConfigurationForm = new Form();
        taskConfigurationForm.setCaption("Task Configuration for "
                + getType().getSimpleName());
        taskConfigurationForm.setWriteThrough(false);
        taskConfigurationForm
                .setFormFieldFactory(new ClassicRobotTaskConfigurationFieldFormFactory());
        setCompositionRoot(taskConfigurationForm);
    }

    @Override
    public void setInternalValue(Object newValue) throws ReadOnlyException,
            ConversionException {
        ClassicRobotTaskConfiguration taskConfiguration = (newValue instanceof ClassicRobotTaskConfiguration) ? (ClassicRobotTaskConfiguration) newValue
                : new ClassicRobotTaskConfiguration();
        super.setInternalValue(taskConfiguration);

        List<String> visibleProperties = new ArrayList<String>();
        visibleProperties.add("host");
        visibleProperties.add("followRedirects");
        visibleProperties.add("followExternal");
        visibleProperties.add("agentName");
        visibleProperties.add("emailAddress");
        visibleProperties.add("webAddress");
        taskConfigurationForm.setItemDataSource(
                new BeanItem<ClassicRobotTaskConfiguration>(taskConfiguration),
                visibleProperties);
        
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