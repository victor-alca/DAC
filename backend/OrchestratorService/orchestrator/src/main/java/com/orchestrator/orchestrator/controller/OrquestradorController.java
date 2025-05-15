package com.orchestrator.orchestrator.controller;

import com.orchestrator.orchestrator.service.OrquestradorService;
import com.orchestrator.orchestrator.utils.MessageUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class OrquestradorController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrquestradorService orquestradorService;

    // Endpoint para criar uma reserva
    @PostMapping("/reservas")
    public ResponseEntity<String> criarReserva(@RequestBody String jsonReserva) {
        String message = MessageUtils.empacotaMensagem("CriarReserva", jsonReserva);
        String response = (String) rabbitTemplate.convertSendAndReceive("reserva.v1.reserva", message);  // 
        return orquestradorService.handleReservaResponse(response);
    }

    // Endpoint para cancelar uma reserva
    @DeleteMapping("/reservas/{codigoReserva}")
    public ResponseEntity<String> cancelarReserva(@PathVariable String codigoReserva) {
        String message = MessageUtils.empacotaMensagem("CancelarReserva", codigoReserva);
        String response = (String) rabbitTemplate.convertSendAndReceive("reserva.v1.reserva", message);  // 
        return orquestradorService.handleCancelarReservaResponse(response);
    }

    // Endpoint para alterar o estado do voo
    @PatchMapping("/voos/{codigoVoo}/estado")
    public ResponseEntity<String> alterarEstadoVoo(@PathVariable String codigoVoo, @RequestBody String jsonEstado) {
        try {
            org.json.JSONObject jsonObject = new org.json.JSONObject(jsonEstado);
            String estado = jsonObject.getString("estado");
            String message = "";
            String routingKey = "voo.v1.voo";

            switch (estado.toUpperCase()) {
                case "CANCELADO":
                    message = MessageUtils.empacotaMensagem("CancelarVoo", codigoVoo);
                    break;
                case "REALIZADO":
                    message = MessageUtils.empacotaMensagem("RealizarVoo", codigoVoo);
                    break;
                default:
                    return ResponseEntity.badRequest().body("{\"message\": \"Estado de voo inválido.\"}");
            }

            String response = (String) rabbitTemplate.convertSendAndReceive(routingKey, message);
            if (estado.equalsIgnoreCase("CANCELADO")) {
                return orquestradorService.handleCancelarVooResponse(response, codigoVoo);
            } else if (estado.equalsIgnoreCase("REALIZADO")) {
                return orquestradorService.handleRealizarVooResponse(response, codigoVoo);
            }
            return ResponseEntity.internalServerError().body("{\"message\": \"Erro interno ao processar o estado do voo.\"}");

        } catch (org.json.JSONException e) {
            return ResponseEntity.badRequest().body("{\"message\": \"JSON de estado inválido.\"}");
        }
    }
}
