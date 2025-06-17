package com.orchestrator.orchestrator.dto;

public class FlightDTO {
    private String codigo_voo;
    private String estado;

    public FlightDTO() {}

    public FlightDTO(String codigo_voo, String estado) {
        this.codigo_voo = codigo_voo;
        this.estado = estado;
    }

    public String getCodigo_voo() {
        return codigo_voo;
    }

    public void setCodigo_voo(String codigo_voo) {
        this.codigo_voo = codigo_voo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}