package com.nelliebly.routeserver.controller;

import com.nelliebly.routeserver.model.Poi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@RestController
public class PoiController {

	// Static list of sample POIs
	private static final List<Poi> POIS = Arrays.asList(new Poi("1", "Central Park", 40.7812, -73.9665, "Park"),
			new Poi("2", "Empire State Building", 40.7484, -73.9857, "Landmark"),
			new Poi("3", "Brooklyn Bridge", 40.7061, -73.9969, "Bridge"),
			new Poi("4", "Statue of Liberty", 40.6892, -74.0445, "Monument"),
			new Poi("5", "Times Square", 40.7580, -73.9855, "Landmark"));

	@Operation(summary = "Get points of interest", description = "Returns list of POIs near specified coordinates")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "POIs retrieved successfully"),
			@ApiResponse(responseCode = "400",
					description = "Invalid parameters or non-mock implementation requested") })
	@GetMapping("/getPoi")
	public List<Poi> getPoi(@Parameter(description = "Latitude coordinate") @RequestParam double lat,
			@Parameter(description = "Longitude coordinate") @RequestParam double lon,
			@Parameter(description = "Maximum number of results to return") @RequestParam(defaultValue = "5") int limit,
			@Parameter(description = "Use mock data if true, otherwise return error") @RequestParam(
					defaultValue = "true") boolean mock) {

		if (mock) {
			// Use static implementation
			return POIS.stream().limit(limit).toList();
		}
		else {
			// Return HTTP 400 when mock is false
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Non-mock implementation not available");
		}
	}

}
