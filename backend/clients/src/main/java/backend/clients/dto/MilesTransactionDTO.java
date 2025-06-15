package backend.clients.dto;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MilesTransactionDTO {

    private int codigo;
    private double saldo_milhas;
    private List<Transaction> transacoes;

    public static class Transaction {
        private Date data;
        @JsonProperty("valor_reais")
        private double valorReais;
        @JsonProperty("quantidade_milhas")
        private int quantidade_milhas;
        private String descricao;
        @JsonProperty("codigo_reserva")
        private String codigoReserva;
        private String tipo;

        public Transaction(Date data, double valorReais, int quantidade_milhas, String descricao, String codigoReserva,
                String tipo) {
            this.data = data;
            this.valorReais = valorReais;
            this.quantidade_milhas = quantidade_milhas;
            this.descricao = descricao;
            this.codigoReserva = codigoReserva;
            this.tipo = tipo;
        }

        public Date getData() {
            return data;
        }

        public void setData(Date data) {
            this.data = data;
        }

        public double getValorReais() {
            return valorReais;
        }

        public void setValorReais(double valorReais) {
            this.valorReais = valorReais;
        }

        public int getQuantidadeMilhas() {
            return quantidade_milhas;
        }

        public void setQuantidadeMilhas(int quantidade_milhas) {
            this.quantidade_milhas = quantidade_milhas;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public String getCodigoReserva() {
            return codigoReserva;
        }

        public void setCodigoReserva(String codigoReserva) {
            this.codigoReserva = codigoReserva;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public double getSaldo_milhas() {
        return saldo_milhas;
    }

    public void setSaldo_milhas(double saldo_milhas) {
        this.saldo_milhas = saldo_milhas;
    }

    public List<Transaction> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<Transaction> transacoes) {
        this.transacoes = transacoes;
    }

}