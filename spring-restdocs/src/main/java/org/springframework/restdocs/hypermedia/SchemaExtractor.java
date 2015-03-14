package org.springframework.restdocs.hypermedia;

import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

/**
 * A {@code SchemaExtractor} is used to extract {@link SchemaProperty properties} from a JSON schema response. The
 * expected format of the schema properties in the response are determined by the implementation.
 *
 * @author Kenny Bastani
 *
 */
public interface SchemaExtractor {

    /**
     * Extract the schema from the given response, returning a {@code SchemaDescriptor} of properties.
     *
     * @param response The response from which the schema is to be extracted
     * @return The extracted schema descriptor
     * @throws java.io.IOException if schema extraction fails
     */
    SchemaDescriptor extractSchema(MockHttpServletResponse response)
            throws IOException;

}
