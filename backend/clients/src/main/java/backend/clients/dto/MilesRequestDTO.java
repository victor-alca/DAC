package backend.clients.dto;

public class MilesRequestDTO {
    private Double quantidade;

    public MilesRequestDTO() {
    }

    public MilesRequestDTO(Double quantidade) {
        this.quantidade = quantidade;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }
}