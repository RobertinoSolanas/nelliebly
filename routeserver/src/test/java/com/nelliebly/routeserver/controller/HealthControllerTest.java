package com.nelliebly.routeserver.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(HealthController.class)
class HealthControllerTest {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void heartbeat_shouldReturnOk() {
		webTestClient.get().uri("/heartbeat").exchange().expectStatus().isOk();
	}

}
