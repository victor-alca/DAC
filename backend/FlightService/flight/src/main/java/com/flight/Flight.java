

import java.io.Serializable;

import jakarta.persistence.*;

@Entity
@Table(name = "Flight")
public class Flight implements Serializable {
    @Id
    private Integer ID;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private String originAirport;

    @Column(nullable = false)
    private String destinationAirport;

    @Column(nullable = false)
    private Double totalSeats;

    @Column(nullable = false)
    private Double occupatedSeats;

    @Column(nullable = false)
    private Double status;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer iD) {
        ID = iD;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOriginAirport() {
        return originAirport;
    }

    public void setOriginAirport(String originAirport) {
        this.originAirport = originAirport;
    }

    public String getDestinationAirport() {
        return destinationAirport;
    }

    public void setDestinationAirport(String destinationAirport) {
        this.destinationAirport = destinationAirport;
    }

    public Double getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Double totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Double getOccupatedSeats() {
        return occupatedSeats;
    }

    public void setOccupatedSeats(Double occupatedSeats) {
        this.occupatedSeats = occupatedSeats;
    }

    public Double getStatus() {
        return status;
    }

    public void setStatus(Double status) {
        this.status = status;
    }

    public Flight(Integer ID, Date date, String originAirport, String destinationAirport, Double totalSeats, Double occupatedSeats, Double status) {
        this.ID = ID;
        this.date = date;
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
        this.totalSeats = totalSeats;
        this.occupatedSeats = occupatedSeats;
        this.status = status;
    }

    public Flight() {
    }
}