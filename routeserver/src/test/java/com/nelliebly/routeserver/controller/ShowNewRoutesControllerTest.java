package com.nelliebly.routeserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShowNewRoutesController.class)
class ShowNewRoutesControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void getNewRoutes_withValidPoiId_shouldReturnRoutes() throws Exception {
		mockMvc.perform(get("/getNewRoutes").param("poiId", "poi123"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].id").value("newroute1"))
			.andExpect(jsonPath("$[0].start").value("Current Location"));
	}

	@Test
	void getNewRoutes_withoutPoiId_shouldReturnBadRequest() throws Exception {
		mockMvc.perform(get("/getNewRoutes")).andExpect(status().isBadRequest());
	}

	@Test
	void getNewRoutes_withEmptyPoiId_shouldReturnBadRequest() throws Exception {
		mockMvc.perform(get("/getNewRoutes").param("poiId", "")).andExpect(status().isOk());
	}

}
