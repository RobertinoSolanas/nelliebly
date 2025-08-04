package com.nelliebly.routeserver.model;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Data
@Entity
public class Poi {

	@Id
	private String id;

	private String name;

	private double latitude;

	private double longitude;

	private String category;

	public Poi() {
	}

	public Poi(String id, String name, double latitude, double longitude, String category) {
		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.category = category;
	}

}
