package com.nelliebly.routeserver.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShowNewRoutesController.class)
class ShowNewRoutesControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getNewRoutes_withValidPoiId_shouldReturnRoutes() throws Exception {
		mockMvc.perform(get("/getNewRoutes?poiId=1")).andExpect(status().isOk());
	}

	@Test
	void getNewRoutes_withoutPoiId_shouldReturnBadRequest() throws Exception {
		mockMvc.perform(get("/getNewRoutes")).andExpect(status().isBadRequest());
	}

	@Test
	void getNewRoutes_withEmptyPoiId_shouldReturnBadRequest() throws Exception {
		mockMvc.perform(get("/getNewRoutes?poiId=")).andExpect(status().isBadRequest());
	}

}
