package com.nelliebly.routeserver.controller;

import com.nelliebly.routeserver.model.Poi;
import com.nelliebly.routeserver.repository.PoiRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@RestController
public class PoiController {

	private final PoiRepository poiRepository;

	public PoiController(PoiRepository poiRepository) {
		this.poiRepository = poiRepository;
	}

	@PostConstruct
	public void init() {
		// Initialize with static data if database is empty
		if (poiRepository.count() == 0) {
			initializeStaticData();
		}
	}

	private void initializeStaticData() {
		List<Poi> staticPois = Arrays.asList(new Poi("1", "Central Park", 40.7812, -73.9665, "Park"),
				new Poi("2", "Empire State Building", 40.7484, -73.9857, "Landmark"),
				new Poi("3", "Brooklyn Bridge", 40.7061, -73.9969, "Bridge"),
				new Poi("4", "Statue of Liberty", 40.6892, -74.0445, "Monument"),
				new Poi("5", "Times Square", 40.7580, -73.9855, "Landmark"));
		poiRepository.saveAll(staticPois);
	}

	@Operation(summary = "Get points of interest", description = "Returns list of POIs near specified coordinates")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "POIs retrieved successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid parameters") })
	@GetMapping("/getPoi")
	public List<Poi> getPoi(@Parameter(description = "Latitude coordinate") @RequestParam double lat,
			@Parameter(description = "Longitude coordinate") @RequestParam double lon,
			@Parameter(description = "Maximum number of results to return") @RequestParam(defaultValue = "5") int limit,
			@Parameter(description = "Use database if false, otherwise use static data") @RequestParam(
					defaultValue = "false") boolean mock) {

		if (mock) {
			// Use static implementation
			List<Poi> staticPois = Arrays.asList(new Poi("1", "Central Park", 40.7812, -73.9665, "Park"),
					new Poi("2", "Empire State Building", 40.7484, -73.9857, "Landmark"),
					new Poi("3", "Brooklyn Bridge", 40.7061, -73.9969, "Bridge"),
					new Poi("4", "Statue of Liberty", 40.6892, -74.0445, "Monument"),
					new Poi("5", "Times Square", 40.7580, -73.9855, "Landmark"));
			return staticPois.stream().limit(limit).toList();
		}
		else {
			// Use database implementation
			return poiRepository.findAll().stream().limit(limit).toList();
		}
	}

}
