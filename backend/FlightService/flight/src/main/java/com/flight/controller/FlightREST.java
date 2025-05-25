package com.flight.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flight.models.Flight;
import com.flight.service.FlightService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


    @CrossOrigin
    @RestController
    public class FlightREST {

        @Autowired
        private FlightService flightService;

        @GetMapping("/voos")
        public ResponseEntity<List<Flight>> getFlights() {
            List<Flight> flights = flightService.getFlights();
            return ResponseEntity.status(HttpStatus.OK).body(flights);
        }

        @GetMapping("voo/{code}")
        public ResponseEntity<Flight> getFlight(@PathVariable("code") String code) {
            Flight flight = flightService.getFlight(code);
            return ResponseEntity.status(HttpStatus.OK).body(flight);
        }

        @PostMapping("voos")
        public ResponseEntity<Flight> createFlight(@RequestBody Flight flight) {
            flightService.addFlight(flight);
            return ResponseEntity.status(HttpStatus.CREATED).body(flight);
        }
    }
