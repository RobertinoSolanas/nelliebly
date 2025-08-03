package com.nelliebly.routeserver.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(CalculateRouteController.class)
class CalculateRouteControllerTest {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void calculateRoute_shouldReturnRouteInformation() {
		webTestClient.get()
			.uri("/calculateRoute?start=New York&end=Boston")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.jsonPath("$.start")
			.isEqualTo("New York")
			.jsonPath("$.end")
			.isEqualTo("Boston")
			.jsonPath("$.distance")
			.isEqualTo("5.2 km")
			.jsonPath("$.duration")
			.isEqualTo("15 minutes");
	}

	@Test
	void calculateRoute_withoutParameters_shouldReturnBadRequest() {
		webTestClient.get().uri("/calculateRoute").exchange().expectStatus().is4xxClientError();
	}

	@Test
	void calculateRoute_withStartOnly_shouldReturnBadRequest() {
		webTestClient.get().uri("/calculateRoute?start=New York").exchange().expectStatus().is4xxClientError();
	}

	@Test
	void calculateRoute_withEndOnly_shouldReturnBadRequest() {
		webTestClient.get().uri("/calculateRoute?end=Boston").exchange().expectStatus().is4xxClientError();
	}

}
