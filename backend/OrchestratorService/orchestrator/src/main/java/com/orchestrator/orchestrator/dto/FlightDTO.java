package com.orchestrator.orchestrator.dto;

import java.util.List;
import java.util.Map;

import com.orchestrator.orchestrator.enums.CancelationSagaStep;

public class FlightDTO {
    private Integer quantidade_poltronas;
    private String codigo_voo;
    private String codigo_aeroporto_origem;
    private String codigo_aeroporto_destino;
    private List<String> reservas;

    // Getters e Setters
    public Integer getQuantidade_poltronas() {
        return quantidade_poltronas;
    }
    public void setQuantidade_poltronas(Integer quantidade_poltronas) {
        this.quantidade_poltronas = quantidade_poltronas;
    }
    public String getCodigo_voo() {
        return codigo_voo;
    }
    public void setCodigo_voo(String codigo_voo) {
        this.codigo_voo = codigo_voo;
    }
    public String getCodigo_aeroporto_origem() {
        return codigo_aeroporto_origem;
    }
    public void setCodigo_aeroporto_origem(String codigo_aeroporto_origem) {
        this.codigo_aeroporto_origem = codigo_aeroporto_origem;
    }
    public String getCodigo_aeroporto_destino() {
        return codigo_aeroporto_destino;
    }
    public void setCodigo_aeroporto_destino(String codigo_aeroporto_destino) {
        this.codigo_aeroporto_destino = codigo_aeroporto_destino;
    }
    public List<String> getReservas() {
        return reservas;
    }
    public void setCodigo_reserva(List<String> reservas) {
        this.reservas = reservas;
    }
}
