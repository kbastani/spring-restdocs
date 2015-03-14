/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.hypermedia;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link org.springframework.restdocs.hypermedia.SchemaExtractors}.
 *
 * @author Kenny Bastani
 */
@RunWith(Parameterized.class)
public class SchemaExtractorsTests {

	private final SchemaExtractor SchemaExtractor;

	private final String SchemaType;

	@Parameters
	public static Collection<Object[]> data() {
        ArrayList<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[] { SchemaExtractors.resourceSchema(), "resource" });
		return parameters;
	}

	public SchemaExtractorsTests(SchemaExtractor SchemaExtractor, String SchemaType) {
		this.SchemaExtractor = SchemaExtractor;
		this.SchemaType = SchemaType;
	}

	@Test
	public void singleSchema() throws IOException {
		SchemaDescriptor actual = this.SchemaExtractor
				.extractSchema(createResponse("single-schema"));
        SchemaDescriptor expected = new SchemaDescriptor("com.example.notes.Note");

        expected.description("rest.description.note", Arrays.asList(
                new SchemaProperty("id", "long", "rest.description.note.id", false ),
                new SchemaProperty("title", "string", "rest.description.note.title", false ),
                new SchemaProperty("body", "string", "rest.description.note.body", false )));

		assertSchemas(expected, actual);
	}

	@Test
	public void noSchema() throws IOException {
        SchemaDescriptor actual = this.SchemaExtractor
                .extractSchema(createResponse("no-schemas"));
        SchemaDescriptor expected = new SchemaDescriptor(null);
        assertSchemas(expected, actual);
	}

	@Test
	public void schemaInTheWrongFormat() throws IOException {
        SchemaDescriptor actual = this.SchemaExtractor
                .extractSchema(createResponse("wrong-format"));
        SchemaDescriptor expected = new SchemaDescriptor("com.example.notes.Note");
        expected.description("rest.description.note", Collections.<SchemaProperty> emptyList());
        assertSchemas(expected, actual);
	}

	private void assertSchemas(SchemaDescriptor expectedSchema, SchemaDescriptor actualSchema) {
		assertEquals(expectedSchema, actualSchema);
	}

	private MockHttpServletResponse createResponse(String contentName) throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		FileCopyUtils.copy(new FileReader(getPayloadFile(contentName)),
				response.getWriter());
		return response;
	}

	private File getPayloadFile(String name) {
		return new File("src/test/resources/schema-payloads/" + this.SchemaType + "/" + name
				+ ".json");
	}
}
