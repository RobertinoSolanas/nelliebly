package com.nelliebly.routeserver.controller;

import com.nelliebly.routeserver.model.Poi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

	@GetMapping("/getPoi")
	public List<Poi> getPoi(@RequestParam double lat, @RequestParam double lon,
			@RequestParam(defaultValue = "5") int limit) {

		// For now, return all POIs (static implementation)
		// In a real implementation, you would filter by proximity to (lat, lon)
		return POIS.stream().limit(limit).toList();
	}

}
