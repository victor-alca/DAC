package backend.clients.models;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

public class MilesRecordId implements Serializable {

    private String clientCpf;
    private Timestamp transactionDate;

    // Construtor padrão
    public MilesRecordId() {}

    // Construtor com argumentos
    public MilesRecordId(String clientCpf, Timestamp transactionDate) {
        this.clientCpf = clientCpf;
        this.transactionDate = transactionDate;
    }

    // Getters e Setters
    public String getClientCpf() {
        return clientCpf;
    }

    public void setClientCpf(String clientCpf) {
        this.clientCpf = clientCpf;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }

    // Implementação de equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MilesRecordId that = (MilesRecordId) o;
        return Objects.equals(clientCpf, that.clientCpf) &&
               Objects.equals(transactionDate, that.transactionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientCpf, transactionDate);
    }
}