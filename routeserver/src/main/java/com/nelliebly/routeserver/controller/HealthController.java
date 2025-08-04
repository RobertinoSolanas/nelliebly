package com.nelliebly.routeserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

	@Operation(summary = "Health check endpoint", description = "Returns 200 OK if service is healthy")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Service is healthy") })
	@GetMapping("/heartbeat")
	public ResponseEntity<Void> heartbeat() {
		return ResponseEntity.ok().build();
	}

}
