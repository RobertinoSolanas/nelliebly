package com.nelliebly.routeserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CalculateRouteController {

	private static final Logger logger = LoggerFactory.getLogger(CalculateRouteController.class);

	@Operation(summary = "Calculate route between two points",
			description = "Returns route information between start and end locations")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Route calculated successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input parameters") })
	@GetMapping("/calculateRoute")
	public ResponseEntity<Map<String, Object>> calculateRoute(
			@Parameter(description = "Start location identifier") @RequestParam String start,
			@Parameter(description = "End location identifier") @RequestParam String end,
			@Parameter(description = "Use mock data if true, otherwise use real data") @RequestParam(
					defaultValue = "true") boolean mock) {

		// Debug: Log incoming parameters
		logger.info("calculateRoute called with parameters: start={}, end={}, mock={}", start, end, mock);

		if (mock) {
			// Debug: Log mock path
			logger.debug("Using mock implementation");

			// Mock implementation - in a real application this would calculate an actual
			// route
			Map<String, Object> response = new HashMap<>();
			response.put("start", start);
			response.put("end", end);
			response.put("distance", "5.2 km");
			response.put("duration", "15 minutes");
			response.put("route", new String[] { "Point A", "Point B", "Point C", "Point D" });
			response.put("source", "mock");

			// Debug: Log response before sending
			logger.debug("Mock response: {}", response);

			return ResponseEntity.ok(response);
		}
		else {
			// Debug: Log real implementation path
			logger.debug("Using real OpenStreetMap implementation");

			// Real implementation using OpenStreetMap
			try {
				RestTemplate restTemplate = new RestTemplate();

				// Get coordinates for start location
				String startUrl = "https://nominatim.openstreetmap.org/search?q=" + start + "&format=json&limit=1";
				logger.debug("Calling start location API: {}", startUrl);
				String startResponse = restTemplate.getForObject(startUrl, String.class);
				logger.debug("Start location API response: {}", startResponse);

				// Get coordinates for end location
				String endUrl = "https://nominatim.openstreetmap.org/search?q=" + end + "&format=json&limit=1";
				logger.debug("Calling end location API: {}", endUrl);
				String endResponse = restTemplate.getForObject(endUrl, String.class);
				logger.debug("End location API response: {}", endResponse);

				// In a real implementation, you would parse the responses,
				// calculate the actual route, and return real data
				Map<String, Object> response = new HashMap<>();
				response.put("start", start);
				response.put("end", end);
				response.put("startCoordinatesResponse", startResponse);
				response.put("endCoordinatesResponse", endResponse);
				response.put("distance", "calculated distance");
				response.put("duration", "calculated duration");
				response.put("route", new String[] { "Calculated Point 1", "Calculated Point 2" });
				response.put("source", "openstreetmap");

				// Debug: Log final response
				logger.debug("Real implementation response: {}", response);

				return ResponseEntity.ok(response);
			}
			catch (Exception e) {
				// Debug: Log exception
				logger.error("Exception occurred: ", e);

				Map<String, Object> errorResponse = new HashMap<>();
				errorResponse.put("error", "Failed to calculate route: " + e.getMessage());
				
				// Debug: Log error response
				logger.error("Error response: {}", errorResponse);
				
				return ResponseEntity.status(500).body(errorResponse);
			}
		}
	}

}
