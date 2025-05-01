package backend.clients.models;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "MilesRecord")
@IdClass(MilesRecordId.class)
public class MilesRecord implements Serializable{
    @Id
    @Column(name = "client_cpf", nullable = false)
    private String clientCpf;

    @Id
    @Column(name = "transaction_date", nullable = false)
    private Date transactionDate;

    @Column(nullable = false)
    private int value;

    @Column(name = "in_out", nullable = false)
    private boolean inOut;

    @Column(nullable = false)
    private String description;
}
