package mmatula.bookingapp.controller;

import mmatula.bookingapp.model.Booking;
import mmatula.bookingapp.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/booking")
    public List<Booking> getAllBookings(){
        return this.bookingService.getAllBookings();
    }

    @PostMapping("/booking")
    public void addBooking(@RequestBody Booking booking){
        this.bookingService.addBooking(booking);
    }
}
