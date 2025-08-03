package com.nelliebly.routeserver.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(PoiController.class)
class PoiControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getPoi_shouldReturnListOfPois() {
        webTestClient
            .get()
            .uri("/getPoi?lat=40.7128&lon=-74.0060")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(Object.class)
            .hasSize(5);
    }

    @Test
    void getPoi_withLimit_shouldReturnLimitedResults() {
        webTestClient
            .get()
            .uri("/getPoi?lat=40.7128&lon=-74.0060&limit=2")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(Object.class)
            .hasSize(2);
    }
}
