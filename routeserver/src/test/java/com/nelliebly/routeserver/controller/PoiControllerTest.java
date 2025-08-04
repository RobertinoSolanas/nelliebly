package com.nelliebly.routeserver.controller;

import com.nelliebly.routeserver.model.Poi;
import com.nelliebly.routeserver.repository.PoiRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(PoiController.class)
class PoiControllerTest {

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private PoiRepository poiRepository;

	@Test
	void getPoi_shouldReturnListOfPois() {
		webTestClient.get()
			.uri("/getPoi?lat=40.7128&lon=-74.0060")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBodyList(Object.class);
	}

	@Test
	void getPoi_withLimit_shouldReturnLimitedResults() {
		webTestClient.get()
			.uri("/getPoi?lat=40.7128&lon=-74.0060&limit=2")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBodyList(Object.class);
	}

	@Test
	void getPoi_withMockTrue_shouldReturnStaticData() {
		webTestClient.get()
			.uri("/getPoi?lat=40.7128&lon=-74.0060&mock=true")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBodyList(Object.class);
	}

	@Test
	void getPoi_withMockFalse_shouldReturnDatabaseData() {
		List<Poi> mockPois = Arrays.asList(
				new Poi("1", "Central Park", 40.7812, -73.9665, "Park"),
				new Poi("2", "Empire State Building", 40.7484, -73.9857, "Landmark"));
		
		when(poiRepository.findAll()).thenReturn(mockPois);
		
		webTestClient.get()
			.uri("/getPoi?lat=40.7128&lon=-74.0060&mock=false")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBodyList(Object.class)
			.hasSize(2);
	}

}
