/*
 * This file configures the representation builder.
 *
 * When using the representation builder, resource classes can simply return a
 * KauriRepresentation object containing a logical representation
 * name and some data objects, and this configuration file maps
 * the logical representation name to an actual implementation.
 *
 * This file also defines the representation to send in case an exception occurs.
 *
 * See the documentation for more information.
 */
builder.representations {
    select {
        when(name: "pages/{name,all}") {
            template(src: "module:/pages/{name}.html.xml")
        }

      when(name: "{name,all}") {
          template(src: "module:/{name}.xml")
      }
    }

    exceptions {
        exception {
            template(src: "clap://thread/org/kauriproject/representation/defaulterrorpage.xml")
        }
    }

    errors {
        error {
            template(src: "clap://thread/org/kauriproject/representation/defaulterrorpage.xml");
        }
    }
}