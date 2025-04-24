package main.java.com.flight;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.flight.model.Flight;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


public class FlightREST {

    @CrossOrigin
    @RestController
    public class FlightREST {
    public static List<Flight> flights = new ArrayList<>();
    
        @GetMapping("/flights")
        public List<Flight> getFlights() {
            return flights;
        }

        @GetMapping("/flights/{id}")
        public Flight getFlightById(@PathVariable("id") int id) {
            Flight flight = flights.stream().filter(f -> f.getId() == id).
            findAny().orElse(null);
            return flight;
        }

        @PostMapping("/flights")
        public Flight insertFlight(@RequestBody Flight flight) {

            Flight newFlight = flights.stream().
            max(Comparator.comparing(Flight::getId)).
            orElse(null);
            if (newFlight == null)
            flight.setId(1);
            else
            flight.setId(newFlight.getId() + 1);
            flights.add(flight);
            return flight;
        }

        @PutMapping("/flights/{id}")
        public Flight updateFlight(@PathVariable("id") int id,
        @RequestBody Flight flight) {

            Flight newFlight = flights.stream().filter(f -> f.getId() == id).
            findAny().orElse(null);
            if (newFlight != null) {
            newFlight.setName(flight.getName());
            newFlight.setDate(flight.getDate());
            newFlight.setOriginAirport(flight.getOriginAirport());
            newFlight.setDestinationAirport(flight.getDestinationAirport());
            newFlight.setTotalSeats(flight.getTotalSeats());
            newFlight.setOccupatedSeats(flight.getOccupatedSeats());
            newFlight.setStatus(flight.getStatus());
            }
            return newFlight;
        }

        @DeleteMapping("/flights/{id}")
        public Flight deleteFlight(@PathVariable("id") int id) {

            Flight flight = flights.stream().
            filter(f -> f.getId() == id).
            findAny().orElse(null);
            if (flight != null)
            flights.removeIf(f -> f.getId()==id);
            return flight;

        }

    }
}
