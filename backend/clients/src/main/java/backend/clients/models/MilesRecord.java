package backend.clients.models;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "MilesRecord")
public class MilesRecord implements Serializable {

    @Id
    @Column(name = "client_cpf", nullable = false)
    private String clientCpf;

    @Id
    @Column(name = "transaction_date", nullable = false)
    private Date transactionDate;

    @ManyToOne
    @JoinColumn(name = "client_cpf", referencedColumnName = "cpf", insertable = false, updatable = false)
    private Client client;

    @Column(nullable = false)
    private double value;

    @Column(name = "in_out", nullable = false)
    private boolean inOut;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int amountOfMiles;

    @Column(nullable = false)
    private String bookingCode;

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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public double getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isInOut() {
        return inOut;
    }

    public void setInOut(boolean inOut) {
        this.inOut = inOut;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmountOfMiles() {
        return amountOfMiles;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setAmountOfMiles(int amountOfMiles) {
        this.amountOfMiles = amountOfMiles;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }
}
