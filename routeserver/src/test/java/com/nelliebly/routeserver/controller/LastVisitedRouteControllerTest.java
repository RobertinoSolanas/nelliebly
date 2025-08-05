package com.nelliebly.routeserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LastVisitedRouteController.class)
class LastVisitedRouteControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void getLastRoutes_withValidUserId_shouldReturnRoutes() throws Exception {
		mockMvc.perform(get("/getLastRoutes").param("userid", "user123"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].id").value("route1"))
			.andExpect(jsonPath("$[0].start").value("Times Square"));
	}

	@Test
	void getLastRoutes_withoutUserId_shouldReturnEmptyList() throws Exception {
		mockMvc.perform(get("/getLastRoutes"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void getLastRoutes_withEmptyUserId_shouldReturnBadRequest() throws Exception {
		mockMvc.perform(get("/getLastRoutes").param("userid", ""))
			.andExpect(status().isBadRequest());
	}

}
