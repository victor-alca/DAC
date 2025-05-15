package backend.clients.dto;


public class MilesBalanceDTO {

    public MilesBalanceDTO(int code, Double balance) {
        this.codigo = code;
        this.saldo_milhas = balance;
    }
    public int codigo;
    public Double saldo_milhas;
}
