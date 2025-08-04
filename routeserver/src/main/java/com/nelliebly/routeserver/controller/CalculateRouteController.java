package com.nelliebly.routeserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CalculateRouteController {

	@Operation(summary = "Calculate route between two points", 
	           description = "Returns route information between start and end locations")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Route calculated successfully"),
	    @ApiResponse(responseCode = "400", description = "Invalid input parameters")
	})
	@GetMapping("/calculateRoute")
	public ResponseEntity<Map<String, Object>> calculateRoute(
			@Parameter(description = "Start location identifier") @RequestParam String start,
			@Parameter(description = "End location identifier") @RequestParam String end) {

		// Mock implementation - in a real application this would calculate an actual
		// route
		Map<String, Object> response = new HashMap<>();
		response.put("start", start);
		response.put("end", end);
		response.put("distance", "5.2 km");
		response.put("duration", "15 minutes");
		response.put("route", new String[] { "Point A", "Point B", "Point C", "Point D" });

		return ResponseEntity.ok(response);
	}

}
