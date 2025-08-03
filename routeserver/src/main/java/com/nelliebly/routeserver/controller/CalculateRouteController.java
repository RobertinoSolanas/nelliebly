package com.nelliebly.routeserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CalculateRouteController {

    @GetMapping("/calculateRoute")
    public ResponseEntity<Map<String, Object>> calculateRoute(
            @RequestParam String start,
            @RequestParam String end) {
        
        // Mock implementation - in a real application this would calculate an actual route
        Map<String, Object> response = new HashMap<>();
        response.put("start", start);
        response.put("end", end);
        response.put("distance", "5.2 km");
        response.put("duration", "15 minutes");
        response.put("route", new String[]{"Point A", "Point B", "Point C", "Point D"});
        
        return ResponseEntity.ok(response);
    }
}
