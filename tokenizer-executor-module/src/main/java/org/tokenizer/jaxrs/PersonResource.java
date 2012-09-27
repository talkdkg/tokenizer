package org.tokenizer.jaxrs;

import org.kauriproject.representation.build.KauriRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.dao.DataRetrievalFailureException;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.HashMap;

@Path("/persons/{id}.html")
public class PersonResource {

    private JpaDaoSupport jpaDaoSupport;

    @GET
    @Produces("text/html")
    public KauriRepresentation foo(@PathParam("id") String idParam) {
        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            throw new WebApplicationException(Response.status(400).entity("Not a valid id: " + idParam).build());
        }

        Person person = jpaDaoSupport.getJpaTemplate().find(Person.class, id);

        // Just for the demonstration purposes, foresee a fake person entity,
        // to avoid problems with initial database loading
        if (person == null && id == 1) {
            person = new Person();
            person.setId(1);
            person.setName("Mr. Production");
            person.setCity("Anderlecht");
        } else if (person == null) {
            throw new WebApplicationException(404);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("person", person);

        // The "persons/{id}" argument is a logical name identifying the representation
        // to be build. The mapping between this logical name and the real representation
        // implementation can be found in representations.groovy
        return new KauriRepresentation("pages/persons/{id}", data);
    }

    @Autowired
    public void setJpaDaoSupport(JpaDaoSupport jpaDaoSupport) {
        this.jpaDaoSupport = jpaDaoSupport;
    }
}