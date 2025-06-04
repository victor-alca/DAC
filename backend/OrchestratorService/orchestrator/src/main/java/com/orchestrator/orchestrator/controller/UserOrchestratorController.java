package com.orchestrator.orchestrator.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orchestrator.orchestrator.dto.ClientDTO;
import com.orchestrator.orchestrator.service.SagaUserService;

@RestController
@RequestMapping("/saga/usuarios")
public class UserOrchestratorController {

    @Autowired
    private SagaUserService sagaUserService;

    @PostMapping
    public ResponseEntity<?> criarUsuario(@RequestBody ClientDTO clientDTO) {
        String correlationId = sagaUserService.startUserRegistrationSaga(clientDTO);
        return ResponseEntity.accepted().body(Map.of(
                "message", "Cadastro iniciado com sucesso",
                "correlationId", correlationId));
    }
}