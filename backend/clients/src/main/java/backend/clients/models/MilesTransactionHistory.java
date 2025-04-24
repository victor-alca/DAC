package backend.clients.models;

import java.util.Date;
import java.util.List;

public class MilesTransactionHistory extends MilesBalance{

    public List<Transacao> transacoes;

    public MilesTransactionHistory(int code, Double balance) {
        super(code, balance);
    }
    public MilesTransactionHistory(int code, Double balance, List<Transacao> transacoes) {
        super(code, balance);
        this.transacoes = transacoes;
    }

    public static class Transacao {
        public Date data;
        public Double valor_reais;
        public int quantidade_milhas;
        public String descricao;
        public String codigo_reserva;
        public String tipo;

        public Transacao(Date data, Double valor_reais, int quantidade_milhas, String descricao, String codigo_reserva, String tipo) {
            this.data = data;
            this.valor_reais = valor_reais;
            this.quantidade_milhas = quantidade_milhas;
            this.descricao = descricao;
            this.codigo_reserva = codigo_reserva;
            this.tipo = tipo;
        }
    }
}