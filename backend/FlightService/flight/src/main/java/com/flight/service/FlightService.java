package com.flight.service;

import com.flight.model.Flight;
import com.flight.repository.FlightRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public boolean reservarPoltronas(String codigoVoo, Integer quantidade) {
        Flight voo = flightRepository.findById(codigoVoo).orElse(null);
        if (voo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Voo não encontrado");
        }
        
        int disponiveis = voo.getTotalSeats() - voo.getOccupatedSeats();
        if (disponiveis < quantidade) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Poltronas insuficientes no voo");
        }
        
        voo.setOccupatedSeats(voo.getOccupatedSeats() + quantidade);
        flightRepository.save(voo);
        return true;
    }

    public void liberarPoltronas(String codigoVoo, Integer quantidade) {
        Flight voo = flightRepository.findById(codigoVoo).orElse(null);
        if (voo == null) return;
        int ocupadas = voo.getOccupatedSeats() != null ? voo.getOccupatedSeats() : 0;
        int liberar = quantidade != null ? quantidade : 0;
        int novaOcupacao = Math.max(0, ocupadas - liberar);
        voo.setOccupatedSeats(novaOcupacao);
        flightRepository.save(voo);
        System.out.println("[VOO] Poltronas liberadas no rollback: " + liberar + " para voo " + codigoVoo);
    }

}