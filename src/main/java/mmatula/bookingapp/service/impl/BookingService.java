package mmatula.bookingapp.service.impl;

import mmatula.bookingapp.model.Booking;
import mmatula.bookingapp.repository.BookingRepository;
import mmatula.bookingapp.service.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService implements IBookingService {

    private final BookingRepository bookingRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<Booking> getAllBookings() {
        return this.bookingRepository.findAll();
    }

    @Override
    public void addBooking(Booking booking) {
        this.bookingRepository.save(booking);
    }
}
