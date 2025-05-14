package com.orchestrator.orchestrator.rabbitmq.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class InitiateBookingSagaComandoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idSaga; // ID único para identificar a saga
    private Long codigoCliente;
    private BigDecimal valorTotalReserva;
    private Integer milhasUtilizadas;
    private Integer quantidadePoltronas;
    private String codigoVoo;
    // Você pode optar por não enviar os códigos dos aeroportos se o microserviço
    // de Voo/Reserva consegue obter essa info a partir do codigoVoo.
    // Se o primeiro passo da saga precisar deles explicitamente, inclua-os.
    // private String codigoAeroportoOrigem;
    // private String codigoAeroportoDestino;


    public InitiateBookingSagaComandoDTO() {
    }

    public InitiateBookingSagaComandoDTO(String idSaga, Long codigoCliente, BigDecimal valorTotalReserva, Integer milhasUtilizadas, Integer quantidadePoltronas, String codigoVoo) {
        this.idSaga = idSaga;
        this.codigoCliente = codigoCliente;
        this.valorTotalReserva = valorTotalReserva;
        this.milhasUtilizadas = milhasUtilizadas;
        this.quantidadePoltronas = quantidadePoltronas;
        this.codigoVoo = codigoVoo;
    }

    // Getters e Setters
    public String getIdSaga() { return idSaga; }
    public void setIdSaga(String idSaga) { this.idSaga = idSaga; }
    public Long getCodigoCliente() { return codigoCliente; }
    public void setCodigoCliente(Long codigoCliente) { this.codigoCliente = codigoCliente; }
    public BigDecimal getValorTotalReserva() { return valorTotalReserva; }
    public void setValorTotalReserva(BigDecimal valorTotalReserva) { this.valorTotalReserva = valorTotalReserva; }
    public Integer getMilhasUtilizadas() { return milhasUtilizadas; }
    public void setMilhasUtilizadas(Integer milhasUtilizadas) { this.milhasUtilizadas = milhasUtilizadas; }
    public Integer getQuantidadePoltronas() { return quantidadePoltronas; }
    public void setQuantidadePoltronas(Integer quantidadePoltronas) { this.quantidadePoltronas = quantidadePoltronas; }
    public String getCodigoVoo() { return codigoVoo; }
    public void setCodigoVoo(String codigoVoo) { this.codigoVoo = codigoVoo; }

    @Override
    public String toString() {
        return "InitiateBookingSagaComandoDTO{" +
                "idSaga='" + idSaga + '\'' +
                ", codigoCliente=" + codigoCliente +
                ", valorTotalReserva=" + valorTotalReserva +
                ", milhasUtilizadas=" + milhasUtilizadas +
                ", quantidadePoltronas=" + quantidadePoltronas +
                ", codigoVoo='" + codigoVoo + '\'' +
                '}';
    }
}