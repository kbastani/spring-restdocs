package org.springframework.restdocs.hypermedia;

import org.springframework.restdocs.snippet.DocumentationWriter;
import org.springframework.restdocs.snippet.SnippetWritingResultHandler;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * A {@link SnippetWritingResultHandler} that produces a snippet documenting a RESTful
 * resource's schema.
 *
 * @author Kenny Bastani
 */
public class SchemaSnippetResultHandler extends SnippetWritingResultHandler {

    private final SchemaExtractor extractor;
    private SchemaDescriptor schemaDescriptor;

    SchemaSnippetResultHandler(String outputDir, SchemaExtractor schemaExtractor,
            SchemaDescriptor schemaDescriptor) {
        super(outputDir, "schemas");
        this.extractor = schemaExtractor;
        Assert.hasText(schemaDescriptor.getName());
        Assert.hasText(schemaDescriptor.getDescription());
        this.schemaDescriptor = schemaDescriptor;
    }

    @Override
    protected void handle(MvcResult result, DocumentationWriter writer)
    throws IOException {
        SchemaDescriptor schema;
        if (this.extractor != null) {
            schema = this.extractor.extractSchema(result.getResponse());
        } else {
            String contentType = result.getResponse().getContentType();
            SchemaExtractor extractorForContentType = SchemaExtractors
                    .extractorForContentType(contentType);
            if (extractorForContentType != null) {
                schema = extractorForContentType.extractSchema(result.getResponse());
            } else {
                throw new IllegalStateException(
                        "No SchemaExtractor has been provided and one is not available for the content type "
                                + contentType);
            }

        }

        // Allow calling test to override name format and description
        writer.println(schemaDescriptor.getDescription() == null ? schema.getDescription() : schemaDescriptor.getDescription());

        writer.println("|===");
        writer.println("| Property | Type | Description | Required");

        for (SchemaProperty schemaProperty : schema.getProperties()) {
            writer.println();
            writer.println("| " + schemaProperty.getName());
            writer.println("| " + schemaProperty.getType());
            writer.println("| " + schemaProperty.getDescription());
            writer.println("| " + schemaProperty.getRequired());
        }

        writer.println("|===");
    }
}
