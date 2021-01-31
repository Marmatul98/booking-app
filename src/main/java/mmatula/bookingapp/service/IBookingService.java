package mmatula.bookingapp.service;

import mmatula.bookingapp.model.Booking;

import java.util.List;

public interface IBookingService {
    List<Booking> getAllBookings();
    void addBooking(Booking booking);
}
