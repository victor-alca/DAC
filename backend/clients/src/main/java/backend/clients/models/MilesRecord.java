package backend.clients.models;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.*;

@Entity
@Table(schema="client", name = "miles_records")
@IdClass(MilesRecordId.class)
public class MilesRecord implements Serializable {

    @Id
    @Column(name = "client_code", nullable = false)
    private int clientCode;

    @Id
    @Column(name = "transaction_date", nullable = false)
    private Timestamp transactionDate;

    @ManyToOne
    @JoinColumn(name = "client_code", referencedColumnName = "code", insertable = false, updatable = false)
    private Client client;

    @Column(name = "value")
    private Integer value;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "type")
    private String type;

    @Column(name = "description")
    private String description;

    @Column(name = "booking_code")
    private String bookingCode;
    
    public int getClientCode() {
        return clientCode;
    }

    public void setClientCode(int clientCode) {
        this.clientCode = clientCode;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }
}