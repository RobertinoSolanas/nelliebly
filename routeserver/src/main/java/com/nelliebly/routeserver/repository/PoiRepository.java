package com.nelliebly.routeserver.repository;

import com.nelliebly.routeserver.model.Poi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoiRepository extends JpaRepository<Poi, String> {
}
