package backend.clients.models;


public class MilesBalance {

    public MilesBalance(int i, Double balance) {
        this.codigo = i;
        this.saldo_milhas = balance;
    }
    public int codigo;
    public Double saldo_milhas;
}
