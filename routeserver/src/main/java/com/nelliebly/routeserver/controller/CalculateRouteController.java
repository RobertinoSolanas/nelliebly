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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

@RestController
public class CalculateRouteController {

	private static final Logger logger = LoggerFactory.getLogger(CalculateRouteController.class);

	private static final String USER_AGENT = "Mozilla/5.0"; // More realistic user agent

	private static final Pattern COORDINATE_PATTERN = Pattern
		.compile("^[-+]?([1-9]\\d*|0)(\\.\\d+)?,[-+]?([1-9]\\d*|0)(\\.\\d+)?$");

	@Operation(summary = "Calculate route between two points",
			description = "Returns route information between start and end locations")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Route calculated successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input parameters"),
			@ApiResponse(responseCode = "500", description = "Route calculation failed") })
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

			try {
				// Real implementation using OpenStreetMap and OSRM
				RouteResult result = calculateRealRoute(start, end);

				Map<String, Object> response = new HashMap<>();
				response.put("start", start);
				response.put("end", end);
				response.put("distance", result.distance);
				response.put("duration", result.duration);
				response.put("route", result.route);
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

	/**
	 * Calculates a real route using OpenStreetMap and OSRM
	 */
	private RouteResult calculateRealRoute(String start, String end) throws Exception {
		logger.debug("Calculating real route from {} to {}", start, end);

		try {
			// Get coordinates for start location
			Coordinates startCoords = toCoordinates(start);
			logger.debug("Start coordinates: lat={}, lon={}", startCoords.lat, startCoords.lon);

			// Get coordinates for end location
			Coordinates endCoords = toCoordinates(end);
			logger.debug("End coordinates: lat={}, lon={}", endCoords.lat, endCoords.lon);

			// Fetch route using OSRM
			return fetchRoute(startCoords, endCoords);
		}
		catch (Exception e) {
			logger.error("Error calculating route: ", e);
			throw e;
		}
	}

	/**
	 * Converts a location (coordinate string or address) to coordinates
	 */
	private Coordinates toCoordinates(String location) throws Exception {
		logger.debug("Converting location to coordinates: {}", location);

		// Check if it's already coordinates
		if (COORDINATE_PATTERN.matcher(location).matches()) {
			String[] parts = location.split(",");
			return new Coordinates(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
		}

		// Geocode the address
		return geocode(location);
	}

	/**
	 * Geocodes an address using Nominatim
	 */
	@SuppressWarnings("unchecked")
	private Coordinates geocode(String q) throws Exception {
		logger.debug("Geocoding address: {}", q);

		String url = "https://nominatim.openstreetmap.org/search";

		// Build URL with query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
			.queryParam("format", "json")
			.queryParam("addressdetails", "1")
			.queryParam("limit", "1")
			.queryParam("q", q);

		String finalUrl = builder.toUriString();
		logger.debug("Calling Nominatim API: {}", finalUrl);

		// Create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("User-Agent", USER_AGENT);
		headers.add("Referer", "http://localhost"); // Use localhost instead of file://

		HttpEntity<?> entity = new HttpEntity<>(headers);

		// Make request
		RestTemplate restTemplate = new RestTemplate();
		try {
			// Using exchange method to have full control over headers
			ResponseEntity<List> response = restTemplate.exchange(finalUrl, HttpMethod.GET, entity, List.class);

			List<Map<String, Object>> results = response.getBody();
			logger.debug("Nominatim API returned {} results", results.size());

			if (results != null && !results.isEmpty()) {
				// Parse the first result
				Map<String, Object> firstResult = results.get(0);
				double lat = Double.parseDouble(firstResult.get("lat").toString());
				double lon = Double.parseDouble(firstResult.get("lon").toString());
				return new Coordinates(lat, lon);
			}
			else {
				throw new Exception("No results found for address: " + q);
			}
		}
		catch (HttpClientErrorException e) {
			logger.error("HTTP error from Nominatim: {}", e.getMessage());
			throw new Exception("Nominatim API error: " + e.getMessage());
		}
	}

	/**
	 * Fetches route using OSRM
	 */
	@SuppressWarnings("unchecked")
	private RouteResult fetchRoute(Coordinates start, Coordinates end) throws Exception {
		logger.debug("Fetching route from ({},{}) to ({},{})", start.lat, start.lon, end.lat, end.lon);

		String url = String.format(
				"https://router.project-osrm.org/route/v1/cycling/%f,%f;%f,%f?overview=full&geometries=geojson&steps=true",
				start.lon, start.lat, end.lon, end.lat);

		logger.debug("Calling OSRM API: {}", url);

		RestTemplate restTemplate = new RestTemplate();
		try {
			// Using exchange method to control headers if needed
			HttpHeaders headers = new HttpHeaders();
			headers.add("User-Agent", USER_AGENT);

			HttpEntity<?> entity = new HttpEntity<>(headers);

			ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

			Map<String, Object> result = response.getBody();
			logger.debug("OSRM API response received");

			// Parse route information
			List<Map<String, Object>> routesList = (List<Map<String, Object>>) result.get("routes");
			if (routesList != null && !routesList.isEmpty()) {
				// Get first route
				Map<String, Object> firstRoute = routesList.get(0);
				double distanceValue = (Double) firstRoute.get("distance");
				double durationValue = (Double) firstRoute.get("duration");

				String distance = String.format("%.2f km", distanceValue / 1000);
				String duration = formatDuration(durationValue);

				// Extract route geometry
				Map<String, Object> geometry = (Map<String, Object>) firstRoute.get("geometry");
				List<List<Double>> coordinates = (List<List<Double>>) geometry.get("coordinates");

				List<String> routePoints = new ArrayList<>();
				for (List<Double> coord : coordinates) {
					routePoints.add(coord.get(1) + "," + coord.get(0)); // lat,lon format
				}

				return new RouteResult(distance, duration, routePoints.toArray(new String[0]));
			}

			throw new Exception("No route found in OSRM response");
		}
		catch (HttpClientErrorException e) {
			logger.error("HTTP error from OSRM: {}", e.getMessage());
			throw new Exception("OSRM API error: " + e.getMessage());
		}
	}

	/**
	 * Format duration in seconds to human readable format
	 */
	private String formatDuration(double seconds) {
		int minutes = (int) (seconds / 60);
		if (minutes < 60) {
			return minutes + " min";
		}
		else {
			int hours = minutes / 60;
			int remainingMinutes = minutes % 60;
			return hours + " h " + remainingMinutes + " min";
		}
	}

	/**
	 * Helper class for coordinates
	 */
	private static class Coordinates {

		public final double lat;

		public final double lon;

		public Coordinates(double lat, double lon) {
			this.lat = lat;
			this.lon = lon;
		}

	}

	/**
	 * Helper class for route result
	 */
	private static class RouteResult {

		public final String distance;

		public final String duration;

		public final String[] route;

		public RouteResult(String distance, String duration, String[] route) {
			this.distance = distance;
			this.duration = duration;
			this.route = route;
		}

	}

}
