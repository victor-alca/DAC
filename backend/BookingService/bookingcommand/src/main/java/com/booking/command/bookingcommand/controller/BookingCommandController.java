package main.java.com.booking.command.bookingcommand.controller;

import org.springframework.web.bind.annotation.RestController;

import main.java.com.booking.command.bookingcommand.entity.Booking;
import main.java.com.booking.command.bookingcommand.service.BookingCommandService;

import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/reservas")
public class BookingCommandController {

    @Autowired
    private BookingCommandService commandService;

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        return commandService.createBooking(booking);
    }

    @PutMapping("/{code}")
    public Booking updateBooking(@PathVariable String code, @RequestBody Booking booking) {
        return commandService.updateBooking(code, booking);
    }
}
