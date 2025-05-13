package backend.clients.dto;

import java.util.List;

public class ClientBookingsDTO {

    public List<String> booking_codes;

    public ClientBookingsDTO(List<String> booking_codes) {
        this.booking_codes = booking_codes;
    }

}
