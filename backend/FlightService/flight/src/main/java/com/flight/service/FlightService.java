package com.flight.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.flight.models.Flight;
import com.flight.repository.FlightRepository;

@Service
public class FlightService {

    @Autowired
    private FlightRepository flightRepository; 

        public Flight addFlight(Flight newFlight) {

        if(flightRepository.findByCode(newFlight.getCode()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Flight already exists");
        }
        flightRepository.save(newFlight);
        return newFlight;
    }

    public Flight getFlight (String code) {
        Flight flight = flightRepository.findByCode(code);
        if(flight == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Flight not found");
        }
        return flight;
    }

    public List<Flight> getFlights () {
        List<Flight> flights = flightRepository.findAll();
        return flights;
    }

    public Flight updateFlight (Flight flight){
        if(flightRepository.findByCode(flight.getCode()) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Flight not found");
        }
        flightRepository.save(flight);
        return flight;
    }


}
