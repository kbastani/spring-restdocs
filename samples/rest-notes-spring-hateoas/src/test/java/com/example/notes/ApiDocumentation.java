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

package com.example.notes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.config.RestDocumentationConfigurer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.RequestDispatcher;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.RestDocumentation.document;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.schemaForResource;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RestNotesSpringHateoas.class)
@WebAppConfiguration
public class ApiDocumentation {

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(new RestDocumentationConfigurer()).build();
	}

	@Test
	public void errorExample() throws Exception {
		this.mockMvc
			.perform(get("/error")
					.requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 400)
					.requestAttr(RequestDispatcher.ERROR_REQUEST_URI,
							"/notes")
					.requestAttr(RequestDispatcher.ERROR_MESSAGE,
							"The tag 'http://localhost:8080/tags/123' does not exist"))
			.andDo(print()).andExpect(status().isBadRequest())
			.andExpect(jsonPath("error", is("Bad Request")))
			.andExpect(jsonPath("timestamp", is(notNullValue())))
			.andExpect(jsonPath("status", is(400)))
			.andExpect(jsonPath("path", is(notNullValue())))
			.andDo(document("error-example"));
	}

    @Test
    public void schemaNoteExample() throws Exception {
        this.mockMvc.perform(get("/notes/schema").accept("application/schema+json"))
                .andExpect(status().isOk())
                .andDo(document("notes-schema").withSchema(schemaForResource("notes")
                .description("The <<resources-notes,Notes resource>>")));
    }

	@Test
	public void indexExample() throws Exception {
		this.mockMvc.perform(get("/").accept("application/hal+json"))
			.andExpect(status().isOk())
			.andDo(document("index-example").withLinks(
						linkWithRel("notes").description(
								"The <<resources-notes,Notes resource>>"),
						linkWithRel("tags").description(
								"The <<resources-tags,Tags resource>>"),
                        linkWithRel("profile").description(
                                "The <<resources-profile,Profile resource>>")));
	}

	@Test
	public void notesListExample() throws Exception {
		this.noteRepository.deleteAll();

		createNote("REST maturity model",
				"http://martinfowler.com/articles/richardsonMaturityModel.html");
		createNote("Hypertext Application Language (HAL)",
				"http://stateless.co/hal_specification.html");
		createNote("Application-Level Profile Semantics (ALPS)", "http://alps.io/spec/");

		this.mockMvc.perform(get("/notes"))
			.andExpect(status().isOk())
			.andDo(document("notes-list-example"));
	}

	@Test
	public void notesCreateExample() throws Exception {
		Map<String, String> tag = new HashMap<String, String>();
		tag.put("name", "REST");

		String tagLocation = this.mockMvc
				.perform(
						post("/tags").contentType(MediaTypes.HAL_JSON).content(
								this.objectMapper.writeValueAsString(tag)))
				.andExpect(status().isCreated()).andReturn().getResponse()
				.getHeader("Location");

		Map<String, Object> note = new HashMap<String, Object>();
		note.put("title", "REST maturity model");
		note.put("body", "http://martinfowler.com/articles/richardsonMaturityModel.html");
		note.put("tags", Arrays.asList(tagLocation));

		this.mockMvc.perform(
				post("/notes").contentType(MediaTypes.HAL_JSON).content(
						this.objectMapper.writeValueAsString(note)))
				.andExpect(status().isCreated())
				.andDo(document("notes-create-example"));
	}

	@Test
	public void noteGetExample() throws Exception {
		Map<String, String> tag = new HashMap<String, String>();
		tag.put("name", "REST");

		String tagLocation = this.mockMvc
				.perform(
						post("/tags").contentType(MediaTypes.HAL_JSON).content(
								this.objectMapper.writeValueAsString(tag)))
				.andExpect(status().isCreated()).andReturn().getResponse()
				.getHeader("Location");

		Map<String, Object> note = new HashMap<String, Object>();
		note.put("title", "REST maturity model");
		note.put("body", "http://martinfowler.com/articles/richardsonMaturityModel.html");
		note.put("tags", Arrays.asList(tagLocation));

		String noteLocation = this.mockMvc
				.perform(
						post("/notes").contentType(MediaTypes.HAL_JSON).content(
								this.objectMapper.writeValueAsString(note)))
				.andExpect(status().isCreated()).andReturn().getResponse()
				.getHeader("Location");

		this.mockMvc.perform(get(noteLocation))
			.andExpect(status().isOk())
			.andExpect(jsonPath("title", is(note.get("title"))))
			.andExpect(jsonPath("body", is(note.get("body"))))
			.andExpect(jsonPath("_links.self.href", is(noteLocation)))
			.andExpect(jsonPath("_links.tags", is(notNullValue())))
			.andDo(document("note-get-example").withLinks(
						linkWithRel("self").description("This <<resources-note,note>>"),
						linkWithRel("tags").description(
								"This note's <<resources-tags,tags>>")));

	}

	@Test
	public void tagsListExample() throws Exception {
		this.noteRepository.deleteAll();
		this.tagRepository.deleteAll();

		createTag("REST");
		createTag("Hypermedia");
		createTag("HTTP");

		this.mockMvc.perform(get("/tags"))
			.andExpect(status().isOk())
			.andDo(document("tags-list-example"));
	}

	@Test
	public void tagsCreateExample() throws Exception {
		Map<String, String> tag = new HashMap<String, String>();
		tag.put("name", "REST");

		this.mockMvc.perform(
				post("/tags").contentType(MediaTypes.HAL_JSON).content(
						this.objectMapper.writeValueAsString(tag)))
				.andExpect(status().isCreated())
				.andDo(document("tags-create-example"));
	}

	@Test
	public void noteUpdateExample() throws Exception {
		Map<String, Object> note = new HashMap<String, Object>();
		note.put("title", "REST maturity model");
		note.put("body", "http://martinfowler.com/articles/richardsonMaturityModel.html");

		String noteLocation = this.mockMvc
				.perform(
						post("/notes").contentType(MediaTypes.HAL_JSON).content(
								this.objectMapper.writeValueAsString(note)))
				.andExpect(status().isCreated()).andReturn().getResponse()
				.getHeader("Location");

		this.mockMvc.perform(get(noteLocation)).andExpect(status().isOk())
				.andExpect(jsonPath("title", is(note.get("title"))))
				.andExpect(jsonPath("body", is(note.get("body"))))
				.andExpect(jsonPath("_links.self.href", is(noteLocation)))
				.andExpect(jsonPath("_links.tags", is(notNullValue())));

		Map<String, String> tag = new HashMap<String, String>();
		tag.put("name", "REST");

		String tagLocation = this.mockMvc
				.perform(
						post("/tags").contentType(MediaTypes.HAL_JSON).content(
								this.objectMapper.writeValueAsString(tag)))
				.andExpect(status().isCreated()).andReturn().getResponse()
				.getHeader("Location");

		Map<String, Object> noteUpdate = new HashMap<String, Object>();
		noteUpdate.put("tags", Arrays.asList(tagLocation));

		this.mockMvc.perform(
				patch(noteLocation).contentType(MediaTypes.HAL_JSON).content(
						this.objectMapper.writeValueAsString(noteUpdate)))
				.andExpect(status().isNoContent())
				.andDo(document("note-update-example"));
	}

	@Test
	public void tagGetExample() throws Exception {
		Map<String, String> tag = new HashMap<String, String>();
		tag.put("name", "REST");

		String tagLocation = this.mockMvc
				.perform(
						post("/tags").contentType(MediaTypes.HAL_JSON).content(
								this.objectMapper.writeValueAsString(tag)))
				.andExpect(status().isCreated()).andReturn().getResponse()
				.getHeader("Location");

		this.mockMvc.perform(get(tagLocation).contentType(MediaTypes.HAL_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("name", is(tag.get("name"))))
			.andDo(document("tag-get-example").withLinks(
						linkWithRel("self").description("This <<resources-tag,tag>>"),
						linkWithRel("notes")
								.description(
										"The <<resources-tagged-notes,notes>> that have this tag")));
	}

	@Test
	public void tagUpdateExample() throws Exception {
		Map<String, String> tag = new HashMap<String, String>();
		tag.put("name", "REST");

		String tagLocation = this.mockMvc
				.perform(
						post("/tags").contentType(MediaTypes.HAL_JSON).content(
								this.objectMapper.writeValueAsString(tag)))
				.andExpect(status().isCreated()).andReturn().getResponse()
				.getHeader("Location");

		Map<String, Object> tagUpdate = new HashMap<String, Object>();
		tagUpdate.put("name", "RESTful");

		this.mockMvc.perform(
				patch(tagLocation).contentType(MediaTypes.HAL_JSON).content(
						this.objectMapper.writeValueAsString(tagUpdate)))
				.andExpect(status().isNoContent())
				.andDo(document("tag-update-example"));
	}

	private void createNote(String title, String body) {
		Note note = new Note();
		note.setTitle(title);
		note.setBody(body);

		this.noteRepository.save(note);
	}

	private void createTag(String name) {
		Tag tag = new Tag();
		tag.setName(name);
		this.tagRepository.save(tag);
	}
}
