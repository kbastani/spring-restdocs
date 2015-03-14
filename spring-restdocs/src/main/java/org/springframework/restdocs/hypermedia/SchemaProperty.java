package org.springframework.restdocs.hypermedia;

import org.springframework.core.style.ToStringCreator;

/**
 * Representation of a schema property used in a JSON Hyperschema-based API
 *
 * @author Kenny Bastani
 */
public class SchemaProperty {

    private final String name;

    private final String type;

    private final String description;

    private final Boolean required;

    /**
     * Creates a new {@code Property} with the given {@code name}, {@code type}, {@code description}, and {@code required}
     *
     * @param name The property's name
     * @param type The property's data type
     * @param description The property's description
     * @param required The property is mandatory
     */
    public SchemaProperty(String name, String type, String description, Boolean required) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.required = required;
    }

    /**
     * Returns the property's {@code name}
     * @return the property's {@code name}
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the property's {@code type}
     * @return the property's {@code type}
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the property's {@code description}
     * @return the property's {@code description}
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the property's {@code required} field
     * @return the property's {@code required} field
     */
    public Boolean getRequired() {
        return this.required;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + this.name.hashCode();
        result = prime * result + this.type.hashCode();
        result = prime * result + this.description.hashCode();
        result = prime * result + this.required.hashCode();
        return result;
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
        SchemaProperty other = (SchemaProperty) obj;
        if (!this.name.equals(other.name)) {
            return false;
        }
        if (!this.type.equals(other.type)) {
            return false;
        }
        if (!this.description.equals(other.description)) {
            return false;
        }
        if (!this.required.equals(other.required)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("name", this.name)
                .append("type", this.type)
                .append("description", this.description)
                .append("required", this.required).toString();
    }
}
