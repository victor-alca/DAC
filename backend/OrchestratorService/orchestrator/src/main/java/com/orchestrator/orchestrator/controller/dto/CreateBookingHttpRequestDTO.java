package com.orchestrator.orchestrator.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class CreateBookingHttpRequestDTO {

    @JsonProperty("codigo_cliente") // Mapeia o nome do JSON para o nome do campo Java
    private Long codigoCliente;

    private BigDecimal valor;

    @JsonProperty("milhas_utilizadas")
    private Integer milhasUtilizadas;

    @JsonProperty("quantidade_poltronas")
    private Integer quantidadePoltronas;

    @JsonProperty("codigo_voo")
    private String codigoVoo;

    @JsonProperty("codigo_aeroporto_origem")
    private String codigoAeroportoOrigem;

    @JsonProperty("codigo_aeroporto_destino") 
    private String codigoAeroportoDestino;

    // Getters e Setters
    public Long getCodigoCliente() { return codigoCliente; }
    public void setCodigoCliente(Long codigoCliente) { this.codigoCliente = codigoCliente; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public Integer getMilhasUtilizadas() { return milhasUtilizadas; }
    public void setMilhasUtilizadas(Integer milhasUtilizadas) { this.milhasUtilizadas = milhasUtilizadas; }
    public Integer getQuantidadePoltronas() { return quantidadePoltronas; }
    public void setQuantidadePoltronas(Integer quantidadePoltronas) { this.quantidadePoltronas = quantidadePoltronas; }
    public String getCodigoVoo() { return codigoVoo; }
    public void setCodigoVoo(String codigoVoo) { this.codigoVoo = codigoVoo; }
    public String getCodigoAeroportoOrigem() { return codigoAeroportoOrigem; }
    public void setCodigoAeroportoOrigem(String codigoAeroportoOrigem) { this.codigoAeroportoOrigem = codigoAeroportoOrigem; }
    public String getCodigoAeroportoDestino() { return codigoAeroportoDestino; }
    public void setCodigoAeroportoDestino(String codigoAeroportoDestino) { this.codigoAeroportoDestino = codigoAeroportoDestino; }

    @Override
    public String toString() {
        return "CriarReservaHttpRequestDTO{" +
                "codigoCliente=" + codigoCliente +
                ", valor=" + valor +
                ", milhasUtilizadas=" + milhasUtilizadas +
                ", quantidadePoltronas=" + quantidadePoltronas +
                ", codigoVoo='" + codigoVoo + '\'' +
                ", codigoAeroportoOrigem='" + codigoAeroportoOrigem + '\'' +
                ", codigoAeroportoDestino='" + codigoAeroportoDestino + '\'' +
                '}';
    }
}