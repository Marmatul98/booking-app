package mmatula.bookingapp.repository;

import mmatula.bookingapp.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> getBookingsBySportsFieldId(int id);

    List<Booking> getBookingsBySportsFieldIdAndDateOrderById(int sportsFieldId, LocalDate date);

    List<Booking> getBookingsByRequestedTrueAndConfirmedFalse();
}
