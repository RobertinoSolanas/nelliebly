package com.nelliebly.routeserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class LastVisitedRouteController {

	@Operation(summary = "Get last visited routes",
			description = "Returns list of last visited routes for a given user")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Routes retrieved successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid user ID") })
	@GetMapping("/getLastRoutes")
	public ResponseEntity<List<Map<String, Object>>> getLastRoutes(
			@Parameter(description = "User identifier") @RequestParam(required = false) String userid) {

		// Mock implementation - in a real application this would retrieve from a database
		if (userid == null) {
			// Return empty list when no user ID is provided
			return ResponseEntity.ok(Arrays.asList());
		}

		if (userid.isEmpty()) {
			// Return bad request when user ID is empty
			return ResponseEntity.badRequest().build();
		}

		// Return mock data for demonstration
		Map<String, Object> route1 = new HashMap<>();
		route1.put("id", "route1");
		route1.put("start", "Times Square");
		route1.put("end", "Central Park");
		route1.put("date", "2023-10-15T14:30:00Z");
		route1.put("distance", "3.2 km");

		Map<String, Object> route2 = new HashMap<>();
		route2.put("id", "route2");
		route2.put("start", "Empire State Building");
		route2.put("end", "Brooklyn Bridge");
		route2.put("date", "2023-10-10T10:15:00Z");
		route2.put("distance", "5.7 km");

		List<Map<String, Object>> routes = Arrays.asList(route1, route2);
		return ResponseEntity.ok(routes);
	}

}
