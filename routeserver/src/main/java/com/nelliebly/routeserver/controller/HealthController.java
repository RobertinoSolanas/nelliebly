package com.nelliebly.routeserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

	@GetMapping("/heartbeat")
	public ResponseEntity<Void> heartbeat() {
		return ResponseEntity.ok().build();
	}

}
