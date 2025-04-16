package backend.clients.models;

public class Address {
    private String CEP;
    private String State;
    private String City;
    private String Neighborhood;
    private String Street;
    private String Number;
    private String Complement;
    
    public Address(String cEP, String state, String city, String neighborhood, String street, String number,
            String complement) {
        CEP = cEP;
        State = state;
        City = city;
        Neighborhood = neighborhood;
        Street = street;
        Number = number;
        Complement = complement;
    }

    public Address() {
    }

    public String getCEP() {
        return CEP;
    }

    public void setCEP(String CEP) {
        this.CEP = CEP;
    }

    public String getState() {
        return State;
    }

    public void setState(String State) {
        this.State = State;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String City) {
        this.City = City;
    }

    public String getNeighborhood() {
        return Neighborhood;
    }

    public void setNeighborhood(String Neighborhood) {
        this.Neighborhood = Neighborhood;
    }

    public String getStreet() {
        return Street;
    }

    public void setStreet(String Street) {
        this.Street = Street;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String Number) {
        this.Number = Number;
    }

    public String getComplement() {
        return Complement;
    }

    public void setComplement(String Complement) {
        this.Complement = Complement;
    }
}
