package org.tokenizer.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.vaadin.addon.customfield.CustomField;


import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
//import com.vaadin.ui.CustomField;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class FormExample extends CustomComponent  {
    private static final long serialVersionUID = -4292553844521293140L;

    public void init (String context) {
        VerticalLayout layout = new VerticalLayout();

        if ("simple".equals(context))
            simple();
        else if ("visibleproperties".equals(context))
            visibleproperties(layout);
        else if ("buffering".equals(context))
            buffering ();
        else if ("select".equals(context))
            selectExample ();
        else if ("attachfield".equals(context))
            attachField ();
        else if ("attachfield2".equals(context))
            attachField2 ();
        else if ("customlayout".equals(context))
            customlayout (layout);
        else if ("gridlayout".equals(context))
            gridlayout(layout);
        else if ("propertyfiltering".equals(context))
            propertyfiltering ();
        else if ("propertyfiltering".equals(context))
            propertyfiltering ();
        else if ("boundselect".equals(context))
            boundselect(layout);
        else if ("nestedtable".equals(context))
            nestedtable(layout);
        else if ("nestedforms".equals(context))
            nestedform(layout);
        else if ("wrapcaptions".equals(context))
            wrapcaptions (layout);
        
        if (getCompositionRoot() == null)
            setCompositionRoot(layout);
    }
    
    void simple() {
        VerticalLayout layout = new VerticalLayout();

        // BEGIN-EXAMPLE: component.form.simple
        //Form form;
        // END-EXAMPLE: component.form.simple
        
        setCompositionRoot(layout);
    }

    void visibleproperties(VerticalLayout layout) {
        // BEGIN-EXAMPLE: component.form.visibleproperties
        // Some data to bind the form to
        PropertysetItem item = new PropertysetItem();
        item.addItemProperty("name",  new ObjectProperty<String>(""));
        item.addItemProperty("email", new ObjectProperty<String>(""));
        item.addItemProperty("city",  new ObjectProperty<String>(""));
        
        Form form = new Form();
        form.setCaption("Form with some fields");
        form.setItemDataSource(item);

        // Show only two of the items, in this order
        form.setVisibleItemProperties(new Object[]{"name", "email"});
        // END-EXAMPLE: component.form.visibleproperties
        
        layout.addComponent(form);
    }
    
    void buffering() {
        VerticalLayout layout = new VerticalLayout();

        // BEGIN-EXAMPLE: component.form.buffering
        // Some data to bind the form to
        PropertysetItem item = new PropertysetItem();
        item.addItemProperty("name",  new ObjectProperty<String>(""));
        item.addItemProperty("email", new ObjectProperty<String>(""));

        // Read-write input form with buffering
        Form form = new Form();
        form.setItemDataSource(item);
        form.setCaption("Input Form");
        form.setWriteThrough(false);

        // Button that calls commit() for the form when clicked
        Button ok = new Button("OK", form, "commit");
        form.getFooter().addComponent(ok);
        
        // Button that calls discard() for the form when clicked
        Button reset = new Button("Reset", form, "discard");
        form.getFooter().addComponent(reset);
        
        // Read-only form to display the same data
        Form reader = new Form();
        reader.setItemDataSource(item);
        reader.setCaption("Read-Only Form");
        reader.setReadOnly(true);
        
        HorizontalLayout h = new HorizontalLayout();
        h.addComponent(form);
        h.addComponent(reader);
        // END-EXAMPLE: component.form.buffering
        
        h.setSpacing(true);
        layout.addComponent(h);
        setCompositionRoot(layout);
    }

    // BEGIN-EXAMPLE: component.form.select
    public class MoonBean implements java.io.Serializable {
        private static final long serialVersionUID = 239857295738497558L;

        String planetName;
        String moonName;
        
        public void setPlanet(String planet) {
            this.planetName = planet;
        }
        public String getPlanet() {
            return planetName;
        }
        public void setMoon(String moon) {
            this.moonName = moon;
        }
        public String getMoon() {
            return moonName;
        }
    }
    
    void selectExample() {
        VerticalLayout layout = new VerticalLayout();
        
        final String[][] planets = new String[][] {
                {"Mercury"},
                {"Venus"},
                {"Earth", "The Moon"},
                {"Mars", "Phobos", "Deimos"},
                {"Jupiter", "Io", "Europa", "Ganymedes", "Callisto"},
                {"Saturn", "Titan", "Tethys", "Dione", "Rhea", "Iapetus"},
                {"Uranus", "Miranda", "Ariel", "Umbriel", "Titania", "Oberon"},
                {"Neptune", "Triton", "Proteus", "Nereid", "Larissa"}};

        class MyFieldFactory implements FormFieldFactory {
            private static final long serialVersionUID = 5851939772204021747L;
            
            public Field createField(Item item, Object propertyId,
                                     Component uiContext) {
                final Form form = (Form) uiContext;
                
                String pid = (String) propertyId;
                if ("planet".equals(pid)) {
                    final ComboBox planet = new ComboBox("Planet");
                    planet.setNullSelectionAllowed(false);
                    planet.setInputPrompt("-- Select a Planet --");
                    
                    for (int pl=0; pl<planets.length; pl++)
                        planet.addItem(planets[pl][0]);
                    
                    planet.addListener(new ValueChangeListener() {
                        private static final long serialVersionUID = -4085124327898850666L;

                        public void valueChange(ValueChangeEvent event) {
                            String selected = (String) planet.getValue();
                            if (selected == null)
                                return;

                            ComboBox moon = (ComboBox) form.getField("moon");
                            for (int pl=0; pl<planets.length; pl++)
                                if (selected.equals(planets[pl][0])) {
                                    moon.removeAllItems();
                                    moon.setInputPrompt("-- Select a Moon --");
                                    moon.setNullSelectionAllowed(false);
                                    
                                    for (int mn=1; mn<planets[pl].length; mn++)
                                        moon.addItem(planets[pl][mn]);
                                    moon.setEnabled(planets[pl].length > 1);
                                }
                        }
                    });
                    planet.setImmediate(true);
                    
                    return planet;
                }

                if ("moon".equals(pid)) {
                    ComboBox moonSel = new ComboBox("Moon");
                    moonSel.setEnabled(false); // Select a planet first
                    moonSel.setImmediate(true);
                    return moonSel;
                }
                return null;
            }
            
        }

        // The form
        Form myform = new Form();
        myform.setCaption("My Little Form");
        myform.setFormFieldFactory(new MyFieldFactory());
        
        // Create a bean to use as a data source for the form
        MoonBean moonbean = new MoonBean();
        BeanItem<MoonBean> beanitem = new BeanItem<MoonBean>(moonbean);
        myform.setItemDataSource(beanitem);
        myform.setVisibleItemProperties(new Object[] {"planet", "moon"});

        // Put a non-Field component to the form and bind it to
        // the selection result
        Label label = new Label();
        label.setCaption("Selected");
        myform.getLayout().addComponent(label);
        label.setPropertyDataSource(beanitem.getItemProperty("moon"));

        // Some prettying
        myform.getLayout().setWidth("-1");
        myform.setWidth("-1");
        label.setWidth("-1");
        
        layout.addComponent(myform);
        setCompositionRoot(layout);
    }
    // END-EXAMPLE: component.form.select

    void attachField() {
        // BEGIN-EXAMPLE: component.form.attachfield
        final Form form = new Form() {
            private static final long serialVersionUID = 4149078195821559631L;
            
            HorizontalLayout hor = null;
            
            @Override
            protected void attachField(Object propertyId, Field field) {
                // Group consecutive check boxes on the same line
                if (field instanceof CheckBox) {
                    if (hor == null) {
                        hor = new HorizontalLayout();
                        
                        // Add it to the FormLayout
                        getLayout().addComponent(hor);
                    }
                    hor.addComponent(field);
                } else { // Some other type of field
                    hor = null;
                    super.attachField(propertyId, field);
                }
            }
        };

        form.addField("other1", new TextField("Name"));
        form.addField("cb1", new CheckBox("This"));
        form.addField("cb2", new CheckBox("That"));
        form.addField("cb3", new CheckBox("It"));
        form.addField("cb4", new CheckBox("Those"));
        form.addField("other2", new TextField("Address"));
        // END-EXAMPLE: component.form.attachfield
        
        setCompositionRoot(form);
    }
    
    void attachField2() {
        // BEGIN-EXAMPLE: component.form.attachfield2
        // A form with fields split into two subsections
        final Form form = new Form() {
            private static final long serialVersionUID = 4149078195821559631L;

            // Replacement root layout for Form
            VerticalLayout ver = new VerticalLayout();

            // Use Form as a layout component for these subsections
            Form section1 = new Form();
            Form section2 = new Form();

            { // Constructor for anonymous class
                section1.setCaption("Basic Data");
                section2.setCaption("Details");
                ver.addComponent(section1);
                ver.addComponent(section2);
                ver.setMargin(true);
                setLayout(ver); // Replace the default FormLayout
            }
            
            @Override
            protected void attachField(Object propertyId, Field field) {
                // Put some of the fields in the first section
                if (Arrays.asList("firstName", "lastName")
                        .contains(propertyId)) {
                    section1.getLayout().addComponent(field);
                } else {
                    // The rest go to the other section
                    section2.getLayout().addComponent(field);
                }
            }
        };
        
        form.setCaption("My Form");
        form.addStyleName("bordered"); // Custom style
        form.setDescription("This form has two data sections.");

        form.addField("firstName", new TextField("First Name"));
        form.addField("lastName", new TextField("Last Name"));
        form.addField("city", new TextField("City"));
        form.addField("birthDate", new TextField("Birth Date"));
        
        // Footer stuff for the outer form
        Button commit = new Button("OK");
        form.getFooter().setWidth("100%");
        form.getFooter().addComponent(commit);
        ((HorizontalLayout)form.getFooter())
            .setComponentAlignment(commit, Alignment.TOP_RIGHT);
        form.setWidth("350px");
        // END-EXAMPLE: component.form.attachfield2

        setCompositionRoot(form);
    }
    
    void customlayout(VerticalLayout layout) {
        // BEGIN-EXAMPLE: component.form.layout.customlayout
        // Have a form
        Form form = new Form();
        form.setCaption("This is a form, clear?");
        form.addStyleName("bordered"); // Custom style
        form.setWidth("400px"); // Would be 100% otherwise

        // Make the <div> elements have "display: inline-block"
        form.addStyleName("inlinefields");
        
        // Use a custom layout template with field placeholders
        String template =
            "Here is one field: " +
            "<div location='one'></div> and here " +
            "<div location='another'></div> is another.";

        // Create the custom layout from a stream
        CustomLayout clayout;
        try {
            clayout = new CustomLayout(
                new ByteArrayInputStream(template.getBytes()));
            
            // Set the form to use the template
            form.setLayout(clayout);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Bind fields to the placeholders
        form.addField("one", new TextField());
        form.addField("another", new TextField());
        // END-EXAMPLE: component.form.layout.customlayout

        layout.addComponent(form);
    }
 
    void gridlayout(VerticalLayout layout) {
        // BEGIN-EXAMPLE: component.form.layout.gridlayout
        // Have a form
        Form form = new Form() {
            private static final long serialVersionUID = -6342457426646710684L;

            // Alternative layout
            GridLayout layout = new GridLayout(2,1);

            {   // Initialization
                setCaption("The fields are forming");
                addStyleName("bordered"); // Custom style
                setWidth("400px"); // Would be 100% otherwise
                setImmediate(true);

                // Alternative layout for the form
                layout.setSpacing(true);
                layout.setWidth("100%");
                layout.setColumnExpandRatio(0, 0.5f);
                layout.setColumnExpandRatio(1, 0.5f);
                setLayout(layout);
            }

            @Override
            protected void attachField(Object propertyId, Field field) {
                if ("comment".equals(propertyId)) {
                    // A two-column field
                    layout.setRows(layout.getRows()+1);
                    layout.addComponent(field, 0, layout.getCursorY(),
                                        1, layout.getCursorY());
                } else // Single-column fields
                    layout.addComponent(field);
            }  
        };

        // Customize some field(s)
        form.setFormFieldFactory(new DefaultFieldFactory() {
            private static final long serialVersionUID = 8766187191796152187L;

            @Override
            public Field createField(Item item, Object propertyId, Component uiContext) {
                Field field;
                if ("maritalStatus".equals(propertyId)) {
                    OptionGroup select = new OptionGroup("Marital Status");
                    select.addStyleName("horizontal");
                    for (String s: new String[]{"married", "unmarried"})
                        select.addItem(s);
                    field = select;
                } else if ("country".equals(propertyId)) {
                    Select select = new Select("Country");
                    for (String s: new String[]{"Finland", "Sweden", "Norway"})
                        select.addItem(s);
                    field = select;
                } else if ("planet".equals(propertyId)) {
                    Select select = new Select("Planet");
                    for (String s: new String[]{"Mercury", "Venus", "Earth"})
                        select.addItem(s);
                    field = select;
                } else {
                    field = super.createField(item, propertyId, uiContext);
                    if ("comment".equals(propertyId))
                        field.setHeight("200px");
                }
                field.setWidth("100%");
                return field;
            }
        });
        
        // Have an item to edit
        PropertysetItem item = new PropertysetItem();
        String props[] = {"firstName", "lastName", "maritalStatus",
                "street", "city", "postCode", "country", "planet",
                "comment"};
        for (String itemId: props)
            item.addItemProperty(itemId,
                    new ObjectProperty<String>(new String()));
        form.setItemDataSource(item);
        
        Button commit = new Button("Save");
        form.getFooter().addComponent(commit);
        // END-EXAMPLE: component.form.layout.gridlayout

        layout.addComponent(form);
    }
    
    void propertyfiltering() {
        // BEGIN-EXAMPLE: component.form.propertyfiltering
        // Create a bean that has two properties: planet and moon
        MoonBean bean = new MoonBean();
        
        // Wrap it as an item
        BeanItem<MoonBean> item = new BeanItem<MoonBean>(bean);

        // Create a form
        final Form form = new Form();
        
        // Set the wrapper as the data source and use only
        // the listed properties in the form
        form.setItemDataSource(item, Arrays.asList("planet"));

        // Add some validation, etc.
        TextField field = (TextField) form.getField("planet"); 
        field.addValidator(
                new RegexpValidator("Mercury|Venus|Earth|Mars",
                                    "Must be an inner planet"));
        field.setNullRepresentation("");
        field.setInputPrompt("Enter a name");

        // Have a commit button
        Button ok = new Button("OK", form, "commit");
        form.getFooter().addComponent(ok);
        // END-EXAMPLE: component.form.propertyfiltering
        
        setCompositionRoot(form);
    }

    // BEGIN-EXAMPLE: component.form.subform.nestedtable
    public class Moon implements Serializable {
        private static final long serialVersionUID = -1954644276739599854L;

        String name;
        int    year; /** Year of discovery */
        String description;
        
        public Moon(String name, int discoveryYear, String description) {
            this.name = name;
            this.year = discoveryYear;
            this.description = description;
        }
        
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public int getYear() {
            return year;
        }
        public void setYear(int year) {
            this.year = year;
        }
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
    }
    
    public class Planet implements Serializable {
        private static final long serialVersionUID = 4522607189270722905L;

        String name;
        ArrayList<Moon> moons = new ArrayList<Moon>();
        
        public Planet(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public List<Moon> getMoons() {
            return moons;
        }
        public void setMoons(List<Moon> moons) {
            this.moons.clear();
            this.moons.addAll(moons);
        }
    }

    class MoonTable extends CustomField {
        private static final long serialVersionUID = 1572209152319890318L;
        
        Table table = new Table();
        BeanItemContainer<Moon> moonContainer =
            new BeanItemContainer<Moon>(Moon.class);

        public MoonTable() {
            table.setContainerDataSource(moonContainer);
            table.setTableFieldFactory(new TableFieldFactory() {
                private static final long serialVersionUID = 145934667135927307L;

                @Override
                public Field createField(Container container, Object itemId,
                        Object propertyId, Component uiContext) {
                    TextField field = new TextField();
                    if ("name".equals(propertyId))
                        field.setWidth("10em");
                    else if ("year".equals(propertyId))
                        field.setWidth("4em");
                    field.setImmediate(true);
                    return field;
                }
            });
            table.setVisibleColumns(new Object[]{"name", "year"});
            table.setEditable(!isReadOnly());
            setCompositionRoot(table);
        }

        @Override
        public Class<?> getType() {
            return ArrayList.class;
        }
        
        @Override
        public void setPropertyDataSource(Property newDataSource) {
            Object value = newDataSource.getValue();
            if (value instanceof List<?>) {
                @SuppressWarnings("unchecked")
                List<Moon> beans = (List<Moon>) value;
                moonContainer.removeAllItems();
                moonContainer.addAll(beans);
                table.setPageLength(beans.size());
            } else
                throw new ConversionException("Invalid type");

            super.setPropertyDataSource(newDataSource);
        }
        
        @Override
        public Object getValue() {
            ArrayList<Moon> beans = new ArrayList<Moon>(); 
            for (Object itemId: moonContainer.getItemIds())
                beans.add(moonContainer.getItem(itemId).getBean());
            return beans;
        }
        
        @Override
        public void setReadOnly(boolean readOnly) {
            table.setEditable(!readOnly);
            super.setReadOnly(readOnly);
        }
    }
    
    void nestedtable(Layout rootLayout) {
        // Create a nested bean
        Planet jupiter = new Planet("Jupiter");
        jupiter.setMoons(Arrays.asList(
                new Moon("io", 1610, null),
                new Moon("europa", 1610, null),
                new Moon("ganymedes", 1610, null),
                new Moon("callisto", 1610, null)
        ));

        // Wrap it
        final BeanItem<Planet> planetBean = new BeanItem<Planet>(jupiter);
        
        class MyNestedFormFactory extends DefaultFieldFactory {
            private static final long serialVersionUID = 7666685545829392917L;
            
            @Override
            public Field createField(Item item, Object propertyId,
                    Component uiContext) {
                Field field;
                if ("moons".equals(propertyId)) {
                    field = new MoonTable();
                    field.setCaption("Moons");
                } else
                    field = super.createField(item, propertyId, uiContext);
                return field;
            }
        }            
        
        // Bind it to a form
        final Form form = new Form();
        form.setFormFieldFactory(new MyNestedFormFactory());
        form.setItemDataSource(planetBean);
        form.setVisibleItemProperties(new Object[]{"name", "moons"});
        form.addStyleName("innertable");
        
        // Read-only form to display the buffered data model value
        final Form valueForm = new Form();
        valueForm.setFormFieldFactory(new MyNestedFormFactory());
        valueForm.setReadOnly(true);
        valueForm.addStyleName("innertable");
        
        Button save = new Button("Save");
        save.addListener(new ClickListener() {
            private static final long serialVersionUID = -5680315979429053662L;

            @Override
            public void buttonClick(ClickEvent event) {
                form.commit();
                valueForm.setItemDataSource(planetBean);
                valueForm.setVisibleItemProperties(new Object[]{"name", "moons"});
                valueForm.setReadOnly(true);
            }
        });
        form.getFooter().addComponent(save);

        // Put the forms in a layout
        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(form);
        layout.addComponent(valueForm);
        layout.setSpacing(true);
        rootLayout.addComponent(layout);
    }
    // END-EXAMPLE: component.form.subform.nestedtable

    // BEGIN-EXAMPLE: component.form.subform.nestedforms
    class MoonFormTable extends CustomField {
        private static final long serialVersionUID = 1572209152319890318L;
        
        Table table = new Table();
        BeanItemContainer<Moon> moonContainer =
            new BeanItemContainer<Moon>(Moon.class);
        VerticalLayout layout = new VerticalLayout();

        public MoonFormTable() {
            table = createTable();
            layout.addComponent(table);
            
            Button newMoon = new Button ("New Moon");
            newMoon.addListener(new ClickListener() {
                private static final long serialVersionUID = -3527533113666999375L;

                @Override
                public void buttonClick(ClickEvent event) {
                    // TODO Have to recreate and re-add all items because
                    // otherwise formats badly. Well, this doesn't work either.
                    table.removeAllItems();
                    for (Object itemId: moonContainer.getItemIds())
                        addItemToTable(itemId);

                    Object itemId = moonContainer.addBean(new Moon(null, 0, null));
                    addItemToTable(itemId);
                    
                    table.setPageLength(moonContainer.size());
                }
            });
            layout.addComponent(newMoon);
            layout.setComponentAlignment(newMoon, Alignment.MIDDLE_RIGHT);
            
            setCompositionRoot(layout);
        }
        
        Table createTable() {
            Table table = new Table();
            table.addContainerProperty("form", Form.class, null);
            table.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
            return table;
        }

        @Override
        public Class<?> getType() {
            return ArrayList.class;
        }
        
        @Override
        public void setPropertyDataSource(Property newDataSource) {
            Object value = newDataSource.getValue();
            if (value instanceof List<?>) {
                @SuppressWarnings("unchecked")
                List<Moon> beans = (List<Moon>) value;
                moonContainer.removeAllItems();
                moonContainer.addAll(beans);
                for (Object itemId: moonContainer.getItemIds())
                    addItemToTable(itemId);
                table.setPageLength(beans.size());
            } else
                throw new ConversionException("Invalid type");

            super.setPropertyDataSource(newDataSource);
        }
        
        class MoonForm extends Form {
            private static final long serialVersionUID = 7871869207247162191L;
            
            public MoonForm() {
                setHeight("150px"); // Must be fixed
                setWidth("300px");  // Must be fixed

                GridLayout layout = new GridLayout(2,2);
                layout.setRowExpandRatio(1, 1.0f);
                layout.setSizeFull();
                layout.setSpacing(true);
                setLayout(layout);

                setFormFieldFactory(new MoonFormFieldFactory());
            }

            @Override
            protected void attachField(Object propertyId,
                    Field field) {
                GridLayout layout = (GridLayout) getLayout();
                if ("description".equals(propertyId))
                    layout.addComponent(field, 0, 1, 1, 1);
                else
                    super.attachField(propertyId, field);
            }
            
            class MoonFormFieldFactory implements FormFieldFactory {
                private static final long serialVersionUID = 6178947951447993892L;

                @Override
                public Field createField(Item item, Object propertyId, Component uiContext) {
                    AbstractTextField field;
                    if ("name".equals(propertyId)) {
                        field = new TextField("Name");
                        field.setWidth("10em");
                    } else if ("year".equals(propertyId)) {
                        field = new TextField("Year");
                        field.setWidth("4em");
                    } else if ("description".equals(propertyId)) {
                        field = new TextArea("Description");
                        field.setWidth("100%");
                        field.setHeight("5em");
                    } else
                        return null;
                    field.setImmediate(true);
                    return field;
                }
            }
        }

        void addItemToTable (Object itemId) {
            // A form with a grid layout where the description
            // field takes one entire row.
            MoonForm form = new MoonForm();
            form.setItemDataSource(moonContainer.getItem(itemId));
            form.setVisibleItemProperties(
                    new Object[]{"name","year","description"});
            table.addItem(new Object[]{form}, itemId);
        }
        
        @Override
        public Object getValue() {
            ArrayList<Moon> beans = new ArrayList<Moon>(); 
            for (Object itemId: moonContainer.getItemIds())
                beans.add(moonContainer.getItem(itemId).getBean());
            return beans;
        }

        /** Forward read-only state to sub-components. */
        @Override
        public void setReadOnly(boolean readOnly) {
            table.setEditable(!readOnly);
            Container container = table.getContainerDataSource();
            for (Object itemId: container.getItemIds()) {
                Form form = (Form) container.getContainerProperty(itemId, "form").getValue();
                form.setReadOnly(readOnly);
            }
            super.setReadOnly(readOnly);
        }
    }
    
    void nestedform(Layout rootLayout) {
        // Create a nested bean
        Planet jupiter = new Planet("Jupiter");
        jupiter.setMoons(Arrays.asList(
                new Moon("Io", 1610, "Really hot"),
                new Moon("Europa", 1610, "Really cool"),
                new Moon("Ganymedes", 1610, "Big and colorful"),
                new Moon("Callisto", 1610, "The eldest child")
        ));

        // Wrap it
        final BeanItem<Planet> planetBean = new BeanItem<Planet>(jupiter);
        
        class MyNestedFormFactory extends DefaultFieldFactory {
            private static final long serialVersionUID = 7666685545829392917L;
            
            @Override
            public Field createField(Item item, Object propertyId,
                    Component uiContext) {
                Field field;
                if ("moons".equals(propertyId)) {
                    field = new MoonFormTable();
                    field.setCaption("Moons");
                } else
                    field = super.createField(item, propertyId, uiContext);
                return field;
            }
        }            
        
        // Bind it to a form
        final Form form = new Form();
        form.setFormFieldFactory(new MyNestedFormFactory());
        form.setItemDataSource(planetBean);
        form.setVisibleItemProperties(new Object[]{"name", "moons"});
        form.addStyleName("innertable");
        
        // Read-only form to display the buffered data model value
        final Form valueForm = new Form();
        valueForm.setFormFieldFactory(new MyNestedFormFactory());
        valueForm.setReadOnly(true);
        valueForm.addStyleName("innertable");
        
        Button save = new Button("Save");
        save.addListener(new ClickListener() {
            private static final long serialVersionUID = -5680315979429053662L;

            @Override
            public void buttonClick(ClickEvent event) {
                form.commit();
                valueForm.setItemDataSource(planetBean);
                valueForm.setVisibleItemProperties(new Object[]{"name", "moons"});
                valueForm.setReadOnly(true);
            }
        });
        form.getFooter().addComponent(save);

        // Put the forms in a layout
        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(form);
        layout.addComponent(valueForm);
        layout.setSpacing(true);
        rootLayout.addComponent(layout);
    }
    // END-EXAMPLE: component.form.subform.nestedforms
    
    // BEGIN-EXAMPLE: component.form.subform.boundselect
    /** There will be a collection of these to select from. */
    public class Constellation implements Serializable {
        private static final long serialVersionUID = -2449616256779384306L;

        String name;
        
        public Constellation(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
    
    /** Bean bound to one instance of the other bean type. */
    public class Star implements Serializable {
        private static final long serialVersionUID = 6751758582390444807L;

        String        name;
        Constellation constellation; // 1:1 relation to the other bean
        
        public Star(String name, Constellation constellation) {
            this.name = name;
            this.constellation = constellation;
        }
        
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Constellation getConstellation() {
            return constellation;
        }
        public void setConstellation(Constellation constellation) {
            this.constellation = constellation;
        }
    }

    void boundselect(Layout rootLayout) {
        // A collection from which to select
        ArrayList<Constellation> constellations =
            new ArrayList<Constellation>();
        constellations.add(new Constellation("Orion"));
        constellations.add(new Constellation("Gemini"));
        constellations.add(new Constellation("Taurus"));
        constellations.add(new Constellation("Canis Minor"));
        Constellation cma = new Constellation("Canis Major");
        constellations.add(cma);

        // Wrap them in a container for binding to a ComboBox
        final BeanItemContainer<Constellation> constellationContainer =
            new BeanItemContainer<Constellation> (Constellation.class);
        constellationContainer.addAll(constellations);
        
        // Create a bean that refers to an item in the collection
        Star sirius = new Star("Sirius", cma);
        
        // Wrap it
        final BeanItem<Star> starBean = new BeanItem<Star>(sirius);
        
        class MyFormFactory implements FormFieldFactory {
            private static final long serialVersionUID = 7666685545829392917L;

            @Override
            public Field createField(Item item, Object propertyId,
                    Component uiContext) {
                if ("constellation".equals(propertyId)) {
                    // Create a selection field that gets its contents
                    // from the possible choices. The actual current
                    // selection is bound automatically by the form.
                    ComboBox select = new ComboBox("Constellation",
                            constellationContainer);

                    // Show the constellation names in the ComboBox
                    select.setItemCaptionPropertyId("name");
                    
                    select.setNullSelectionAllowed(false);
                    return select;
                } else
                    return DefaultFieldFactory.get().createField(item, propertyId, uiContext);
            }
        }            
        
        // Bind it to a form
        final Form form = new Form();
        form.setCaption("The Star Editor");
        form.setFormFieldFactory(new MyFormFactory());
        form.setItemDataSource(starBean);
        form.setVisibleItemProperties(new Object[]{"name", "constellation"});
        
        // Read-only form to display the buffered data model value
        final Form valueForm = new Form();
        valueForm.setCaption("The Data Value");
        valueForm.setFormFieldFactory(new MyFormFactory());
        
        Button save = new Button("Save");
        save.addListener(new ClickListener() {
            private static final long serialVersionUID = -5680315979429053662L;

            @Override
            public void buttonClick(ClickEvent event) {
                form.commit();
                valueForm.setItemDataSource(starBean);
                valueForm.setVisibleItemProperties(new Object[]{"name", "constellation"});
                valueForm.setReadOnly(true);
            }
        });
        form.getFooter().addComponent(save);

        // Put the forms in a layout
        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(form);
        layout.addComponent(valueForm);
        layout.setSpacing(true);
        rootLayout.addComponent(layout);
    }
    // END-EXAMPLE: component.form.subform.boundselect

    void wrapcaptions(VerticalLayout layout) {
        // BEGIN-EXAMPLE: component.form.styling.wrapcaptions
        Form form = new Form();
        form.addField("name", new TextArea("This is a long caption " +
        		"which should wrap at some convenient length"));
        form.addStyleName("wrapcaption");
        // END-EXAMPLE: component.form.styling.wrapcaptions
        
        layout.addComponent(form);
    }

    void customvalidator(VerticalLayout layout) {
        // BEGIN-EXAMPLE: component.form.validation.customvalidator
        class MyValidator implements Validator {
            private static final long serialVersionUID = -8281962473854901819L;

            @Override
            public void validate(Object value) throws InvalidValueException {
                if (!isValid(value))
                    throw new InvalidValueException("You did not greet");
            }

            @Override
            public boolean isValid(Object value) {
                if (value instanceof String &&
                        ((String)value).equals("hello"))
                    return true;
                return false;
            }
        }
        
        final Form form = new Form();
        form.setFormFieldFactory(new FormFieldFactory() {
            private static final long serialVersionUID = -6689267221685345820L;

            @Override
            public Field createField(Item item, Object propertyId, Component uiContext) {
                if ("hello".equals(propertyId)) {
                    TextField field = new TextField("Say 'hello' here");
                    field.addValidator(new MyValidator());
                }
                return null;
            }
        });
        form.addItemProperty("hello", new ObjectProperty<String>(null));
        
        // Handle commit
        Button validate = new Button("Validate");
        validate.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 6976755192979152002L;

            @Override
            public void buttonClick(ClickEvent event) {
                form.validate();
            }
        });
        form.getFooter().addComponent(validate);
        // END-EXAMPLE: component.form.validation.customvalidator
        
        layout.addComponent(form);
    }
}
