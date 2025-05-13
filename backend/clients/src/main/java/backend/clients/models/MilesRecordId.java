package backend.clients.models;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

public class MilesRecordId implements Serializable {

    private int clientCode;
    private Timestamp transactionDate;

    // Construtor padrão
    public MilesRecordId() {}

    // Construtor com argumentos
    public MilesRecordId(int clientCode, Timestamp transactionDate) {
        this.clientCode = clientCode;
        this.transactionDate = transactionDate;
    }

    // Getters e Setters
    public int getclientCode() {
        return clientCode;
    }

    public void setclientCode(int clientCode) {
        this.clientCode = clientCode;
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
        return Objects.equals(clientCode, that.clientCode) &&
               Objects.equals(transactionDate, that.transactionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientCode, transactionDate);
    }
}