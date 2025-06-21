package com.flight.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.flight.model.Airport;
import com.flight.repository.AirportRepository;

@Configuration
public class InitialAirportSetup {
    @Bean
    CommandLineRunner initAirports(AirportRepository AirportRepository) {
        return args -> {
            if (AirportRepository.findById("GRU") == null) {

                Airport airport = new Airport();
                airport.setCode("GRU");
                airport.setName("Aeroporto Internacional de São Paulo/Guarulhos");
                airport.setCity("Guarulhos");
                airport.setFederativeUnit("SP");

                AirportRepository.save(airport);
                System.out.println("[INIT] Airport created: GRU");
            }

            if (AirportRepository.findById("GIG") == null) {

                Airport airport = new Airport();
                airport.setCode("GIG");
                airport.setName("Aeroporto Internacional do Rio de Janeiro/Galeão");
                airport.setCity("Rio de Janeiro");
                airport.setFederativeUnit("RJ");

                AirportRepository.save(airport);
                System.out.println("[INIT] Airport created: GIG");
            }

            if (AirportRepository.findById("CWB") == null) {

                Airport airport = new Airport();
                airport.setCode("CWB");
                airport.setName("Aeroporto Internacional de Curitiba");
                airport.setCity("Curitiba");
                airport.setFederativeUnit("PR");

                AirportRepository.save(airport);
                System.out.println("[INIT] Airport created: CWB");
            }

            if (AirportRepository.findById("POA") == null) {

                Airport airport = new Airport();
                airport.setCode("POA");
                airport.setName("Aeroporto Internacional Salgado Filho");
                airport.setCity("Porto Alegre");
                airport.setFederativeUnit("RS");

                AirportRepository.save(airport);
                System.out.println("[INIT] Airport created: POA");
            }
        };
    }
}
