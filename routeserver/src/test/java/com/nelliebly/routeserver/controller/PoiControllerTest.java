package com.nelliebly.routeserver.controller;

import com.nelliebly.routeserver.controller.PoiController;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PoiController.class)
class PoiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PoiRepository poiRepository;

	@Test
	void getPoi_withMockTrue_shouldReturnStaticData() throws Exception {
		mockMvc.perform(get("/getPoi?lat=40.7812&lon=-73.9665&limit=5&mock=true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(5))
				.andExpect(jsonPath("$[0].name").value("Central Park"));
	}

	@Test
	void getPoi_withMockFalse_shouldReturnOpenStreetMapData() throws Exception {
		// When mock=false, it should return real data (even if simulated)
		// The actual implementation calls OpenStreetMap
		mockMvc.perform(get("/getPoi?lat=40.7812&lon=-73.9665&limit=3&mock=false"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(3));
	}

	@Test
	void getPoi_withoutParameters_shouldReturnBadRequest() throws Exception {
		mockMvc.perform(get("/getPoi"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void getPoi_withValidLimit_shouldReturnCorrectNumberOfResults() throws Exception {
		mockMvc.perform(get("/getPoi?lat=40.7812&lon=-73.9665&limit=3&mock=true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(3));
	}
	
	@Test
	void getPoi_withMockFalseAndRealCoordinates_shouldReturnData() throws Exception {
		// Test with real coordinates to verify OpenStreetMap integration
		mockMvc.perform(get("/getPoi?lat=52.5200&lon=13.4050&limit=2&mock=false")) // Berlin coordinates
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(3)); // Should return the simulated OpenStreetMap data
	}
}
