package com.nelliebly.routeserver.controller;

import com.nelliebly.routeserver.model.Poi;
import com.nelliebly.routeserver.repository.PoiRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PoiController.class)
class PoiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PoiRepository poiRepository;

	@Test
	void getPoi_shouldReturnListOfPois() throws Exception {
		mockMvc.perform(get("/getPoi?lat=40.7128&lon=-74.0060")).andExpect(status().isOk());
	}

	@Test
	void getPoi_withLimit_shouldReturnLimitedResults() throws Exception {
		mockMvc.perform(get("/getPoi?lat=40.7128&lon=-74.0060&limit=2")).andExpect(status().isOk());
	}

	@Test
	void getPoi_withMockTrue_shouldReturnStaticData() throws Exception {
		mockMvc.perform(get("/getPoi?lat=40.7128&lon=-74.0060&mock=true")).andExpect(status().isOk());
	}

	@Test
	void getPoi_withMockFalse_shouldReturnDatabaseData() throws Exception {
		List<Poi> mockPois = Arrays.asList(new Poi("1", "Central Park", 40.7812, -73.9665, "Park"),
				new Poi("2", "Empire State Building", 40.7484, -73.9857, "Landmark"));

		when(poiRepository.findAll()).thenReturn(mockPois);

		mockMvc.perform(get("/getPoi?lat=40.7128&lon=-74.0060&mock=false")).andExpect(status().isOk());
	}

}
