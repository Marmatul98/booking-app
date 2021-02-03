package mmatula.bookingapp.service;

import mmatula.bookingapp.model.Booking;
import mmatula.bookingapp.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<Booking> getAllBookings() {
        return this.bookingRepository.findAll();
    }

    public void addBooking(Booking booking) {
        this.bookingRepository.save(booking);
    }
}
