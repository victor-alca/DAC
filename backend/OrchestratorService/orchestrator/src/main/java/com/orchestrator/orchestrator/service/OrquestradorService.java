package com.orchestrator.orchestrator.service;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class OrquestradorService {

    public ResponseEntity<String> handleReservaResponse(String response) {
        // Lógica para processar a resposta de criação de reserva
        // Analisar o JSON de resposta e retornar o ResponseEntity apropriado
        JSONObject jsonResponse = new JSONObject(response);
        String message = jsonResponse.getString("message");
        String payload = jsonResponse.getString("payload");

        switch (message) {
            case "ReservaCriada":
                return ResponseEntity.status(HttpStatus.CREATED).body(payload);
            case "FalhaCriarReserva":
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
            case "ConflitoCriarReserva":
                return ResponseEntity.status(HttpStatus.CONFLICT).body(payload);
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro desconhecido ao criar reserva");
        }
    }

    public ResponseEntity<String> handleCancelarReservaResponse(String response) {
        // Lógica para processar a resposta de cancelamento de reserva
         JSONObject jsonResponse = new JSONObject(response);
        String message = jsonResponse.getString("message");
        String payload = jsonResponse.getString("payload");

        switch (message) {
            case "ReservaCancelada":
                return ResponseEntity.ok(payload);
            case "FalhaCancelarReserva":
                return ResponseEntity.notFound().build();
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro desconhecido ao cancelar reserva");
        }
    }

    public ResponseEntity<String> handleCancelarVooResponse(String response, String codigoVoo) {
        // Lógica para processar a resposta de cancelamento de voo
         JSONObject jsonResponse = new JSONObject(response);
        String message = jsonResponse.getString("message");
        String payload = jsonResponse.getString("payload");

        switch (message) {
            case "VooCancelado":
                return ResponseEntity.ok(payload);
            case "FalhaCancelarVoo":
                return ResponseEntity.notFound().build();
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro desconhecido ao cancelar voo");
        }
    }

    public ResponseEntity<String> handleRealizarVooResponse(String response, String codigoVoo) {
         JSONObject jsonResponse = new JSONObject(response);
        String message = jsonResponse.getString("message");
        String payload = jsonResponse.getString("payload");
        // Lógica para processar a resposta de realização de voo
        switch (message) {
            case "VooRealizado":
                return ResponseEntity.ok(payload);
            case "FalhaRealizarVoo":
                return ResponseEntity.notFound().build();
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro desconhecido ao realizar voo");
        }
    }
}
