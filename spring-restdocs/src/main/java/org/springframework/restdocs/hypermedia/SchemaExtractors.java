package org.springframework.restdocs.hypermedia;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Static factory methods providing a selection of {@link SchemaExtractor schema extractors}
 * for use when documenting a JSON hyperschema-based API.
 *
 * @author Kenny Bastani
 */
public abstract class SchemaExtractors {
    private SchemaExtractors() {

    }

    /**
     * Returns a {@code SchemaExtractor} capable of extracting a repository's schema in
     * JSON schema (json-schema.org) format where the properties are found in
     * a map named {@code properties}.
     *
     * @return The extract for the hyperschema JSON object
     */
    public static SchemaExtractor resourceSchema() {
        return new SchemaPropertyExtractor();
    }

    /**
     * Returns the {@code SchemaExtractor} for the given {@code contentType} or {@code null}
     * if there is no extractor for the content type.
     *
     * @param contentType The content type
     * @return The extractor for the content type, or {@code null}
     */
    public static SchemaExtractor extractorForContentType(String contentType) {
        if ("application/schema+json".equals(contentType)) {
            return resourceSchema();
        }
        return null;
    }

    private abstract static class JsonContentSchemaExtractor implements SchemaExtractor {

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        @SuppressWarnings("unchecked")
        public SchemaDescriptor extractSchema(MockHttpServletResponse response)
                throws IOException {
            Map<String, Object> jsonContent = this.objectMapper.readValue(
                    response.getContentAsString(), Map.class);
            return extractProperties(jsonContent);
        }

        protected abstract SchemaDescriptor extractProperties(Map<String, Object> json);
    }

    @SuppressWarnings("unchecked")
    private static class SchemaPropertyExtractor extends JsonContentSchemaExtractor {

        @Override
        public SchemaDescriptor extractProperties(Map<String, Object> json) {
            SchemaDescriptor extractedProperties = new SchemaDescriptor((String) json.get("name"));
            List<SchemaProperty> schemaProperties = new ArrayList<>();
            Object possibleProperties = json.get("properties");
            if (possibleProperties instanceof Map) {
                Map<String, Object> property = (Map<String, Object>) possibleProperties;
                for (Map.Entry<String, Object> entry : property.entrySet()) {
                    String name = entry.getKey();
                    schemaProperties.addAll(convertToProperties(entry.getValue(), name));
                }
            }
            extractedProperties.description((String) json.get("description"), schemaProperties);
            return extractedProperties;
        }

        private static List<SchemaProperty> convertToProperties(Object object, String prop) {
            List<SchemaProperty> properties = new ArrayList<>();
            if (object instanceof Map) {
                Map<String, Object> propertyObjects = (Map<String, Object>) object;
                    maybeAddProperty(maybeCreateProperty(prop,
                            propertyObjects.get("type"),
                            propertyObjects.get("description"),
                            propertyObjects.get("type")), properties);
            }
            return properties;
        }

        private static SchemaProperty maybeCreateProperty(String prop, Object type, Object description, Object required) {
            if (type instanceof String) {
                return new SchemaProperty(prop, (String) type, (String) description, Boolean.getBoolean((String)required));
            }
            return null;
        }

        private static void maybeAddProperty(SchemaProperty possibleSchemaProperty, List<SchemaProperty> properties) {
            if (possibleSchemaProperty != null) {
                properties.add(possibleSchemaProperty);
            }
        }
    }
}
