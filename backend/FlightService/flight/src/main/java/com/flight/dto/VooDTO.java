package com.flight.dto;

import java.time.OffsetDateTime;

public class VooDTO {
    private String codigo;
    private OffsetDateTime data;
    private Double valor_passagem;
    private Integer quantidade_poltronas_total;
    private Integer quantidade_poltronas_ocupadas;
    private String estado;
    private AeroportoDTO aeroporto_origem;
    private AeroportoDTO aeroporto_destino;
    
    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public OffsetDateTime getData() {
        return data;
    }
    public void setData(OffsetDateTime data) {
        this.data = data;
    }
    public Double getValor_passagem() {
        return valor_passagem;
    }
    public void setValor_passagem(Double valor_passagem) {
        this.valor_passagem = valor_passagem;
    }
    public Integer getQuantidade_poltronas_total() {
        return quantidade_poltronas_total;
    }
    public void setQuantidade_poltronas_total(Integer quantidade_poltronas_total) {
        this.quantidade_poltronas_total = quantidade_poltronas_total;
    }
    public Integer getQuantidade_poltronas_ocupadas() {
        return quantidade_poltronas_ocupadas;
    }
    public void setQuantidade_poltronas_ocupadas(Integer quantidade_poltronas_ocupadas) {
        this.quantidade_poltronas_ocupadas = quantidade_poltronas_ocupadas;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public AeroportoDTO getAeroporto_origem() {
        return aeroporto_origem;
    }
    public void setAeroporto_origem(AeroportoDTO aeroporto_origem) {
        this.aeroporto_origem = aeroporto_origem;
    }
    public AeroportoDTO getAeroporto_destino() {
        return aeroporto_destino;
    }
    public void setAeroporto_destino(AeroportoDTO aeroporto_destino) {
        this.aeroporto_destino = aeroporto_destino;
    }

    

}