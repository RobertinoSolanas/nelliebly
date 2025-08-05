package com.nelliebly.routeserver.controller;

import com.nelliebly.routeserver.controller.CalculateRouteController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculateRouteController.class)
class CalculateRouteControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void calculateRoute_shouldReturnRouteInformation() throws Exception {
		mockMvc.perform(get("/calculateRoute?start=New York&end=Boston&mock=true"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.start").value("New York"))
			.andExpect(jsonPath("$.end").value("Boston"))
			.andExpect(jsonPath("$.distance").value("5.2 km"))
			.andExpect(jsonPath("$.duration").value("15 minutes"));
	}

	@Test
	void calculateRoute_withoutParameters_shouldReturnBadRequest() throws Exception {
		mockMvc.perform(get("/calculateRoute")).andExpect(status().is4xxClientError());
	}

	@Test
	void calculateRoute_withStartOnly_shouldReturnBadRequest() throws Exception {
		mockMvc.perform(get("/calculateRoute?start=New York")).andExpect(status().is4xxClientError());
	}

	@Test
	void calculateRoute_withEndOnly_shouldReturnBadRequest() throws Exception {
		mockMvc.perform(get("/calculateRoute?end=Boston")).andExpect(status().is4xxClientError());
	}

	@Test
	void calculateRoute_withMockFalse_shouldCallOpenStreetMap() throws Exception {
		mockMvc.perform(get("/calculateRoute?start=Times Square&end=Empire State Building&mock=false"))
			.andExpect(status().isOk());
	}

}
