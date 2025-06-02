package com.flight.service;

import com.flight.model.Flight;
import com.flight.repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public List<Flight> findAll() {
        return flightRepository.findAll();
    }

    public Optional<Flight> findById(String code) {
        return flightRepository.findById(code);
    }

    public Flight save(Flight flight) {
        return flightRepository.save(flight);
    }

    public void deleteById(String code) {
        flightRepository.deleteById(code);
    }
}