package backend.clients.dto;

import java.util.List;

public class ClientBookingsDTO {

    public List<String> bookingCodes;

    public ClientBookingsDTO(List<String> bookingCodes) {
        this.bookingCodes = bookingCodes;
    }

}
