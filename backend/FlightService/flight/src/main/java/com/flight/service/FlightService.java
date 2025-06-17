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

    // Métodos das sagas

    public boolean cancelFlight(String codigoVoo) {
        Flight voo = flightRepository.findById(codigoVoo).orElse(null);
        if (voo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Voo não encontrado");
        }

        // Verifica se o voo está no status CONFIRMADO (1)
        if (voo.getStatus() != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voo deve estar no status CONFIRMADO para ser cancelado");
        }
        
        voo.setStatus(2); // CANCELADO
        flightRepository.save(voo);
        System.out.println("[VOO] Voo " + codigoVoo + " cancelado com sucesso");
        return true;
    }

    public void revertFlightCancellation(String codigoVoo) {
        Flight voo = flightRepository.findById(codigoVoo).orElse(null);
        if (voo == null) return;
        
        voo.setStatus(1); // CONFIRMADO
        flightRepository.save(voo);
        System.out.println("[VOO] Cancelamento do voo " + codigoVoo + " foi revertido");
    }

    public boolean realizeFlight(String codigoVoo) {
        Flight voo = flightRepository.findById(codigoVoo).orElse(null);
        if (voo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Voo não encontrado");
        }
        
        // Verifica se o voo está no status CONFIRMADO (1)
        if (voo.getStatus() != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voo deve estar no status CONFIRMADO para ser realizado");
        }
        
        voo.setStatus(3); // REALIZADO
        flightRepository.save(voo);
        System.out.println("[VOO] Voo " + codigoVoo + " realizado com sucesso");
        return true;
    }

    public void revertFlightRealization(String codigoVoo) {
        Flight voo = flightRepository.findById(codigoVoo).orElse(null);
        if (voo == null) return;
        
        // Volta para CONFIRMADO (assumindo que era o status anterior)
        voo.setStatus(1); // CONFIRMADO
        flightRepository.save(voo);
        System.out.println("[VOO] Realização do voo " + codigoVoo + " foi revertida");
    }

}