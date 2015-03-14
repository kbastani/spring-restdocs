package org.springframework.restdocs.hypermedia;

import java.util.ArrayList;
import java.util.List;

/**
 * A description of a resource schema found in a JSON hyperschema-based API
 *
 * @see HypermediaDocumentation#schemaForResource(String)
 *
 * @author Kenny Bastani
 */
public class SchemaDescriptor {
    private final String resource;
    private String description;
    private List<SchemaProperty> properties = new ArrayList<>();

    SchemaDescriptor(String resource) {
        this.resource = resource;
    }

    public SchemaDescriptor description(String description) {
        this.description = description;
        return this;
    }

    public SchemaDescriptor description(String description, List<SchemaProperty> properties) {
        this.description = description;
        this.properties = properties;
        return this;
    }

    String getName() {
        return this.resource;
    }

    String getDescription() {
        return this.description;
    }

    List<SchemaProperty> getProperties() {
        return this.properties;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SchemaDescriptor other = (SchemaDescriptor) obj;

        if (!((this.resource == null ? "" : this.resource).equals(other.resource == null ? "" : other.resource))) {
            return false;
        }

        if (!((this.description == null ? "" : this.description).equals(other.description == null ? "" : other.description))) {
            return false;
        }

        for(SchemaProperty schemaProperty : this.properties) {
            if(!schemaProperty.equals(other.properties.get(this.properties.indexOf(schemaProperty)))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + this.resource.hashCode();
        result = prime * result + this.description.hashCode();

        for(SchemaProperty schemaProperty : this.properties) {
            result = prime * result + schemaProperty.hashCode();
        }

        return result;
    }


}
