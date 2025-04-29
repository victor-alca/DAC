package backend.clients.dto;


public class MilesBalanceDTO {

    public MilesBalanceDTO(int i, Double balance) {
        this.codigo = i;
        this.saldo_milhas = balance;
    }
    public int codigo;
    public Double saldo_milhas;
}
