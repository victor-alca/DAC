package backend.clients.models;

import java.io.Serializable;
import java.util.Objects;
import java.util.Date;

public class MilesRecordId implements Serializable {
    private String clientCpf;
    private Date transactionDate;

    public MilesRecordId() {}

    public MilesRecordId(String clientCpf, Date transactionDate) {
        this.clientCpf = clientCpf;
        this.transactionDate = transactionDate;
    }
    
    public String getClientCpf() {
        return clientCpf;
    }

    public void setClientCpf(String clientCpf) {
        this.clientCpf = clientCpf;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

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