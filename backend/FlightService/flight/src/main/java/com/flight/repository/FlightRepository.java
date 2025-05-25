package com.flight.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flight.models.Flight;

public interface FlightRepository extends JpaRepository<Flight, String> {

        Flight findByCode(String code);

}
