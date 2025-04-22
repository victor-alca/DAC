package backend.clients.models;


public class addMilesReturn {

    public addMilesReturn(int i, Double newBalance) {
        this.code = i;
        this.miles_balance = newBalance;
    }
    public int code;
    public Double miles_balance;
}
