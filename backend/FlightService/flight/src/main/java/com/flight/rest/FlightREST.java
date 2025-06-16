package com.flight.rest;

import com.flight.dto.VooDTO;
import com.flight.dto.VooEstadoDTO;
import com.flight.dto.AeroportoDTO;
import com.flight.model.Flight;
import com.flight.model.Airport;
import com.flight.service.FlightService;
import com.flight.service.AirportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/voos")
public class FlightREST {

    private final FlightService flightService;
    private final AirportService airportService;

    public FlightREST(FlightService flightService, AirportService airportService) {
        this.flightService = flightService;
        this.airportService = airportService;
    }

    // Buscar voo por código
    @GetMapping("/{codigoVoo}")
    public VooDTO getVooByCodigo(@PathVariable String codigoVoo) {
        Flight flight = flightService.findById(codigoVoo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voo não encontrado"));
        return toDTO(flight);
    }

    // Buscar voos por aeroportos e data
    @GetMapping(params = {"data", "origem", "destino"})
    public Map<String, Object> getVoosPorAeroportos(
            @RequestParam String data,
            @RequestParam String origem,
            @RequestParam String destino) {
        // data: "2025-06-22" ou "2024-10-13T16:00:00-03:00"
        LocalDate filtroData;
        try {
            if (data.length() == 10) {
                filtroData = LocalDate.parse(data);
            } else {
                filtroData = OffsetDateTime.parse(data).toLocalDate();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data inválida");
        }

        List<Flight> voos = flightService.findAll().stream()
                .filter(f -> f.getOriginAirport().equalsIgnoreCase(origem))
                .filter(f -> f.getDestinationAirport().equalsIgnoreCase(destino))
                // Supondo que o campo date é LocalTime, então só filtra por dia se possível
                .collect(Collectors.toList());

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("data", data);
        resp.put("origem", origem);
        resp.put("destino", destino);
        resp.put("voos", voos.stream().map(this::toDTO).collect(Collectors.toList()));
        return resp;
    }

    // Buscar todos os voos em um período
    @GetMapping(params = {"inicio", "fim"})
    public ResponseEntity<Map<String, Object>> getVoosPorPeriodo(
            @RequestParam String inicio,
            @RequestParam String fim) {
        LocalDate dataInicio, dataFim;
        try {
            // Parse apenas da data (YYYY-MM-DD)
            dataInicio = LocalDate.parse(inicio);
            dataFim = LocalDate.parse(fim);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datas inválidas. Use formato YYYY-MM-DD");
        }

        List<Flight> voos = flightService.findAll().stream()
                .filter(f -> {
                    if (f.getDate() == null) return false;
                    LocalDate dataVoo = f.getDate().toLocalDate();
                    return !dataVoo.isBefore(dataInicio) && !dataVoo.isAfter(dataFim);
                })
                .collect(Collectors.toList());

        if (voos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("inicio", inicio);
        resp.put("fim", fim);
        resp.put("voos", voos.stream().map(this::toDTO).collect(Collectors.toList()));
        return ResponseEntity.ok(resp);
    }

    // Inserir um voo novo
    @PostMapping
    public ResponseEntity<VooDTO> inserirVoo(@RequestBody Map<String, Object> body) {
        try {
            Flight flight = new Flight();
            
            // Parse da data com suporte a timezone
            String dataStr = (String) body.get("data");
            if (dataStr != null) {
                try {
                    // Tenta primeiro com timezone (OffsetDateTime)
                    if (dataStr.contains("T") && (dataStr.contains("+") || dataStr.contains("-") || dataStr.endsWith("Z"))) {
                        OffsetDateTime offsetDateTime = OffsetDateTime.parse(dataStr);
                        flight.setDate(offsetDateTime.toLocalDateTime());
                    } else {
                        // Fallback para LocalDateTime
                        flight.setDate(LocalDateTime.parse(dataStr));
                    }
                } catch (Exception e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de data inválido. Use: YYYY-MM-DDTHH:mm:ss ou YYYY-MM-DDTHH:mm:ss±HH:mm");
                }
            }
            
            // Gera código automaticamente se não fornecido
            String codigo = (String) body.get("codigo");
            if (codigo == null || codigo.trim().isEmpty()) {
                // Gera código baseado em timestamp
                codigo = "TADS" + String.format("%04d", (int)(System.currentTimeMillis() % 10000));
            }
            flight.setCode(codigo);
            
            flight.setOriginAirport((String) body.get("codigo_aeroporto_origem"));
            flight.setDestinationAirport((String) body.get("codigo_aeroporto_destino"));
            flight.setTotalSeats((Integer) body.get("quantidade_poltronas_total"));
            
            // Define poltronas ocupadas (padrão 0 se não informado)
            Integer ocupadas = (Integer) body.get("quantidade_poltronas_ocupadas");
            flight.setOccupatedSeats(ocupadas != null ? ocupadas : 0);
            
            if (body.get("valor_passagem") != null) {
                flight.setValorPassagem(Double.valueOf(body.get("valor_passagem").toString()));
            }
            
            // Define status padrão como CONFIRMADO (1)
            flight.setStatus(1);
            
            Flight saved = flightService.save(flight);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(saved));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao criar voo: " + e.getMessage());
        }
    }

    // Alterar estado do voo
    @PatchMapping("/{codigoVoo}/estado")
    public ResponseEntity<VooDTO> alterarEstado(@PathVariable String codigoVoo, @RequestBody VooEstadoDTO estadoDTO) {
        Flight flight = flightService.findById(codigoVoo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voo não encontrado"));
        String estado = estadoDTO.getEstado();
        int status = mapEstadoToStatus(estado);
        if (status == 1 && !"CONFIRMADO".equalsIgnoreCase(estado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado inválido");
        }
        flight.setStatus(status);
        Flight atualizado = flightService.save(flight);
        return ResponseEntity.ok(toDTO(atualizado));
    }

    // Utilitário para converter Flight em VooDTO
    private VooDTO toDTO(Flight flight) {
        VooDTO dto = new VooDTO();
        dto.setCodigo(flight.getCode());
        if (flight.getDate() != null) {
            dto.setData(flight.getDate().atZone(ZoneId.of("America/Sao_Paulo")).toOffsetDateTime());
        }
        dto.setValor_passagem(flight.getValorPassagem());
        dto.setQuantidade_poltronas_total(flight.getTotalSeats());
        dto.setQuantidade_poltronas_ocupadas(flight.getOccupatedSeats());
        dto.setEstado(mapStatusToEstado(flight.getStatus()));
        dto.setAeroporto_origem(toAeroportoDTO(flight.getOriginAirport()));
        dto.setAeroporto_destino(toAeroportoDTO(flight.getDestinationAirport()));
        return dto;
    }

    private AeroportoDTO toAeroportoDTO(String codigo) {
        Airport airport = airportService.findAll().stream()
                .filter(a -> a.getCode().equals(codigo))
                .findFirst()
                .orElse(null);
        if (airport == null) return null;
        AeroportoDTO dto = new AeroportoDTO();
        dto.setCodigo(airport.getCode());
        dto.setNome(airport.getName());
        dto.setCidade(airport.getCity());
        dto.setUf(airport.getFederativeUnit());
        return dto;
    }

    // Mapeamento de status int <-> estado string
    private String mapStatusToEstado(Integer status) {
        if (status == null) return null;
        switch (status) {
            case 1: return "CONFIRMADO";
            case 2: return "CANCELADO";
            case 3: return "REALIZADO";
            default: return "DESCONHECIDO";
        }
    }

    private int mapEstadoToStatus(String estado) {
        if (estado == null) return 1;
        switch (estado.toUpperCase()) {
            case "CONFIRMADO": return 1;
            case "CANCELADO": return 2;
            case "REALIZADO": return 3;
            default: return 1;
        }
    }
}