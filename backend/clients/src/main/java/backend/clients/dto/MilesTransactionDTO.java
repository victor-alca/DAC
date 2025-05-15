package backend.clients.dto;

import java.util.Date;
import java.util.List;

public class MilesTransactionDTO {

    private int codigo;
    private double saldoMilhas;
    private List<Transaction> transacoes;

    public static class Transaction {
        private Date data;
        private double valorReais;
        private int quantidadeMilhas;
        private String descricao;
        private String codigoReserva;
        private String tipo;

        public Transaction(Date data, double valorReais, int quantidadeMilhas, String descricao, String codigoReserva,
                String tipo) {
            this.data = data;
            this.valorReais = valorReais;
            this.quantidadeMilhas = quantidadeMilhas;
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
            return quantidadeMilhas;
        }

        public void setQuantidadeMilhas(int quantidadeMilhas) {
            this.quantidadeMilhas = quantidadeMilhas;
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

    public double getSaldoMilhas() {
        return saldoMilhas;
    }

    public void setSaldoMilhas(double saldoMilhas) {
        this.saldoMilhas = saldoMilhas;
    }

    public List<Transaction> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<Transaction> transacoes) {
        this.transacoes = transacoes;
    }

}