package com.nelliebly.routeserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ShowNewRoutesController {

	@Operation(summary = "Get new routes based on a point of interest",
			description = "Returns list of new routes suggested based on a given point of interest")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Routes retrieved successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid POI ID") })
	@GetMapping("/getNewRoutes")
	public ResponseEntity<List<Map<String, Object>>> getNewRoutes(
			@Parameter(description = "Point of interest identifier") @RequestParam String poiId) {

		// Mock implementation - in a real application this would retrieve from a database
		if (poiId == null || poiId.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}

		// Return mock data for demonstration
		Map<String, Object> route1 = new HashMap<>();
		route1.put("id", "newroute1");
		route1.put("start", "Current Location");
		route1.put("end", "Central Park");
		route1.put("distance", "2.1 km");
		route1.put("estimatedTime", "25 mins");
		route1.put("poiVisited", "Central Park");

		Map<String, Object> route2 = new HashMap<>();
		route2.put("id", "newroute2");
		route2.put("start", "Current Location");
		route2.put("end", "Metropolitan Museum");
		route2.put("distance", "3.5 km");
		route2.put("estimatedTime", "40 mins");
		route2.put("poiVisited", "Metropolitan Museum");

		List<Map<String, Object>> routes = Arrays.asList(route1, route2);
		return ResponseEntity.ok(routes);
	}

}
